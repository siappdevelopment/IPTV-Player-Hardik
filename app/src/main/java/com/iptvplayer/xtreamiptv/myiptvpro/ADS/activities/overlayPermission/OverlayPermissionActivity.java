package com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.overlayPermission;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.transition.Explode;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents;
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.PermissionFirebaseEvents;
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.permissions.PermissionActivity;
import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.iptvplayer.xtreamiptv.myiptvpro.utils.LocaleAwareAppCompatActivity;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.Nullable;

public class OverlayPermissionActivity extends LocaleAwareAppCompatActivity {

    public static final String EXTRA_HOST_TASK_ID = "extra_host_task_id";
    public static final String EXTRA_RETURN_TO_HOST = "extra_return_to_host";
    /** Same guide card shown over the system "Default home app" screen; no overlay-permission handling. */
    public static final String EXTRA_DEFAULT_HOME_GUIDE = "extra_default_home_guide";
    private static final long CHECK_INTERVAL_MS = 500L;

    @Override
    protected boolean shouldAutoRefreshLocale() {
        return false;
    }

    private Window window;
    private WindowManager.LayoutParams layoutParams;
    private View overlay_root;
    private final Handler overlayHandler = new Handler(Looper.getMainLooper());
    private static final long DEFAULT_HOME_GUIDE_AUTO_HIDE_MS = 2000L;
    private android.animation.ValueAnimator handClickAnimator;
    private final Runnable defaultHomeGuideAutoHide = () -> {
        if (!isFinishing() && !isDestroyed()) {
            finish();
        }
    };
    private boolean grantHandled = false;
    private final Runnable overlayChecker = new Runnable() {
        @Override
        public void run() {
            if (grantHandled) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(OverlayPermissionActivity.this)) {
                returnToApp();
                return;
            }
            overlayHandler.postDelayed(this, CHECK_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final boolean defaultHomeGuide = getIntent().getBooleanExtra(EXTRA_DEFAULT_HOME_GUIDE, false);
        setContentView(defaultHomeGuide ? R.layout.activity_overlay_permission_default_home
                : R.layout.activity_overlay_permission);
        if (defaultHomeGuide) {
            ((android.widget.ImageView) findViewById(R.id.ivGuideIcon))
                    .setImageDrawable(getApplicationInfo().loadIcon(getPackageManager()));
            ((android.widget.TextView) findViewById(R.id.tvGuideName))
                    .setText(getApplicationInfo().loadLabel(getPackageManager()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        overlay_root = findViewById(R.id.overlay_root);
        window = getWindow();
        window.addFlags(7078560);
        window.setSoftInputMode(2);
        setFinishOnTouchOutside(true);
        int animEnter = R.anim.optin_slide_up;
        overridePendingTransition(animEnter, 0);
        window.setEnterTransition(new Explode());
        window.setExitTransition(new Explode());
        overridePendingTransition(animEnter, 0);

        layoutParams = window.getAttributes();
        layoutParams.gravity = defaultHomeGuide ? Gravity.CENTER : Gravity.BOTTOM;
        if (defaultHomeGuide) {
            overlayHandler.postDelayed(defaultHomeGuideAutoHide, DEFAULT_HOME_GUIDE_AUTO_HIDE_MS);
            startHandClickAnimation();
        }
        layoutParams.x = 1;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.dimAmount = 0.2f;
        window.setAttributes(layoutParams);

        overlay_root.setOnClickListener(v -> {
            AppAnalyticsEvents.track(OverlayPermissionActivity.this, AppAnalyticsEvents.OVERLAY_PERMISSION_DENY);
            OverlayPermissionActivity.this.finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra(EXTRA_DEFAULT_HOME_GUIDE, false)) {
            // Default Home guide: nothing to poll; just go away once this app is the default Home.
            if (com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common.Utils.isDefaultHomeApp(this)) {
                finish();
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            returnToApp();
        } else {
            startOverlayChecker();
        }
    }

    @Override
    protected void onPause() {
        stopOverlayChecker();
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        finish();
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 132) {
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            if (Settings.canDrawOverlays(this)) {
                bundle.putString("PermissionStatus", "Allowed");
            } else {
                bundle.putString("PermissionStatus", "Denied");
            }
            firebaseAnalytics.logEvent("OverlayPermissionReport", bundle);
        }
    }

    private void startOverlayChecker() {
        stopOverlayChecker();
        overlayHandler.post(overlayChecker);
    }

    private void stopOverlayChecker() {
        overlayHandler.removeCallbacks(overlayChecker);
    }

    private void returnToApp() {
        if (grantHandled) {
            return;
        }
        grantHandled = true;
        stopOverlayChecker();
        PermissionFirebaseEvents.trackOverlayGranted(this);

        boolean returnToHost = getIntent().getBooleanExtra(EXTRA_RETURN_TO_HOST, false);
        if (!returnToHost) {
            Intent intent = new Intent(this, PermissionActivity.class);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
                            | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            | Intent.FLAG_ACTIVITY_NEW_TASK
            );
            intent.putExtra(PermissionActivity.EXTRA_OVERLAY_GRANTED, true);
            startActivity(intent);
        }

        int hostTaskId = getIntent().getIntExtra(EXTRA_HOST_TASK_ID, -1);
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (activityManager != null && hostTaskId != -1) {
                activityManager.moveTaskToFront(hostTaskId, ActivityManager.MOVE_TASK_WITH_HOME);
            }
        } catch (Exception ignored) {
        }

        finish();
    }

    /** Looping "tap" on the radio: hand rises 30dp while the ripple and the radio dot grow in. */
    private void startHandClickAnimation() {
        final View hand = findViewById(R.id.homeGuideHand);
        final View ripple = findViewById(R.id.homeGuideRipple);
        final View dot = findViewById(R.id.homeGuideRadioDot);
        final float rise = 30f * getResources().getDisplayMetrics().density;
        handClickAnimator = android.animation.ValueAnimator.ofFloat(0f, 1f);
        handClickAnimator.setDuration(900L);
        handClickAnimator.setRepeatCount(android.animation.ValueAnimator.INFINITE);
        handClickAnimator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        handClickAnimator.addUpdateListener(animation -> {
            float p = (float) animation.getAnimatedValue();
            hand.setTranslationY(rise * (1f - p));
            ripple.setAlpha(p);
            ripple.setScaleX(0.55f + 0.45f * p);
            ripple.setScaleY(0.55f + 0.45f * p);
            dot.setAlpha(p);
            dot.setScaleX(0.3f + 0.7f * p);
            dot.setScaleY(0.3f + 0.7f * p);
        });
        handClickAnimator.start();
    }

    @Override
    protected void onDestroy() {
        if (handClickAnimator != null) {
            handClickAnimator.cancel();
        }
        overlayHandler.removeCallbacks(defaultHomeGuideAutoHide);
        stopOverlayChecker();
        super.onDestroy();
        overridePendingTransition(0, R.anim.optin_slide_down);
    }
}
