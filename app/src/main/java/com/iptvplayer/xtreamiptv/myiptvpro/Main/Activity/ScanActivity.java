package com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege;
import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.google.firebase.analytics.FirebaseAnalytics;

public class ScanActivity extends BaseActivity {

    public final Handler handler = new Handler();
    public ProgressBar progressBar;
    public TextView scanStatusTextView;
    public TextView appNameTextView;
    public TextView versionTextView;
    public ImageView appIconImageView;
    public int anInt = 0;
    public String package_name;

    public Button openAppButton;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private int dotCount = 0;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            dotCount++;
            if (dotCount > 3) {
                dotCount = 1;
            }

            StringBuilder dots = new StringBuilder();
            for (int i = 0; i < dotCount; i++) {
                dots.append(".");
            }

            scanStatusTextView.setText("Scanning" + dots.toString());

            handler.postDelayed(this, 1000);
        }
    };
    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {

            int i4 = anInt + 10;
            anInt = i4;

            progressBar.setProgress(i4);
            scanStatusTextView.setText("Scanning... " + anInt + "%");

            if (anInt < 100) {
                uiHandler.postDelayed(this, 250L);
                return;
            }

            scanStatusTextView.setText("Scan Complete! Good App");
            progressBar.setVisibility(View.GONE);
            openAppButton.setVisibility(View.VISIBLE);

            openAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (package_name != null) {

                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(package_name);

                        if (launchIntent != null) {

                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(launchIntent);

                        } else {

                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                        }

                    } else {

                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }

                    finish();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle newBundle = new Bundle();
        newBundle.putBoolean("appOtherInstall", true);
        firebaseAnalytics.logEvent("appOtherInstall", newBundle);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.scanStatusTextView = (TextView) findViewById(R.id.statusText);
        this.appNameTextView = (TextView) findViewById(R.id.appName);
        this.versionTextView = (TextView) findViewById(R.id.versioncode);
        this.appIconImageView = (ImageView) findViewById(R.id.appIcon);
        this.openAppButton = (Button) findViewById(R.id.openbtn);
        uiHandler.post(runnable);

        String stringExtra = getIntent().getStringExtra("package_name");
        this.package_name = stringExtra;
        if (stringExtra != null) {
            try {
                PackageManager packageManager = getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(stringExtra, 0);
                String charSequence = packageManager.getApplicationLabel(applicationInfo).toString();
                Drawable applicationIcon = packageManager.getApplicationIcon(applicationInfo);
                PackageInfo packageInfo = packageManager.getPackageInfo(stringExtra, 0);
                TextView textView = this.versionTextView;
                textView.setText("Version: " + packageInfo.versionName);
                this.appNameTextView.setText(charSequence);
                this.appIconImageView.setImageDrawable(applicationIcon);
            } catch (PackageManager.NameNotFoundException unused) {
                this.appNameTextView.setText("Unknown App");
            }
        }
        this.uiHandler.postDelayed(scanRunnable, 250L);
    }

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