package com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.splash.GlobalParameterManage;
import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Quiz ads (fallback / priority) — config + UI only.
 * Wired from ADS* classes when Ad_Priority=QUIZ or Google_Ad_Failed_Show_Quiz=true.
 */
public final class AdPlacement {

    public static final String LAUNCHER_APP_AD_TYPE_GOOGLE_INTER = "google_inter";
    public static final String LAUNCHER_APP_AD_TYPE_GOOGLE_APP_OPEN = "google_app_open";
    public static final String LAUNCHER_APP_AD_TYPE_QUIZ_INTER = "quiz_inter";
    public static final String LAUNCHER_APP_AD_TYPE_QUIZ_APP_OPEN = "quiz_app_open";
    public static final String LAUNCHER_APP_AD_TYPE_QUIZ_BROWSER = "quiz_browser";
    public static final String LAUNCHER_APP_AD_TYPE_FULLSCREEN_NATIVE_GOOGLE = "fullscreen_native_google";
    public static final String LAUNCHER_APP_AD_TYPE_FULLSCREEN_NATIVE_QUIZ = "fullscreen_native_quiz";
    private static final AtomicInteger appProxyLookupToken = new AtomicInteger(0);
    private static final AtomicInteger launcherAppClickAdToken = new AtomicInteger(0);
    public static String adPriority = "";
    public static boolean googleAdFailedShowQuiz = false;
    private static boolean launcherAppClickAdShow = false;
    private static boolean launcherAppBackClickAdShow = false;

    private static final List<String> quizBannerTitleList = new ArrayList<>();
    private static final List<String> quizBannerDescriptionList = new ArrayList<>();
    private static final List<String> quizNativeTitleList = new ArrayList<>();
    private static final List<String> quizNativeDescriptionList = new ArrayList<>();
    private static final List<String> quizInterstitialTitleList = new ArrayList<>();
    private static final List<String> quizInterstitialDescriptionList = new ArrayList<>();
    private static final List<String> quizInterstitialRateList = new ArrayList<>();
    private static final List<String> quizAppIconList = new ArrayList<>();
    private static final List<String> quizNativeMediaList = new ArrayList<>();
    private static final List<String> quizInterstitialMediaList = new ArrayList<>();
    private static final List<String> quizAppOpenMediaList = new ArrayList<>();
    private static int launcherAppCount = 0;

    private static final int QUIZ_IMAGE_FALLBACK = R.mipmap.ic_launcher;
    private static final float QUIZ_IMAGE_REVEAL_START_SCALE = 0.93f;
    private static final long QUIZ_IMAGE_REVEAL_DURATION_MS = 200L;
    private static final ExecutorService QUIZ_IMAGE_EXECUTOR = Executors.newCachedThreadPool();
    private static final String[] QUIZ_INTERSTITIAL_RATES = {"4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9"};
    private static final String[] QUIZ_INTERSTITIAL_USERS = {"50K+ Users", "100K+ Users", "500K+ Users", "1M+ Users"};
    private static final long QUIZ_INTERSTITIAL_CLOSE_DELAY_MS = 5000L;

    private static final List<String> quizLinkList = new ArrayList<>();
    private static final List<String> quizLinkExcludeList = new ArrayList<>();
    private static final List<String> activeQuizLinkList = new ArrayList<>();
    private static final List<String> appProxyListCity = new ArrayList<>();
    private static final List<String> appProxyListState = new ArrayList<>();
    private static final List<String> appProxyListCountry = new ArrayList<>();
    private static boolean appProxyCheckIp = false;
    private static String appProxyIpCheckerUrl = "";
    private static String launcherAppAdType = "";
    /** LauncherApp_Ad_Type sequence; each triggered ad uses the next entry (cycling). */
    private static final List<String> launcherAppAdTypeList = new ArrayList<>();
    private static final List<String> launcherAppBackAdTypeList = new ArrayList<>();
    private static int launcherAppAdTypeIndex = 0;
    private static int launcherAppBackAdTypeIndex = 0;
    private static String launcherAppInterstitialId = "";
    private static String launcherAppFullscreenNativeId = "";
    private static String launcherAppBackAdType = "";
    private static String launcherAppBackInterstitialId = "";
    private static String launcherAppBackFullscreenNativeId = "";
    private static int launcherAppBackCount = 0;
    private static int launcherAppClickCount = 1;
    private static int launcherAppBackReturnCount = 1;
    @Nullable
    private static Runnable pendingLauncherAppOpenAfterBrowser;
    private static boolean waitingLauncherBrowserReturn;
    /** Set when launcher is about to open an external app; consumed in onStop to arm back-ad on resume. */
    private static boolean expectLauncherAppBackAd;
    private static boolean isShowingLauncherAppBackAd;
    /** When true, completing an ad must not arm expectLauncherAppBackAd (back-ad path). */
    private static boolean suppressArmLauncherAppBackAd;
    private static String quizButtonText = "";
    private static boolean isLinkOpenApp = false;
    private static boolean isQuizBrowserShow = false;
    public static String getAdPriority() {
        return adPriority;
    }

    public static void setAdPriority(String adPriority) {
        AdPlacement.adPriority = adPriority == null ? "" : adPriority;
    }

    public static boolean shouldUseQuizPriority() {
        return "QUIZ".equalsIgnoreCase(getAdPriority());
    }

    public static boolean getGoogleAdFailedShowQuiz() {
        return googleAdFailedShowQuiz;
    }

    public static void setGoogleAdFailedShowQuiz(boolean googleAdFailedShowQuiz) {
        AdPlacement.googleAdFailedShowQuiz = googleAdFailedShowQuiz;
    }

    public static boolean getLauncherAppClickAdShow() {
        return launcherAppClickAdShow;
    }

    public static void setLauncherAppClickAdShow(boolean launcherAppClickAdShow) {
        AdPlacement.launcherAppClickAdShow = launcherAppClickAdShow;
    }

    public static int getLauncherAppCount() {
        return launcherAppCount;
    }

    public static void setLauncherAppCount(int launcherAppCount) {
        AdPlacement.launcherAppCount = Math.max(0, launcherAppCount);
    }

    public static String getLauncherAppAdType() {
        return launcherAppAdType;
    }

    public static void setLauncherAppAdType(String launcherAppAdType) {
        AdPlacement.launcherAppAdType = launcherAppAdType == null ? "" : launcherAppAdType.trim();
        launcherAppAdTypeList.clear();
        if (!AdPlacement.launcherAppAdType.isEmpty()) {
            launcherAppAdTypeList.add(AdPlacement.launcherAppAdType);
        }
    }

    public static List<String> getLauncherAppAdTypes() {
        return new ArrayList<>(launcherAppAdTypeList);
    }

    public static void setLauncherAppAdTypes(@Nullable List<String> adTypes) {
        launcherAppAdTypeList.clear();
        if (adTypes != null) {
            launcherAppAdTypeList.addAll(adTypes);
        }
        launcherAppAdTypeIndex = 0;
        AdPlacement.launcherAppAdType = launcherAppAdTypeList.isEmpty() ? "" : launcherAppAdTypeList.get(0);
    }

    public static String getLauncherAppInterstitialId() {
        return launcherAppInterstitialId;
    }

    public static void setLauncherAppInterstitialId(String launcherAppInterstitialId) {
        AdPlacement.launcherAppInterstitialId = launcherAppInterstitialId == null
                ? ""
                : launcherAppInterstitialId.trim();
    }

    public static String getLauncherAppFullscreenNativeId() {
        return launcherAppFullscreenNativeId;
    }

    public static void setLauncherAppFullscreenNativeId(String launcherAppFullscreenNativeId) {
        AdPlacement.launcherAppFullscreenNativeId = launcherAppFullscreenNativeId == null
                ? ""
                : launcherAppFullscreenNativeId.trim();
    }

    public static boolean getLauncherAppBackClickAdShow() {
        return launcherAppBackClickAdShow;
    }

    public static void setLauncherAppBackClickAdShow(boolean launcherAppBackClickAdShow) {
        AdPlacement.launcherAppBackClickAdShow = launcherAppBackClickAdShow;
    }

    public static int getLauncherAppBackCount() {
        return launcherAppBackCount;
    }

    public static void setLauncherAppBackCount(int launcherAppBackCount) {
        AdPlacement.launcherAppBackCount = Math.max(0, launcherAppBackCount);
    }

    public static String getLauncherAppBackAdType() {
        return launcherAppBackAdType;
    }

    public static void setLauncherAppBackAdType(String launcherAppBackAdType) {
        AdPlacement.launcherAppBackAdType = launcherAppBackAdType == null
                ? ""
                : launcherAppBackAdType.trim();
        launcherAppBackAdTypeList.clear();
        if (!AdPlacement.launcherAppBackAdType.isEmpty()) {
            launcherAppBackAdTypeList.add(AdPlacement.launcherAppBackAdType);
        }
    }

    public static List<String> getLauncherAppBackAdTypes() {
        return new ArrayList<>(launcherAppBackAdTypeList);
    }

    public static void setLauncherAppBackAdTypes(@Nullable List<String> adTypes) {
        launcherAppBackAdTypeList.clear();
        if (adTypes != null) {
            launcherAppBackAdTypeList.addAll(adTypes);
        }
        launcherAppBackAdTypeIndex = 0;
        AdPlacement.launcherAppBackAdType = launcherAppBackAdTypeList.isEmpty() ? "" : launcherAppBackAdTypeList.get(0);
    }

    public static String getLauncherAppBackInterstitialId() {
        return launcherAppBackInterstitialId;
    }

    public static void setLauncherAppBackInterstitialId(String launcherAppBackInterstitialId) {
        AdPlacement.launcherAppBackInterstitialId = launcherAppBackInterstitialId == null
                ? ""
                : launcherAppBackInterstitialId.trim();
    }

    public static String getLauncherAppBackFullscreenNativeId() {
        return launcherAppBackFullscreenNativeId;
    }

    public static void setLauncherAppBackFullscreenNativeId(String launcherAppBackFullscreenNativeId) {
        AdPlacement.launcherAppBackFullscreenNativeId = launcherAppBackFullscreenNativeId == null
                ? ""
                : launcherAppBackFullscreenNativeId.trim();
    }

    private AdPlacement() {
    }

    public static boolean getIsLinkOpenApp() {
        return isLinkOpenApp;
    }

    public static void setIsLinkOpenApp(boolean value) {
        isLinkOpenApp = value;
    }

    public static boolean getIsQuizBrowserShow() {
        return isQuizBrowserShow;
    }

    public static void setIsQuizBrowserShow(boolean value) {
        isQuizBrowserShow = value;
    }
    private static void runLauncherAppClickCompleteOnce(@Nullable Runnable openSelectedApp, AtomicBoolean completed) {
        if (openSelectedApp != null && completed.compareAndSet(false, true)) {
            if (suppressArmLauncherAppBackAd) {
                suppressArmLauncherAppBackAd = false;
            } else {
                // Arm back-ad so returning to launcher after this external app can show LauncherApp_Back_* ads.
                expectLauncherAppBackAd = true;
            }
            new Handler(Looper.getMainLooper()).post(openSelectedApp);
        }
    }

    /**
     * Called from LauncherHomeActivity.onStop when leaving for an external app.
     * @return true if a back-ad should be shown on the next resume
     */
    public static boolean consumeExpectLauncherAppBackAd() {
        if (!expectLauncherAppBackAd) {
            return false;
        }
        expectLauncherAppBackAd = false;
        return true;
    }

    public static boolean shouldShowLauncherAppAd() {
        int threshold = getLauncherAppCount();
        return threshold > 0 && threshold == launcherAppClickCount;
    }

    public static void resetLauncherAppClickCount() {
        launcherAppClickCount = 1;
    }

    public static boolean shouldShowLauncherAppBackAd() {
        int threshold = getLauncherAppBackCount();
        return threshold > 0 && threshold == launcherAppBackReturnCount;
    }

    public static void resetLauncherAppBackReturnCount() {
        launcherAppBackReturnCount = 1;
    }

    private static void trackLauncherAppBackReturnCount() {
        launcherAppBackReturnCount++;
    }

    public static void handleLauncherAppClickAd(@Nullable Activity activity, @Nullable Runnable openSelectedApp) {
        if (openSelectedApp == null) {
            return;
        }
        suppressArmLauncherAppBackAd = false;
        if (activity == null || activity.isFinishing()) {
            runLauncherAppClickCompleteOnce(openSelectedApp, new AtomicBoolean(false));
            return;
        }

        if (ADSMainClass.getAds_Free()
                || !getLauncherAppClickAdShow()
                || getLauncherAppCount() <= 0) {
            runLauncherAppClickCompleteOnce(openSelectedApp, new AtomicBoolean(false));
            return;
        }

        if (shouldShowLauncherAppAd()) {
            resetLauncherAppClickCount();
            executeLauncherAppClickAd(activity, openSelectedApp);
            return;
        }

        trackLauncherAppClickCount();
        runLauncherAppClickCompleteOnce(openSelectedApp, new AtomicBoolean(false));
    }

    /**
     * Show launcher back ad when user returns from an external app to the default launcher.
     * Uses LauncherApp_Back_Count the same way click ads use LauncherApp_Count.
     */
    public static void handleLauncherAppBackAd(@Nullable Activity activity) {
        if (activity == null || !isValidLauncherAdActivity(activity)) {
            return;
        }
        if (ADSMainClass.getAds_Free()
                || !getLauncherAppBackClickAdShow()
                || getLauncherAppBackCount() <= 0
                || waitingLauncherBrowserReturn
                || isShowingLauncherAppBackAd) {
            return;
        }
        if (!shouldShowLauncherAppBackAd()) {
            trackLauncherAppBackReturnCount();
            return;
        }
        resetLauncherAppBackReturnCount();
        isShowingLauncherAppBackAd = true;
        final Runnable clearShowing = () -> isShowingLauncherAppBackAd = false;
        executeLauncherAppBackAd(activity, clearShowing);
    }

    private static void trackLauncherAppClickCount() {
        launcherAppClickCount++;
    }

    public static void applyLauncherAppConfig(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        if (jsonObject.has("LauncherApp_Click_Ad_Show")) {
            setLauncherAppClickAdShow(jsonObject.optBoolean("LauncherApp_Click_Ad_Show", false));
        }
        if (jsonObject.has("LauncherApp_Count")) {
            setLauncherAppCount(jsonObject.optInt("LauncherApp_Count", 0));
        }
        if (jsonObject.has("LauncherApp_Ad_Type")) {
            setLauncherAppAdTypes(parseQuizStringList(jsonObject, GlobalParameterManage.LAUNCHER_APP_AD_TYPE));
        }
        if (jsonObject.has("LauncherApp_Interstitial_Id")) {
            setLauncherAppInterstitialId(jsonObject.optString("LauncherApp_Interstitial_Id", ""));
        }
        if (jsonObject.has(GlobalParameterManage.LAUNCHER_APP_FULLSCREEN_NATIVE_ID)) {
            setLauncherAppFullscreenNativeId(
                    jsonObject.optString(GlobalParameterManage.LAUNCHER_APP_FULLSCREEN_NATIVE_ID, ""));
        }
        if (jsonObject.has(GlobalParameterManage.LAUNCHER_APP_BACK_CLICK_AD_SHOW)) {
            setLauncherAppBackClickAdShow(
                    jsonObject.optBoolean(GlobalParameterManage.LAUNCHER_APP_BACK_CLICK_AD_SHOW, false));
        }
        if (jsonObject.has(GlobalParameterManage.LAUNCHER_APP_BACK_COUNT)) {
            setLauncherAppBackCount(
                    jsonObject.optInt(GlobalParameterManage.LAUNCHER_APP_BACK_COUNT, 0));
        }
        if (jsonObject.has(GlobalParameterManage.LAUNCHER_APP_BACK_AD_TYPE)) {
            setLauncherAppBackAdTypes(
                    parseQuizStringList(jsonObject, GlobalParameterManage.LAUNCHER_APP_BACK_AD_TYPE));
        }
        if (jsonObject.has(GlobalParameterManage.LAUNCHER_APP_BACK_INTERSTITIAL_ID)) {
            setLauncherAppBackInterstitialId(
                    jsonObject.optString(GlobalParameterManage.LAUNCHER_APP_BACK_INTERSTITIAL_ID, ""));
        }
        if (jsonObject.has(GlobalParameterManage.LAUNCHER_APP_BACK_FULLSCREEN_NATIVE_ID)) {
            setLauncherAppBackFullscreenNativeId(
                    jsonObject.optString(GlobalParameterManage.LAUNCHER_APP_BACK_FULLSCREEN_NATIVE_ID, ""));
        }
        if (jsonObject.has(GlobalParameterManage.IS_LINK_OPEN_APP)) {
            setIsLinkOpenApp(jsonObject.optBoolean(GlobalParameterManage.IS_LINK_OPEN_APP, false));
        }
    }
    private static void executeLauncherAppClickAd(Activity activity, Runnable openSelectedApp) {
        suppressArmLauncherAppBackAd = false;
        executeLauncherAppAd(
                activity,
                nextLauncherAppAdType(launcherAppAdTypeList, false),
                getLauncherAppInterstitialId(),
                getLauncherAppFullscreenNativeId(),
                openSelectedApp
        );
    }

    private static void executeLauncherAppBackAd(Activity activity, @Nullable Runnable onComplete) {
        suppressArmLauncherAppBackAd = true;
        executeLauncherAppAd(
                activity,
                nextLauncherAppAdType(launcherAppBackAdTypeList, true),
                getLauncherAppBackInterstitialId(),
                getLauncherAppBackFullscreenNativeId(),
                onComplete
        );
    }

    /** Returns the next entry of the configured sequence (cycling) so ads follow the config order. */
    private static String nextLauncherAppAdType(List<String> sequence, boolean back) {
        if (sequence.isEmpty()) {
            return "";
        }
        int index = back ? launcherAppBackAdTypeIndex : launcherAppAdTypeIndex;
        String adType = sequence.get(index % sequence.size());
        int next = (index + 1) % sequence.size();
        if (back) {
            launcherAppBackAdTypeIndex = next;
        } else {
            launcherAppAdTypeIndex = next;
        }
        return adType;
    }

    private static String launcherIdOrDefault(@Nullable String configuredId, String defaultKey) {
        if (configuredId != null && !configuredId.trim().isEmpty()) {
            return configuredId.trim();
        }
        String defaultId = ADSMainClass.getStringValue(defaultKey);
        return defaultId != null ? defaultId.trim() : "";
    }

    private static void executeLauncherAppAd(
            Activity activity,
            String adType,
            String interstitialId,
            String fullscreenNativeId,
            @Nullable Runnable onComplete
    ) {
        switch (normalizeLauncherAppAdType(adType)) {
            case LAUNCHER_APP_AD_TYPE_GOOGLE_INTER:
                showLauncherAppGoogleInterstitial(
                        activity, onComplete, launcherIdOrDefault(interstitialId, ADSMainClass.INTER_FIRST_TIME));
                break;
            case LAUNCHER_APP_AD_TYPE_GOOGLE_APP_OPEN:
                showLauncherAppGoogleAppOpen(activity, onComplete);
                break;
            case LAUNCHER_APP_AD_TYPE_QUIZ_INTER:
                showLauncherAppQuizInterstitial(activity, onComplete);
                break;
            case LAUNCHER_APP_AD_TYPE_QUIZ_APP_OPEN:
                showLauncherAppQuizAppOpen(activity, onComplete);
                break;
            case LAUNCHER_APP_AD_TYPE_QUIZ_BROWSER:
                openLauncherAppQuizBrowser(activity, onComplete);
                break;
            case LAUNCHER_APP_AD_TYPE_FULLSCREEN_NATIVE_GOOGLE:
                showLauncherAppGoogleFullscreenNative(
                        activity, onComplete, launcherIdOrDefault(fullscreenNativeId, ADSMainClass.CALL_END_Native));
                break;
            case LAUNCHER_APP_AD_TYPE_FULLSCREEN_NATIVE_QUIZ:
                showLauncherAppQuizFullscreenNative(activity, onComplete);
                break;
            default:
                runLauncherAppClickCompleteOnce(onComplete, new AtomicBoolean(false));
                break;
        }
    }

    private static void showLauncherAppQuizFullscreenNative(Activity activity, Runnable openSelectedApp) {
        final AtomicBoolean completed = new AtomicBoolean(false);
        final Runnable completeOnce = () -> runLauncherAppClickCompleteOnce(openSelectedApp, completed);
        if (!showQuizFullscreenNativeAd(activity, completeOnce)) {
            completeOnce.run();
        }
    }

    @SuppressLint("InflateParams")
    private static void showLauncherAppGoogleFullscreenNative(
            Activity activity,
            Runnable openSelectedApp,
            @Nullable String nativeAdIdOverride
    ) {
        final int loadToken = launcherAppClickAdToken.incrementAndGet();
        String nativeAdId = nativeAdIdOverride != null ? nativeAdIdOverride : getLauncherAppFullscreenNativeId();
        if (nativeAdId == null || nativeAdId.trim().isEmpty()) {
            if (getGoogleAdFailedShowQuiz() && isValidLauncherAdActivity(activity)) {
                showLauncherAppQuizFullscreenNative(activity, openSelectedApp);
            } else {
                runLauncherAppClickCompleteOnce(openSelectedApp, new AtomicBoolean(false));
            }
            return;
        }

        final AtomicBoolean completed = new AtomicBoolean(false);
        final Runnable completeOnce = () -> runLauncherAppClickCompleteOnce(openSelectedApp, completed);
        if (!isValidLauncherAdActivity(activity)) {
            completeOnce.run();
            return;
        }

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        final FrameLayout root = new FrameLayout(activity);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        root.setBackgroundColor(Color.WHITE);

        final ShimmerFrameLayout shimmerFrame = new ShimmerFrameLayout(activity);
        shimmerFrame.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        View shimmerContent = LayoutInflater.from(activity).inflate(R.layout.native_full_ad_shimmer, shimmerFrame, false);
        shimmerFrame.addView(shimmerContent);
        root.addView(shimmerFrame);
        shimmerFrame.startShimmer();

        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        final AtomicBoolean dialogDismissed = new AtomicBoolean(false);
        Runnable dismissDialog = () -> {
            if (!dialogDismissed.compareAndSet(false, true)) {
                return;
            }
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (Exception ignored) {
            }
        };

        try {
            dialog.show();
        } catch (Exception e) {
            completeOnce.run();
            return;
        }

        AdLoader adLoader = new AdLoader.Builder(activity, nativeAdId.trim())
                .forNativeAd(nativeAd -> {
                    if (loadToken != launcherAppClickAdToken.get() || !isValidLauncherAdActivity(activity)) {
                        nativeAd.destroy();
                        dismissDialog.run();
                        completeOnce.run();
                        return;
                    }

                    try {
                        NativeAdView adView = (NativeAdView) LayoutInflater.from(activity)
                                .inflate(R.layout.native_full_ad_layout, root, false);
                        populateLauncherFullscreenNativeAdView(nativeAd, adView, activity);

                        View ivClose = adView.findViewById(R.id.ivClose);
                        if (ivClose != null) {
                            ivClose.setVisibility(View.VISIBLE);
                            ivClose.bringToFront();
                            ivClose.setClickable(true);
                            ivClose.setFocusable(true);
                            ivClose.setOnClickListener(v -> {
                                dismissDialog.run();
                                completeOnce.run();
                            });
                        }

                        shimmerFrame.stopShimmer();
                        root.removeAllViews();
                        root.addView(adView);
                        if (adView.isAttachedToWindow()) {
                            adView.setNativeAd(nativeAd);
                        } else {
                            adView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                                @Override
                                public void onViewAttachedToWindow(View v) {
                                    adView.removeOnAttachStateChangeListener(this);
                                    adView.setNativeAd(nativeAd);
                                }

                                @Override
                                public void onViewDetachedFromWindow(View v) {
                                }
                            });
                        }
                        ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.NATIVE_LOAD);
                    } catch (Exception e) {
                        nativeAd.destroy();
                        dismissDialog.run();
                        if (getGoogleAdFailedShowQuiz() && isValidLauncherAdActivity(activity)) {
                            showLauncherAppQuizFullscreenNative(activity, openSelectedApp);
                        } else {
                            completeOnce.run();
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.NATIVE_FAIL);
                        dismissDialog.run();
                        if (loadToken != launcherAppClickAdToken.get()) {
                            completeOnce.run();
                            return;
                        }
                        if (getGoogleAdFailedShowQuiz() && isValidLauncherAdActivity(activity)) {
                            showLauncherAppQuizFullscreenNative(activity, openSelectedApp);
                        } else {
                            completeOnce.run();
                        }
                    }
                })
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private static void populateLauncherFullscreenNativeAdView(
            @NonNull NativeAd nativeAd,
            @NonNull NativeAdView adView,
            @NonNull Context context
    ) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        if (mediaView != null) {
            adView.setMediaView(mediaView);
        }

        View headlineView = adView.findViewById(R.id.ad_headline);
        if (headlineView instanceof TextView) {
            adView.setHeadlineView(headlineView);
            ((TextView) headlineView).setText(nativeAd.getHeadline());
        }

        View bodyView = adView.findViewById(R.id.ad_body);
        if (bodyView instanceof TextView) {
            adView.setBodyView(bodyView);
            if (nativeAd.getBody() == null) {
                bodyView.setVisibility(View.INVISIBLE);
            } else {
                bodyView.setVisibility(View.VISIBLE);
                ((TextView) bodyView).setText(nativeAd.getBody());
            }
        }

        View ctaView = adView.findViewById(R.id.ad_call_to_action);
        if (ctaView instanceof TextView) {
            adView.setCallToActionView(ctaView);
            if (nativeAd.getCallToAction() == null) {
                ctaView.setVisibility(View.INVISIBLE);
            } else {
                ctaView.setVisibility(View.VISIBLE);
                ((TextView) ctaView).setText(nativeAd.getCallToAction());
            }
        }

        View iconView = adView.findViewById(R.id.ad_app_icon);
        if (iconView instanceof ImageView) {
            adView.setIconView(iconView);
            if (nativeAd.getIcon() == null) {
                iconView.setVisibility(View.GONE);
            } else {
                iconView.setVisibility(View.VISIBLE);
                ((ImageView) iconView).setImageDrawable(nativeAd.getIcon().getDrawable());
            }
        }

        try {
            TextView cta = ctaView instanceof TextView ? (TextView) ctaView : null;
            int buttonColor = Color.parseColor(ADSMainClass.getLightNativeButtonColor());
            int buttonTextColor = Color.parseColor(ADSMainClass.getLightNativeButtonTextColor());
            int textColor = Color.parseColor(ADSMainClass.getLightNativeAllTextColor());

            if (headlineView instanceof TextView) {
                ((TextView) headlineView).setTextColor(textColor);
            }
            if (bodyView instanceof TextView) {
                ((TextView) bodyView).setTextColor(textColor);
            }
            if (cta != null) {
                int radius = Integer.parseInt(ADSMainClass.getButtonMargins());
                int dimenId = context.getResources().getIdentifier(
                        "_" + radius + "sdp",
                        "dimen",
                        context.getPackageName()
                );
                float radiusPx = 20f;
                if (dimenId != 0) {
                    radiusPx = context.getResources().getDimension(dimenId);
                }
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setColor(buttonColor);
                drawable.setCornerRadius(radiusPx);
                cta.setBackground(drawable);
                cta.setTextColor(buttonTextColor);
            }
        } catch (Exception ignored) {
        }
    }

    private static void showLauncherAppQuizInterstitial(Activity activity, Runnable openSelectedApp) {
        final AtomicBoolean completed = new AtomicBoolean(false);
        final Runnable completeOnce = () -> runLauncherAppClickCompleteOnce(openSelectedApp, completed);
        if (!showQuizInterstitialAd(activity, completeOnce)) {
            completeOnce.run();
        }
    }

    private static void openLauncherAppQuizBrowser(Activity activity, Runnable openSelectedApp) {
        final AtomicBoolean completed = new AtomicBoolean(false);
        final Runnable completeOnce = () -> runLauncherAppClickCompleteOnce(openSelectedApp, completed);
        String link = pickRandomQuizLink();
        if (link != null && !link.trim().isEmpty() && isValidLauncherAdActivity(activity)) {
            waitingLauncherBrowserReturn = true;
            pendingLauncherAppOpenAfterBrowser = completeOnce;
            if (openQuizLinkForLauncherReturn(activity, link)) {
                return;
            }
            waitingLauncherBrowserReturn = false;
            pendingLauncherAppOpenAfterBrowser = null;
        }
        completeOnce.run();
    }

    public static void onLauncherActivityResumed(@Nullable Activity activity) {
        if (!waitingLauncherBrowserReturn || pendingLauncherAppOpenAfterBrowser == null) {
            return;
        }
        if (!isValidLauncherAdActivity(activity)) {
            return;
        }
        waitingLauncherBrowserReturn = false;
        Runnable pending = pendingLauncherAppOpenAfterBrowser;
        pendingLauncherAppOpenAfterBrowser = null;
        pending.run();
    }

    private static boolean openQuizLinkForLauncherReturn(Activity activity, String link) {
        if (activity == null || link == null || link.trim().isEmpty()) {
            return false;
        }
        try {
            if (getIsLinkOpenApp()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Bundle bundle = new Bundle();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    bundle.putBinder("android.support.customtabs.extra.SESSION", (IBinder) null);
                }
                intent.putExtras(bundle);
                intent.putExtra(
                        "android.support.customtabs.extra.TOOLBAR_COLOR",
                        ContextCompat.getColor(activity, R.color.calendar_primary));
                intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                intent.setData(Uri.parse(link.trim()));
                activity.startActivity(intent);
            } else {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(activity, R.color.calendar_primary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                customTabsIntent.launchUrl(activity, Uri.parse(link.trim()));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void showLauncherAppQuizAppOpen(Activity activity, Runnable openSelectedApp) {
        final AtomicBoolean completed = new AtomicBoolean(false);
        final Runnable completeOnce = () -> runLauncherAppClickCompleteOnce(openSelectedApp, completed);
        if (!showQuizAppOpenAd(activity, completeOnce)) {
            completeOnce.run();
        }
    }


    private static boolean isValidLauncherAdActivity(@Nullable Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }
    private static void handleLauncherGoogleAdUnavailable(Activity activity, Runnable openSelectedApp, boolean quizInterFallback) {
        ADSAppManage.isAppOpenBlocked = false;
        if (getGoogleAdFailedShowQuiz() && isValidLauncherAdActivity(activity)) {
            if (quizInterFallback) {
                showLauncherAppQuizInterstitial(activity, openSelectedApp);
            } else {
                showLauncherAppQuizAppOpen(activity, openSelectedApp);
            }
            return;
        }
        runLauncherAppClickCompleteOnce(openSelectedApp, new AtomicBoolean(false));
    }

    private static String resolveLauncherAppOpenAdId() {
//        String launcherId = getLauncherAppInterstitialId();
//        if (launcherId != null && !launcherId.trim().isEmpty()) {
//            return launcherId.trim();
//        }
        String defaultId = ADSMainClass.getStringValue(ADSMainClass.APP_OPEN_ID);
        return defaultId != null ? defaultId.trim() : "";
    }

    private static void showLauncherAppGoogleAppOpen(Activity activity, Runnable openSelectedApp) {
        final int loadToken = launcherAppClickAdToken.incrementAndGet();
        String appOpenId = resolveLauncherAppOpenAdId();
        if (appOpenId.isEmpty()) {
            handleLauncherGoogleAdUnavailable(activity, openSelectedApp, false);
            return;
        }

        final AtomicBoolean completed = new AtomicBoolean(false);
        final Runnable completeOnce = () -> {
            ADSAppManage.isAppOpenBlocked = false;
            runLauncherAppClickCompleteOnce(openSelectedApp, completed);
        };
        final boolean showLoadingDialog = ADSMainClass.isAppOpenLoad();

        ADSAppManage.isAppOpenBlocked = true;
        if (showLoadingDialog) {
            ADSUtilitis.MassageBoxFull(activity);
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        AppOpenAd.load(activity, appOpenId, adRequest, new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd loadedAd) {
                if (loadToken != launcherAppClickAdToken.get() || !isValidLauncherAdActivity(activity)) {
                    if (showLoadingDialog) {
                        ADSUtilitis.MassageBoxFullDismiss();
                    }
                    completeOnce.run();
                    return;
                }

                if (showLoadingDialog) {
                    ADSUtilitis.MassageBoxFullDismiss();
                }

                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.APP_OPEN_AD_LOAD);
                loadedAd.setOnPaidEventListener(adValue -> logAdRevenue(activity, adValue));
                loadedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.APP_OPEN_AD_IMPRESSION);
                        completeOnce.run();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.APP_OPEN_AD_FAIL);
                        if (getGoogleAdFailedShowQuiz() && isValidLauncherAdActivity(activity)) {
                            ADSAppManage.isAppOpenBlocked = false;
                            showLauncherAppQuizAppOpen(activity, openSelectedApp);
                            return;
                        }
                        completeOnce.run();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.APP_OPEN_AD_IMPRESSION);
                    }
                });

                try {
                    loadedAd.show(activity);
                } catch (Exception e) {
                    if (getGoogleAdFailedShowQuiz() && isValidLauncherAdActivity(activity)) {
                        ADSAppManage.isAppOpenBlocked = false;
                        showLauncherAppQuizAppOpen(activity, openSelectedApp);
                        return;
                    }
                    completeOnce.run();
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (loadToken != launcherAppClickAdToken.get()) {
                    return;
                }
                if (showLoadingDialog) {
                    ADSUtilitis.MassageBoxFullDismiss();
                }
                ADSUtilitis.trackScreen(activity, AppAnalyticsEvents.APP_OPEN_AD_FAIL);
                handleLauncherGoogleAdUnavailable(activity, openSelectedApp, false);
            }
        });
    }
    public static void logAdRevenue(Context context, AdValue adValue) {
        if (context == null || adValue == null) {
            return;
        }
        try {
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            double revenue = adValue.getValueMicros() / 1_000_000.0;
            String currency = adValue.getCurrencyCode();
            Bundle adRevenueParams = new Bundle();
            adRevenueParams.putString(FirebaseAnalytics.Param.AD_PLATFORM, "Google Ad Manager");
            adRevenueParams.putString(FirebaseAnalytics.Param.CURRENCY, currency);
            adRevenueParams.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, adRevenueParams);
        } catch (Exception ignored) {
        }
    }
    private static void showLauncherAppGoogleInterstitial(
            Activity activity,
            Runnable openSelectedApp,
            @Nullable String interstitialIdOverride
    ) {
        final int loadToken = launcherAppClickAdToken.incrementAndGet();
        String interstitialId = interstitialIdOverride != null
                ? interstitialIdOverride
                : getLauncherAppInterstitialId();
        if (interstitialId == null || interstitialId.trim().isEmpty()) {
            handleLauncherGoogleAdUnavailable(activity, openSelectedApp, true);
            return;
        }

        final AtomicBoolean completed = new AtomicBoolean(false);
        final Runnable completeOnce = () -> runLauncherAppClickCompleteOnce(openSelectedApp, completed);

        // Launcher app google_inter: always load-on-click with loading dialog (never PreLoad).
        ADSUtilitis.MassageBoxFull(activity);

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, interstitialId.trim(), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd loadedAd) {
                if (loadToken != launcherAppClickAdToken.get() || !isValidLauncherAdActivity(activity)) {
                    ADSUtilitis.MassageBoxFullDismiss();
                    completeOnce.run();
                    return;
                }

                ADSUtilitis.MassageBoxFullDismiss();

                loadedAd.setOnPaidEventListener(adValue -> logAdRevenue(activity, adValue));
                loadedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        completeOnce.run();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        if (getGoogleAdFailedShowQuiz() && isValidLauncherAdActivity(activity)) {
                            showLauncherAppQuizInterstitial(activity, openSelectedApp);
                            return;
                        }
                        completeOnce.run();
                    }
                });
                loadedAd.show(activity);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (loadToken != launcherAppClickAdToken.get()) {
                    return;
                }
                ADSUtilitis.MassageBoxFullDismiss();
                handleLauncherGoogleAdUnavailable(activity, openSelectedApp, true);
            }
        });
    }

    private static String normalizeLauncherAppAdType(@Nullable String adType) {
        if (adType == null || adType.trim().isEmpty()) {
            return LAUNCHER_APP_AD_TYPE_GOOGLE_INTER;
        }
        String normalized = adType.trim().toLowerCase(Locale.US);
        switch (normalized) {
            case "google_inter":
            case "inter":
            case "interstitial":
                return LAUNCHER_APP_AD_TYPE_GOOGLE_INTER;
            case "google_app_open":
            case "app_open":
            case "appopen":
                return LAUNCHER_APP_AD_TYPE_GOOGLE_APP_OPEN;
            case "quiz_inter":
            case "quiz_interstitial":
                return LAUNCHER_APP_AD_TYPE_QUIZ_INTER;
            case "quiz_app_open":
            case "quiz_appopen":
                return LAUNCHER_APP_AD_TYPE_QUIZ_APP_OPEN;
            case "quiz_browser":
            case "browser":
                return LAUNCHER_APP_AD_TYPE_QUIZ_BROWSER;
            case "google_native":
            case "fullscreen_native_google":
            case "full_native_google":
            case "native_fullscreen_google":
                return LAUNCHER_APP_AD_TYPE_FULLSCREEN_NATIVE_GOOGLE;
            case "quiz_native":
            case "fullscreen_native_quiz":
            case "full_native_quiz":
            case "native_fullscreen_quiz":
                return LAUNCHER_APP_AD_TYPE_FULLSCREEN_NATIVE_QUIZ;
            default:
                return normalized;
        }
    }
    // ==================== ClEnd (call end) back ad config ====================

    public static final String CLEND_BACK_AD_INTER = "inter";
    public static final String CLEND_BACK_AD_APP_OPEN = "appopen";
    public static final String CLEND_BACK_AD_NATIVE = "native";
    public static final String CLEND_BACK_AD_ALTERNATE = "alternate";
    private static final String CLEND_BACK_AD_SEQUENCE_PREF = "clend_back_ad_sequence";
    private static final String CLEND_BACK_AD_POSITION_PREF = "clend_back_ad_position";

    /** One [type, count] entry of ClEnd_Back_Ad_Sequence. */
    public static final class ClEndBackAdItem {
        public final String type;
        public final int count;

        ClEndBackAdItem(String type, int count) {
            this.type = type;
            this.count = count;
        }
    }

    /** Returns one of inter / appopen / native / alternate (anything else falls back to inter). */
    @Nullable
    private static String normalizeClEndBackSingleType(@Nullable String type) {
        if (type == null) {
            return null;
        }
        String t = type.trim().toLowerCase(Locale.US);
        switch (t) {
            case CLEND_BACK_AD_INTER:
            case CLEND_BACK_AD_APP_OPEN:
            case CLEND_BACK_AD_NATIVE:
                return t;
            default:
                return null;
        }
    }

    public static String getClEndBackAdType() {
        String t = ADSMainClass.getCallEndInterAdsType();
        if (t != null && CLEND_BACK_AD_ALTERNATE.equalsIgnoreCase(t.trim())) {
            return CLEND_BACK_AD_ALTERNATE;
        }
        String single = normalizeClEndBackSingleType(t);
        return single != null ? single : CLEND_BACK_AD_INTER;
    }

    public static void setClEndBackAdType(@Nullable String type) {
        ADSMainClass.setCallEndInterAdsType(type == null ? CLEND_BACK_AD_INTER : type.trim().toLowerCase(Locale.US));
    }

    /** Parses [[type, count], ...]; invalid types are skipped and invalid counts default to 1. */
    private static List<ClEndBackAdItem> parseClEndBackAdSequence(@Nullable JSONArray array) {
        List<ClEndBackAdItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (int i = 0; i < array.length(); i++) {
            Object entry = array.opt(i);
            String type = null;
            int count = 1;
            if (entry instanceof JSONArray) {
                JSONArray pair = (JSONArray) entry;
                type = normalizeClEndBackSingleType(pair.optString(0, ""));
                if (pair.length() > 1) {
                    try {
                        count = Integer.parseInt(pair.optString(1, "1").trim());
                    } catch (NumberFormatException e) {
                        count = 1;
                    }
                }
            } else if (entry instanceof String) {
                type = normalizeClEndBackSingleType((String) entry);
            }
            if (type != null) {
                items.add(new ClEndBackAdItem(type, Math.max(1, count)));
            }
        }
        return items;
    }

    public static List<ClEndBackAdItem> getClEndBackAdSequence() {
        try {
            String raw = ADSMainClass.getStringValue(CLEND_BACK_AD_SEQUENCE_PREF);
            if (raw != null && !raw.trim().isEmpty()) {
                return parseClEndBackAdSequence(new JSONArray(raw));
            }
        } catch (Exception ignored) {
        }
        return new ArrayList<>();
    }

    public static void setClEndBackAdSequence(@Nullable JSONArray sequence) {
        List<ClEndBackAdItem> items = parseClEndBackAdSequence(sequence);
        JSONArray clean = new JSONArray();
        for (ClEndBackAdItem item : items) {
            JSONArray pair = new JSONArray();
            pair.put(item.type);
            pair.put(String.valueOf(item.count));
            clean.put(pair);
        }
        String newValue = clean.toString();
        if (!newValue.equals(ADSMainClass.getStringValue(CLEND_BACK_AD_SEQUENCE_PREF))) {
            ADSMainClass.setStringValue(CLEND_BACK_AD_SEQUENCE_PREF, newValue);
            ADSMainClass.setStringValue(CLEND_BACK_AD_POSITION_PREF, "0");
        }
    }

    public static void applyClEndBackConfig(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        Object value = jsonObject.opt(GlobalParameterManage.CLEND_BACK_AD_SEQUENCE);
        JSONArray array = null;
        try {
            if (value instanceof JSONArray) {
                array = (JSONArray) value;
            } else if (value instanceof String) {
                array = new JSONArray((String) value);
            }
        } catch (Exception ignored) {
        }
        setClEndBackAdSequence(array);
    }

    /**
     * Ad type to use for this back press. For "alternate" it walks ClEnd_Back_Ad_Sequence, where each
     * [type, count] repeats count times before moving on, and restarts after the last item.
     */
    public static String nextClEndBackAdType() {
        String type = getClEndBackAdType();
        if (!CLEND_BACK_AD_ALTERNATE.equals(type)) {
            return type;
        }
        List<ClEndBackAdItem> sequence = getClEndBackAdSequence();
        int total = 0;
        for (ClEndBackAdItem item : sequence) {
            total += item.count;
        }
        if (total <= 0) {
            return CLEND_BACK_AD_INTER;
        }
        int position = 0;
        try {
            position = Integer.parseInt(ADSMainClass.getStringValue(CLEND_BACK_AD_POSITION_PREF).trim());
        } catch (Exception ignored) {
        }
        int slot = Math.abs(position) % total;
        ADSMainClass.setStringValue(CLEND_BACK_AD_POSITION_PREF, String.valueOf((slot + 1) % total));
        for (ClEndBackAdItem item : sequence) {
            if (slot < item.count) {
                return item.type;
            }
            slot -= item.count;
        }
        return CLEND_BACK_AD_INTER;
    }

    /** Ad types that may be needed by the current config; only these should ever be preloaded. */
    public static Set<String> getClEndBackPreloadTypes() {
        Set<String> types = new LinkedHashSet<>();
        String type = getClEndBackAdType();
        if (CLEND_BACK_AD_ALTERNATE.equals(type)) {
            for (ClEndBackAdItem item : getClEndBackAdSequence()) {
                types.add(item.type);
            }
            if (types.isEmpty()) {
                types.add(CLEND_BACK_AD_INTER);
            }
        } else {
            types.add(type);
        }
        return types;
    }

    /** Shows a preloaded Google native ad full screen; onClosed runs once when the user closes it. */
    public static void showClEndBackNativeAd(Activity activity, NativeAd nativeAd, Runnable onClosed) {
        showClEndBackNativeAd(activity, nativeAd, null, onClosed);
    }

    /** Same as above; onShown runs once the dialog is actually on screen. */
    public static void showClEndBackNativeAd(
            Activity activity, NativeAd nativeAd, @Nullable Runnable onShown, Runnable onClosed) {
        final AtomicBoolean closed = new AtomicBoolean(false);
        final Runnable closeOnce = () -> {
            if (closed.compareAndSet(false, true)) {
                nativeAd.destroy();
                onClosed.run();
            }
        };
        if (!isValidLauncherAdActivity(activity)) {
            closeOnce.run();
            return;
        }
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            NativeAdView adView = (NativeAdView) LayoutInflater.from(activity)
                    .inflate(R.layout.native_full_ad_layout, null, false);
            populateLauncherFullscreenNativeAdView(nativeAd, adView, activity);
            View ivClose = adView.findViewById(R.id.ivClose);
            if (ivClose != null) {
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(v -> {
                    try {
                        dialog.dismiss();
                    } catch (Exception ignored) {
                    }
                    closeOnce.run();
                });
            }
            dialog.setContentView(adView);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            }
            dialog.show();
            adView.setNativeAd(nativeAd);
            if (onShown != null) {
                onShown.run();
            }
        } catch (Exception e) {
            closeOnce.run();
        }
    }

    public static boolean isQuizType(@Nullable String type) {
        if (type == null) {
            return false;
        }
        String t = type.trim().toLowerCase(Locale.US);
        return t.contains("quiz");
    }
    public static void applyQuizAdsConfig(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        if (jsonObject.has("Ad_Priority")) {
            setAdPriority(jsonObject.optString("Ad_Priority", ""));
        }
        if (jsonObject.has("Google_Ad_Failed_Show_Quiz")) {
            setGoogleAdFailedShowQuiz(jsonObject.optBoolean("Google_Ad_Failed_Show_Quiz", false));
        }

        replaceListIfPresent(quizAppIconList, parseQuizStringList(jsonObject, "Quiz_App_Icon"));
        replaceListIfPresent(quizNativeMediaList, parseQuizStringList(jsonObject, "Quiz_Native_Media"));
        replaceListIfPresent(quizInterstitialMediaList, parseQuizStringList(jsonObject, "Quiz_Interstitial_Media"));
        replaceListIfPresent(quizAppOpenMediaList, parseQuizStringList(jsonObject, "Quiz_App_Open_Media"));
        replaceListIfPresent(quizBannerTitleList, parseQuizStringList(jsonObject, "Quiz_Banner_Title"));
        replaceListIfPresent(quizBannerDescriptionList, parseQuizStringList(jsonObject, "Quiz_Banner_Description"));
        replaceListIfPresent(quizNativeTitleList, parseQuizStringList(jsonObject, "Quiz_Native_Title"));
        replaceListIfPresent(quizNativeDescriptionList, parseQuizStringList(jsonObject, "Quiz_Native_Description"));
        replaceListIfPresent(quizInterstitialTitleList, parseQuizStringList(jsonObject, "Quiz_Interstitial_Title"));
        replaceListIfPresent(quizInterstitialDescriptionList, parseQuizStringList(jsonObject, "Quiz_Interstitial_Description"));
        replaceListIfPresent(quizInterstitialRateList, parseQuizStringList(jsonObject, "Quiz_Interstitial_Rate"));

        List<String> buttonTexts = parseQuizStringList(jsonObject, "Quiz_Button_Text");
        if (!buttonTexts.isEmpty()) {
            quizButtonText = buttonTexts.get(0);
        } else if (jsonObject.has("Quiz_Button_Text")) {
            quizButtonText = jsonObject.optString("Quiz_Button_Text", "").trim();
        }
    }

    public static void applyAppProxyConfig(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }

        appProxyCheckIp = jsonObject.optBoolean("App_Proxy_Check_Ip", false);
        appProxyIpCheckerUrl = jsonObject.optString("App_Proxy_Ip_Checker_Url", "").trim();
        if (jsonObject.has(GlobalParameterManage.IS_LINK_OPEN_APP)) {
            setIsLinkOpenApp(jsonObject.optBoolean(GlobalParameterManage.IS_LINK_OPEN_APP, false));
        }
        if (jsonObject.has(GlobalParameterManage.IS_QUIZ_BROWSER_SHOW)) {
            setIsQuizBrowserShow(jsonObject.optBoolean(GlobalParameterManage.IS_QUIZ_BROWSER_SHOW, false));
        }

        replaceListIfPresent(quizLinkList, parseQuizStringList(jsonObject, "Quiz_Link_List"));
        replaceListIfPresent(quizLinkExcludeList, parseQuizStringList(jsonObject, "Quiz_Link_List_Exclude"));
        replaceListIfPresent(appProxyListCity, parseQuizStringList(jsonObject, "App_Proxy_List_City"));
        replaceListIfPresent(appProxyListState, parseQuizStringList(jsonObject, "App_Proxy_List_State"));
        replaceListIfPresent(appProxyListCountry, parseQuizStringList(jsonObject, "App_Proxy_List_Country"));

        refreshActiveQuizLinks();
    }

    private static void replaceListIfPresent(@NonNull List<String> target, @NonNull List<String> source) {
        if (source.isEmpty()) {
            return;
        }
        target.clear();
        target.addAll(source);
    }

    private static void refreshActiveQuizLinks() {
        if (!appProxyCheckIp) {
            setActiveQuizLinkList(quizLinkList);
            return;
        }

        setActiveQuizLinkList(quizLinkList);
        if (appProxyIpCheckerUrl.isEmpty()) {
            return;
        }

        final int lookupToken = appProxyLookupToken.incrementAndGet();
        IPAddressHelper.getLocationInfo(appProxyIpCheckerUrl, new IPAddressHelper.LocationCallback() {
            @Override
            public void onResponse(@NonNull IPAddressHelper.LocationInfo locationInfo) {
                if (lookupToken != appProxyLookupToken.get()) {
                    return;
                }
                if (matchesAppProxyLocation(locationInfo)) {
                    setActiveQuizLinkList(!quizLinkExcludeList.isEmpty() ? quizLinkExcludeList : quizLinkList);
                } else {
                    setActiveQuizLinkList(quizLinkList);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (lookupToken != appProxyLookupToken.get()) {
                    return;
                }
                setActiveQuizLinkList(quizLinkList);
            }
        });
    }

    private static void setActiveQuizLinkList(@NonNull List<String> sourceLinks) {
        activeQuizLinkList.clear();
        if (!sourceLinks.isEmpty()) {
            activeQuizLinkList.addAll(sourceLinks);
        }
    }

    private static boolean matchesAppProxyLocation(@NonNull IPAddressHelper.LocationInfo locationInfo) {
        return containsAppProxyValue(appProxyListCity, locationInfo.city)
                || containsAppProxyValue(appProxyListState, locationInfo.state)
                || containsAppProxyValue(appProxyListCountry, locationInfo.country);
    }

    private static boolean containsAppProxyValue(@NonNull List<String> configuredValues, @Nullable String detectedValue) {
        if (detectedValue == null || detectedValue.trim().isEmpty() || configuredValues.isEmpty()) {
            return false;
        }
        String normalizedDetectedValue = detectedValue.trim().toLowerCase(Locale.US);
        for (String configuredValue : configuredValues) {
            if (configuredValue != null
                    && !configuredValue.trim().isEmpty()
                    && configuredValue.trim().toLowerCase(Locale.US).equals(normalizedDetectedValue)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> parseQuizStringList(@Nullable JSONObject jsonObject, @NonNull String key) {
        List<String> items = new ArrayList<>();
        if (jsonObject == null || !jsonObject.has(key)) {
            return items;
        }
        Object value = jsonObject.opt(key);
        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.length(); i++) {
                String item = array.optString(i, "").trim();
                if (!item.isEmpty()) {
                    items.add(item);
                }
            }
            return items;
        }
        if (value instanceof String) {
            String text = ((String) value).trim();
            if (!text.isEmpty()) {
                items.add(text);
            }
        }
        return items;
    }

    private static int getQuizSyncedItemCount() {
        return getQuizItemCount(
                quizAppIconList,
                quizBannerTitleList,
                quizBannerDescriptionList,
                quizNativeTitleList,
                quizNativeDescriptionList,
                quizInterstitialTitleList,
                quizInterstitialDescriptionList
        );
    }

    @SafeVarargs
    private static int getQuizItemCount(List<String>... requiredLists) {
        int count = Integer.MAX_VALUE;
        for (List<String> list : requiredLists) {
            if (list == null || list.isEmpty()) {
                return 0;
            }
            count = Math.min(count, list.size());
        }
        return count == Integer.MAX_VALUE ? 0 : Math.min(3, count);
    }

    private static int pickQuizSyncedIndex(int itemCount) {
        if (itemCount <= 0) {
            return 0;
        }
        return new Random().nextInt(itemCount);
    }

    private static String pickRandomQuizLink() {
        List<String> links = !activeQuizLinkList.isEmpty() ? activeQuizLinkList : quizLinkList;
        if (links.isEmpty()) {
            return "";
        }
        return links.get(new Random().nextInt(links.size()));
    }

    private static String getQuizButtonText() {
        return quizButtonText == null ? "" : quizButtonText.trim();
    }

    private static void applyQuizButtonText(@Nullable AppCompatTextView button) {
        if (button == null) {
            return;
        }
        String buttonText = getQuizButtonText();
        if (!buttonText.isEmpty()) {
            button.setText(buttonText);
        }
    }

    private static String getNativeAdButtonColor(@Nullable Context context) {
        return ADSMainClass.getLightNativeButtonColor();
    }

    private static void applyQuizAdColors(@Nullable View view) {
        if (view == null) {
            return;
        }
        String buttonColor = getNativeAdButtonColor(view.getContext());
        View labelView = view.findViewById(R.id.tvQZLabel);
        if (labelView != null) {
            applyBackgroundColor(labelView, buttonColor, R.color.calendar_primary);
        }
        View buttonView = view.findViewById(R.id.btnQZClick);
        if (buttonView != null) {
            applyBackgroundColor(buttonView, buttonColor, R.color.calendar_primary);
        }
    }

    private static void applyBackgroundColor(View view, String colorHex, int fallbackColorRes) {
        if (view == null) {
            return;
        }
        Integer color = parseAdColor(colorHex);
        if (color == null && fallbackColorRes != 0) {
            color = ContextCompat.getColor(view.getContext(), fallbackColorRes);
        }
        if (color == null) {
            return;
        }
        if (view.getBackground() != null) {
            DrawableCompat.setTint(DrawableCompat.wrap(view.getBackground().mutate()), color);
        } else {
            view.setBackgroundColor(color);
        }
    }

    private static Integer parseAdColor(String colorHex) {
        if (colorHex == null || colorHex.trim().isEmpty()) {
            return null;
        }
        try {
            String value = colorHex.trim();
            if (!value.startsWith("#")) {
                value = "#" + value;
            }
            return Color.parseColor(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String getQuizListItem(@Nullable List<String> list, int index) {
        if (list == null || list.isEmpty() || index < 0 || index >= list.size()) {
            return "";
        }
        return list.get(index);
    }

    private static void loadQuizIconImage(@Nullable View root, @Nullable AppCompatImageView imageView, int index) {
        loadQuizIconImage(root, imageView, index, false, null);
    }

    private static void loadQuizIconImage(@Nullable View root, @Nullable AppCompatImageView imageView, int index,
                                          boolean showFallbackOnError) {
        loadQuizIconImage(root, imageView, index, showFallbackOnError, null);
    }

    private static void loadQuizIconImage(@Nullable View root, @Nullable AppCompatImageView imageView, int index,
                                          boolean showFallbackOnError, @Nullable Runnable onFinished) {
        loadQuizShimmerImage(root, imageView, quizAppIconList, index, R.id.qzShimmerIcon, showFallbackOnError, onFinished);
    }

    private static void loadQuizMediaImage(@Nullable View root, @Nullable AppCompatImageView imageView, @Nullable List<String> urls, int index) {
        loadQuizMediaImage(root, imageView, urls, index, true);
    }

    private static void loadQuizMediaImage(@Nullable View root, @Nullable AppCompatImageView imageView,
                                           @Nullable List<String> urls, int index, boolean showFallbackOnError) {
        loadQuizShimmerImage(root, imageView, urls, index, R.id.qzShimmer, showFallbackOnError, null);
    }

    private static void loadQuizShimmerImage(@Nullable View root, @Nullable AppCompatImageView imageView,
                                             @Nullable List<String> urls, int index, int shimmerId) {
        loadQuizShimmerImage(root, imageView, urls, index, shimmerId, true, null);
    }

    private static void loadQuizShimmerImage(@Nullable View root, @Nullable AppCompatImageView imageView,
                                             @Nullable List<String> urls, int index, int shimmerId,
                                             boolean showFallbackOnError) {
        loadQuizShimmerImage(root, imageView, urls, index, shimmerId, showFallbackOnError, null);
    }

    private static void loadQuizShimmerImage(@Nullable View root, @Nullable AppCompatImageView imageView,
                                             @Nullable List<String> urls, int index, int shimmerId,
                                             boolean showFallbackOnError, @Nullable Runnable onFinished) {
        if (imageView == null) {
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }
        ShimmerFrameLayout shimmer = root == null ? null : root.findViewById(shimmerId);
        String imageUrl = getQuizListItem(urls, index).trim();
        if (imageUrl.isEmpty()) {
            if (showFallbackOnError) {
                stopQuizShimmer(shimmer);
                imageView.setImageResource(QUIZ_IMAGE_FALLBACK);
                imageView.setVisibility(View.VISIBLE);
            } else {
                startQuizShimmer(shimmer, imageView);
            }
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }
        startQuizShimmer(shimmer, imageView);
        imageView.setTag(imageUrl);
        QUIZ_IMAGE_EXECUTOR.execute(() -> {
            Bitmap bitmap = downloadQuizBitmap(imageUrl);
            new Handler(Looper.getMainLooper()).post(() -> {
                Object tag = imageView.getTag();
                if (!(tag instanceof String) || !imageUrl.equals(tag)) {
                    return;
                }
                if (bitmap != null) {
                    finishQuizShimmerLoadSuccess(shimmer, imageView, bitmap);
                } else {
                    finishQuizShimmerLoadFailure(shimmer, imageView, showFallbackOnError);
                }
                if (onFinished != null) {
                    onFinished.run();
                }
            });
        });
    }

    private static void startQuizShimmer(@Nullable ShimmerFrameLayout shimmer, @Nullable AppCompatImageView imageView) {
        if (imageView != null) {
            imageView.animate().cancel();
            imageView.setAlpha(1f);
            imageView.setScaleX(1f);
            imageView.setScaleY(1f);
            imageView.setVisibility(View.GONE);
        }
        if (shimmer == null) {
            return;
        }
        shimmer.setVisibility(View.VISIBLE);
        shimmer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        shimmer.post(shimmer::startShimmer);
    }

    private static void stopQuizShimmer(@Nullable ShimmerFrameLayout shimmer) {
        if (shimmer == null) {
            return;
        }
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
    }

    private static void revealQuizLoadedImage(@NonNull AppCompatImageView imageView, @NonNull Runnable applyImage) {
        imageView.animate().cancel();
        applyImage.run();
        imageView.setAlpha(0f);
        imageView.setScaleX(QUIZ_IMAGE_REVEAL_START_SCALE);
        imageView.setScaleY(QUIZ_IMAGE_REVEAL_START_SCALE);
        imageView.setVisibility(View.VISIBLE);
        imageView.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(QUIZ_IMAGE_REVEAL_DURATION_MS).start();
    }

    private static void finishQuizShimmerLoadSuccess(@Nullable ShimmerFrameLayout shimmer,
                                                     @NonNull AppCompatImageView imageView,
                                                     @NonNull Bitmap bitmap) {
        stopQuizShimmer(shimmer);
        revealQuizLoadedImage(imageView, () -> imageView.setImageBitmap(bitmap));
    }

    private static void finishQuizShimmerLoadFailure(@Nullable ShimmerFrameLayout shimmer,
                                                     @NonNull AppCompatImageView imageView) {
        finishQuizShimmerLoadFailure(shimmer, imageView, true);
    }

    private static void finishQuizShimmerLoadFailure(@Nullable ShimmerFrameLayout shimmer,
                                                     @NonNull AppCompatImageView imageView,
                                                     boolean showFallbackOnError) {
        if (showFallbackOnError) {
            stopQuizShimmer(shimmer);
            imageView.setImageResource(QUIZ_IMAGE_FALLBACK);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    private static Bitmap downloadQuizBitmap(@NonNull String imageUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void openQuizLink(Activity activity, String link) {
        if (activity == null || link == null || link.trim().isEmpty()) {
            return;
        }
        try {
            if (getIsLinkOpenApp()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Bundle bundle = new Bundle();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    bundle.putBinder("android.support.customtabs.extra.SESSION", (IBinder) null);
                }
                intent.putExtras(bundle);
                intent.putExtra(
                        "android.support.customtabs.extra.TOOLBAR_COLOR",
                        ContextCompat.getColor(activity, R.color.calendar_primary));
                intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                intent.setData(Uri.parse(link.trim()));
                activity.startActivity(intent);
            } else {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(activity, R.color.calendar_primary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                customTabsIntent.launchUrl(activity, Uri.parse(link.trim()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hideShimmer(@Nullable ShimmerFrameLayout shimmer) {
        if (shimmer == null) {
            return;
        }
        if (shimmer.isShimmerStarted()) {
            shimmer.stopShimmer();
        }
        shimmer.setVisibility(View.GONE);
    }

    /**
     * Native Ad loading shimmer for Quiz priority Banner (Ad_Priority=QUIZ).
     */
    /** Set as a ShimmerFrameLayout tag to keep that screen's own (big) shimmer for the Quiz Banner. */
    public static final String KEEP_BIG_SHIMMER_TAG = "keep_big_shimmer";

    private static void showNativeAdLoadingShimmer(@Nullable ShimmerFrameLayout shimmer) {
        if (shimmer == null) {
            return;
        }
        // Only a shimmer explicitly tagged by its screen (charging screen) keeps its big shimmer;
        // every other container is still switched to the small native shimmer.
        boolean keepBigShimmer = KEEP_BIG_SHIMMER_TAG.equals(shimmer.getTag());
        if (!keepBigShimmer
                && (shimmer.findViewById(R.id.banner_shimmer_root) != null
                || shimmer.findViewById(R.id.shimmer_app_icon) == null)) {
            shimmer.removeAllViews();
            View.inflate(shimmer.getContext(), R.layout.fill_in_ad_unifiled_small, shimmer);
        }
        shimmer.setVisibility(View.VISIBLE);
        shimmer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        shimmer.post(shimmer::startShimmer);
    }

    /**
     * Banner loading shimmer for Google Banner (and Google-fail → Quiz fallback).
     */
    private static void showBannerAdLoadingShimmer(@Nullable ShimmerFrameLayout shimmer) {
        if (shimmer == null) {
            return;
        }
        if (shimmer.findViewById(R.id.banner_shimmer_root) == null) {
            shimmer.removeAllViews();
            View.inflate(shimmer.getContext(), R.layout.fill_in_banner_shimmer, shimmer);
        }
        shimmer.setVisibility(View.VISIBLE);
        shimmer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        shimmer.post(shimmer::startShimmer);
    }

    // region Banner / Native

    /**
     * Shows Quiz Banner. Shimmer type follows Ad_Priority when not specified:
     * QUIZ → Native shimmer, otherwise Banner shimmer (e.g. Google fail fallback).
     */
    public static boolean showQuizBannerAd(@Nullable Activity activity,
                                           @Nullable FrameLayout adContainer,
                                           @Nullable ShimmerFrameLayout shimmer) {
        return showQuizBannerAd(activity, adContainer, shimmer, shouldUseQuizPriority());
    }

    /**
     * @param useNativeShimmer true = Native shimmer (QUIZ + BANNER);
     *                         false = Banner shimmer (GOOGLE + BANNER, including Quiz fallback)
     */
    public static boolean showQuizBannerAd(@Nullable Activity activity,
                                           @Nullable FrameLayout adContainer,
                                           @Nullable ShimmerFrameLayout shimmer,
                                           boolean useNativeShimmer) {
        // Charging screen only (tagged shimmer): the Quiz Banner is shown as the big Quiz ad.
        if (shimmer != null && KEEP_BIG_SHIMMER_TAG.equals(shimmer.getTag())) {
            return showQuizNativeAd(activity, adContainer, shimmer, "big");
        }
        try {
            int itemCount = getQuizSyncedItemCount();
            if (itemCount <= 0 || activity == null || adContainer == null) {
                return false;
            }

            if (useNativeShimmer) {
                showNativeAdLoadingShimmer(shimmer);
            } else {
                showBannerAdLoadingShimmer(shimmer);
            }
            ADSNativeDisplay.clearNativeAdViewsFromContainer(adContainer);

            View view = LayoutInflater.from(activity).inflate(R.layout.qz_banner_ad, adContainer, false);
            AppCompatImageView ivQZAppIcon = view.findViewById(R.id.ivQZAppIcon);
            AppCompatTextView tvQZAppTitle = view.findViewById(R.id.tvQZAppTitle);
            AppCompatTextView tvQZAppDescription = view.findViewById(R.id.tvQZAppDescription);
            AppCompatTextView btnQZClick = view.findViewById(R.id.btnQZClick);
            if (ivQZAppIcon == null || tvQZAppTitle == null || tvQZAppDescription == null || btnQZClick == null) {
                hideShimmer(shimmer);
                return false;
            }

            int index = pickQuizSyncedIndex(itemCount);
            tvQZAppTitle.setText(quizBannerTitleList.get(index));
            tvQZAppDescription.setText(quizBannerDescriptionList.get(index));
            applyQuizButtonText(btnQZClick);
            applyQuizAdColors(view);

            View.OnClickListener clickListener = v -> openQuizLink(activity, pickRandomQuizLink());
            view.setOnClickListener(clickListener);
            btnQZClick.setOnClickListener(clickListener);

            adContainer.addView(view);
            // Keep Quiz Banner hidden until shimmer loading finishes (icon ready)
            adContainer.setVisibility(View.GONE);
            final Runnable revealQuizBanner = () -> {
                hideShimmer(shimmer);
                adContainer.setVisibility(View.VISIBLE);
            };
            loadQuizIconImage(view, ivQZAppIcon, index, false, revealQuizBanner);
            return true;
        } catch (Exception e) {
            hideShimmer(shimmer);
            return false;
        }
    }

    public static boolean showQuizNativeAd(@Nullable Activity activity,
                                           @Nullable FrameLayout adContainer,
                                           @Nullable ShimmerFrameLayout shimmer,
                                           @Nullable String type) {
        try {
            int itemCount = getQuizSyncedItemCount();
            if (itemCount <= 0 || activity == null || adContainer == null) {
                return false;
            }

            // Full Native Ad shimmer for initial load and auto-refresh (not logo-only).
            showNativeAdLoadingShimmer(shimmer);
            ADSNativeDisplay.clearNativeAdViewsFromContainer(adContainer);

            View view = LayoutInflater.from(activity).inflate(resolveQuizNativeLayout(type), adContainer, false);
            int index = pickQuizSyncedIndex(itemCount);
            if (!canBindQuizNativeContent(view, index)) {
                hideShimmer(shimmer);
                return false;
            }

            adContainer.addView(view);
            // Keep Quiz Native hidden until shimmer loading finishes (icon ready).
            adContainer.setVisibility(View.GONE);
            final Runnable revealQuizNative = () -> {
                hideShimmer(shimmer);
                adContainer.setVisibility(View.VISIBLE);
            };
            bindQuizNativeContent(activity, view, index, type, revealQuizNative);
            return true;
        } catch (Exception e) {
            hideShimmer(shimmer);
            return false;
        }
    }

    private static int resolveQuizNativeLayout(@Nullable String type) {

        if (type != null && (type.equalsIgnoreCase("big") || type.equalsIgnoreCase("large"))) {
            return R.layout.qz_native_large_ad;
        }
        if (type != null && type.equalsIgnoreCase("card")) {
            return R.layout.qz_native_large_card_ad;
        }
        // RecyclerView list native only — matches admob_small_native_ad2 look
        if (type != null && (type.equalsIgnoreCase("list") || type.equalsIgnoreCase("small2"))) {
            return R.layout.qz_native_small_ad2;
        }
        return R.layout.qz_native_small_ad;
    }

    private static boolean isListQuizNativeType(@Nullable String type) {
        return type != null && (type.equalsIgnoreCase("list") || type.equalsIgnoreCase("small2"));
    }

    private static boolean canBindQuizNativeContent(@Nullable View view, int index) {
        if (view == null) {
            return false;
        }
        if (index < 0 || index >= getQuizSyncedItemCount()) {
            return false;
        }
        return view.findViewById(R.id.ivQZAppIcon) != null
                && view.findViewById(R.id.tvQZAppTitle) != null
                && view.findViewById(R.id.tvQZAppDescription) != null
                && view.findViewById(R.id.btnQZClick) != null;
    }

    private static boolean bindQuizNativeContent(Activity activity, View view, int index, @Nullable String type) {
        return bindQuizNativeContent(activity, view, index, type, null);
    }

    private static boolean bindQuizNativeContent(Activity activity, View view, int index, @Nullable String type,
                                                 @Nullable Runnable onIconReady) {
        AppCompatImageView ivQZAppIcon = view.findViewById(R.id.ivQZAppIcon);
        AppCompatTextView tvQZAppTitle = view.findViewById(R.id.tvQZAppTitle);
        AppCompatTextView tvQZAppDescription = view.findViewById(R.id.tvQZAppDescription);
        AppCompatTextView btnQZClick = view.findViewById(R.id.btnQZClick);
        View mainBacs = view.findViewById(R.id.mainBacs);
        if (ivQZAppIcon == null || tvQZAppTitle == null || tvQZAppDescription == null || btnQZClick == null) {
            if (onIconReady != null) {
                onIconReady.run();
            }
            return false;
        }
        if (index < 0 || index >= getQuizSyncedItemCount()) {
            if (onIconReady != null) {
                onIconReady.run();
            }
            return false;
        }

        // Icon loads behind the full Native Ad shimmer; reveal when ready.
        loadQuizIconImage(view, ivQZAppIcon, index, false, onIconReady);
        tvQZAppTitle.setText(quizNativeTitleList.get(index));
        tvQZAppDescription.setText(quizNativeDescriptionList.get(index));
        applyQuizButtonText(btnQZClick);
        try {
            if (mainBacs != null) {
                if (isListQuizNativeType(type)) {
                    // Full-bleed list style: flat bg, 0dp radius (same as Google list native)
                    ADSNativeDisplay.applyListNativeAdBackground(mainBacs, activity);
                } else {
                    int backgroundColor;
                    backgroundColor = Color.parseColor(ADSMainClass.getLightNativeBackgroundColor());
                    int strokeWidth = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            1.7f,
                            activity.getResources().getDisplayMetrics()
                    );

                    float adCornerRadiusPx = activity.getResources().getDimension(R.dimen.onboarding_dot_size);
                    GradientDrawable adBackground = new GradientDrawable();
                    adBackground.setShape(GradientDrawable.RECTANGLE);
                    adBackground.setColor(backgroundColor);
                    adBackground.setCornerRadius(adCornerRadiusPx);
                    adBackground.setStroke(strokeWidth, Color.parseColor("#CCE6E6E6"));
                    mainBacs.setBackground(adBackground);
                }
            }
        } catch (Exception e) {

        }
        View.OnClickListener clickListener = v -> openQuizLink(activity, pickRandomQuizLink());
        view.setOnClickListener(clickListener);
        btnQZClick.setOnClickListener(clickListener);

        View mediaView = view.findViewById(R.id.ivQZAppMedia);
        if (mediaView instanceof AppCompatImageView) {
            if (type != null && (type.equalsIgnoreCase("big")
                    || type.equalsIgnoreCase("large")
                    || type.equalsIgnoreCase("card"))) {
                loadQuizMediaImage(view, (AppCompatImageView) mediaView, quizNativeMediaList, index);
            } else {
                mediaView.setVisibility(View.GONE);
            }
        }

        applyQuizAdColors(view);
        return true;
    }

    public static boolean tryShowQuizBannerOnGoogleFail(@Nullable Activity activity,
                                                        @Nullable FrameLayout adContainer,
                                                        @Nullable ShimmerFrameLayout shimmer) {
        // GOOGLE + BANNER fail → Quiz fallback still uses Banner shimmer
        return getGoogleAdFailedShowQuiz() && showQuizBannerAd(activity, adContainer, shimmer, false);
    }

    public static boolean tryShowQuizNativeOnGoogleFail(@Nullable Activity activity,
                                                        @Nullable FrameLayout adContainer,
                                                        @Nullable ShimmerFrameLayout shimmer,
                                                        @Nullable String type) {
        return getGoogleAdFailedShowQuiz() && showQuizNativeAd(activity, adContainer, shimmer, type);
    }

    // endregion

    // region Interstitial / App Open

    public static boolean showQuizInterstitialOrBrowserAd(@Nullable Activity activity, @Nullable Runnable onComplete) {
        if (getIsQuizBrowserShow() && openQuizBrowserDirect(activity, onComplete)) {
            return true;
        }
        return showQuizInterstitialAd(activity, onComplete)
                || showQuizAppOpenAd(activity, onComplete);
    }

    private static boolean openQuizBrowserDirect(@Nullable Activity activity, @Nullable Runnable onComplete) {
        try {
            if (activity == null || activity.isFinishing()) {
                return false;
            }
            String link = pickRandomQuizLink();
            if (link == null || link.trim().isEmpty()) {
                return false;
            }
            openQuizLink(activity, link);
            if (onComplete != null) {
                onComplete.run();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean showQuizInterstitialAd(@Nullable Activity activity, @Nullable Runnable onComplete) {
        AtomicBoolean completed = new AtomicBoolean(false);
        return showQuizInterstitialAd(activity, onComplete, completed);
    }

    @SuppressLint("InflateParams")
    private static boolean showQuizInterstitialAd(@Nullable Activity activity,
                                                  @Nullable Runnable onComplete,
                                                  @NonNull AtomicBoolean completed) {
        try {
            int itemCount = getQuizSyncedItemCount();
            if (itemCount <= 0 || activity == null || activity.isFinishing()) {
                return false;
            }

            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View contentView = LayoutInflater.from(activity).inflate(R.layout.qz_interstitial_ad, null);
            dialog.setContentView(contentView);
            dialog.setCancelable(false);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            AppCompatTextView tvQZTimer = contentView.findViewById(R.id.tvQZTimer);
            AppCompatImageView ivQZClose = contentView.findViewById(R.id.ivQZClose);
            AppCompatImageView ivQZAppMedia = contentView.findViewById(R.id.ivQZAppMedia);
            AppCompatImageView ivQZAppIcon = contentView.findViewById(R.id.ivQZAppIcon);
            AppCompatTextView tvQZAppTitle = contentView.findViewById(R.id.tvQZAppTitle);
            AppCompatTextView tvQZAppDescription = contentView.findViewById(R.id.tvQZAppDescription);
            AppCompatTextView tvQZRate = contentView.findViewById(R.id.tvQZRate);
            AppCompatRatingBar rbQZRating = contentView.findViewById(R.id.rbQZRating);
            AppCompatTextView tvQZUser = contentView.findViewById(R.id.tvQZUser);
            AppCompatTextView btnQZClose = contentView.findViewById(R.id.btnQZClose);
            AppCompatTextView btnQZClick = contentView.findViewById(R.id.btnQZClick);
            if (ivQZAppMedia == null || ivQZAppIcon == null || tvQZAppTitle == null || tvQZAppDescription == null
                    || tvQZRate == null || rbQZRating == null || tvQZUser == null || tvQZTimer == null
                    || ivQZClose == null || btnQZClose == null || btnQZClick == null) {
                return false;
            }

            int index = pickQuizSyncedIndex(itemCount);
            if (getQuizListItem(quizInterstitialMediaList, index).trim().isEmpty()) {
                return false;
            }
            loadQuizMediaImage(contentView, ivQZAppMedia, quizInterstitialMediaList, index, false);
            loadQuizIconImage(contentView, ivQZAppIcon, index);
            tvQZAppTitle.setText(quizInterstitialTitleList.get(index));
            tvQZAppDescription.setText(quizInterstitialDescriptionList.get(index));
            bindQuizInterstitialRating(tvQZRate, rbQZRating, index);
            tvQZUser.setText(QUIZ_INTERSTITIAL_USERS[index % QUIZ_INTERSTITIAL_USERS.length]);
            applyQuizButtonText(btnQZClick);
            applyQuizAdColors(contentView);

            ivQZClose.setVisibility(View.GONE);
            setQuizInterstitialCloseButtonState(btnQZClose, false);

            Runnable dismissAndComplete = () -> {
                if (dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception ignored) {
                    }
                }
                notifyCompleteOnce(onComplete, completed);
            };

            ivQZClose.setOnClickListener(v -> dismissAndComplete.run());
            contentView.setOnClickListener(v -> openQuizLink(activity, pickRandomQuizLink()));
            btnQZClose.setOnClickListener(v -> {
                if (!btnQZClose.isEnabled()) {
                    return;
                }
                dismissAndComplete.run();
            });
            btnQZClick.setOnClickListener(v -> openQuizLink(activity, pickRandomQuizLink()));

            // No countdown for the Quiz Inter Ad: the close controls are available immediately.
            tvQZTimer.setVisibility(View.GONE);
            ivQZClose.setVisibility(View.VISIBLE);
            setQuizInterstitialCloseButtonState(btnQZClose, true);

            dialog.show();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean showQuizFullscreenNativeAd(@Nullable Activity activity, @Nullable Runnable onComplete) {
        AtomicBoolean completed = new AtomicBoolean(false);
        return showQuizFullscreenNativeAd(activity, onComplete, completed);
    }

    @SuppressLint("InflateParams")
    private static boolean showQuizFullscreenNativeAd(@Nullable Activity activity,
                                                      @Nullable Runnable onComplete,
                                                      @NonNull AtomicBoolean completed) {
        try {
            int itemCount = getQuizSyncedItemCount();
            if (itemCount <= 0 || activity == null || activity.isFinishing()) {
                return false;
            }

            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View contentView = LayoutInflater.from(activity).inflate(R.layout.qz_native_full_ad, null);
            dialog.setContentView(contentView);
            dialog.setCancelable(false);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            }

            AppCompatImageView ivClose = contentView.findViewById(R.id.ivClose);
            AppCompatImageView ivQZAppMedia = contentView.findViewById(R.id.ivQZAppMedia);
            AppCompatImageView ivQZAppIcon = contentView.findViewById(R.id.ivQZAppIcon);
            AppCompatTextView tvQZAppTitle = contentView.findViewById(R.id.tvQZAppTitle);
            AppCompatTextView tvQZAppDescription = contentView.findViewById(R.id.tvQZAppDescription);
            AppCompatTextView btnQZClick = contentView.findViewById(R.id.btnQZClick);
            if (ivClose == null || ivQZAppMedia == null || ivQZAppIcon == null
                    || tvQZAppTitle == null || tvQZAppDescription == null || btnQZClick == null) {
                return false;
            }

            int index = pickQuizSyncedIndex(itemCount);
            loadQuizMediaImage(contentView, ivQZAppMedia, quizNativeMediaList, index);
            loadQuizIconImage(contentView, ivQZAppIcon, index);
            tvQZAppTitle.setText(quizNativeTitleList.get(index));
            tvQZAppDescription.setText(quizNativeDescriptionList.get(index));
            applyQuizButtonText(btnQZClick);
            applyQuizAdColors(contentView);

            ivClose.setVisibility(View.VISIBLE);
            ivClose.bringToFront();
            ivClose.setClickable(true);
            ivClose.setFocusable(true);

            Runnable dismissAndComplete = () -> {
                if (dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception ignored) {
                    }
                }
                notifyCompleteOnce(onComplete, completed);
            };

            ivClose.setOnClickListener(v -> dismissAndComplete.run());
            btnQZClick.setOnClickListener(v -> openQuizLink(activity, pickRandomQuizLink()));
            ivQZAppMedia.setOnClickListener(v -> openQuizLink(activity, pickRandomQuizLink()));

            dialog.show();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean showQuizAppOpenAd(@Nullable Activity activity, @Nullable Runnable onComplete) {
        AtomicBoolean completed = new AtomicBoolean(false);
        return showQuizAppOpenAd(activity, onComplete, completed);
    }

    @SuppressLint("InflateParams")
    private static boolean showQuizAppOpenAd(@Nullable Activity activity,
                                             @Nullable Runnable onComplete,
                                             @NonNull AtomicBoolean completed) {
        try {
            int itemCount = getQuizSyncedItemCount();
            if (itemCount <= 0 || activity == null || activity.isFinishing()) {
                return false;
            }

            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View contentView = LayoutInflater.from(activity).inflate(R.layout.qz_app_open_ad, null);
            dialog.setContentView(contentView);
            dialog.setCancelable(false);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            View llQZClose = contentView.findViewById(R.id.llQZClose);
            AppCompatImageView ivQZAppIcon = contentView.findViewById(R.id.ivQZAppIcon);
            AppCompatImageView ivQZAppMedia = contentView.findViewById(R.id.ivQZAppMedia);
            AppCompatTextView btnQZClick = contentView.findViewById(R.id.btnQZClick);
            if (llQZClose == null || ivQZAppIcon == null || ivQZAppMedia == null || btnQZClick == null) {
                return false;
            }

            int index = pickQuizSyncedIndex(itemCount);
            loadQuizIconImage(contentView, ivQZAppIcon, index);
            loadQuizMediaImage(contentView, ivQZAppMedia, quizAppOpenMediaList, index);
            applyQuizButtonText(btnQZClick);
            applyQuizAdColors(contentView);

            Runnable dismissAndComplete = () -> {
                if (dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception ignored) {
                    }
                }
                notifyCompleteOnce(onComplete, completed);
            };

            llQZClose.setOnClickListener(v -> dismissAndComplete.run());
            ivQZAppMedia.setOnClickListener(v -> openQuizLink(activity, pickRandomQuizLink()));
            btnQZClick.setOnClickListener(v -> openQuizLink(activity, pickRandomQuizLink()));

            dialog.show();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean tryShowQuizInterstitialOnGoogleFail(@Nullable Activity activity, @Nullable Runnable onComplete) {
        return getGoogleAdFailedShowQuiz() && showQuizInterstitialAd(activity, onComplete);
    }

    public static boolean tryShowQuizAppOpenOnGoogleFail(@Nullable Activity activity, @Nullable Runnable onComplete) {
        return getGoogleAdFailedShowQuiz() && showQuizAppOpenAd(activity, onComplete);
    }

    private static void notifyCompleteOnce(@Nullable Runnable onComplete, @NonNull AtomicBoolean completed) {
        if (onComplete == null || !completed.compareAndSet(false, true)) {
            return;
        }
        onComplete.run();
    }

    private static float resolveQuizInterstitialRating(int index) {
        if (index >= 0 && index < quizInterstitialRateList.size()) {
            return clampQuizRating(parseQuizRatingValue(quizInterstitialRateList.get(index)));
        }
        if (index >= 0 && index < QUIZ_INTERSTITIAL_RATES.length) {
            return clampQuizRating(parseQuizRatingValue(QUIZ_INTERSTITIAL_RATES[index]));
        }
        return 4.5f;
    }

    private static float parseQuizRatingValue(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            return 4.5f;
        }
        try {
            return Float.parseFloat(value.trim());
        } catch (Exception ignored) {
            return 4.5f;
        }
    }

    private static float clampQuizRating(float rating) {
        return Math.max(0f, Math.min(5f, rating));
    }

    private static String formatQuizInterstitialRating(float rating) {
        float clampedRating = clampQuizRating(rating);
        return String.format(Locale.US, "%.1f", clampedRating);
    }

    private static void setQuizInterstitialCloseButtonState(AppCompatTextView btnQZClose, boolean enabled) {
        if (btnQZClose == null) {
            return;
        }
        Context context = btnQZClose.getContext();
        if (enabled) {
            btnQZClose.setEnabled(true);
            btnQZClose.setClickable(true);
            applyQuizCloseButtonBorderColor(btnQZClose, getNativeAdButtonColor(context), R.color.calendar_primary);
            Integer buttonColor = parseAdColor(getNativeAdButtonColor(context));
            if (buttonColor == null) {
                buttonColor = ContextCompat.getColor(context, R.color.calendar_primary);
            }
            btnQZClose.setTextColor(buttonColor);
            btnQZClose.animate().alpha(1f).setDuration(200L).start();
            return;
        }
        btnQZClose.setEnabled(false);
        btnQZClose.setClickable(false);
        btnQZClose.setAlpha(1f);
        applyQuizCloseButtonBorderColor(btnQZClose, "", R.color.text_secondary_light);
        btnQZClose.setTextColor(ContextCompat.getColor(context, R.color.text_secondary_light));
    }

    private static void applyQuizCloseButtonBorderColor(AppCompatTextView button, String colorHex, int fallbackColorRes) {
        if (button == null) {
            return;
        }
        Context context = button.getContext();
        Integer color = parseAdColor(colorHex);
        if (color == null && fallbackColorRes != 0) {
            color = ContextCompat.getColor(context, fallbackColorRes);
        }
        if (color == null) {
            return;
        }
        Drawable background = ContextCompat.getDrawable(context, R.drawable.custom_button_radius_border_100);
        if (background == null) {
            return;
        }
        background = background.mutate();
        ViewCompat.setBackgroundTintList(button, null);
        if (background instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) background;
            int strokeWidthResId = context.getResources().getIdentifier("_1sdp", "dimen", context.getPackageName());
            int strokeWidth = strokeWidthResId != 0
                    ? context.getResources().getDimensionPixelSize(strokeWidthResId)
                    : Math.round(context.getResources().getDisplayMetrics().density);
            drawable.setStroke(strokeWidth, color);
            drawable.setColor(Color.TRANSPARENT);
            button.setBackground(drawable);
            return;
        }
        button.setBackground(background);
    }

    private static void bindQuizInterstitialRating(AppCompatTextView tvQZRate, AppCompatRatingBar rbQZRating, int index) {
        float rating = resolveQuizInterstitialRating(index);
        tvQZRate.setText(formatQuizInterstitialRating(rating));
        rbQZRating.setMax(5);
        rbQZRating.setNumStars(5);
        rbQZRating.setStepSize(0.1f);
        rbQZRating.setIsIndicator(true);
        Drawable progressDrawable = rbQZRating.getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable = progressDrawable.mutate();
            DrawableCompat.setTintList(progressDrawable, null);
            rbQZRating.setProgressDrawable(progressDrawable);
        }
        rbQZRating.setRating(rating);
    }

    // endregion
}
