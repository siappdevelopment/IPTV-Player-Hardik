package com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.intro;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.WindowCompat;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents;
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSBannerSmall;
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSInterDisplay;
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSMainClass;
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSNativeDisplay;
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSUtilitis;
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.interfaces.IntroNavigation;
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils.LocaleAwareAppCompatActivity;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

import java.util.ArrayList;
import java.util.List;


public class Intro3Activity extends LocaleAwareAppCompatActivity {
    private AppCompatTextView tvDescription, btnNext;
    LinearLayout indicatorContainer;
    private final List<View> indicatorDots = new ArrayList<>();
    private int onboardingCount = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_3);
        indicatorContainer = findViewById(R.id.indicatorContainer);

        onboardingCount = ADSMainClass.getOnboardingCountShow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }

        initViews();
        setupBackPress();
        setupIndicators();
        updateIndicators(2);
    }

    private void makeFullScreenImmersive() {

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            @SuppressWarnings("deprecation")
            int flags =
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    private void setupBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void initViews() {
        makeFullScreenImmersive();
        tvDescription = findViewById(R.id.tvDescription);
        btnNext = findViewById(R.id.btnNext);

        bindTexts();
        clickEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeFullScreenImmersive();
    }

    private void bindTexts() {

        if (btnNext != null) {
            btnNext.setText(getString(R.string.continue_));
        }
    }

    private void setupIndicators() {
        indicatorContainer.removeAllViews();
        indicatorDots.clear();

        for (int i = 0; i < onboardingCount; i++) {
            View dot = new View(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.onboarding_dot_size),
                    getResources().getDimensionPixelSize(R.dimen.onboarding_dot_size)
            );

            if (i > 0) {
                params.setMarginStart(
                        getResources().getDimensionPixelSize(R.dimen.onboarding_dot_spacing)
                );
            }

            dot.setLayoutParams(params);
            indicatorContainer.addView(dot);
            indicatorDots.add(dot);
        }
    }

    private void updateIndicators(int position) {
        for (int index = 0; index < indicatorDots.size(); index++) {
            View dot = indicatorDots.get(index);
            boolean active = (index == position);

            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) dot.getLayoutParams();

            params.width = getResources().getDimensionPixelSize(
                    active
                            ? R.dimen.onboarding_dot_active_width
                            : R.dimen.onboarding_dot_size
            );

            params.height = getResources().getDimensionPixelSize(
                    R.dimen.onboarding_dot_size
            );

            if (index > 0) {
                params.setMarginStart(
                        getResources().getDimensionPixelSize(
                                R.dimen.onboarding_dot_spacing
                        )
                );
            }

            dot.setLayoutParams(params);

            dot.setBackground(
                    androidx.core.content.ContextCompat.getDrawable(
                            this,
                            active
                                    ? R.drawable.bg_onboarding_dot_active
                                    : R.drawable.bg_onboarding_dot_inactive
                    )
            );
        }
    }

    private void clickEvents() {
        if (ADSMainClass.getOnboardingScreenBottomAdShow()) {
            if (ADSMainClass.getOnboardingAdsType().equals("native")) {
                ADSNativeDisplay.loadAdmobNativeAdBig(
                        ADSMainClass.getStringValue(ADSMainClass.onboarding_native_id),
                        findViewById(R.id.flNativeSmallPlaceholder),
                        findViewById(R.id.shimmer_container_banner),
                        "small",
                        this
                );
            } else {
                ADSBannerSmall.loadAdMobBanner(
                        ADSMainClass.getStringValue(ADSMainClass.onboarding_banner_id),
                        findViewById(R.id.flBannerSmallPlaceholder),
                        findViewById(R.id.shimmer_container_banner),
                        this,
                        "small");
            }
        } else {
            findViewById(R.id.shimmer_container_banner).setVisibility(View.GONE);
            findViewById(R.id.flNativeSmallPlaceholder).setVisibility(View.GONE);
            findViewById(R.id.flBannerSmallPlaceholder).setVisibility(View.GONE);
        }

        if (ADSMainClass.getOnboardingInterAdsShow()
                && ADSMainClass.isInterPreLoad()
                && ADSUtilitis.IsNetworkConnected(this)) {
            String interId = ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME);
            android.util.Log.d("ADS_INTER", "Intro3 preload | adsId=[" + interId
                    + "] | loadType=[" + ADSMainClass.getInterAdsLoadType() + "]");
            ADSInterDisplay.preloadInterstitialAd(this, interId);
        }

        btnNext.setOnClickListener(view -> {
            AppAnalyticsEvents.track(
                    Intro3Activity.this,
                    AppAnalyticsEvents.ONBOARDING_NEXT_CLICK,
                    AppAnalyticsEvents.PARAM_STEP,
                    "3"
            );
            if (ADSMainClass.getOnboardingInterAdsShow()) {
                String interId = ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME);
                android.util.Log.d("ADS_INTER", "Intro3 Continue click | adsId=[" + interId
                        + "] | loadType=[" + ADSMainClass.getInterAdsLoadType()
                        + "] | Ad_Priority=[" + com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.AdPlacement.getAdPriority()
                        + "] | googleFailQuiz=" + com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.AdPlacement.getGoogleAdFailedShowQuiz());
                ADSInterDisplay.ADSInterstitialShowing(
                        Intro3Activity.this,
                        interId,
                        finished -> IntroNavigation.goToNextIntroButtonScreen(Intro3Activity.this, 3),
                        true
                );
            } else {
                IntroNavigation.goToNextIntroButtonScreen(Intro3Activity.this, 3);
            }
        });
    }
}
