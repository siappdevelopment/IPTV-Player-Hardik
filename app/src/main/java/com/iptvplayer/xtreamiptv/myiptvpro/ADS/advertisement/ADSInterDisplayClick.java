package com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.VpnHelper;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ADSInterDisplayClick {

    public static int ADSClick = 0;
    public static int BackADClick = 0;
    public static int BottomNavADClick = 0;
    public static int LauncherSwipeADClick = 0;
    public static Boolean ADSDisplayCheck = false;
    public static int ArrayIndexInterstitialId = 0;
    /** Blocks a second Inter while one is already showing. */
    private static volatile boolean interstitialShowInProgress = false;

    public static OnFinishAds onFinishAds;
    public static com.facebook.ads.InterstitialAd ADSFBInterstitial;

    private static InterstitialAd ADSAdmobmInterstitial;
    public static boolean LoadingCheck = true;
    /** Set when a user click is waiting for an interstitial load to finish (Load + PreLoad). */
    private static boolean pendingClickInterstitialShow = false;
    private static Activity pendingShowActivity;

    /**
     * Quiz after Google fail when Google_Ad_Failed_Show_Quiz=true.
     * Used for PreLoad when the preloaded interstitial fails to load or show.
     */
    private static void finishWithOptionalQuizFallback(Activity context, String reason) {
        ADSUtilitis.MassageBoxFullDismiss();
        if (context != null && AdPlacement.getGoogleAdFailedShowQuiz()) {
            if (AdPlacement.showQuizInterstitialAd(context, () -> {
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
            })) {
                return;
            }
        }
        if (onFinishAds != null) {
            onFinishAds.onFinishAds(true);
        }
    }

    private static void showLoadedInterstitial(Activity context) {
        if (ADSAdmobmInterstitial == null) {
            if (ADSMainClass.isInterPreLoad() && !ADSMainClass.isInterLoad()) {
                finishWithOptionalQuizFallback(context, "ready_ad_null");
            } else if (onFinishAds != null) {
                onFinishAds.onFinishAds(true);
            }
            return;
        }
        interstitialShowInProgress = true;
        pendingClickInterstitialShow = false;
        pendingShowActivity = null;
        ADSUtilitis.MassageBoxFullDismiss();
        AdsDisplayCheck(false);
        InterstitialAd adToShow = ADSAdmobmInterstitial;
        ADSAdmobmInterstitial = null; // consume — Inter can show only once
        onAdsLoadAdListener(context, adToShow);
        adToShow.show(context);
    }

    private static void requestInterstitialShow(Activity context, String adsId) {
        pendingClickInterstitialShow = true;
        pendingShowActivity = context;

        if (ADSAdmobmInterstitial != null) {
            showLoadedInterstitial(context);
            return;
        }

        // PreLoad: never loading dialog. Still loading → wait for that result.
        // Not ready (preload failed / unavailable) → Quiz fallback, keep preloading.
        if (ADSMainClass.isInterPreLoad() && !ADSMainClass.isInterLoad()) {
            if (!LoadingCheck) {
                return;
            }
            pendingClickInterstitialShow = false;
            pendingShowActivity = null;
            if (adsId != null && !adsId.isEmpty()) {
                preloadInterstitialAd(context, adsId);
            }
            finishWithOptionalQuizFallback(context, "preload_unavailable");
            return;
        }

        // Load type only
        ADSUtilitis.MassageBoxFull(context);
        if (!LoadingCheck) {
            return;
        }
        LoadingCheck = false;
        AdmobInterstitialAd(context, adsId);
    }

    private static void tryShowPendingInterstitial(Activity context) {
        if (!pendingClickInterstitialShow || ADSAdmobmInterstitial == null) {
            return;
        }
        Activity showActivity = pendingShowActivity != null ? pendingShowActivity : context;
        if (showActivity.isFinishing()) {
            pendingClickInterstitialShow = false;
            pendingShowActivity = null;
            ADSUtilitis.MassageBoxFullDismiss();
            if (onFinishAds != null) {
                onFinishAds.onFinishAds(true);
            }
            return;
        }
        showLoadedInterstitial(showActivity);
    }

    private static void scheduleInterstitialReload(Activity context) {
        if (ADSMainClass.isInterPreLoad()) {
            String interId = ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME);
            if (interId != null && !interId.isEmpty()) {
                preloadInterstitialAd(context, interId);
            }
        }
    }

    private static void clearLoadedInterstitial() {
        ADSAdmobmInterstitial = null;
        LoadingCheck = true;
    }


    public static void ADSInterstitialShowing(Activity context, String adsId, OnFinishAds onFinishAd, boolean screenInterShow) {
        onFinishAds = onFinishAd;
        if (ADSMainClass.getAds_Free()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        boolean isQuizInter = AdPlacement.shouldUseQuizPriority();

        if (!isQuizInter && (!ADSMainClass.getInterAdsShow() || !screenInterShow)) {
            onFinishAds.onFinishAds(true);
            return;
        }

        if (ADSInterDisplayClick.ADSClick == ADSMainClass.getAdsClick()) {
            ADSInterDisplayClick.ADSClick = 0;
            ADSInterDisplayClick.ArrayIndexInterstitialId = 0;
            if (isQuizInter) {
                if (AdPlacement.showQuizInterstitialOrBrowserAd(context, () -> {
                    if (onFinishAd != null) {
                        onFinishAd.onFinishAds(true);
                    }
                })) {
                    return;
                }
            }
            if (!ADSMainClass.getInterAdsShow() || !screenInterShow) {
                onFinishAds.onFinishAds(true);
                return;
            }
            ADSMedDisplay(context, adsId);
        } else {
            onFinishAds.onFinishAds(true);
            ADSInterDisplayClick.ADSClick++;
        }
    }


    public static void ADSBackDisplayInterstitial(Activity context, String adsId, OnFinishAds onFinishAd, boolean screenInterShow) {
        onFinishAds = onFinishAd;
        if (ADSMainClass.getAds_Free()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        boolean isQuizInter = AdPlacement.shouldUseQuizPriority();

        if (isQuizInter) {
            if (!screenInterShow) {
                onFinishAds.onFinishAds(true);
                return;
            }
            if (ADSInterDisplayClick.BackADClick == ADSMainClass.getAdsBackClick()) {
                ADSInterDisplayClick.BackADClick = 0;
                ADSInterDisplayClick.ArrayIndexInterstitialId = 0;
                if (AdPlacement.showQuizInterstitialOrBrowserAd(context, () -> {
                    if (onFinishAd != null) {
                        onFinishAd.onFinishAds(true);
                    }
                })) {
                    return;
                }
                onFinishAds.onFinishAds(true);
            } else {
                onFinishAds.onFinishAds(true);
                ADSInterDisplayClick.BackADClick++;
            }
            return;
        }

        if (!ADSMainClass.getInterAdsOnBackShow() || !screenInterShow) {
            onFinishAds.onFinishAds(true);
            return;
        }

        if (ADSInterDisplayClick.BackADClick == ADSMainClass.getAdsBackClick()) {
            ADSInterDisplayClick.BackADClick = 0;
            ADSInterDisplayClick.ArrayIndexInterstitialId = 0;
            if (!ADSMainClass.getInterAdsOnBackShow() || !screenInterShow) {
                onFinishAds.onFinishAds(true);
                return;
            }
            ADSMedDisplay(context, adsId);
        } else {
            onFinishAds.onFinishAds(true);
            ADSInterDisplayClick.BackADClick++;
        }
    }

    public static void showClickInterstitial(Activity context, OnFinishAds onFinishAd, boolean screenInterShow) {
        ADSInterstitialShowing(
                context,
                ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME),
                onFinishAd,
                screenInterShow
        );
    }

    public static void showBackInterstitial(Activity context, OnFinishAds onFinishAd, boolean screenBackAdsShow) {
        ADSBackDisplayInterstitial(
                context,
                ADSMainClass.getStringValue(ADSMainClass.INTER_SECOND_TIME),
                onFinishAd,
                screenBackAdsShow
        );
    }

    public static void ADSBottomNavDisplayInterstitial(Activity context, String adsId, OnFinishAds onFinishAd) {
        onFinishAds = onFinishAd;
        if (ADSMainClass.getAds_Free()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        // home_bottom_nav_inter_show gates both Google and Quiz (same as remote config on/off)
        if (!ADSMainClass.getHomeBottomNavInterShow()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        boolean isQuizInter = AdPlacement.shouldUseQuizPriority();

        if (!isQuizInter && !ADSMainClass.getInterAdsShow()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        // home_bottom_count applies to Quiz too — not every bottom nav click
        if (BottomNavADClick == ADSMainClass.getHomeBottomCount()) {
            BottomNavADClick = 0;
            ADSAppManage.isActivityChecked = true;
            if (isQuizInter) {
                if (AdPlacement.showQuizInterstitialAd(context, () -> {
                    if (onFinishAd != null) {
                        onFinishAd.onFinishAds(true);
                    }
                })) {
                    return;
                }
            }
            if (!ADSMainClass.getInterAdsShow()) {
                onFinishAds.onFinishAds(true);
                return;
            }
            ADSMedDisplay(context, adsId);
        } else {
            onFinishAds.onFinishAds(true);
            BottomNavADClick++;
        }
    }

    public static void ADSLauncherSwipeDisplayInterstitial(Activity context, String adsId, OnFinishAds onFinishAd) {
        onFinishAds = onFinishAd;
        if (ADSMainClass.getAds_Free()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        // Do not request / show interstitial while VPN dialog is up
        if (VpnHelper.shouldBlockAds(context)) {
            onFinishAds.onFinishAds(true);
            return;
        }

        // swipe_inter_show gates both Google and Quiz (same as remote config on/off)
        if (!ADSMainClass.getLauncherHomeSwipeInterShow()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        boolean isQuizInter = AdPlacement.shouldUseQuizPriority();

        if (!isQuizInter && !ADSMainClass.getInterAdsShow()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        // swipe_inter_count applies to Quiz too â€” not every swipe
        if (LauncherSwipeADClick == ADSMainClass.getLauncherHomeSwipeInterCount()) {
            LauncherSwipeADClick = 0;
            ADSAppManage.isActivityChecked = true;
            if (isQuizInter) {
                if (AdPlacement.showQuizInterstitialAd(context, () -> {
                    if (onFinishAd != null) {
                        onFinishAd.onFinishAds(true);
                    }
                })) {
                    return;
                }
            }
            if (!ADSMainClass.getInterAdsShow()) {
                onFinishAds.onFinishAds(true);
                return;
            }
            ADSMedDisplay(context, adsId);
        } else {
            onFinishAds.onFinishAds(true);
            LauncherSwipeADClick++;
        }
    }

    public static void ADSMedDisplay(Activity context, String adsId) {
        if (interstitialShowInProgress || ADSAppStarting.ADSIsShowing) {
            if (onFinishAds != null) {
                onFinishAds.onFinishAds(true);
            }
            return;
        }

        if (AdPlacement.shouldUseQuizPriority()) {
            if (AdPlacement.showQuizInterstitialAd(context, () -> {
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
            })) {
                return;
            }
        }

        if (ADSMainClass.isInterLoad()) {
            if (ADSAdmobmInterstitial != null) {
                clearLoadedInterstitial();
            }
            requestInterstitialShow(context, adsId);
            return;
        }

        // PreLoad: ready → direct show (no dialog).
        // Still loading → wait; success shows Google, fail shows Quiz.
        // Not ready (preload failed / unavailable) → Quiz fallback, keep preloading.
        if (ADSAdmobmInterstitial != null) {
            showLoadedInterstitial(context);
            return;
        }

        if (!LoadingCheck) {
            pendingClickInterstitialShow = true;
            pendingShowActivity = context;
            return;
        }

        if (adsId != null && !adsId.isEmpty()) {
            preloadInterstitialAd(context, adsId);
        }
        finishWithOptionalQuizFallback(context, "preload_unavailable");
    }

    public static void AdsDisplayCheck(Boolean aBoolean) {

        if (aBoolean) {
            ADSAppStarting.ADSIsShowing = false;
            ADSAppManage.FastStart = false;
            ADSDisplayCheck = true;
        } else {
            ADSAppStarting.ADSIsShowing = true;
            ADSDisplayCheck = false;
            ADSAppManage.FastStart = true;
        }

    }

    public static void preloadInterstitialAd(Activity context, String adsId) {
        if (!ADSMainClass.isInterPreLoad() || ADSMainClass.isInterLoad()) {
            return;
        }
        if (AdPlacement.shouldUseQuizPriority()) {
            return;
        }
        if (adsId == null || adsId.isEmpty() || ADSMainClass.getAds_Free()) {
            return;
        }
        // Keep ready PreLoad ad — do not wipe and reload (that forces Load-like dialog).
        if (ADSAdmobmInterstitial != null) {
            return;
        }
        if (!LoadingCheck) {
            return;
        }

        LoadingCheck = false;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, adsId.trim(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitialAd.setOnPaidEventListener(adValue ->
                                ADSUtilitis.logAdRevenue(context, adValue));
                        LoadingCheck = true;
                        ADSAdmobmInterstitial = interstitialAd;
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_PRELOAD);
                        tryShowPendingInterstitial(context);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        LoadingCheck = true;
                        if (pendingClickInterstitialShow) {
                            pendingClickInterstitialShow = false;
                            pendingShowActivity = null;
                            ADSUtilitis.MassageBoxFullDismiss();
                            finishWithOptionalQuizFallback(context, "preload_fail_" + loadAdError.getCode());
                        }
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_FAIL);
                    }
                });
    }

    public static boolean hasReadyInterstitial() {
        return ADSAdmobmInterstitial != null;
    }

    public static void AdmobInterstitialAd(Activity context, String AdsID) {

        AdRequest adRequest = new AdRequest.Builder().build();
        ADSAdmobmInterstitial = null;
        InterstitialAd.load(context, AdsID.trim(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_LOAD);
                        interstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(AdValue adValue) {
                                ADSUtilitis.logAdRevenue(context, adValue);
                            }
                        });

                        LoadingCheck = true;

                        ADSAdmobmInterstitial = interstitialAd;
                        tryShowPendingInterstitial(context);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_FAIL);
                        ADSUtilitis.MassageBoxFullDismiss();
                        LoadingCheck = true;
                        pendingClickInterstitialShow = false;
                        pendingShowActivity = null;
                        if (AdPlacement.getGoogleAdFailedShowQuiz()) {
                            if (AdPlacement.showQuizInterstitialAd(context, () -> {
                                if (onFinishAds != null) {
                                    onFinishAds.onFinishAds(true);
                                }
                            })) {
                                return;
                            }
                        }
                        if (onFinishAds != null) {
                            onFinishAds.onFinishAds(true);
                        }
                    }
                });
    }

    public static void onAdsLoadAdListener(Context context, InterstitialAd interstitialAd) {
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_CLICK);
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                interstitialShowInProgress = false;
                AdsDisplayCheck(true);
                clearLoadedInterstitial();
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
                if (context instanceof Activity) {
                    scheduleInterstitialReload((Activity) context);
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                interstitialShowInProgress = false;
                AdsDisplayCheck(true);
                clearLoadedInterstitial();
                String msg = adError.getMessage() != null ? adError.getMessage() : "";
                boolean alreadyShown = msg.toLowerCase().contains("already been shown");
                if (alreadyShown && context instanceof Activity) {
                    String retryId = ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME);
                    if (retryId != null && !retryId.isEmpty()) {
                        requestInterstitialShow((Activity) context, retryId);
                        return;
                    }
                }
                if (context instanceof Activity && AdPlacement.getGoogleAdFailedShowQuiz()) {
                    if (AdPlacement.showQuizInterstitialAd((Activity) context, () -> {
                        if (onFinishAds != null) {
                            onFinishAds.onFinishAds(true);
                        }
                    })) {
                        if (context instanceof Activity) {
                            scheduleInterstitialReload((Activity) context);
                        }
                        return;
                    }
                }
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
                if (context instanceof Activity) {
                    scheduleInterstitialReload((Activity) context);
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

    public interface OnFinishAds {
        void onFinishAds(boolean b);

    }
}