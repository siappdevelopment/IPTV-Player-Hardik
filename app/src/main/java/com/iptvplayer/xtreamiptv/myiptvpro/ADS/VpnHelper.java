package com.iptvplayer.xtreamiptv.myiptvpro.ADS;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.iptvplayer.xtreamiptv.myiptvpro.Theme.ThemeManager;
import com.iptvplayer.xtreamiptv.myiptvpro.Theme.ThemePreference;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

public final class VpnHelper {
    private static Dialog vpnDialog;

    private VpnHelper() {
    }

    public static boolean isVpnActive(@Nullable Context context) {
        if (context == null) {
            return false;
        }
        return hasVpnTransport(context) || hasVpnNetworkInterface();
    }

    /** ConnectivityManager: TRANSPORT_VPN or active network without NOT_VPN. */
    private static boolean hasVpnTransport(Context context) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            }

            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities activeCaps =
                        connectivityManager.getNetworkCapabilities(activeNetwork);
                if (isVpnCapabilities(activeCaps)) {
                    return true;
                }
            }

            Network[] networks = connectivityManager.getAllNetworks();
            for (Network network : networks) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (isVpnCapabilities(capabilities)) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static boolean isVpnCapabilities(@Nullable NetworkCapabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            return true;
        }
        // VPN tunnel networks typically lack NET_CAPABILITY_NOT_VPN
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN);
    }

    /** Fallback: detect common VPN interface names (tun / ppp / tap / wg). */
    private static boolean hasVpnNetworkInterface() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces == null) {
                return false;
            }
            for (NetworkInterface networkInterface : Collections.list(networkInterfaces)) {
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }
                String name = networkInterface.getName();
                if (name == null) {
                    continue;
                }
                String lower = name.toLowerCase();
                if (lower.contains("tun")
                        || lower.contains("ppp")
                        || lower.contains("pptp")
                        || lower.startsWith("tap")
                        || lower.startsWith("wg")
                        || lower.contains("ipsec")
                        || lower.contains("utun")) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Blocks until VPN is off, then runs {@code onAllowed}.
     * Shows a non-cancelable dialog while VPN is active (splash).
     */
    public static void ensureVpnDisabled(@Nullable Activity activity, @Nullable Runnable onAllowed) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        if (!isVpnActive(activity)) {
            dismissDialog();
            if (onAllowed != null) {
                onAllowed.run();
            }
            return;
        }
        showVpnDialog(activity, onAllowed, false, null);
    }

    /**
     * Shows VPN dialog on Home page. Back is enabled — {@code onBackPressed} runs on back.
     * {@code onVpnCleared} runs when user taps Retry and VPN is off.
     */
    public static void showVpnWarningIfNeeded(
            @Nullable Activity activity,
            @Nullable Runnable onBackPressed,
            @Nullable Runnable onVpnCleared
    ) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        if (!isVpnActive(activity)) {
            dismissDialog();
            return;
        }
        showVpnDialog(activity, onVpnCleared, true, onBackPressed);
    }

    /** True when VPN is active or the VPN dialog is currently on screen. */
    public static boolean shouldBlockAds(@Nullable Context context) {
        return isDialogShowing() || isVpnActive(context);
    }

    public static boolean isDialogShowing() {
        return vpnDialog != null && vpnDialog.isShowing();
    }

    public static void dismissDialog() {
        if (vpnDialog != null) {
            try {
                vpnDialog.setOnCancelListener(null);
                if (vpnDialog.isShowing()) {
                    vpnDialog.dismiss();
                }
            } catch (Exception ignored) {
            }
            vpnDialog = null;
        }
    }

    private static void showVpnDialog(
            Activity activity,
            @Nullable Runnable onAllowed,
            boolean cancelable,
            @Nullable Runnable onBackPressed
    ) {
        if (vpnDialog != null && vpnDialog.isShowing()) {
            return;
        }

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(false);

        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_vpn, null);
        dialog.setContentView(contentView);
        ThemeManager.applyDialog(contentView, ThemePreference.getPrimaryColor(activity));

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int margin = activity.getResources().getDimensionPixelSize(R.dimen._10sdp);
            window.setLayout(
                    activity.getResources().getDisplayMetrics().widthPixels - margin * 2,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        contentView.findViewById(R.id.btnOpenSettings).setOnClickListener(v -> openVpnSettings(activity));
        contentView.findViewById(R.id.btnRetry).setOnClickListener(v -> {
            if (isVpnActive(activity)) {
                Toast.makeText(activity, R.string.vpn_still_active, Toast.LENGTH_SHORT).show();
                return;
            }
            dismissDialog();
            if (onAllowed != null) {
                onAllowed.run();
            }
        });

        if (cancelable) {
            dialog.setOnCancelListener(d -> {
                vpnDialog = null;
                if (onBackPressed != null) {
                    onBackPressed.run();
                }
            });
        }

        vpnDialog = dialog;

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            vpnDialog.show();
        }
    }

    private static void openVpnSettings(Activity activity) {
        try {
            activity.startActivity(new Intent(Settings.ACTION_VPN_SETTINGS));
        } catch (Exception e) {
            try {
                activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
            } catch (Exception ignored) {
            }
        }
    }
}
