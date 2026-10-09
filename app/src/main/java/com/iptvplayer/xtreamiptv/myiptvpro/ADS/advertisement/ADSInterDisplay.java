package com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents;


import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ADSInterDisplay {

    public static final String TAG = "ADS_INTER";

    public static Boolean ADSDisplayCheck = false;
    public static OnFinishAds onFinishAds;
    private static InterstitialAd ADSAdmobmInterstitial;
    public static boolean LoadingCheck = true;
    /** Set when interstitial show was requested but ad is still loading (Load + PreLoad). */
    private static boolean pendingInterstitialShow = false;
    private static Activity pendingShowActivity;
    private static String lastRequestedAdId = "";

    private static void logState(String step, String adsId) {
    }

    private static void clearLoadedInterstitial() {
        ADSAdmobmInterstitial = null;
        LoadingCheck = true;
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
        pendingInterstitialShow = false;
        pendingShowActivity = null;
        ADSUtilitis.MassageBoxFullDismiss();
        AdsDisplayCheck(false);
        InterstitialAd adToShow = ADSAdmobmInterstitial;
        // InterstitialAd can be shown only once — clear before show so next screen never reuses it.
        ADSAdmobmInterstitial = null;
        onAdsLoadAdListener(context, adToShow);
        adToShow.show(context);
    }

    private static void requestInterstitialShow(Activity context, String adsId) {
        lastRequestedAdId = adsId != null ? adsId : "";
        pendingInterstitialShow = true;
        pendingShowActivity = context;
        logState("requestInterstitialShow", adsId);

        if (ADSAdmobmInterstitial != null) {
            showLoadedInterstitial(context);
            return;
        }

        if (adsId == null || adsId.trim().isEmpty()) {
            pendingInterstitialShow = false;
            pendingShowActivity = null;
            ADSUtilitis.MassageBoxFullDismiss();
            finishWithOptionalQuizFallback(context, "empty_ads_id");
            return;
        }

        // PreLoad: never loading dialog. Still loading → wait for that result.
        // Not ready (preload failed / unavailable) → Quiz fallback, keep preloading.
        if (ADSMainClass.isInterPreLoad() && !ADSMainClass.isInterLoad()) {
            if (!LoadingCheck) {
                return;
            }
            pendingInterstitialShow = false;
            pendingShowActivity = null;
            if (adsId != null && !adsId.trim().isEmpty()) {
                preloadInterstitialAd(context, adsId);
            }
            finishWithOptionalQuizFallback(context, "preload_unavailable");
            return;
        }

        // Load type only: show loading dialog then load.
        ADSUtilitis.MassageBoxFull(context);
        if (!LoadingCheck) {
            return;
        }
        LoadingCheck = false;
        AdmobInterstitialAd(context, adsId);
    }

    private static void tryShowPendingInterstitial(Activity context) {
        if (!pendingInterstitialShow || ADSAdmobmInterstitial == null) {
            return;
        }
        Activity showActivity = pendingShowActivity != null ? pendingShowActivity : context;
        if (showActivity.isFinishing()) {
            pendingInterstitialShow = false;
            pendingShowActivity = null;
            ADSUtilitis.MassageBoxFullDismiss();
            if (onFinishAds != null) {
                onFinishAds.onFinishAds(true);
            }
            return;
        }
        showLoadedInterstitial(showActivity);
    }

    /**
     * Quiz only after Google fail (or empty id) when Google_Ad_Failed_Show_Quiz=true.
     * Never because inter_ads_load_type is Load/PreLoad.
     */
    private static void finishWithOptionalQuizFallback(Activity context, String reason) {
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

    public static void ADSInterstitialShowing(Activity context, String adsId, OnFinishAds onFinishAd, boolean screenInterShow) {
        onFinishAds = onFinishAd;
        lastRequestedAdId = adsId != null ? adsId : "";
        pendingShowActivity = context;

        logState("SHOW_REQUEST screen=" + (context != null ? context.getClass().getSimpleName() : "null")
                + " screenFlag=" + screenInterShow, adsId);

        if (ADSMainClass.getAds_Free()) {
            onFinishAds.onFinishAds(true);
            return;
        }

        // ONLY Ad_Priority=QUIZ shows Quiz first. Load/PreLoad must NEVER trigger Quiz here.
        if (AdPlacement.shouldUseQuizPriority()) {
            if (AdPlacement.showQuizInterstitialAd(context, () -> {
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
    }

    public static void ADSMedDisplay(Activity context, String adsId) {
        logState("ADSMedDisplay", adsId);

        if (AdPlacement.shouldUseQuizPriority()) {
            if (AdPlacement.showQuizInterstitialAd(context, () -> {
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
            })) {
                return;
            }
        }

        // PreLoad: show ready ad instantly (no dialog). Load: always load on click with dialog.
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
            pendingInterstitialShow = true;
            pendingShowActivity = context;
            lastRequestedAdId = adsId != null ? adsId : "";
            return;
        }

        if (context != null && adsId != null && !adsId.trim().isEmpty()) {
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
        logState("preloadInterstitialAd", adsId);

        if (ADSMainClass.isInterLoad() || !ADSMainClass.isInterPreLoad()) {
            return;
        }
        if (AdPlacement.shouldUseQuizPriority()) {
            return;
        }
        if (adsId == null || adsId.trim().isEmpty() || ADSMainClass.getAds_Free()) {
            return;
        }
        // Never wipe a ready PreLoad ad — that causes Load-like dialog on next click.
        if (ADSAdmobmInterstitial != null) {
            return;
        }
        if (!LoadingCheck) {
            return;
        }

        lastRequestedAdId = adsId.trim();
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
                        if (pendingInterstitialShow) {
                            pendingInterstitialShow = false;
                            pendingShowActivity = null;
                            ADSUtilitis.MassageBoxFullDismiss();
                            finishWithOptionalQuizFallback(context, "preload_fail_" + loadAdError.getCode());
                        }
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_FAIL);
                    }
                });
    }

    /** True when a Google Inter is ready to show instantly (PreLoad). */
    public static boolean hasReadyInterstitial() {
        return ADSAdmobmInterstitial != null;
    }

    public static void AdmobInterstitialAd(Activity context, String AdsID) {
        if (AdsID == null || AdsID.trim().isEmpty()) {
            LoadingCheck = true;
            pendingInterstitialShow = false;
            pendingShowActivity = null;
            ADSUtilitis.MassageBoxFullDismiss();
            finishWithOptionalQuizFallback(context, "empty_ads_id_load");
            return;
        }

        final String unitId = AdsID.trim();
        lastRequestedAdId = unitId;
        AdRequest adRequest = new AdRequest.Builder().build();
        ADSAdmobmInterstitial = null;
        InterstitialAd.load(context, unitId, adRequest,
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
                        ADSAdmobmInterstitial = interstitialAd;
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_LOAD);
                        tryShowPendingInterstitial(context);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        ADSUtilitis.trackScreen(context, AppAnalyticsEvents.INTERSTITIAL_FAIL);
                        ADSUtilitis.MassageBoxFullDismiss();
                        LoadingCheck = true;
                        pendingInterstitialShow = false;
                        pendingShowActivity = null;
                        finishWithOptionalQuizFallback(context, "load_fail_" + loadAdError.getCode());
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
                AdsDisplayCheck(true);
                clearLoadedInterstitial();
                if (onFinishAds != null) {
                    onFinishAds.onFinishAds(true);
                }
                if (context instanceof Activity && ADSMainClass.isInterPreLoad()) {
                    String interId = ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME);
                    if (interId != null && !interId.isEmpty()) {
                        preloadInterstitialAd((Activity) context, interId);
                    }
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                String msg = adError.getMessage() != null ? adError.getMessage() : "";
                AdsDisplayCheck(true);
                clearLoadedInterstitial();

                // Stale ad already shown earlier — load a fresh Google Inter instead of Quiz.
                boolean alreadyShown = msg.toLowerCase().contains("already been shown");
                if (alreadyShown && context instanceof Activity) {
                    Activity activity = (Activity) context;
                    String retryId = lastRequestedAdId;
                    if (retryId == null || retryId.trim().isEmpty()) {
                        retryId = ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME);
                    }
                    if (retryId != null && !retryId.trim().isEmpty() && !activity.isFinishing()) {
                        requestInterstitialShow(activity, retryId);
                        return;
                    }
                }

                if (context instanceof Activity) {
                    finishWithOptionalQuizFallback((Activity) context,
                            "failed_to_show_" + adError.getCode());
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

    public interface OnFinishAds {
        void onFinishAds(boolean b);
    }
}
