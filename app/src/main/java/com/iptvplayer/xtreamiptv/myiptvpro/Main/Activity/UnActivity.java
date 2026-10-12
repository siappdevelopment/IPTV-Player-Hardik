package com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.BaseActivity;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

public class UnActivity extends BaseActivity {
    
    public ProgressBar progressBar;
    
    public TextView statusTextView;

    public int anInt = 0;

    public final Handler handler = new Handler();

    public Button openAppButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
                        Bundle newBundle = new Bundle();
                        newBundle.putBoolean("appOtherUninstall", true);
                        firebaseAnalytics.logEvent("appOtherUninstall", newBundle);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.statusTextView = (TextView) findViewById(R.id.statusText);
        this.openAppButton = (Button) findViewById(R.id.openbtn);
        this.handler.postDelayed(uninstallRunnable, 250L);
    }
    private Runnable uninstallRunnable = new Runnable() {
        @Override
        public void run() {

            anInt += 10;

            progressBar.setProgress(anInt);
            statusTextView.setText("Uninstalling... " + anInt + "%");

            if (anInt < 100) {
                handler.postDelayed(this, 250L);
                return;
            }

            statusTextView.setText("Uninstall Complete!");
            progressBar.setVisibility(View.GONE);
            openAppButton.setVisibility(View.VISIBLE);

            openAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish(); 
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppControllerManege.removeListener();
    }

}