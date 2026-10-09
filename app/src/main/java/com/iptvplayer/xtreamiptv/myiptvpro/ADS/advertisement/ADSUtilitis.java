package com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.iptvplayer.xtreamiptv.myiptvpro.Theme.ThemeManager;
import com.iptvplayer.xtreamiptv.myiptvpro.Theme.ThemePreference;
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Common;
import com.google.android.gms.ads.AdValue;
import com.google.firebase.analytics.FirebaseAnalytics;


public class ADSUtilitis {

    public static Dialog MassageBoxFull;

    public static void MassageBoxFull(Context context) {
        try {
            if (MassageBoxFull != null)
                if (MassageBoxFull.isShowing())
                    return;

            MassageBoxFull = new Dialog(context);
            MassageBoxFull.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View contentView = LayoutInflater.from(context)
                    .inflate(R.layout.fill_in_loader_progressbar_massage_box, null);
            MassageBoxFull.setContentView(contentView);
            ThemeManager.applyDialog(contentView, ThemePreference.getPrimaryColor(context));
            MassageBoxFull.setCancelable(false);

            Window window = MassageBoxFull.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                copyImmersiveFlagsFromActivity(context, window);
                hideDialogNavigationBar(window);
                // Prevent the dialog from restoring the system nav bar when it takes focus.
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }

            MassageBoxFull.show();

            if (window != null) {
                hideDialogNavigationBar(window);
                Common.INSTANCE.applyImmersiveFlagsToPopupRoot(window.getDecorView());
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
            Common.INSTANCE.hideSystemNavigationBar(asActivity(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MassageBoxFullDismiss() {
        try {
            Activity activity = MassageBoxFull != null ? asActivity(MassageBoxFull.getContext()) : null;
            if (MassageBoxFull.isShowing())
                MassageBoxFull.dismiss();
            Common.INSTANCE.hideSystemNavigationBar(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyImmersiveFlagsFromActivity(Context context, Window dialogWindow) {
        Activity activity = asActivity(context);
        if (activity == null) return;
        Window activityWindow = activity.getWindow();
        if (activityWindow == null) return;
        @SuppressWarnings("deprecation")
        int uiOptions = activityWindow.getDecorView().getSystemUiVisibility();
        @SuppressWarnings("deprecation")
        View dialogDecor = dialogWindow.getDecorView();
        dialogDecor.setSystemUiVisibility(uiOptions);
    }

    private static void hideDialogNavigationBar(Window window) {
        if (window == null) return;
        window.setNavigationBarColor(Color.TRANSPARENT);
        WindowCompat.setDecorFitsSystemWindows(window, false);
        View decorView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            @SuppressWarnings("deprecation")
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(flags);
        }
        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(window, decorView);
        insetsController.hide(WindowInsetsCompat.Type.navigationBars());
        insetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    private static Activity asActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static void trackScreen(Context context,String screenName) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(screenName, true);
            FirebaseAnalytics.getInstance(context).logEvent(screenName, bundle);
            ADSMainClass.setCallEndShow1(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean IsNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        android.net.NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void trackPermissionAllowOnce(Context context, String eventName) {
        try {
            String prefKey = "allow_event_" + eventName;
            android.content.SharedPreferences prefs = context.getSharedPreferences(
                    "permission_allow_events", Context.MODE_PRIVATE);
            if (prefs.getBoolean(prefKey, false)) {
                return;
            }
            prefs.edit().putBoolean(prefKey, true).commit();
            trackScreen(context, eventName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void logAdRevenue(Context context, AdValue adValue) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        double revenue = adValue.getValueMicros() / 1_000_000.0;
        String currency = adValue.getCurrencyCode();
        Bundle adRevenueParams = new Bundle();
        adRevenueParams.putString(FirebaseAnalytics.Param.AD_PLATFORM, "Google Ad Manager");
        adRevenueParams.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        adRevenueParams.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, adRevenueParams);
    }
}

