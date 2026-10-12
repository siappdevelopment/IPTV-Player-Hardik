package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.RelativeLayout;

import com.iptvplayer.xtreamiptv.myiptvpro.R;


public class LoadingDialogView {

    private static Dialog reso__Loading_Dialog;

    private static final Handler handler =
            new Handler(Looper.getMainLooper());

    private static final Runnable autoDismissRunnable =
            new Runnable() {
                @Override
                public void run() {
                    reso__Hide_Ads_Loading_Dialog();
                }
            };

    public static void reso__Show_Ads_Loading_Dialog(Activity activity) {

        try {

            // Activity valid check
            if (activity == null ||
                    activity.isFinishing() ||
                    activity.isDestroyed()) {
                return;
            }

            // Already showing
            if (reso__Loading_Dialog != null &&
                    reso__Loading_Dialog.isShowing()) {
                return;
            }

            reso__Loading_Dialog = new Dialog(activity);

            reso__Loading_Dialog.requestWindowFeature(
                    Window.FEATURE_NO_TITLE
            );

            if (reso__Loading_Dialog.getWindow() != null) {

                reso__Loading_Dialog.getWindow()
                        .setBackgroundDrawable(
                                new ColorDrawable(Color.TRANSPARENT)
                        );

                reso__Loading_Dialog.getWindow().setLayout(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );
            }

            reso__Loading_Dialog.setContentView(
                    R.layout.reso_loading_dialog
            );

            reso__Loading_Dialog.setCancelable(false);

            if (!activity.isFinishing() &&
                    !activity.isDestroyed()) {

                reso__Loading_Dialog.show();
            }

            handler.removeCallbacks(autoDismissRunnable);

            handler.postDelayed(autoDismissRunnable, 15000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reso__Hide_Ads_Loading_Dialog() {

        try {

            handler.removeCallbacks(autoDismissRunnable);

            if (reso__Loading_Dialog != null) {

                if (reso__Loading_Dialog.isShowing()) {
                    reso__Loading_Dialog.dismiss();
                }

                reso__Loading_Dialog = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}