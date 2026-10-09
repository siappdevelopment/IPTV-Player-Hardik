package com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ADSAppStarting {

    public static OnFinishAds onFinishAds;
    public static boolean LoadingCheck = true;
    public static AppOpenAd AppStartingAd;
    public static boolean ADSIsShowing = false;
    private static InterstitialAd AdmobmInterstitialAd;
    private static InterstitialAd splashPreloadedInterstitial;
    private static boolean splashInterLoadingCheck = true;

    public static boolean hasPreloadedSplashInterstitial() {
        return splashPreloadedInterstitial != null;
    }

    @Nullable
    public static InterstitialAd takePreloadedSplashInterstitial() {
        InterstitialAd preloaded = splashPreloadedInterstitial;
        splashPreloadedInterstitial = null;
        splashInterLoadingCheck = true;
        return preloaded;
    }

    public static void preloadSplashInterstitialAd(Activity context, String interId) {
        if (interId == null || interId.isEmpty() || ADSMainClass.getAds_Free()) {
            return;
        }
        if (!ADSMainClass.isSplashInterPreLoad()) {
            return;
        }
        if (splashPreloadedInterstitial != null || !splashInterLoadingCheck) {
            return;
        }

        splashInterLoadingCheck = false;
        AdRequest adRequest = new AdRequest.Builder().build();
        splashPreloadedInterstitial = null;
        InterstitialAd.load(context, interId.trim(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitialAd.setOnPaidEventListener(adValue ->
                                ADSUtilitis.logAdRevenue(context, adValue));
                        splashInterLoadingCheck = true;
                        splashPreloadedInterstitial = interstitialAd;
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.SPLASH_INTERSTITIAL_PRELOAD);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        splashInterLoadingCheck = true;
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_FAIL);
                    }
                });
    }

    public static void AdsSplashAppStartingDisplay(Activity context, OnFinishAds onFinishAd, boolean... doShowAds) {
        onFinishAds = onFinishAd;
        if (ADSMainClass.getAds_Free()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        ADSSplashAppStartingLoad(context);
    }

    public static void ADSSplashAppStartingLoad(Activity context) {
        if (AdPlacement.shouldUseQuizPriority()) {
            Runnable complete = () -> {
                ADSUtilitis.MassageBoxFullDismiss();
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
            };
            // Prefer quiz app-open style for splash priority; fall back to quiz interstitial.
            if (AdPlacement.showQuizAppOpenAd(context, complete)
                    || AdPlacement.showQuizInterstitialAd(context, complete)) {
                return;
            }
            complete.run();
            return;
        }

        String splashAdType = ADSMainClass.getSplashADType();

        if (isSplashInterType(splashAdType)) {
            if (ADSMainClass.isSplashInterLoad()) {
                ADSUtilitis.MassageBoxFull(context);
            }
            showSplashInterstitial(context);
            return;
        }

        if (ADSMainClass.isAppOpenLoad()) {
            ADSUtilitis.MassageBoxFull(context);
        }

        String app_open_id = ADSMainClass.getStringValue(ADSMainClass.APP_OPEN_ID);

        if (app_open_id == null || app_open_id.isEmpty()) {
            fallbackToInterOrFinish(context);
            return;
        }

        if (ADSAppBackground.isAppOpenAdReady()) {
            if (ADSMainClass.isAppOpenPreLoad()) {
                ADSAppBackground.showPreloadedAppOpenForSplash(context, onFinishAds);
            } else {
                showSplashAppOpen(context);
            }
        } else {
            AdmobAppOpenAd(context, app_open_id);
        }
    }

    private static void showSplashInterstitial(Activity context) {
        if (tryShowPreloadedSplashInterstitial(context)) {
            return;
        }
        AdmobInterstitialAd(context);
    }

    private static boolean tryShowPreloadedSplashInterstitial(Activity context) {
        if (!ADSMainClass.isSplashInterPreLoad() || !hasPreloadedSplashInterstitial()) {
            return false;
        }
        InterstitialAd preloaded = takePreloadedSplashInterstitial();
        if (preloaded == null) {
            return false;
        }
        showPreloadedSplashInterstitial(context, preloaded);
        return true;
    }

    private static void showPreloadedSplashInterstitial(Activity context, InterstitialAd interstitialAd) {
        AdmobmInterstitialAd = interstitialAd;
        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_IMPRESSION);
        AdmobmInterstitialAd.show(context);
        onAdsLoadAdListener(context);
    }

    private static boolean isSplashInterType(String splashAdType) {
        return splashAdType != null
                && (splashAdType.equalsIgnoreCase("Inter")
                || splashAdType.equalsIgnoreCase("Interstitial"));
    }

    private static boolean isSplashAppOpenType(String splashAdType) {
        return splashAdType != null && splashAdType.equalsIgnoreCase("Appopen");
    }

    private static void AdmobAppOpenAd(Activity context, String appOpenId) {
        AdRequest adRequest = new AdRequest.Builder().build();

        AppOpenAd.load(context, appOpenId.trim(), adRequest,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        ad.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(AdValue adValue) {
                                ADSUtilitis.logAdRevenue(context, adValue);
                            }
                        });

                        AppStartingAd = ad;
                        ADSIsShowing = false;
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.APP_OPEN_AD_LOAD);
                        showSplashAppOpen(context);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        AppStartingAd = null;
                        ADSIsShowing = false;
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.APP_OPEN_AD_FAIL);
                        Runnable complete = () -> {
                            ADSUtilitis.MassageBoxFullDismiss();
                            if (onFinishAds != null) {
                                onFinishAds.onFinishAds(true);
                            }
                        };
                        if (AdPlacement.tryShowQuizAppOpenOnGoogleFail(context, complete)) {
                            return;
                        }
                        fallbackToInterOrFinish(context);
                    }
                });
    }

    private static void showSplashAppOpen(Activity context) {
        if (ADSMainClass.isAppOpenLoad()) {
            ADSUtilitis.MassageBoxFullDismiss();
        }

        if (AppStartingAd == null || ADSIsShowing) {
            fallbackToInterOrFinish(context);
            return;
        }

        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.APP_OPEN_AD_IMPRESSION);

        AppStartingAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                ADSIsShowing = false;
                AppStartingAd = null;
                ADSAppBackground.preloadAppOpenAd(context.getApplicationContext());
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                ADSIsShowing = false;
                AppStartingAd = null;
                ADSUtilitis.trackScreen(context, AppAnalyticsEvents.APP_OPEN_AD_FAIL);
                fallbackToInterOrFinish(context);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                ADSIsShowing = true;
            }
        });

        AppStartingAd.show(context);
    }

    private static void fallbackToInterOrFinish(Activity context) {
        if (isSplashAppOpenType(ADSMainClass.getSplashADType())
                || isSplashInterType(ADSMainClass.getSplashADType())) {
            if (tryShowPreloadedSplashInterstitial(context)) {
                return;
            }
            AdmobInterstitialAd(context);
        } else {
            if (ADSMainClass.isAppOpenLoad()) {
                ADSUtilitis.MassageBoxFullDismiss();
            }
            if (onFinishAds != null) {
                onFinishAds.onFinishAds(true);
            }
        }
    }

    private static void AdmobInterstitialAd(Activity context) {
        if (tryShowPreloadedSplashInterstitial(context)) {
            return;
        }

        if (ADSMainClass.isSplashInterLoad()) {
            ADSUtilitis.MassageBoxFull(context);
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        String interstitialAdIds = ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME);
        AdmobmInterstitialAd = null;

        if (interstitialAdIds == null || interstitialAdIds.isEmpty()) {
            if (ADSMainClass.isSplashInterLoad()) {
                ADSUtilitis.MassageBoxFullDismiss();
            }
            if (onFinishAds != null) {
                onFinishAds.onFinishAds(true);
            }
            return;
        }

        InterstitialAd.load(context, interstitialAdIds.trim(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(AdValue adValue) {
                                ADSUtilitis.logAdRevenue(context, adValue);
                            }
                        });

                        LoadingCheck = true;
                        AdmobmInterstitialAd = interstitialAd;
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_LOAD);

                        if (ADSMainClass.isSplashInterLoad()) {
                            ADSUtilitis.MassageBoxFullDismiss();
                        }
                        AdmobmInterstitialAd.show(context);
                        onAdsLoadAdListener(context);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        if (ADSMainClass.isSplashInterLoad()) {
                            ADSUtilitis.MassageBoxFullDismiss();
                        }
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_FAIL);
                        Runnable complete = () -> {
                            if (onFinishAds != null) {
                                onFinishAds.onFinishAds(true);
                            }
                        };
                        if (AdPlacement.tryShowQuizInterstitialOnGoogleFail(context, complete)) {
                            return;
                        }
                        complete.run();
                    }
                });
    }

    public static void onAdsLoadAdListener(Context context) {
        AdmobmInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_CLICK);
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                if (context instanceof Activity) {
                    preloadSplashInterIfNeeded((Activity) context);
                }
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // PreLoad only: failed show falls back to Quiz. Load keeps the existing finish path.
                if (context instanceof Activity
                        && ADSMainClass.isSplashInterPreLoad()
                        && !ADSMainClass.isSplashInterLoad()) {
                    Activity activity = (Activity) context;
                    Runnable complete = () -> {
                        if (onFinishAds != null) {
                            onFinishAds.onFinishAds(true);
                        }
                    };
                    if (AdPlacement.tryShowQuizInterstitialOnGoogleFail(activity, complete)) {
                        return;
                    }
                    complete.run();
                    return;
                }
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
            }

            @Override
            public void onAdImpression() {
                ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_IMPRESSION);
            }

            @Override
            public void onAdShowedFullScreenContent() {
            }
        });
    }

    private static void preloadSplashInterIfNeeded(Activity context) {
        if (!ADSMainClass.isSplashInterPreLoad()) {
            return;
        }
        String interId = ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME);
        if (interId == null || interId.isEmpty()) {
            return;
        }
        ADSAppStarting.preloadSplashInterstitialAd(context, interId);
    }

    public interface OnFinishAds {
        void onFinishAds(boolean b);
    }

}
