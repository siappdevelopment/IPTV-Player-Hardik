package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.iptvplayer.xtreamiptv.myiptvpro.BuildConfig;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

import java.net.NetworkInterface;
import java.util.Collections;


public class DevelSystemViewAd {

    public static Dialog reso__No_Internet_Dialog;
    public static Dialog reso__Developer_Option_Dialog;

    public static no_internet reso__No_Internet;

    public static Boolean reso__Is_Debuggle(Context context) {
        if (BuildConfig.DEBUG) {
            return false;
        }
        if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) == 1) {
            return true;
        } else {
            return false;
        }
    }
    public static no_debug reso__No_Debug;

    public static Dialog reso__Vpn_Checker_Dialog;
    public static vpn_Checker vpnChecker;

    public static void reso__VpnConnectionDialog(final Context context) {
        if (reso__Vpn_Checker_Dialog == null || !reso__Vpn_Checker_Dialog.isShowing()) {
            reso__Vpn_Checker_Dialog = new Dialog(context);
            reso__Vpn_Checker_Dialog.requestWindowFeature(1);
            reso__Vpn_Checker_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            reso__Vpn_Checker_Dialog.setContentView(R.layout.reso_vpn_checker);
            int width = -1;
            reso__Vpn_Checker_Dialog.getWindow().setLayout(width, -2);
            reso__Vpn_Checker_Dialog.setCancelable(false);
            reso__Vpn_Checker_Dialog.show();
            TextView recheck = reso__Vpn_Checker_Dialog.findViewById(R.id.buttonTurnAgain);

            recheck.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!DevelSystemViewAd.Is_VPN_Checker(context)) {
                        DevelSystemViewAd.vpnChecker.vpn_Checker();
                    }

                }
            });

        }
    }
    public static void reso__Vpn_Checker(vpn_Checker vpnChecker) {
        DevelSystemViewAd.vpnChecker = vpnChecker;
    }
    public interface vpn_Checker {
        void vpn_Checker();
    }
    public static Boolean Is_VPN_Checker(Context context) {

        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp() && (networkInterface.getName().equalsIgnoreCase("tun0") || networkInterface.getName().equalsIgnoreCase("ppp0"))) {
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;

    }

    public static void reso__Check_Network_Availablity(no_internet no_internet) {
        DevelSystemViewAd.reso__No_Internet = no_internet;
    }

    public static boolean reso__Check_Internet_Connection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void reso__Dev_Option_Intent(Context context) {
        try {
            context.startActivity((new Intent("android.settings.APPLICATION_DEVELOPMENT_SETTINGS")).setFlags(268435456));
        } catch (ActivityNotFoundException e) {
        }
    }

    public static void reso__Dev_Dialog(final Context context) {
        if (reso__Developer_Option_Dialog == null || !reso__Developer_Option_Dialog.isShowing()) {
            reso__Developer_Option_Dialog = new Dialog(context);
            reso__Developer_Option_Dialog.requestWindowFeature(1);
            reso__Developer_Option_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            reso__Developer_Option_Dialog.setContentView(R.layout.reso_devloper_dialog);
            int width = -1;
            reso__Developer_Option_Dialog.getWindow().setLayout(width, -2);
            reso__Developer_Option_Dialog.setCancelable(false);
            reso__Developer_Option_Dialog.show();
            TextView recheck = reso__Developer_Option_Dialog.findViewById(R.id.reso__Recheck);
            TextView turnoff = reso__Developer_Option_Dialog.findViewById(R.id.reso__TurnOff);

            recheck.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!DevelSystemViewAd.reso__Is_Debuggle(context)) {
                        DevelSystemViewAd.reso__No_Debug.no_debug();
                    }

                }
            });

            turnoff.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (DevelSystemViewAd.reso__Is_Debuggle(context)) {
                        DevelSystemViewAd.reso__Dev_Option_Intent(context);
                    } else {
                        DevelSystemViewAd.reso__No_Debug.no_debug();
                    }

                }
            });
        }
    }

    public static void reso__Debug_Check(no_debug no_debug) {
        DevelSystemViewAd.reso__No_Debug = no_debug;
    }


    public static void reso__No_Internet_Dialog(Context context) {
        reso__No_Internet_Dialog = new Dialog(context);
        reso__No_Internet_Dialog.requestWindowFeature(1);
        reso__No_Internet_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        reso__No_Internet_Dialog.setContentView(R.layout.reso_dialog_layout_internal_not);
        int width = -1;
        reso__No_Internet_Dialog.getWindow().setLayout(width, -2);
        reso__No_Internet_Dialog.setCancelable(false);
        reso__No_Internet_Dialog.show();
        TextView Retry = reso__No_Internet_Dialog.findViewById(R.id.reso__cancel);
        Retry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DevelSystemViewAd.reso__No_Internet.no_internet();
            }
        });
    }

    public interface no_internet {
        void no_internet();
    }

    public interface no_debug {
        void no_debug();
    }


}
