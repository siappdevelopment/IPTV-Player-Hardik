package com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.facebook.ads.NativeAdLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;


public class ADSBannerAdaptive {

    public static LinearLayout FBAdViewBottom;
    public static NativeAdLayout NativeAdLayout;

    private static AdView preloadedAdView;
    private static String preloadedAdUnitId;
    private static boolean bannerLoading;

    public static void preloadAdMobBanner(String adUnit, Activity activity) {
        if (ADSMainClass.getAds_Free() || !ADSMainClass.isBannerPreLoad()
                || TextUtils.isEmpty(adUnit) || activity == null) {
            if (ADSMainClass.getAds_Free()) {
                clearPreloadedBanner();
            }
            return;
        }
        if (bannerLoading) {
            return;
        }
        if (preloadedAdView != null && adUnit.equals(preloadedAdUnitId)) {
            return;
        }

        clearPreloadedBanner();
        bannerLoading = true;

        AdView adView = new AdView(activity);
        adView.setAdUnitId(adUnit);
        adView.setAdSize(getAdaptiveAdSize(activity));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                bannerLoading = false;
                preloadedAdView = adView;
                preloadedAdUnitId = adUnit;
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_LOAD);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                bannerLoading = false;
                clearPreloadedBanner();
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_FAIL);
            }
            @Override
            public void onAdImpression() {
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_IMPRESSION);
            }

            @Override
            public void onAdClicked() {
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_CLICK);
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }

    @SuppressLint("MissingPermission")
    public static void loadAdMobBanner(String adUint, FrameLayout adContainerView, ShimmerFrameLayout shimmerFrameLayout, Activity activity, String big) {
        if (ADSMainClass.getAds_Free()) {
            hideBannerPlaceholder(adContainerView, shimmerFrameLayout);
            return;
        }
        if (AdPlacement.shouldUseQuizPriority()) {
            Activity activity1 = activity instanceof Activity ? (Activity) activity : null;
            // QUIZ + BANNER → Native shimmer
            if (!AdPlacement.showQuizBannerAd(activity1, adContainerView, shimmerFrameLayout, true)
                    && !AdPlacement.showQuizNativeAd(activity1, adContainerView, shimmerFrameLayout, big)) {
                adContainerView.setVisibility(View.GONE);
                hideBannerShimmer(shimmerFrameLayout);
            }
            return;
        }
        if (TextUtils.isEmpty(adUint)) {
            hideBannerPlaceholder(adContainerView, shimmerFrameLayout);
            return;
        }

        if (ADSMainClass.isBannerPreLoad()
                && preloadedAdView != null
                && adUint.equals(preloadedAdUnitId)) {
            attachPreloadedBanner(adContainerView, shimmerFrameLayout);
            preloadAdMobBanner(adUint, activity);
            return;
        }

        if (ADSMainClass.isBannerLoad()) {
            showBannerShimmer(shimmerFrameLayout);
        }

        AdView admobManagerAdView = new AdView(activity);
        admobManagerAdView.setAdUnitId(adUint);
        adContainerView.addView(admobManagerAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdaptiveAdSize(activity);
        admobManagerAdView.setAdSize(adSize);
        if (shimmerFrameLayout != null && !ADSMainClass.isBannerPreLoad()) {
            showBannerShimmer(shimmerFrameLayout);
        }
        admobManagerAdView.loadAd(adRequest);
        admobManagerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_LOAD);
                admobManagerAdView.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(AdValue adValue) {
                        ADSUtilitis.logAdRevenue(activity, adValue);
                    }
                });
                hideBannerShimmer(shimmerFrameLayout);
                adContainerView.setVisibility(View.VISIBLE);

                if (ADSMainClass.isBannerPreLoad()) {
                    preloadAdMobBanner(adUint, activity);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_FAIL);
                if (AdPlacement.tryShowQuizBannerOnGoogleFail(activity, adContainerView, shimmerFrameLayout)) {
                    return;
                }
                hideBannerShimmer(shimmerFrameLayout);
                adContainerView.setVisibility(View.GONE);
            }

            @Override
            public void onAdImpression() {
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_IMPRESSION);
            }

            @Override
            public void onAdClicked() {
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_CLICK);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public static void loadAdMobBannerWithRefresh(String adUnit, FrameLayout adContainerView,
                                                  ShimmerFrameLayout shimmerFrameLayout,
                                                  Activity activity, String big,
                                                  Runnable onLoadComplete) {
        Runnable complete = () -> {
            if (onLoadComplete != null) {
                onLoadComplete.run();
            }
        };

        if (ADSMainClass.getAds_Free()) {
            hideBannerPlaceholder(adContainerView, shimmerFrameLayout);
            complete.run();
            return;
        }

        if (AdPlacement.shouldUseQuizPriority()) {
            Activity activity1 = activity instanceof Activity ? (Activity) activity : null;
            // QUIZ + BANNER → Native shimmer
            if (!AdPlacement.showQuizBannerAd(activity1, adContainerView, shimmerFrameLayout, true)
                    && !AdPlacement.showQuizNativeAd(activity1, adContainerView, shimmerFrameLayout, big)) {
                adContainerView.setVisibility(View.GONE);
                hideBannerShimmer(shimmerFrameLayout);
            }
            complete.run();
            return;
        }
        if (TextUtils.isEmpty(adUnit)) {
            hideBannerPlaceholder(adContainerView, shimmerFrameLayout);
            complete.run();
            return;
        }

        clearBannerViews(adContainerView);
        if (ADSMainClass.isBannerLoad()) {
            showBannerShimmer(shimmerFrameLayout);
        }

        AdView admobManagerAdView = new AdView(activity);
        admobManagerAdView.setAdUnitId(adUnit);
        adContainerView.addView(admobManagerAdView);
        AdSize adSize = getAdaptiveAdSize(activity);
        admobManagerAdView.setAdSize(adSize);
        if (shimmerFrameLayout != null && !ADSMainClass.isBannerPreLoad()) {
            showBannerShimmer(shimmerFrameLayout);
        }
        admobManagerAdView.loadAd(new AdRequest.Builder().build());
        admobManagerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_LOAD);
                admobManagerAdView.setOnPaidEventListener(adValue -> ADSUtilitis.logAdRevenue(activity, adValue));
                hideBannerShimmer(shimmerFrameLayout);
                adContainerView.setVisibility(View.VISIBLE);
                complete.run();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_FAIL);
                if (AdPlacement.tryShowQuizBannerOnGoogleFail(activity, adContainerView, shimmerFrameLayout)) {
                    complete.run();
                    return;
                }
                hideBannerShimmer(shimmerFrameLayout);
                adContainerView.setVisibility(View.GONE);
                complete.run();
            }

            @Override
            public void onAdImpression() {
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_IMPRESSION);
            }

            @Override
            public void onAdClicked() {
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.BANNER_CLICK);
            }
        });
    }

    private static void clearBannerViews(FrameLayout adContainerView) {
        if (adContainerView == null) {
            return;
        }
        for (int i = adContainerView.getChildCount() - 1; i >= 0; i--) {
            View child = adContainerView.getChildAt(i);
            if (child instanceof AdView) {
                ((AdView) child).destroy();
            }
        }
        adContainerView.removeAllViews();
    }

    private static void attachPreloadedBanner(FrameLayout adContainerView, ShimmerFrameLayout shimmerFrameLayout) {
        if (preloadedAdView == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) preloadedAdView.getParent();
        if (parent != null) {
            parent.removeView(preloadedAdView);
        }
        hideBannerShimmer(shimmerFrameLayout);
        adContainerView.removeAllViews();
        adContainerView.addView(preloadedAdView);
        adContainerView.setVisibility(View.VISIBLE);
        preloadedAdView = null;
        preloadedAdUnitId = null;
    }

    private static void clearPreloadedBanner() {
        if (preloadedAdView != null) {
            preloadedAdView.destroy();
            preloadedAdView = null;
            preloadedAdUnitId = null;
        }
    }

    private static void showBannerShimmer(ShimmerFrameLayout shimmerFrameLayout) {
        if (shimmerFrameLayout == null) {
            return;
        }
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        shimmerFrameLayout.post(shimmerFrameLayout::startShimmer);
    }

    private static void hideBannerShimmer(ShimmerFrameLayout shimmerFrameLayout) {
        if (shimmerFrameLayout == null) {
            return;
        }
        if (shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.stopShimmer();
        }
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    private static void hideBannerPlaceholder(FrameLayout adContainerView, ShimmerFrameLayout shimmerFrameLayout) {
        hideBannerShimmer(shimmerFrameLayout);
        if (adContainerView != null) {
            adContainerView.setVisibility(View.GONE);
            View parent = (View) adContainerView.getParent();
            if (parent != null && parent.getId() != View.NO_ID) {
                String name;
                try {
                    name = parent.getResources().getResourceEntryName(parent.getId());
                } catch (Exception e) {
                    name = "";
                }
                if ("banner".equals(name)) {
                    parent.setVisibility(View.GONE);
                }
            }
        }
    }

    public static AdSize getAdaptiveAdSize(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = outMetrics.density;
        int adWidth = (int) (outMetrics.widthPixels / density);
        return AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, adWidth);
    }
}
