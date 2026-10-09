package com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ADSMainClass {

    private static Context appContext;

    public static void initAppContext(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }
    public static boolean shouldShowConsentOnLanguage() {
        return isConsentOnLanguage() || isConsentPendingOnLanguage();
    }
    private static final String ConsentScreenShowValue = "ConsentScreenShowValue";
    private static final String ConsentPendingOnLanguage = "ConsentPendingOnLanguage";
    public static final String CONSENT_SCREEN_SPLASH = "splash";
    public static final String CONSENT_SCREEN_LANGUAGE = "language";
    private static final String DefaultPermissionBottomAdsShow = "DefaultPermissionBottomAdsShow";
    private static final String DefaultPermissionAdsType = "DefaultPermissionAdsType";
    public static String DEFAULT_PERMISSION_SCREEN_BANNER = "default_permission_screen_banner";
    public static String DEFAULT_PERMISSION_SCREEN_NATIVE = "default_permission_screen_native";

    public static boolean getDefaultPermissionBottomAdsShow() {
        return ADSPrefManage().getBoolean(DefaultPermissionBottomAdsShow, true);
    }

    public static void setDefaultPermissionBottomAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(DefaultPermissionBottomAdsShow, value).apply();
    }

    public static String getDefaultPermissionAdsType() {
        return ADSPrefManage().getString(DefaultPermissionAdsType, "banner");
    }

    public static void setDefaultPermissionAdsType(String value) {
        ADSPrefManage().edit().putString(DefaultPermissionAdsType, value).apply();
    }
    public static String getConsentScreenShow() {
        return ADSPrefManage().getString(ConsentScreenShowValue, CONSENT_SCREEN_SPLASH);
    }

    public static void setConsentScreenShow(String value) {
        ADSPrefManage().edit().putString(ConsentScreenShowValue, value).apply();
    }

    public static boolean isConsentOnSplash() {
        return CONSENT_SCREEN_SPLASH.equalsIgnoreCase(getConsentScreenShow());
    }

    public static boolean isConsentOnLanguage() {
        return CONSENT_SCREEN_LANGUAGE.equalsIgnoreCase(getConsentScreenShow());
    }

    public static boolean isConsentPendingOnLanguage() {
        return ADSPrefManage().getBoolean(ConsentPendingOnLanguage, false);
    }

    public static void setConsentPendingOnLanguage(boolean value) {
        ADSPrefManage().edit().putBoolean(ConsentPendingOnLanguage, value).apply();
    }

    static Context getAppContext() {
        if (appContext != null) {
            return appContext;
        }
        ADSAppManage app = ADSAppManage.getApp();
        if (app != null) {
            appContext = app.getApplicationContext();
            return appContext;
        }

        return null;
    }

    private static final String Ads_Free = "Ads_Free";
    private static final String NativeByPage = "NativeByPage";
    private static final String AdsClick = "AdsClick";
    private static final String AdsBackClick = "AdsBackClick";
    private static final String SplashADType = "SplashADType";
    private static final String ExitAds = "ExitAds";
    private static final String PrivacyPolicy = "PrivacyPolicy";
    private static final String ComingSoon = "ComingSoon";
    private static final String DefaultLauncherShow = "DefaultLauncherShow";

    public static String CallEndShow5 = "CallEndShow5";
    public static String CallEndShow100 = "CallEndShow100";
    public static String CallEndShowEvent = "CallEndShowEvent";
    public static String CallRetention7Day = "CallRetention7Day";
    public static String CallRetention3Day = "CallRetention3Day";
    public static String CallRetention1Day = "CallRetention1Day";
    public static String CallEndShow50 = "CallEndShow50";
    public static String CallEndShow10 = "CallEndShow10";
    public static String CallEndShow1 = "CallEndShow1";

    public static final String LANGUAGE_CODE = "language_code";

    public static String lehfv_HomeRetention1Day = "Simple_HomeRetention1Day";
    public static String lehfv_HomeRetention3Day = "Simple_HomeRetention3Day";
    public static String lehfv_HomeRetention7Day = "Simple_HomeRetention7Day";

    public static String splash_banner_id = "splash_banner_id";
    public static String splash_native_id = "splash_native_id";

    public static String onboarding_banner_id = "onboarding_banner_id";
    public static String onboarding_native_id = "onboarding_native_id";

    public static String LANGUAGE_SCREEN_BANNER = "language_screen_banner";
    public static String LANGUAGE_SCREEN_NATIVE = "language_screen_native";
    public static String PERMISSION_SCREEN_BANNER = "permission_screen_banner";
    public static String PERMISSION_SCREEN_NATIVE = "permission_screen_native";
    public static String HOME_SCREEN_BANNER = "home_screen_banner";
    public static String HOME_SCREEN_NATIVE = "home_screen_native";
    public static String LAUNCHER_HOME_SCREEN_BANNER = "launcher_home_screen_banner";
    public static String LAUNCHER_HOME_SCREEN_NATIVE = "launcher_home_screen_native";
    public static String RECENT_LAUNCHER_SCREEN_BANNER = "recent_launcher_screen_banner";
    public static String RECENT_LAUNCHER_SCREEN_NATIVE = "recent_launcher_screen_native";

    public static String SETTING_SCREEN_BANNER = "setting_screen_banner";
    public static String SETTING_SCREEN_NATIVE = "setting_screen_native";
    public static String LAUNCHER_SETTING_SCREEN_BANNER = "launcher_setting_screen_banner";
    public static String LAUNCHER_SETTING_SCREEN_NATIVE = "launcher_setting_screen_native";


    public static String OTHER_SCREEN_NATIVE = "other_screen_native";
    public static String OTHER_SCREEN_BANNER = "other_screen_banner";

    /** Per-activity Firebase ads prefixes (matches {prefix}_screen JSON blocks) */
    public static final String SCREEN_AGENDA = "agenda";
    public static final String SCREEN_EVENT_ADD = "event_add";
    public static final String SCREEN_EVENT_DETAIL = "event_detail";
    public static final String SCREEN_ADD_TASK = "add_task";
    public static final String SCREEN_TASK_DETAIL = "task_detail";
    public static final String SCREEN_ADD_MEMO = "add_memo";
    public static final String SCREEN_MEMO_DETAILS = "memo_details";
    public static final String SCREEN_CUSTOM_EVENT_LIST = "custom_event_list";
    public static final String SCREEN_ALARM = "alarm";
    public static final String SCREEN_RINGTONE_SELECT = "ringtone_select";
    public static final String SCREEN_THEME = "theme";
    public static final String SCREEN_THEME_APPLY = "theme_apply";
    public static final String SCREEN_WIDGET = "widget";
    public static final String SCREEN_WIDGET_SETTING = "widget_setting";
    public static final String SCREEN_MAP_PICKER = "map_picker";
    public static final String SCREEN_SETTING = "setting";
    public static final String SCREEN_HOME = "home";

    public static String CALL_END_BANNER = "callend_banner_ad_id";
    public static String CALL_END_Native = "callend_native_ad_id";
    public static String CALL_END_Inter = "callend_inter_ad_id";
    //Test
    public static String APP_OPEN_ID = "app_open_id";
    public static String INTER_FIRST_TIME = "inter_first_time";
    public static String INTER_SECOND_TIME = "inter_second_time";

    //    public static String CHAT_BOX_BANNER = "chat_box_banner";
//    public static String OTHER_BANNER = "other_banner";
//    public static String PERMISSION_SCREEN_BANNER = "permission_screen_banner";
    public static String EXIT_SCREEN_NATIVE = "exit_screen_native";
    public static String OVERLAY_PERMISSION_NOTIFICATION_SHOW = "overlay_permission_notification_show";
    public static String OVERLAY_PERMISSION_NOTIFICATION_DAYS_SHOW_COUNT = "overlay_permission_notification_days_show_count";
    public static String LAST_OPEN_DATE = "last_open_date";
    public static String CONSECUTIVE_DAYS_COUNT = "consecutive_days_count";
    public static String NOTIFICATION_INSTALL_DAYS = "notification_install_days";
    public static String NOTIFICATION_CALL_INSTALL_DAYS = "notification_call_install_days";
    public static String NOTIFICATION_CALL_OVERLAY_INSTALL_DAYS = "notification_call_overlay_install_days";
    public static String NOTIFICATION_COUNTRY = "notification_country";
    public static String NOTIFICATION_CALL_COUNTRY = "notification_call_country";
    public static String NOTIFICATION_CALL_OVERLAY_COUNTRY = "notification_call_overlay_country";
    public static String ALL_ALLOW_PERMISSION_SHOW_FB_NOTIFICATION = "All_allow_permission_show_fb_notification";
    public static String CLOSE_BUTTON_SHOW_ON_FULL_NATIVE_ADS = "close_button_show_on_full_native_ads";

    private static final String SplashAppOpenShow = "SplashAppOpenShow";
    private static final String SplashBottomAdsShow = "SplashBottomAdsShow";
    private static final String splash_ads_type = "splash_ads_type";
    private static final String SplashAppOpenAfterCount = "SplashAppOpenAfterCount";
    private static final String SplashAppOpenVisitCount = "SplashAppOpenVisitCount";

    private static final String AppOpenBackgroundShow = "AppOpenBackgroundShow";

    public static String call_end_last_ad_date = "call_end_last_ad_date";
    public static String call_end_ad_count_today = "call_end_ad_count_today";
    public static String call_end_last_ad_timestamp = "call_end_last_ad_timestamp";
    public static String update_last_show_date = "update_last_show_date";
    public static String update_show_count_today = "update_show_count_today";

    public static String IsShowCallEnd = "is_call_end_show";
    public static String CALL_END_INTER_DAY_COUNT = "call_end_inter_day_count";
    public static String CALL_END_INTER_SHOW_COUNT = "call_end_inter_show_count";
    public static String CALL_END_AGAIN_OPEN_COUNT_VALUE = "call_end_again_open_count_value";
    private static final String InterAdsShow = "InterAdsShow";
    private static final String InterAdsOnBackShow = "InterAdsOnBackShow";
    public static String APP_INSTALL_DATE = "app_install_date";

    private static final String SplashToLanguage = "SplashToLanguage";
    private static final String LanguageScreenBottomAdShow = "LanguageScreenBottomAdShow";
    private static final String LanguageInterAdsShow = "LanguageInterAdsShow";
    private static final String LanguageAdsType = "LanguageAdsType";
    private static final String OnboardingScreenBottomAdShow = "OnboardingScreenBottomAdShow";
    private static final String OnboardingInterAdsShow = "OnboardingInterAdsShow";
    private static final String onboardingCountShow = "onboardingCountShow";
    private static final String OnboardingAdsType = "OnboardingAdsType";
    private static final String HomeScreenAdsType = "HomeScreenAdsType";
    private static final String PermissionSmallAdsShow = "PermissionSmallAdsShow";
    private static final String PermissionAdsType = "PermissionAdsType";
    private static final String DefaultPermissionButtonAdsShow = "DefaultPermissionButtonAdsShow";
    private static final String DefaultPermissionButtonAdsType = "DefaultPermissionButtonAdsType";
    private static final String HomeScreenBottomShow = "HomeScreenBottomShow";
    private static final String HomeScreenBottomAdsRefresh = "HomeScreenBottomAdsRefresh";
    private static final String HomeScreenBottomAdsRefreshTime = "HomeScreenBottomAdsRefreshTime";
    private static final String HomeBottomNavInterShow = "HomeBottomNavInterShow";
    private static final String HomeBottomCount = "HomeBottomCount";
    private static final String CustomAfterHourShow = "CustomAfterHourShow";
    private static final String HOME_CUSTOM_DIALOG_PREFS = "home_custom_dialog_preferences";
    private static final String HOME_CUSTOM_DIALOG_LAST_TIME = "home_custom_dialog_last_time";
    private static final String HOME_CUSTOM_DIALOG_COUNTER = "home_custom_dialog_counter";
    /** One show per process/app open — clears only when app process restarts. */
    private static boolean homeCustomDialogShownThisSession = false;
    private static final String LauncherHomeSwipeInterShow = "LauncherHomeSwipeInterShow";
    private static final String LauncherHomeSwipeInterCount = "LauncherHomeSwipeInterCount";
    private static final String LauncherHomeAdsType = "LauncherHomeAdsType";
    private static final String RecentLauncherBottomAdsShow = "RecentLauncherBottomAdsShow";
    private static final String RecentLauncherAdsType = "RecentLauncherAdsType";
    private static final String LauncherBottomDrawerAdsShow = "LauncherBottomDrawerAdsShow";
    private static final String LAUNCHER_DRAWER_AD_WINDOW_START = "launcher_drawer_ad_window_start";
    private static final String LAUNCHER_DRAWER_AD_PREFS = "launcher_drawer_ad_preferences";
    private static final String SettingBottomAdsShow = "SettingBottomAdsShow";
    private static final String SettingAdsType = "SettingAdsType";
    public static String CallEndBottomAdsShow = "CallEndBottomAdsShow";
    public static String CallEndInterAdsShow = "CallEndInterAdsShow";
    public static String CallEndBottomAdsType = "CallEndBottomAdsType";
    public static String OtherAdsType = "OtherAdsType";
    public static String OtherAdsShow = "OtherAdsShow";
    public static String InterAdsLoadType = "InterAdsLoadType";
    public static String SplashInterAdsLoadType = "SplashInterAdsLoadType";
    public static String NativeAdsLoadType = "NativeAdsLoadType";
    public static String BannerAdsLoadType = "BannerAdsLoadType";
    public static String AppOpenAdsLoadType = "AppOpenAdsLoadType";
    public static String CALL_END_INTER_ADS_SHOW_COUNTRIES = "call_end_inter_ads_show_country";
    public static String CALL_END_INTER_ADS_TYPE = "call_end_inter_ads_type";
    public static String NOTIFICATION_SCREEN_BACK_ADS_SHOW = "notification_screen_back_ads_show";
    public static String LAUNCHER_SETTING_BACK_ADS_SHOW = "launcher_setting_back_ads_show";
    private static final String LauncherSettingBottomAdsShow = "LauncherSettingBottomAdsShow";
    private static final String LauncherSettingAdsType = "LauncherSettingAdsType";
    public static String IP_COUNTRY_NAME = "ip_country_name";
    public static String COUNTRY_GET_WITH_IP = "country_get_with_ip";

    public static final String LIGHT_NATIVE_BUTTON_COLOR = "LIGHT_NATIVE_BUTTON_COLOR";
    public static final String LIGHT_NATIVE_BACKGROUND_COLOR = "LIGHT_NATIVE_BACKGROUND_COLOR";
    public static final String LIGHT_NATIVE_ALL_TEXT_COLOR = "LIGHT_NATIVE_ALL_TEXT_COLOR";
    public static final String LIGHT_NATIVE_BUTTON_TEXT_COLOR = "LIGHT_NATIVE_BUTTON_TEXT_COLOR";

    public static final String DARK_NATIVE_BUTTON_COLOR = "DARK_NATIVE_BUTTON_COLOR";
    public static final String SetsReferrerUrl = "SetsReferrerUrl";
    public static final String DARK_NATIVE_BACKGROUND_COLOR = "DARK_NATIVE_BACKGROUND_COLOR";
    public static final String DARK_NATIVE_ALL_TEXT_COLOR = "DARK_NATIVE_ALL_TEXT_COLOR";
    public static final String DARK_NATIVE_BUTTON_TEXT_COLOR = "DARK_NATIVE_BUTTON_TEXT_COLOR";
    public static final String Button_Margins_ads = "Button_Margins_ads";

    public static boolean getOnboardingScreenBottomAdShow() {
        return ADSPrefManage().getBoolean(OnboardingScreenBottomAdShow, true);
    }

    public static void setOnboardingScreenBottomAdShow(boolean value) {
        ADSPrefManage().edit().putBoolean(OnboardingScreenBottomAdShow, value).apply();
    }

    public static boolean getOnboardingInterAdsShow() {
        return ADSPrefManage().getBoolean(OnboardingInterAdsShow, false);
    }

    public static void setOnboardingInterAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(OnboardingInterAdsShow, value).apply();
    }
    public static int getOnboardingCountShow() {
        return ADSPrefManage().getInt(onboardingCountShow, 0);
    }

    public static void setOnboardingCountShow(int value) {
        ADSPrefManage().edit().putInt(onboardingCountShow, value).apply();
    }


    public static String getOnboardingAdsType() {
        return ADSPrefManage().getString(OnboardingAdsType, "native");
    }

    public static void setOnboardingAdsType(String value) {
        ADSPrefManage().edit().putString(OnboardingAdsType, value).apply();
    }

    public static String getButtonMargins() {
        return ADSPrefManage().getString(Button_Margins_ads, "");
    }

    public static void setButtonMargins(String value) {
        ADSPrefManage().edit().putString(Button_Margins_ads, value).apply();
    }

    public static String getLightNativeButtonColor() {
        return ADSPrefManage().getString(LIGHT_NATIVE_BUTTON_COLOR, "");
    }

    public static void setLightNativeButtonColor(String value) {
        ADSPrefManage().edit().putString(LIGHT_NATIVE_BUTTON_COLOR, value).apply();
    }

    // Light Native Background Color
    public static String getLightNativeBackgroundColor() {
        return ADSPrefManage().getString(LIGHT_NATIVE_BACKGROUND_COLOR, "");
    }

    public static void setLightNativeBackgroundColor(String value) {
        ADSPrefManage().edit().putString(LIGHT_NATIVE_BACKGROUND_COLOR, value).apply();
    }

    // Light Native All Text Color
    public static String getLightNativeAllTextColor() {
        return ADSPrefManage().getString(LIGHT_NATIVE_ALL_TEXT_COLOR, "");
    }

    public static void setLightNativeAllTextColor(String value) {
        ADSPrefManage().edit().putString(LIGHT_NATIVE_ALL_TEXT_COLOR, value).apply();
    }

    // Light Native Button Text Color
    public static String getLightNativeButtonTextColor() {
        return ADSPrefManage().getString(LIGHT_NATIVE_BUTTON_TEXT_COLOR, "");
    }

    public static void setLightNativeButtonTextColor(String value) {
        ADSPrefManage().edit().putString(LIGHT_NATIVE_BUTTON_TEXT_COLOR, value).apply();
    }

    // Dark Native Button Color
    public static String getDarkNativeButtonColor() {
        return ADSPrefManage().getString(DARK_NATIVE_BUTTON_COLOR, "");
    }

    public static void setDarkNativeButtonColor(String value) {
        ADSPrefManage().edit().putString(DARK_NATIVE_BUTTON_COLOR, value).apply();
    }
    private static volatile String referrerUrlCache;

    public static String getReferrerUrl() {
        String cached = referrerUrlCache;
        if (cached != null) {
            return cached;
        }
        String stored = ADSPrefManage().getString(SetsReferrerUrl, "");
        referrerUrlCache = stored == null ? "" : stored;
        return referrerUrlCache;
    }

    public static void setReferrerUrl(String value) {
        String safe = value == null ? "" : value;
        referrerUrlCache = safe;
        ADSPrefManage().edit().putString(SetsReferrerUrl, safe).apply();
    }

    // Dark Native Background Color
    public static String getDarkNativeBackgroundColor() {
        return ADSPrefManage().getString(DARK_NATIVE_BACKGROUND_COLOR, "");
    }

    public static void setDarkNativeBackgroundColor(String value) {
        ADSPrefManage().edit().putString(DARK_NATIVE_BACKGROUND_COLOR, value).apply();
    }

    // Dark Native All Text Color
    public static String getDarkNativeAllTextColor() {
        return ADSPrefManage().getString(DARK_NATIVE_ALL_TEXT_COLOR, "");
    }

    public static void setDarkNativeAllTextColor(String value) {
        ADSPrefManage().edit().putString(DARK_NATIVE_ALL_TEXT_COLOR, value).apply();
    }

    // Dark Native Button Text Color
    public static String getDarkNativeButtonTextColor() {
        return ADSPrefManage().getString(DARK_NATIVE_BUTTON_TEXT_COLOR, "");
    }

    public static void setDarkNativeButtonTextColor(String value) {
        ADSPrefManage().edit().putString(DARK_NATIVE_BUTTON_TEXT_COLOR, value).apply();
    }

    public static String getOtherAdsType() {
        return ADSPrefManage().getString(OtherAdsType, "native");
    }

    public static void setOtherAdsType(String value) {
        ADSPrefManage().edit().putString(OtherAdsType, value).apply();
    }

    public static String getInterAdsLoadType() {
        return ADSPrefManage().getString(InterAdsLoadType, "PreLoad");
    }

    public static void setInterAdsLoadType(String value) {
        String normalized = value == null ? "PreLoad" : value.trim();
        if (normalized.equalsIgnoreCase("Load")) {
            normalized = "Load";
        } else if (normalized.isEmpty() || normalized.equalsIgnoreCase("PreLoad")
                || normalized.equalsIgnoreCase("Preload")) {
            normalized = "PreLoad";
        }
        android.util.Log.d("ADS_INTER", "setInterAdsLoadType=[" + normalized + "] (raw=[" + value + "])");
        ADSPrefManage().edit().putString(InterAdsLoadType, normalized).apply();
    }

    public static String getSplashInterAdsLoadType() {
        return ADSPrefManage().getString(SplashInterAdsLoadType, "PreLoad");
    }

    public static void setSplashInterAdsLoadType(String value) {
        String normalized = value == null ? "PreLoad" : value.trim();
        if (normalized.equalsIgnoreCase("Load")) {
            normalized = "Load";
        } else if (normalized.isEmpty() || normalized.equalsIgnoreCase("PreLoad")
                || normalized.equalsIgnoreCase("Preload")) {
            normalized = "PreLoad";
        }
        ADSPrefManage().edit().putString(SplashInterAdsLoadType, normalized).apply();
    }

    public static String getNativeAdsLoadType() {
        return ADSPrefManage().getString(NativeAdsLoadType, "PreLoad");
    }

    public static void setNativeAdsLoadType(String value) {
        ADSPrefManage().edit().putString(NativeAdsLoadType, value).apply();
    }

    public static String getBannerAdsLoadType() {
        return ADSPrefManage().getString(BannerAdsLoadType, "PreLoad");
    }

    public static void setBannerAdsLoadType(String value) {
        ADSPrefManage().edit().putString(BannerAdsLoadType, value).apply();
    }

    public static String getAppOpenAdsLoadType() {
        return ADSPrefManage().getString(AppOpenAdsLoadType, "PreLoad");
    }

    public static void setAppOpenAdsLoadType(String value) {
        ADSPrefManage().edit().putString(AppOpenAdsLoadType, value).apply();
    }

    public static boolean isAdLoadType(String value) {
        return value != null && value.trim().equalsIgnoreCase("Load");
    }

    public static boolean isAdPreLoadType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        String trimmed = value.trim();
        return trimmed.equalsIgnoreCase("PreLoad") || trimmed.equalsIgnoreCase("Preload");
    }

    public static boolean isNativeLoad() {
        return isAdLoadType(getNativeAdsLoadType());
    }

    public static boolean isNativePreLoad() {
        return isAdPreLoadType(getNativeAdsLoadType());
    }

    public static boolean isInterLoad() {
        return isAdLoadType(getInterAdsLoadType());
    }

    public static boolean isInterPreLoad() {
        return isAdPreLoadType(getInterAdsLoadType());
    }

    public static boolean isSplashInterLoad() {
        return isAdLoadType(getSplashInterAdsLoadType());
    }

    public static boolean isSplashInterPreLoad() {
        return isAdPreLoadType(getSplashInterAdsLoadType());
    }

    public static boolean isBannerLoad() {
        return isAdLoadType(getBannerAdsLoadType());
    }

    public static boolean isBannerPreLoad() {
        return isAdPreLoadType(getBannerAdsLoadType());
    }

    public static boolean isAppOpenLoad() {
        return isAdLoadType(getAppOpenAdsLoadType());
    }

    public static boolean isAppOpenPreLoad() {
        return isAdPreLoadType(getAppOpenAdsLoadType());
    }

    public static boolean getOtherAdsShow() {
        return ADSPrefManage().getBoolean(OtherAdsShow, false);
    }

    public static void setOtherAdsShow(Boolean value) {
        ADSPrefManage().edit().putBoolean(OtherAdsShow, value).apply();
    }

    /** Per-activity bottom ads — Firebase {prefix}_bottom_ads_show */
    public static boolean getActivityBottomAdsShow(String screenPrefix) {
        return ADSPrefManage().getBoolean(screenPrefix + "_bottom_ads_show", false);
    }

    public static void setActivityBottomAdsShow(String screenPrefix, boolean value) {
        ADSPrefManage().edit().putBoolean(screenPrefix + "_bottom_ads_show", value).apply();
    }

    /** Per-activity ads type — Firebase {prefix}_ads_type (native / banner) */
    public static String getActivityAdsType(String screenPrefix) {
        return ADSPrefManage().getString(screenPrefix + "_ads_type", "native");
    }

    public static void setActivityAdsType(String screenPrefix, String value) {
        ADSPrefManage().edit().putString(screenPrefix + "_ads_type", value).apply();
    }

    public static String getActivityBannerId(String screenPrefix) {
        return getStringValue(screenPrefix + "_banner_id");
    }

    public static void setActivityBannerId(String screenPrefix, String value) {
        setStringValue(screenPrefix + "_banner_id", value);
    }

    public static String getActivityNativeId(String screenPrefix) {
        return getStringValue(screenPrefix + "_native_id");
    }

    public static void setActivityNativeId(String screenPrefix, String value) {
        setStringValue(screenPrefix + "_native_id", value);
    }

    /** Per-activity button-click interstitial — Firebase {prefix}_button_ads_show */
    public static boolean getActivityButtonAdsShow(String screenPrefix) {
        return ADSPrefManage().getBoolean(screenPrefix + "_button_ads_show", true);
    }

    public static void setActivityButtonAdsShow(String screenPrefix, boolean value) {
        ADSPrefManage().edit().putBoolean(screenPrefix + "_button_ads_show", value).apply();
    }

    /** Per-activity back-click interstitial — Firebase {prefix}_back_ads_show */
    public static boolean getActivityBackAdsShow(String screenPrefix) {
        return ADSPrefManage().getBoolean(screenPrefix + "_back_ads_show", true);
    }

    public static void setActivityBackAdsShow(String screenPrefix, boolean value) {
        ADSPrefManage().edit().putBoolean(screenPrefix + "_back_ads_show", value).apply();
    }

    public static boolean getLanguageScreenBottomAdShow() {
        return ADSPrefManage().getBoolean(LanguageScreenBottomAdShow, true);
    }

    public static void setLanguageScreenBottomAdShow(boolean value) {
        ADSPrefManage().edit().putBoolean(LanguageScreenBottomAdShow, value).apply();
    }

    public static boolean getLanguageInterAdsShow() {
        return ADSPrefManage().getBoolean(LanguageInterAdsShow, false);
    }

    public static void setLanguageInterAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(LanguageInterAdsShow, value).apply();
    }


    public static String getLanguageAdsType() {
        return ADSPrefManage().getString(LanguageAdsType, "native");
    }

    public static void setLanguageAdsType(String value) {
        ADSPrefManage().edit().putString(LanguageAdsType, value).apply();
    }

    public static boolean getSplashToLanguage() {
        return ADSPrefManage().getBoolean(SplashToLanguage, true);
    }

    public static void setSplashToLanguage(boolean value) {
        ADSPrefManage().edit().putBoolean(SplashToLanguage, value).apply();
    }

    public static boolean getPermissionSmallAdsShow() {
        return ADSPrefManage().getBoolean(PermissionSmallAdsShow, true);
    }

    public static void setPermissionSmallAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(PermissionSmallAdsShow, value).apply();
    }


    public static String getPermissionAdsType() {
        return ADSPrefManage().getString(PermissionAdsType, "native");
    }

    public static void setPermissionAdsType(String value) {
        ADSPrefManage().edit().putString(PermissionAdsType, value).apply();
    }

    public static boolean getDefaultPermissionButtonAdsShow() {
        return ADSPrefManage().getBoolean(DefaultPermissionButtonAdsShow, false);
    }

    public static void setDefaultPermissionButtonAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(DefaultPermissionButtonAdsShow, value).apply();
    }

    public static String getDefaultPermissionButtonAdsType() {
        return ADSPrefManage().getString(DefaultPermissionButtonAdsType, "Appopen");
    }

    public static void setDefaultPermissionButtonAdsType(String value) {
        ADSPrefManage().edit().putString(DefaultPermissionButtonAdsType, value).apply();
    }

    public static boolean getHomeScreenBottomShow() {
        return ADSPrefManage().getBoolean(HomeScreenBottomShow, true);
    }

    public static void setHomeScreenBottomShow(boolean value) {
        ADSPrefManage().edit().putBoolean(HomeScreenBottomShow, value).apply();
    }

    public static boolean getHomeScreenBottomAdsRefresh() {
        return ADSPrefManage().getBoolean(HomeScreenBottomAdsRefresh, false);
    }

    public static void setHomeScreenBottomAdsRefresh(boolean value) {
        ADSPrefManage().edit().putBoolean(HomeScreenBottomAdsRefresh, value).apply();
    }

    public static int getHomeScreenBottomAdsRefreshTime() {
        return ADSPrefManage().getInt(HomeScreenBottomAdsRefreshTime, 30);
    }

    public static void setHomeScreenBottomAdsRefreshTime(int value) {
        ADSPrefManage().edit().putInt(HomeScreenBottomAdsRefreshTime, value).apply();
    }

    public static String getHomeScreenAdsType() {
        return ADSPrefManage().getString(HomeScreenAdsType, "native");
    }

    public static void setHomeScreenAdsType(String value) {
        ADSPrefManage().edit().putString(HomeScreenAdsType, value).apply();
    }

    public static boolean getHomeBottomNavInterShow() {
        return ADSPrefManage().getBoolean(HomeBottomNavInterShow, false);
    }

    public static void setHomeBottomNavInterShow(boolean value) {
        ADSPrefManage().edit().putBoolean(HomeBottomNavInterShow, value).apply();
    }

    public static int getHomeBottomCount() {
        return ADSPrefManage().getInt(HomeBottomCount, 2);
    }

    public static void setHomeBottomCount(int value) {
        ADSPrefManage().edit().putInt(HomeBottomCount, value).apply();
    }

    public static int getCustomAfterHourShow() {
        return ADSPrefManage().getInt(CustomAfterHourShow, 2);
    }

    public static void setCustomAfterHourShow(int value) {
        ADSPrefManage().edit().putInt(CustomAfterHourShow, value).apply();
    }

    /**
     * Home default-launcher dialog eligibility:
     * - Firebase custom_dialog_show must be true
     * - App must NOT already be the default home launcher
     * - Max 1 show per app open/session (not instant back-to-back)
     * - Across separate app opens: at most custom_after_hour_show times per 24h window
     *
     * Example (custom_after_hour_show = 2):
     * 1st app open → show (count 1)
     * User kills app, 2nd open → show (count 2)
     * 3rd open → no dialog until 24h window elapses
     */
    public static boolean canShowHomeCustomDialog(Context context) {
        if (context == null) {
            return false;
        }
        if (!getDefaultLauncherShow()) {
            return false;
        }
        // Same app open / process — never show again until process restarts
        if (homeCustomDialogShownThisSession) {
            return false;
        }
        if (Utils.isDefaultHomeApp(context)) {
            return false;
        }
        int maxCount = getCustomAfterHourShow();
        if (maxCount <= 0) {
            return false;
        }
        long windowMillis = 24L * 60L * 60L * 1000L;
        SharedPreferences prefs = context.getSharedPreferences(HOME_CUSTOM_DIALOG_PREFS, Context.MODE_PRIVATE);
        long lastDialogTime = prefs.getLong(HOME_CUSTOM_DIALOG_LAST_TIME, 0L);
        int counter = prefs.getInt(HOME_CUSTOM_DIALOG_COUNTER, 0);
        long now = System.currentTimeMillis();

        // First time, or 24h window elapsed → new eligible window (counter resets on record)
        if (lastDialogTime == 0L || (now - lastDialogTime) >= windowMillis) {
            return true;
        }
        // Still inside 24h window → only on a NEW app open, and only while under max count
        return counter < maxCount;
    }

    public static void recordHomeCustomDialogShown(Context context) {
        if (context == null) {
            return;
        }
        // Lock this process/session so dialog cannot appear again until app is killed
        homeCustomDialogShownThisSession = true;

        long windowMillis = 24L * 60L * 60L * 1000L;
        SharedPreferences prefs = context.getSharedPreferences(HOME_CUSTOM_DIALOG_PREFS, Context.MODE_PRIVATE);
        long lastDialogTime = prefs.getLong(HOME_CUSTOM_DIALOG_LAST_TIME, 0L);
        int counter = prefs.getInt(HOME_CUSTOM_DIALOG_COUNTER, 0);
        long now = System.currentTimeMillis();

        if (lastDialogTime == 0L || (now - lastDialogTime) >= windowMillis) {
            // New window: reset counter, anchor last_dialog_time to this show
            counter = 0;
        }
        // last_dialog_time = now, dialog_counter += 1
        prefs.edit()
                .putLong(HOME_CUSTOM_DIALOG_LAST_TIME, now)
                .putInt(HOME_CUSTOM_DIALOG_COUNTER, counter + 1)
                .apply();
    }

    public static boolean getLauncherHomeSwipeInterShow() {
        return ADSPrefManage().getBoolean(LauncherHomeSwipeInterShow, false);
    }

    public static void setLauncherHomeSwipeInterShow(boolean value) {
        ADSPrefManage().edit().putBoolean(LauncherHomeSwipeInterShow, value).apply();
    }

    public static int getLauncherHomeSwipeInterCount() {
        return ADSPrefManage().getInt(LauncherHomeSwipeInterCount, 2);
    }

    public static void setLauncherHomeSwipeInterCount(int value) {
        ADSPrefManage().edit().putInt(LauncherHomeSwipeInterCount, value).apply();
    }

    public static String getLauncherHomeAdsType() {
        return ADSPrefManage().getString(LauncherHomeAdsType, "banner");
    }

    public static void setLauncherHomeAdsType(String value) {
        ADSPrefManage().edit().putString(LauncherHomeAdsType, value).apply();
    }

    public static boolean getRecentLauncherBottomAdsShow() {
        return ADSPrefManage().getBoolean(RecentLauncherBottomAdsShow, false);
    }

    public static void setRecentLauncherBottomAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(RecentLauncherBottomAdsShow, value).apply();
    }

    public static String getRecentLauncherAdsType() {
        return ADSPrefManage().getString(RecentLauncherAdsType, "banner");
    }

    public static void setRecentLauncherAdsType(String value) {
        ADSPrefManage().edit().putString(RecentLauncherAdsType, value).apply();
    }

    public static boolean getLauncherBottomDrawerAdsShow() {
        return ADSPrefManage().getBoolean(LauncherBottomDrawerAdsShow, true);
    }

    public static void setLauncherBottomDrawerAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(LauncherBottomDrawerAdsShow, value).apply();
    }

    /**
     * Drawer bottom ad eligibility using custom_after_hour_show as times-per-day:
     * intervalHours = 24 / custom_after_hour_show
     * Example: custom_after_hour_show = 2 → show again only after 12 hours.
     */
    public static boolean canShowLauncherDrawerBottomAd(Context context) {
        if (context == null) {
            return false;
        }
        int showCountPerDay = getCustomAfterHourShow();
        if (showCountPerDay <= 0) {
            return false;
        }
        long intervalMillis = (24L * 60L * 60L * 1000L) / showCountPerDay;
        SharedPreferences prefs = context.getSharedPreferences(LAUNCHER_DRAWER_AD_PREFS, Context.MODE_PRIVATE);
        long lastShownAt = prefs.getLong(LAUNCHER_DRAWER_AD_WINDOW_START, 0L);
        long now = System.currentTimeMillis();
        return lastShownAt == 0L || (now - lastShownAt) >= intervalMillis;
    }

    public static void recordLauncherDrawerBottomAdShown(Context context) {
        if (context == null) {
            return;
        }
        context.getSharedPreferences(LAUNCHER_DRAWER_AD_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putLong(LAUNCHER_DRAWER_AD_WINDOW_START, System.currentTimeMillis())
                .apply();
    }

    public static boolean getSettingBottomAdsShow() {
        return ADSPrefManage().getBoolean(SettingBottomAdsShow, true);
    }

    public static void setSettingBottomAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(SettingBottomAdsShow, value).apply();
    }

    public static String getSettingAdsType() {
        return ADSPrefManage().getString(SettingAdsType, "native");
    }

    public static void setSettingAdsType(String value) {
        ADSPrefManage().edit().putString(SettingAdsType, value).apply();
    }

    public static boolean getCallEndBottomAdsShow() {
        return ADSPrefManage().getBoolean(CallEndBottomAdsShow, false);
    }

    public static void setCallEndBottomAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(CallEndBottomAdsShow, value).apply();
    }

    public static boolean getCallEndInterAdsShow() {
        return ADSPrefManage().getBoolean(CallEndInterAdsShow, false);
    }

    public static void setCallEndInterAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(CallEndInterAdsShow, value).apply();
    }

    public static String getCallEndBottomAdsType() {
        return ADSPrefManage().getString(CallEndBottomAdsType, "native");
    }

    public static void setCallEndBottomAdsType(String value) {
        ADSPrefManage().edit().putString(CallEndBottomAdsType, value).apply();
    }

    public static String getCallEndInterAdsType() {
        return ADSPrefManage().getString(CALL_END_INTER_ADS_TYPE, "inter");
    }

    public static void setCallEndInterAdsType(String value) {
        ADSPrefManage().edit().putString(CALL_END_INTER_ADS_TYPE, value).apply();
    }

    public static boolean getNotificationScreenBackAdsShow() {
        return ADSPrefManage().getBoolean(NOTIFICATION_SCREEN_BACK_ADS_SHOW, false);
    }

    public static void setNotificationScreenBackAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(NOTIFICATION_SCREEN_BACK_ADS_SHOW, value).apply();
    }

    public static boolean getLauncherSettingBackAdsShow() {
        return ADSPrefManage().getBoolean(LAUNCHER_SETTING_BACK_ADS_SHOW, true);
    }

    public static void setLauncherSettingBackAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(LAUNCHER_SETTING_BACK_ADS_SHOW, value).apply();
    }

    public static boolean getLauncherSettingBottomAdsShow() {
        return ADSPrefManage().getBoolean(LauncherSettingBottomAdsShow, true);
    }

    public static void setLauncherSettingBottomAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(LauncherSettingBottomAdsShow, value).apply();
    }

    public static String getLauncherSettingAdsType() {
        return ADSPrefManage().getString(LauncherSettingAdsType, "banner");
    }

    public static void setLauncherSettingAdsType(String value) {
        ADSPrefManage().edit().putString(LauncherSettingAdsType, value).apply();
    }

    public static void setCloseButtonShowOnFullNativeAds(boolean value) {
        ADSPrefManage().edit().putBoolean(CLOSE_BUTTON_SHOW_ON_FULL_NATIVE_ADS, value).apply();
    }

    public static boolean getCloseButtonShowOnFullNativeAds() {
        return ADSPrefManage().getBoolean(CLOSE_BUTTON_SHOW_ON_FULL_NATIVE_ADS, false);
    }

    public static String getIpCountryName() {
        return ADSPrefManage().getString(IP_COUNTRY_NAME, "");
    }

    public static void setIpCountryName(String value) {
        ADSPrefManage().edit().putString(IP_COUNTRY_NAME, value).apply();
    }

    public static boolean getCountryGetWithIp() {
        return ADSPrefManage().getBoolean(COUNTRY_GET_WITH_IP, false);
    }

    public static void setCountryGetWithIp(boolean value) {
        ADSPrefManage().edit().putBoolean(COUNTRY_GET_WITH_IP, value).apply();
    }

    public static boolean getInAppUpdateShow() {
        return ADSPrefManage().getBoolean("in_app_update_show", false);
    }

    public static void setInAppUpdateShow(boolean value) {
        ADSPrefManage().edit().putBoolean("in_app_update_show", value).apply();
    }

    public static String getInAppUpdateType() {
        return ADSPrefManage().getString("in_app_update_type", "Flexible");
    }

    public static void setInAppUpdateType(String value) {
        ADSPrefManage().edit().putString("in_app_update_type", value).apply();
    }

    public static int getInApppDialogDailyShowCount() {
        return ADSPrefManage().getInt("in_appp_dailog_daily_show_count", 0);
    }

    public static void setInApppDialogDailyShowCount(int value) {
        ADSPrefManage().edit().putInt("in_appp_dailog_daily_show_count", value).apply();
    }

    public static void setUpdateLastShowDate(String value) {
        ADSPrefManage().edit().putString(update_last_show_date, value).apply();
    }

    public static String getUpdateLastShowDate() {
        return ADSPrefManage().getString(update_last_show_date, "");
    }

    public static void setUpdateShowCountToday(int value) {
        ADSPrefManage().edit().putInt(update_show_count_today, value).apply();
    }

    public static int getUpdateShowCountToday() {
        return ADSPrefManage().getInt(update_show_count_today, 0);
    }

    public static boolean shouldShowAppUpdate() {
        if (!getInAppUpdateShow()) return false;

        int maxCount = getInApppDialogDailyShowCount();

        String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        String lastDate = getUpdateLastShowDate();

        if (todayDate.equals(lastDate)) {
            return getUpdateShowCountToday() < maxCount;
        }

        return true;
    }

    public static void updateAppUpdateShowCount() {
        String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        String lastDate = getUpdateLastShowDate();

        if (todayDate.equals(lastDate)) {
            setUpdateShowCountToday(getUpdateShowCountToday() + 1);
        } else {
            setUpdateLastShowDate(todayDate);
            setUpdateShowCountToday(1);
        }
    }

    public static boolean getInterAdsShow() {
        return ADSPrefManage().getBoolean(InterAdsShow, false);
    }

    public static void setInterAdsShow(boolean value) {
        ADSPrefManage().edit().putBoolean(InterAdsShow, value).apply();
    }
   public static boolean getInterAdsOnBackShow() {
        return ADSPrefManage().getBoolean(InterAdsOnBackShow, false);
    }

    public static void setInterAdsOnBackShow(boolean value) {
        ADSPrefManage().edit().putBoolean(InterAdsOnBackShow, value).apply();
    }

    public static boolean getAppOpenBackgroundShow() {
        return ADSPrefManage().getBoolean(AppOpenBackgroundShow, true);
    }

    public static void setAppOpenBackgroundShow(boolean value) {
        ADSPrefManage().edit().putBoolean(AppOpenBackgroundShow, value).apply();
    }

    public static int getAppOpenAdDailyLimit() {
        return ADSPrefManage().getInt("AppOpenAdDailyLimit", 3);
    }

    public static void setAppOpenAdDailyLimit(int value) {
        ADSPrefManage().edit().putInt("AppOpenAdDailyLimit", value).apply();
    }

    public static String getSplashAdsType() {
        return ADSPrefManage().getString(splash_ads_type, "");
    }

    public static void setSplashAdsType(String value) {
        ADSPrefManage().edit().putString(splash_ads_type, value).apply();
    }

    public static boolean getSplashAppOpenShow() {
        return ADSPrefManage().getBoolean(SplashAppOpenShow, true);
    }

    public static void setSplashAppOpenShow(boolean value) {
        ADSPrefManage().edit().putBoolean(SplashAppOpenShow, value).apply();
    }
    public static boolean getBottomSplashShow() {
        return ADSPrefManage().getBoolean(SplashBottomAdsShow, true);
    }

    public static void setBottomSplashShow(boolean value) {
        ADSPrefManage().edit().putBoolean(SplashBottomAdsShow, value).apply();
    }

    public static int getSplashAppOpenAfterCount() {
        return ADSPrefManage().getInt(SplashAppOpenAfterCount, 1);
    }

    public static void setSplashAppOpenAfterCount(int value) {
        ADSPrefManage().edit().putInt(SplashAppOpenAfterCount, value).apply();
    }

    public static int getSplashAppOpenVisitCount() {
        return ADSPrefManage().getInt(SplashAppOpenVisitCount, 0);
    }

    public static void setSplashVisitCount(int value) {
        ADSPrefManage().edit().putInt(SplashAppOpenVisitCount, value).apply();
    }


    public static boolean shouldShowSplashAd() {

        if (!getSplashAppOpenShow()) {
            return false;
        }

        int afterCount = getSplashAppOpenAfterCount();
        if (afterCount <= 0) {
            return false;
        }

        int currentVisit = getSplashAppOpenVisitCount() + 1;
        setSplashVisitCount(currentVisit);

        if (currentVisit % afterCount == 0) {
            return true;
        }

        return false;
    }

    public static String getCallEndLastAdDate() {
        return ADSPrefManage().getString(call_end_last_ad_date, "");
    }

    public static void setCallEndLastAdDate(String value) {
        ADSPrefManage().edit().putString(call_end_last_ad_date, value).apply();
    }


    public static int getCallEndAdCountToday() {
        return ADSPrefManage().getInt(call_end_ad_count_today, 0);
    }

    public static void setCallEndAdCountToday(int value) {
        ADSPrefManage().edit().putInt(call_end_ad_count_today, value).apply();
    }

    public static long getCallEndLastAdTimestamp() {
        return ADSPrefManage().getLong(call_end_last_ad_timestamp, 0L);
    }

    public static void setCallEndLastAdTimestamp(long value) {
        ADSPrefManage().edit().putLong(call_end_last_ad_timestamp, value).apply();
    }

    private static String getTodayDateString() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
    }

    private static void resetCallEndAdDailyCounters() {
        setCallEndAdCountToday(0);
        setCallEndLastAdTimestamp(0L);
    }

    private static boolean isCallEndAdSlotAvailable(int maxCount, int currentCount) {
        if (maxCount <= 0) return false;
        // 1st ad of the day — available immediately
        if (currentCount <= 0) return true;

        long lastAdTime = getCallEndLastAdTimestamp();
        if (lastAdTime <= 0) return true;

        // Only apply gap when last ad was shown today (previous day does not carry over)
        String todayDate = getTodayDateString();
        String lastAdDay = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date(lastAdTime));
        if (!todayDate.equals(lastAdDay)) return true;

        long slotDurationMs = (24L * 60 * 60 * 1000) / maxCount;
        return (System.currentTimeMillis() - lastAdTime) >= slotDurationMs;
    }

    public static boolean getIsShowCallEnd() {
        return ADSPrefManage().getBoolean(IsShowCallEnd, false);
    }

    public static void setIsShowCallEnd(boolean value) {
        ADSPrefManage().edit().putBoolean(IsShowCallEnd, value).apply();
    }

    public static void setCallEndInterDayCount(int value) {
        ADSPrefManage().edit().putInt(CALL_END_INTER_DAY_COUNT, value).apply();
    }

    public static int getCallEndInterDayCount() {
        return ADSPrefManage().getInt(CALL_END_INTER_DAY_COUNT, 0);
    }

    public static void setCallEndInterShowCount(int value) {
        ADSPrefManage().edit().putInt(CALL_END_INTER_SHOW_COUNT, value).apply();
    }

    public static int getCallEndAgainOpenCount() {
        return ADSPrefManage().getInt(CALL_END_AGAIN_OPEN_COUNT_VALUE, 0);
    }

    public static void setCallEndAgainOpenCount(int value) {
        ADSPrefManage().edit().putInt(CALL_END_AGAIN_OPEN_COUNT_VALUE, value).apply();
    }

    public static int getCallEndInterShowCount() {
        return ADSPrefManage().getInt(CALL_END_INTER_SHOW_COUNT, 0);
    }

    public static void setCallEndAdCountries(List<String> countries) {
        String json = new Gson().toJson(countries);
        ADSPrefManage().edit().putString(CALL_END_INTER_ADS_SHOW_COUNTRIES, json).apply();
    }

    public static List<String> getCallEndAdCountries() {
        String json = ADSPrefManage().getString(CALL_END_INTER_ADS_SHOW_COUNTRIES, "[]");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static String getDeviceCountry(Context context) {
        if (getCountryGetWithIp() && !getIpCountryName().isEmpty()) {
            return getIpCountryName().toUpperCase();
        }
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String countryCode = tm.getNetworkCountryIso();
            if (countryCode == null || countryCode.isEmpty()) {
                countryCode = tm.getSimCountryIso();
            }
            if (countryCode == null || countryCode.isEmpty()) {
                countryCode = context.getResources().getConfiguration().locale.getCountry();
            }
            if (countryCode != null && !countryCode.isEmpty()) {
                java.util.Locale l = new java.util.Locale("", countryCode);
                return l.getDisplayCountry(java.util.Locale.ENGLISH).toUpperCase();
            }
            return "";
        } catch (Exception e) {
            try {
                java.util.Locale locale = context.getResources().getConfiguration().locale;
                return locale.getDisplayCountry(java.util.Locale.ENGLISH).toUpperCase();
            } catch (Exception e2) {
                return "";
            }
        }
    }

    public static boolean isCountryAllowedForCallEnd(Context context) {
        List<String> allowedCountries = getCallEndAdCountries();
        if (allowedCountries == null || allowedCountries.isEmpty()) {
            return true;
        }
        String currentCountry = getDeviceCountry(context);
        for (String country : allowedCountries) {
            if (country.equalsIgnoreCase(currentCountry)) {
                return true;
            }
        }
        return false;
    }


    public static boolean shouldShowCallEndAd(Context context) {
        if (!getIsShowCallEnd()) return false;
        if (getAds_Free()) return false;
        if (!getCallEndInterAdsShow()) return false;

        int interval = getCallEndInterDayCount();
        int maxCount = getCallEndInterShowCount();

        if (interval <= 0 || maxCount <= 0) return false;

        if (getDaysSinceInstall() < interval) {
            return false;
        }

        boolean isFcmFlow = false;
        if (context instanceof android.app.Activity) {
            android.app.Activity activity = (android.app.Activity) context;
            if (activity.getIntent() != null) {
                String callType = activity.getIntent().getStringExtra("CallType");
                isFcmFlow = "Notification".equalsIgnoreCase(callType) || "call_end".equalsIgnoreCase(callType);
            }
        }

        if (isFcmFlow) {
            if (!isCountryAllowedForCallEnd(context)) return false;
        }

        String todayDate = getTodayDateString();
        String lastDate = getCallEndLastAdDate();

        if (!todayDate.equals(lastDate)) {
            resetCallEndAdDailyCounters();
            return true;
        }

        int currentCount = getCallEndAdCountToday();
        if (currentCount >= maxCount) return false;
        return isCallEndAdSlotAvailable(maxCount, currentCount);
    }

    public static void updateCallEndAdCount(Context context) {
        String todayDate = getTodayDateString();
        String lastDate = getCallEndLastAdDate();

        if (todayDate.equals(lastDate)) {
            setCallEndAdCountToday(getCallEndAdCountToday() + 1);
        } else {
            // New day or first time
            setCallEndLastAdDate(todayDate);
            setCallEndAdCountToday(1);
        }
        setCallEndLastAdTimestamp(System.currentTimeMillis());
    }

    public static void setOverlayPermissionNotificationShow(boolean value) {
        ADSPrefManage().edit().putBoolean(OVERLAY_PERMISSION_NOTIFICATION_SHOW, value).apply();
    }

    public static boolean getOverlayPermissionNotificationShow() {
        return ADSPrefManage().getBoolean(OVERLAY_PERMISSION_NOTIFICATION_SHOW, false);
    }

    public static void setOverlayPermissionNotificationDaysShowCount(int value) {
        ADSPrefManage().edit().putInt(OVERLAY_PERMISSION_NOTIFICATION_DAYS_SHOW_COUNT, value).apply();
    }

    public static int getOverlayPermissionNotificationDaysShowCount() {
        return ADSPrefManage().getInt(OVERLAY_PERMISSION_NOTIFICATION_DAYS_SHOW_COUNT, 0);
    }

    public static void setNotificationInstallDays(int value) {
        ADSPrefManage().edit().putInt(NOTIFICATION_INSTALL_DAYS, value).apply();
    }

    public static int getNotificationInstallDays() {
        return ADSPrefManage().getInt(NOTIFICATION_INSTALL_DAYS, 0);
    }

    public static void setNotificationCallInstallDays(int value) {
        ADSPrefManage().edit().putInt(NOTIFICATION_CALL_INSTALL_DAYS, value).apply();
    }

    public static int getNotificationCallInstallDays() {
        return ADSPrefManage().getInt(NOTIFICATION_CALL_INSTALL_DAYS, 0);
    }

    public static void setNotificationCallOverlayInstallDays(int value) {
        ADSPrefManage().edit().putInt(NOTIFICATION_CALL_OVERLAY_INSTALL_DAYS, value).apply();
    }

    public static int getNotificationCallOverlayInstallDays() {
        return ADSPrefManage().getInt(NOTIFICATION_CALL_OVERLAY_INSTALL_DAYS, 0);
    }

    public static void setNotificationCountries(List<String> countries) {
        String json = new Gson().toJson(countries);
        ADSPrefManage().edit().putString(NOTIFICATION_COUNTRY, json).apply();
    }

    public static List<String> getNotificationCountries() {
        String json = ADSPrefManage().getString(NOTIFICATION_COUNTRY, "[]");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setNotificationCallCountries(List<String> countries) {
        String json = new Gson().toJson(countries);
        ADSPrefManage().edit().putString(NOTIFICATION_CALL_COUNTRY, json).apply();
    }

    public static List<String> getNotificationCallCountries() {
        String json = ADSPrefManage().getString(NOTIFICATION_CALL_COUNTRY, "[]");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setNotificationCallOverlayCountries(List<String> countries) {
        String json = new Gson().toJson(countries);
        ADSPrefManage().edit().putString(NOTIFICATION_CALL_OVERLAY_COUNTRY, json).apply();
    }

    public static List<String> getNotificationCallOverlayCountries() {
        String json = ADSPrefManage().getString(NOTIFICATION_CALL_OVERLAY_COUNTRY, "[]");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setAllAllowPermissionShowFbNotification(boolean value) {
        ADSPrefManage().edit().putBoolean(ALL_ALLOW_PERMISSION_SHOW_FB_NOTIFICATION, value).apply();
    }

    public static boolean getAllAllowPermissionShowFbNotification() {
        return ADSPrefManage().getBoolean(ALL_ALLOW_PERMISSION_SHOW_FB_NOTIFICATION, false);
    }


    public static boolean isNotificationGranted(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
                    == android.content.pm.PackageManager.PERMISSION_GRANTED;
        }
        return true; // Granted by default on older versions
    }

    public static boolean isCallStateGranted(Context context) {
        return androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) 
                == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isOverlayGranted(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return android.provider.Settings.canDrawOverlays(context);
        }
        return true;
    }


    public static boolean isCallEndPerformanceAllowed(Context context, boolean isFcmTrigger) {
        if (!getIsShowCallEnd()) {
//            android.util.Log.d("CallEndCheck", "Blocked: getIsShowCallEnd is false");
            return false;
        }

        boolean notif = isNotificationGranted(context);
        boolean call = isCallStateGranted(context);
        boolean overlay = isOverlayGranted(context);

        long installDays = getDaysSinceInstall();
        String currentCountry = getDeviceCountry(context);

        if (notif && call && overlay) {
            if (isFcmTrigger && !getAllAllowPermissionShowFbNotification()) {
                return false; 
            }
            boolean result = checkDaysAndCountry(installDays, getNotificationCallOverlayInstallDays(), 
                    currentCountry, getNotificationCallOverlayCountries(), isFcmTrigger);
            return result;

        } else if (notif && call) {
            boolean result = checkDaysAndCountry(installDays, getNotificationCallInstallDays(),
                    currentCountry, getNotificationCallCountries(), isFcmTrigger);
            return result;

        } else if (notif) {
            if (!isFcmTrigger) {
                return false; 
            }
            boolean result = checkDaysAndCountry(installDays, getNotificationInstallDays(), 
                    currentCountry, getNotificationCountries(), isFcmTrigger);
            return result;
        }

        return false;
    }

    private static boolean checkDaysAndCountry(long currentDays, int requiredDays, 
                                             String currentCountry, List<String> allowedCountries,
                                             boolean isFcmTrigger) {
        // If local flow (not FCM), ignore both required days and country checks
        if (!isFcmTrigger) return true;

        // requiredDays == 0 → skip install-days wait; decide only by country list
        if (requiredDays > 0 && currentDays < requiredDays) return false;
        
        if (allowedCountries == null || allowedCountries.isEmpty()) return true;
        
        for (String country : allowedCountries) {
            if (country.equalsIgnoreCase(currentCountry)) return true;
        }
        return false;
    }

    public static void updateConsecutiveStreak(Context context) {
        String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        String lastDate = ADSPrefManage().getString(LAST_OPEN_DATE, "");
        int currentStreak = ADSPrefManage().getInt(CONSECUTIVE_DAYS_COUNT, 0);

        if (todayDate.equals(lastDate)) {
            // Already counted today
            return;
        }

        if (lastDate.isEmpty()) {
            // First time ever
            currentStreak = 1;
        } else {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                java.util.Date d1 = sdf.parse(lastDate);
                java.util.Date d2 = sdf.parse(todayDate);
                long diff = d2.getTime() - d1.getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000);

                if (diffDays == 1) {
                    // Consecutive day
                    currentStreak++;
                } else {
                    // Day missed, reset to 1
                    currentStreak = 1;
                }
            } catch (Exception e) {
                currentStreak = 1;
            }
        }

        ADSPrefManage().edit()
                .putString(LAST_OPEN_DATE, todayDate)
                .putInt(CONSECUTIVE_DAYS_COUNT, currentStreak)
                .apply();

        // Days Since Install Logic
        String installDate = ADSPrefManage().getString(APP_INSTALL_DATE, "");
        if (installDate.isEmpty()) {
            installDate = todayDate;
            ADSPrefManage().edit().putString(APP_INSTALL_DATE, installDate).apply();
        }
    }

    public static long getDaysSinceInstall() {
        String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        String installDate = ADSPrefManage().getString(APP_INSTALL_DATE, "");
        if (installDate.isEmpty()) {
            return 1;
        }

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date d1 = sdf.parse(installDate);
            java.util.Date d2 = sdf.parse(todayDate);
            long diff = d2.getTime() - d1.getTime();
            return (diff / (24 * 60 * 60 * 1000)) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public static int getConsecutiveDaysCount() {
        return ADSPrefManage().getInt(CONSECUTIVE_DAYS_COUNT, 0);
    }

    public static String getStringValue(String key) {
        return ADSPrefManage().getString(key, "");
    }

    public static void setStringValue(String key, String value) {
        ADSPrefManage().edit().putString(key, value).apply();
    }

    public static boolean getHomeRetention1Day() {
        return ADSPrefManage().getBoolean(lehfv_HomeRetention1Day, false);
    }

    public static void setHomeRetention1Day(boolean value) {
        ADSPrefManage().edit().putBoolean(lehfv_HomeRetention1Day, value);
    }

    public static boolean getHomeRetention3Day() {
        return ADSPrefManage().getBoolean(lehfv_HomeRetention3Day, false);
    }


    public static void setHomeRetention3Day(boolean value) {
        ADSPrefManage().edit().putBoolean(lehfv_HomeRetention3Day, value);
    }

    public static boolean getHomeRetention7Day() {
        return ADSPrefManage().getBoolean(lehfv_HomeRetention7Day, false);
    }

    public static void setHomeRetention7Day(boolean value) {
        ADSPrefManage().edit().putBoolean(lehfv_HomeRetention7Day, value);
    }

    public static String getLanguage() {
        return ADSPrefManage().getString(LANGUAGE_CODE, "en");
    }

    public static void setLanguage(String key) {
        ADSPrefManage().edit().putString(LANGUAGE_CODE, key);
    }

    public static boolean getCallEndShow50() {
        return ADSPrefManage().getBoolean(CallEndShow50, false);
    }

    public static void setCallEndShow50(boolean value) {
        ADSPrefManage().edit().putBoolean(CallEndShow50, value).apply();
    }

    public static boolean getCallEndShow10() {
        return ADSPrefManage().getBoolean(CallEndShow10, false);
    }

    public static void setCallEndShow10(boolean value) {
        ADSPrefManage().edit().putBoolean(CallEndShow10, value).apply();
    }

    public static boolean getCallEndShow1() {
        return ADSPrefManage().getBoolean(CallEndShow1, false);
    }

    public static void setCallEndShow1(boolean value) {
        ADSPrefManage().edit().putBoolean(CallEndShow1, value).apply();
    }

    public static boolean getCallEndShow5() {
        return ADSPrefManage().getBoolean(CallEndShow5, false);
    }

    public static void setCallEndShow5(boolean value) {
        ADSPrefManage().edit().putBoolean(CallEndShow5, value).apply();
    }

    public static boolean getCallEndShow100() {
        return ADSPrefManage().getBoolean(CallEndShow100, false);
    }

    public static void setCallEndShow100(boolean value) {
        ADSPrefManage().edit().putBoolean(CallEndShow100, value).apply();
    }

    public static int getCallEndShowEvent() {
        return ADSPrefManage().getInt(CallEndShowEvent, 0);
    }

    public static void setCallEndShowEvent(int value) {
        ADSPrefManage().edit().putInt(CallEndShowEvent, value).apply();
    }

    public static boolean getCallRetention7Day() {
        return ADSPrefManage().getBoolean(CallRetention7Day, false);
    }

    public static void setCallRetention7Day(boolean value) {
        ADSPrefManage().edit().putBoolean(CallRetention7Day, value).apply();
    }

    public static boolean getCallRetention3Day() {
        return ADSPrefManage().getBoolean(CallRetention3Day, false);
    }

    public static void setCallRetention3Day(boolean value) {
        ADSPrefManage().edit().putBoolean(CallRetention3Day, value).apply();
    }

    public static boolean getCallRetention1Day() {
        return ADSPrefManage().getBoolean(CallRetention1Day, false);
    }

    public static void setCallRetention1Day(boolean value) {
        ADSPrefManage().edit().putBoolean(CallRetention1Day, value).apply();
    }

    public static String getSplashADType() {
        return ADSPrefManage().getString(SplashADType, "");
    }

    public static void setSplashADType(String value) {
        ADSPrefManage().edit().putString(SplashADType, value).apply();
    }

    private static SharedPreferences ADSPrefManage() {
        Context context = getAppContext();
        if (context == null) {
            throw new IllegalStateException("Application context not initialized");
        }
        return context.getSharedPreferences("ADSAppManage", Context.MODE_PRIVATE);
    }

    public static boolean getAds_Free() {
        return ADSPrefManage().getBoolean(Ads_Free, false);
    }

    public static void setAds_Free(boolean value) {
        ADSPrefManage().edit().putBoolean(Ads_Free, value).apply();
    }

    public static int getNativeByPage() {
        return ADSPrefManage().getInt(NativeByPage, 0);
    }

    public static void setNativeByPage(int value) {
        ADSPrefManage().edit().putInt(NativeByPage, value).apply();
    }

    public static int getAdsBackClick() {
        return ADSPrefManage().getInt(AdsBackClick, 0);
    }

    public static void setAdsBackClick(int value) {
        ADSPrefManage().edit().putInt(AdsBackClick, value).apply();
    }

    public static int getAdsClick() {
        return ADSPrefManage().getInt(AdsClick, 0);
    }

    public static void setAdsClick(int value) {
        ADSPrefManage().edit().putInt(AdsClick, value).apply();
    }

    public static String getPrivacyPolicy() {
        return ADSPrefManage().getString(PrivacyPolicy, "https://sites.google.com/view/messages02/home");
    }

    public static void setPrivacyPolicy(String value) {
        ADSPrefManage().edit().putString(PrivacyPolicy, value).apply();
    }

    public static boolean getComingSoon() {
        return ADSPrefManage().getBoolean(ComingSoon, false);
    }

    public static void setComingSoon(boolean value) {
        ADSPrefManage().edit().putBoolean(ComingSoon, value).apply();
    }

    public static boolean getDefaultLauncherShow() {
        return ADSPrefManage().getBoolean(DefaultLauncherShow, true);
    }

    public static void setDefaultLauncherShow(boolean value) {
        ADSPrefManage().edit().putBoolean(DefaultLauncherShow, value).apply();
    }

    public static boolean getExitAds() {
        return ADSPrefManage().getBoolean(ExitAds, false);
    }

    public static void setExitAds(Boolean value) {
        ADSPrefManage().edit().putBoolean(ExitAds, value).apply();
    }


    // ==================== Quiz / Launcher app config (AdPlacement) ====================

    public static void applyQuizAdsConfig(JSONObject jsonObject) {
        AdPlacement.applyQuizAdsConfig(jsonObject);
    }

    public static void applyAppProxyConfig(JSONObject jsonObject) {
        AdPlacement.applyAppProxyConfig(jsonObject);
    }

    public static void applyLauncherAppConfig(JSONObject jsonObject) {
        AdPlacement.applyLauncherAppConfig(jsonObject);
    }

}

