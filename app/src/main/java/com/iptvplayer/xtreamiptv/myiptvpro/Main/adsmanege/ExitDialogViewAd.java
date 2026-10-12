package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iptvplayer.xtreamiptv.myiptvpro.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ExitDialogViewAd {

    public static Dialog reso__Exit_Dialog;

    public static void reso__App_Exit_Dialog(Activity activity) {
        reso__Exit_Dialog = new Dialog(activity);
        reso__Exit_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater inflater = reso__Exit_Dialog.getLayoutInflater();
        View customView = inflater.inflate(R.layout.reso_exit_dialog, null);
        reso__Exit_Dialog.setContentView(customView);
        reso__Exit_Dialog.getWindow().setGravity(Gravity.BOTTOM);
        reso__Exit_Dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        reso__Exit_Dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        reso__Exit_Dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        reso__Exit_Dialog.setCancelable(true);
        reso__Exit_Dialog.setCanceledOnTouchOutside(true);
        TextView YesExit = reso__Exit_Dialog.findViewById(R.id.reso__txt_done);
        TextView Cancel = reso__Exit_Dialog.findViewById(R.id.reso__txt_no);


        YesExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finishAffinity();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reso__Exit_Dialog.dismiss();
            }
        });

        reso__Exit_Dialog.show();
    }

    public static void reso__Quiz_Timer_Dialog() {

        QuizAdManege.reso__Quiz_Click = false;

        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;

                QuizAdManege.reso__Quiz_Close_Text.setText(String.valueOf(sec));
                QuizAdManege.reso__Tv_Timer1.setText(String.valueOf(sec));
            }

            public void onFinish() {
                QuizAdManege.reso__Quiz_Click = true;
                QuizAdManege.reso__Quiz_Close_Text.setVisibility(View.GONE);
                QuizAdManege.reso__LL_View.setVisibility(View.VISIBLE);
                QuizAdManege.reso__Card_View.setVisibility(View.VISIBLE);
                QuizAdManege.reso__Tv_Timer1.setVisibility(View.GONE);
                QuizAdManege.reso__Done1.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}
