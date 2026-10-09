package com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.splash;

/**
 * Firebase Remote Config JSON keys — screen-wise constants.
 * All Firebase config keys are defined here and used from BaseSplashActivity.
 */
public class GlobalParameterManage {

    private GlobalParameterManage() {
    }

    // ==================== Root Config user types ====================
    /** Organic / non-marketing user config block */
    public static final String NORMAL_USER = "normal_user";
    /** Marketing / paid-campaign user config block */
    public static final String PAID_USER = "paid_user";

    // ==================== Root Config (configJson) ====================
    /** App privacy policy URL */
    public static final String APP_PRIVACY_POLICY = "App_PrivacyPolicy";
    /** Splash screen ad load type (Appopen / Inter / etc.) */
    public static final String SPLASH_AD_LOAD_TYPE = "Splash_Ad_LoadType";
    /** Total ads click count limit */
    public static final String ADS_CLICK = "ADSClick";
    /** Back button click count limit for ads */
    public static final String BACKS_CLICK = "BacksClick";
    /** Native ad show frequency per page */
    public static final String NATIVE_BY_PAGE = "Native_ByPage";
    /** Enable / disable all ads (ads blocker) */
    public static final String ADS_BLOCKS = "AdsBlocks";
    /** Show coming soon screen */
    public static final String COMING_SOON = "Coming_Soon";
    /** Show exit dialog with ad */
    public static final String EXIT_DIALOG = "ExitDialog";
    /** Enable Firebase analytics collection */
    public static final String FIREBASE_ANALYTICS = "Firebaseanalytics";
    /** Show update coming soon screen */
    public static final String UPDATE_COMING_SOON = "Update_Coming_Soon";
    public static final String DEFUALT_LAUNCHER_SHOW = "defualt_launcher_show";
    /** Show launcher home when true; open HomeActivity when false */
    public static final String DEFAULT_LAUNCHER_SHOW = "default_launcher_show";
    public static final String consent_form_show_screen = "consent_form_show_screen";
    /** Global interstitial ads load type */
    public static final String INTER_ADS_LOAD_TYPE = "inter_ads_load_type";
    /** Global native ads load type */
    public static final String NATIVE_ADS_LOAD_TYPE = "native_adAdPlacements_load_type";
    /** Global banner ads load type */
    public static final String BANNER_ADS_LOAD_TYPE = "banner_ads_load_type";
    /** Global app open ads load type */
    public static final String APPOPEN_ADS_LOAD_TYPE = "appopen_ads_load_type";
    /** Startup intro screen flow steps array */
    public static final String SHOW_INTRO_SCREEN_FLOW = "show_intro_screen_flow";
    /** Show overlay permission notification */
    public static final String OVERLAY_PERMISSION_NOTIFICATION_SHOW = "overlay_permission_notification_show";
    /** Overlay permission notification show days count */
    public static final String OVERLAY_PERMISSION_NOTIFICATION_DAYS_SHOW_COUNT = "overlay_permission_notification_days_show_count";
    public static final String CONSENT_SCREEN_SHOW = "consent_screen_show";

    // ==================== screen ====================
    /** Parent JSON object — all screen-wise ad configs */
    public static final String SCREEN = "screen";

    // ==================== ads_layout ====================
    /** Native ad layout styling — button margin, light/dark mode colors */
    public static final String ADS_LAYOUT = "ads_layout";
    public static final String LIGHT_MODE = "light_mode";
    public static final String DARK_MODE = "dark_mode";
    /** Native ad button margin */
    public static final String BUTTON_MARGIN = "button_margin";
    /** Native ad button background color */
    public static final String NATIVE_ADS_BUTTON_COLOR = "Native_Ads_Button_Color";
    /** Native ad background color */
    public static final String NATIVE_AD_BACKGROUND_COLOR = "Native_Ad_Background_Color";
    /** Native ad all text color */
    public static final String NATIVE_AD_ALL_TEXT_COLOR = "Native_Ad_All_Text_Color";
    /** Native ad button text color */
    public static final String NATIVE_AD_BUTTON_TEXT_COLOR = "Native_Ad_Button_Text_Color";

    // ==================== splash_screen ====================
    /** Splash screen — app open, banner, native ad IDs and ad type */
    public static final String SPLASH_SCREEN = "splash_screen";
    /** Show app open ad on splash */
    public static final String SPLASH_APP_OPEN_ADS_SHOW = "splash_app_open_ads_show";
    /** App open ad show after this open count */
    public static final String SPLASH_APP_OPEN_AFTER_COUNT = "splash_app_open_after_count";
    /** Show app open ad when app returns from background */
    public static final String APP_OPEN_SHOW_IN_BACKGROUND = "app_open_show_in_background";
    /** Daily app open ad show limit */
    public static final String APP_OPEN_DAILY_SHOW_COUNT = "app_open_daily_show_count";
    public static final String APP_OPEN_ID = "app_open_id";
    public static final String SPLASH_BANNER_ID = "splash_banner_id";
    public static final String SPLASH_NATIVE_ID = "splash_native_id";
    public static final String SPLASH_ADS_TYPE = "splash_ads_type";
    /** Show bottom ad on splash screen */
    public static final String SPLASH_SCREEN_BOTTOM_AD_SHOW = "splash_screen_bottom_ad_show";

    // ==================== language_screen ====================
    /** Language selection screen — ad type and ad unit IDs */
    public static final String LANGUAGE_SCREEN = "language_screen";
    /** Navigate from splash to language screen */
    public static final String SPLASH_TO_LANGUAGE = "splash_to_language";
    /** Show bottom ad on language screen */
    public static final String LANGUAGE_SCREEN_BOTTOM_AD_SHOW = "language_screen_bottom_ad_show";
    public static final String LANGUAGE_ADS_TYPE = "language_ads_type";
    /** Show interstitial ad on language screen */
    public static final String LANGUAGE_INTER_ADS_SHOW = "language_inter_ads_show";
    public static final String LANGUAGE_BANNER_ID = "language_banner_id";
    public static final String LANGUAGE_NATIVE_ID = "language_native_id";

    // ==================== onboarding_intro ====================
    /** Onboarding / intro screens — ad type and ad unit IDs */
    public static final String ONBOARDING_INTRO = "onboarding_intro";
    /** Number of onboarding intro screens to show */
    public static final String ONBOARDING_COUNT_SHOW = "onboarding_count_show";
    public static final String ONBOARDING_ADS_TYPE = "onboarding_ads_type";
    /** Show interstitial ad on onboarding screen */
    public static final String ONBOARDING_INTER_ADS_SHOW = "onboarding_inter_ads_show";
    /** Show bottom ad on onboarding screen */
    public static final String ONBOARDING_SCREEN_BOTTOM_AD_SHOW = "onboarding_screen_bottom_ad_show";
    public static final String ONBOARDING_NATIVE_ID = "onboarding_native_id";
    public static final String ONBOARDING_BANNER_ID = "onboarding_banner_id";

    // ==================== permission_screen ====================
    /** Permission screen — ad type and ad unit IDs */
    public static final String PERMISSION_SCREEN = "permission_screen";
    /** Show small (banner/native) ad on permission screen */
    public static final String PERMISSION_SMALL_ADS_SHOW = "permission_small_ads_show";
    public static final String PERMISSION_ADS_TYPE = "permission_ads_type";
    public static final String PERMISSION_NATIVE_ID = "permission_native_id";
    public static final String PERMISSION_BANNER_ID = "permission_banner_id";

    // ==================== default_permission_screem ====================
    /** Default app permission screen — bottom ad type and ad unit IDs */
    public static final String DEFAULT_PERMISSION_SCREEM = "default_permission_screem";
    /** Show bottom ad on default permission screen */
    public static final String DEFAULT_PERMISSION_BOTTOM_ADS_SHOW = "default_permission_bottom_ads_show";
    public static final String DEFAULT_PERMISSION_ADS_TYPE = "default_permission_ads_type";
    public static final String DEFAULT_PERMISSION_BANNER_ID = "default_permission_banner_id";
    public static final String DEFAULT_PERMISSION_NATIVE_ID = "default_permission_native_id";
    /** Show AppOpen/Inter ad after DefaultAppActivity Set as Default button result */
    public static final String DEFAULT_PERMISSION_BUTTON_ADS_SHOW = "default_permission_button_ads_show";
    /** Ad type for DefaultAppActivity button result — "Appopen" or "Inter" */
    public static final String DEFAULT_PERMISSION_BUTTON_ADS_TYPE = "default_permission_button_ads_type";

    // ==================== home_screen ====================
    /** Home screen — bottom ad type and ad unit IDs */
    public static final String HOME_SCREEN = "home_screen";
    /** Show bottom ad on home screen */
    public static final String HOME_SCREEN_BOTTOM_SHOW = "home_screen_bottom_show";
    public static final String HOME_SCREEN_BOTTOM_ADS_REFRESH = "home_screen_bottom_ads_refresh";
    public static final String HOME_SCREEN_BOTTOM_ADS_REFRESH_TIME = "home_screen_bottom_ads_refresh_time";
    public static final String HOME_SCREEN_ADS_TYPE = "home_screen_ads_type";
    public static final String HOME_BANNER_ID = "home_banner_id";
    public static final String HOME_NATIVE_ID = "home_native_id";
    /** Show interstitial on bottom nav tab click */
    public static final String HOME_BOTTOM_NAV_INTER_SHOW = "home_bottom_nav_inter_show";
    /** Bottom nav click count before interstitial */
    public static final String HOME_BOTTOM_COUNT = "home_bottom_count";
    /** Times per 24h for home custom dialog / drawer bottom ad interval (24 ÷ count hours) */
    public static final String CUSTOM_AFTER_HOUR_SHOW = "custom_after_hour_show";
    /** Show interstitial ad on home drawer / button clicks */
    public static final String HOME_BUTTON_ADS_SHOW = "home_button_ads_show";

    // ==================== launcher_home_screen ====================
    /** Launcher home screen — swipe left interstitial and bottom ad config */
    public static final String LAUNCHER_HOME_SCREEN = "launcher_home_screen";
    /** Show interstitial on swipe left to home screen */
    public static final String SWIPE_INTER_SHOW = "swipe_inter_show";
    /** Swipe count before interstitial */
    public static final String SWIPE_INTER_COUNT = "swipe_inter_count";
    /** Show bottom ad on launcher home screen */
    public static final String LAUNCHER_BOTTOM_ADS_SHOW = "launcher_bottom_ads_show";
    /** Launcher home screen bottom ad type (banner / native) */
    public static final String LAUNCHER_ADS_TYPE = "launcher_ads_type";
    public static final String LAUNCHER_BANNER_ID = "launcher_banner_id";
    public static final String LAUNCHER_NATIVE_ID = "launcher_native_id";
    /** Show bottom ad on launcher apps drawer when recent apps exist */
    public static final String BOTTOM_DRAWER_ADS_SHOW = "bottom_drawer_ads_show";

    // ==================== recent_launcher_screen ====================
    /** Launcher recent screen — bottom ad type and ad unit IDs */
    public static final String RECENT_LAUNCHER_SCREEN = "recent_launcher_screen";
    /** Show bottom ad on launcher recent screen */
    public static final String RECENT_BOTTOM_ADS_SHOW = "recent_bottom_ads_show";
    /** Launcher recent screen bottom ad type (banner / native) */
    public static final String RECENT_ADS_TYPE = "recent_ads_type";
    public static final String RECENT_BANNER_ID = "recent_banner_id";
    public static final String RECENT_NATIVE_ID = "recent_native_id";

    // ==================== setting_screen ====================
    /** Settings screen — bottom ad type and ad unit IDs */
    public static final String SETTING_SCREEN = "setting_screen";
    /** Show bottom ad on settings screen */
    public static final String SETTING_BOTTOM_ADS_SHOW = "setting_bottom_ads_show";
    public static final String SETTING_ADS_TYPE = "setting_ads_type";
    public static final String SETTING_BANNER_ID = "setting_banner_id";
    public static final String SETTING_NATIVE_ID = "setting_native_id";
    /** Show interstitial ad on settings button click */
    public static final String SETTING_BUTTON_ADS_SHOW = "setting_button_ads_show";
    /** Show interstitial ad on settings back press */
    public static final String SETTING_BACK_ADS_SHOW = "setting_back_ads_show";

    // ==================== launcher_setting_screen ====================
    /** Launcher settings screen — bottom ad type and ad unit IDs */
    public static final String LAUNCHER_SETTING_SCREEN = "launcher_setting_screen";
    /** Show interstitial ad on launcher settings back press */
    public static final String LAUNCHER_SETTING_BACK_ADS_SHOW = "launcher_setting_back_ads_show";
    /** Show bottom ad on launcher settings screen */
    public static final String LAUNCHER_SETTING_BOTTOM_ADS_SHOW = "launcher_bottom_ads_show";
    public static final String LAUNCHER_SETTING_ADS_TYPE = "launcher_setting_ads_type";
    public static final String LAUNCHER_SETTING_BANNER_ID = "launcher_setting_banner_id";
    public static final String LAUNCHER_SETTING_NATIVE_ID = "launcher_setting_native_id";

    // ==================== callend_screen ====================
    /** Call end screen — bottom ads, notification countries, interstitial config */
    public static final String CALLEND_SCREEN = "callend_screen";
    /** Show call end screen after call */
    public static final String IS_CALLEND_SHOW = "is_callend_show";
    /** Show bottom ad on call end screen */
    public static final String IS_CALLEND_BOTTOM_AD_SHOW = "is_callend_bottom_ad_show";
    public static final String CALLEND_BOTTOM_ADS_TYPE = "callend_bottom_ads_type";
    public static final String CALLEND_BANNER_AD_ID = "callend_banner_ad_id";
    public static final String CALLEND_NATIVE_AD_ID = "callend_native_ad_id";
    /** Days after install to show notification */
    public static final String NOTIFICATION_INSTALL_DAYS = "notification_install_days";
    /** Days after install to show call notification */
    public static final String NOTIFICATION_CALL_INSTALL_DAYS = "notification_call_install_days";
    /** Days after install to show call overlay notification */
    public static final String NOTIFICATION_CALL_OVERLAY_INSTALL_DAYS = "notification_call_overlay_install_days";
    /** Get user country via IP address */
    public static final String COUNTRY_GET_WITH_IP = "country_get_with_ip";
    /** Show all allow permission in FB notification */
    public static final String ALL_ALLOW_PERMISSION_SHOW_FB_NOTIFICATION = "All_allow_permission_show_fb_notification";
    /** Show close button on full native ads */
    public static final String CLOSE_BUTTON_SHOW_ON_FULL_NATIVE_ADS = "close_button_show_on_full_native_ads";
    /** Notification allowed countries list */
    public static final String NOTIFICATION_COUNTRY = "notification_country";
    /** Call notification allowed countries list */
    public static final String NOTIFICATION_CALL_COUNTRY = "notification_call_country";
    /** Call overlay notification allowed countries list */
    public static final String NOTIFICATION_CALL_OVERLAY_COUNTRY = "notification_call_overlay_country";

    // ==================== call_end_bacK_inter (inside callend_screen) ====================
    /** Call end back interstitial — ad type, ID, and show countries */
    public static final String CALL_END_BACK_INTER = "call_end_bacK_inter";
    /** Show interstitial ad on call end back press */
    public static final String CALL_END_INTER_ADS_SHOW = "call_end_inter_ads_show";
    /** Show back ads on notification screen */
    public static final String NOTIFICATION_SCREEN_BACK_ADS_SHOW = "notification_screen_back_ads_show";
    public static final String CALL_END_INTER_ADS_TYPE = "call_end_inter_ads_type";
    /** Days count for call end interstitial */
    public static final String CALL_END_INTER_DAY_COUNT = "call_end_inter_day_count";
    /** Call end screen reopen count before interstitial */
    public static final String CALLEND_AGAIN_OPEN_COUNT = "callend_again_open_count";
    /** Total active show count for call end interstitial */
    public static final String CALL_END_INTER_ACTIVE_TOTAL_SHOW_COUNT = "call_end_inter_active_total_show_count";
    public static final String CALLEND_INTER_AD_ID = "callend_inter_ad_id";
    /** Countries where call end interstitial ads are shown */
    public static final String CALL_END_INTER_ADS_SHOW_COUNTRY = "call_end_inter_ads_show_country";

    // ==================== other_screen ====================
    /** Other / global screens — interstitial timing, shared native/banner IDs, exit ad */
    public static final String OTHER_SCREEN = "other_screen";
    /** First interstitial show delay time */
    public static final String INTER_FIRST_TIME = "inter_first_time";
    /** Second interstitial show delay time */
    public static final String INTER_SECOND_TIME = "inter_second_time";
    public static final String OTHER_NATIVE_ID = "other_native_id";
    public static final String OTHER_BANNER_ID = "other_banner_id";
    /** App-level interstitial ads load type (inside other_screen) */
    public static final String OTHER_INTER_ADS_LOAD_TYPE = "inter_ads_load_type";
    public static final String OTHER_BOTTOM_ADS_TYPE = "other_bottom_ads_type";
    /** Show bottom ad on other screens (legacy fallback) */
    public static final String OTHER_BOTTOM_ADS_SHOW = "other_bottom_ads_show";
    /** Show interstitial ads globally */
    public static final String INTER_ADS_SHOW = "inter_ads_show";
    /** Show interstitial ad on back press */
    public static final String INTER_ADS_SHOW_ON_BACK = "inter_ads_show_on_back";
    /** Exit dialog native ad unit ID */
    public static final String EXIT_NATIVE_ID = "exit_native_id";

    // ==================== Per-activity bottom ads (same shape as recent_launcher_screen) ====================
    public static final String AGENDA_SCREEN = "agenda_screen";
    public static final String AGENDA_BOTTOM_ADS_SHOW = "agenda_bottom_ads_show";
    public static final String AGENDA_ADS_TYPE = "agenda_ads_type";
    public static final String AGENDA_BANNER_ID = "agenda_banner_id";
    public static final String AGENDA_NATIVE_ID = "agenda_native_id";
    public static final String AGENDA_BUTTON_ADS_SHOW = "agenda_button_ads_show";
    public static final String AGENDA_BACK_ADS_SHOW = "agenda_back_ads_show";

    public static final String EVENT_ADD_SCREEN = "event_add_screen";
    public static final String EVENT_ADD_BOTTOM_ADS_SHOW = "event_add_bottom_ads_show";
    public static final String EVENT_ADD_ADS_TYPE = "event_add_ads_type";
    public static final String EVENT_ADD_BANNER_ID = "event_add_banner_id";
    public static final String EVENT_ADD_NATIVE_ID = "event_add_native_id";
    public static final String EVENT_ADD_BUTTON_ADS_SHOW = "event_add_button_ads_show";
    public static final String EVENT_ADD_BACK_ADS_SHOW = "event_add_back_ads_show";

    public static final String EVENT_DETAIL_SCREEN = "event_detail_screen";
    public static final String EVENT_DETAIL_BOTTOM_ADS_SHOW = "event_detail_bottom_ads_show";
    public static final String EVENT_DETAIL_ADS_TYPE = "event_detail_ads_type";
    public static final String EVENT_DETAIL_BANNER_ID = "event_detail_banner_id";
    public static final String EVENT_DETAIL_NATIVE_ID = "event_detail_native_id";
    public static final String EVENT_DETAIL_BUTTON_ADS_SHOW = "event_detail_button_ads_show";
    public static final String EVENT_DETAIL_BACK_ADS_SHOW = "event_detail_back_ads_show";

    public static final String ADD_TASK_SCREEN = "add_task_screen";
    public static final String ADD_TASK_BOTTOM_ADS_SHOW = "add_task_bottom_ads_show";
    public static final String ADD_TASK_ADS_TYPE = "add_task_ads_type";
    public static final String ADD_TASK_BANNER_ID = "add_task_banner_id";
    public static final String ADD_TASK_NATIVE_ID = "add_task_native_id";
    public static final String ADD_TASK_BUTTON_ADS_SHOW = "add_task_button_ads_show";
    public static final String ADD_TASK_BACK_ADS_SHOW = "add_task_back_ads_show";

    public static final String TASK_DETAIL_SCREEN = "task_detail_screen";
    public static final String TASK_DETAIL_BOTTOM_ADS_SHOW = "task_detail_bottom_ads_show";
    public static final String TASK_DETAIL_ADS_TYPE = "task_detail_ads_type";
    public static final String TASK_DETAIL_BANNER_ID = "task_detail_banner_id";
    public static final String TASK_DETAIL_NATIVE_ID = "task_detail_native_id";
    public static final String TASK_DETAIL_BUTTON_ADS_SHOW = "task_detail_button_ads_show";
    public static final String TASK_DETAIL_BACK_ADS_SHOW = "task_detail_back_ads_show";

    public static final String ADD_MEMO_SCREEN = "add_memo_screen";
    public static final String ADD_MEMO_BOTTOM_ADS_SHOW = "add_memo_bottom_ads_show";
    public static final String ADD_MEMO_ADS_TYPE = "add_memo_ads_type";
    public static final String ADD_MEMO_BANNER_ID = "add_memo_banner_id";
    public static final String ADD_MEMO_NATIVE_ID = "add_memo_native_id";
    public static final String ADD_MEMO_BUTTON_ADS_SHOW = "add_memo_button_ads_show";
    public static final String ADD_MEMO_BACK_ADS_SHOW = "add_memo_back_ads_show";

    public static final String MEMO_DETAILS_SCREEN = "memo_details_screen";
    public static final String MEMO_DETAILS_BOTTOM_ADS_SHOW = "memo_details_bottom_ads_show";
    public static final String MEMO_DETAILS_ADS_TYPE = "memo_details_ads_type";
    public static final String MEMO_DETAILS_BANNER_ID = "memo_details_banner_id";
    public static final String MEMO_DETAILS_NATIVE_ID = "memo_details_native_id";
    public static final String MEMO_DETAILS_BUTTON_ADS_SHOW = "memo_details_button_ads_show";
    public static final String MEMO_DETAILS_BACK_ADS_SHOW = "memo_details_back_ads_show";

    public static final String CUSTOM_EVENT_LIST_SCREEN = "custom_event_list_screen";
    public static final String CUSTOM_EVENT_LIST_BOTTOM_ADS_SHOW = "custom_event_list_bottom_ads_show";
    public static final String CUSTOM_EVENT_LIST_ADS_TYPE = "custom_event_list_ads_type";
    public static final String CUSTOM_EVENT_LIST_BANNER_ID = "custom_event_list_banner_id";
    public static final String CUSTOM_EVENT_LIST_NATIVE_ID = "custom_event_list_native_id";
    public static final String CUSTOM_EVENT_LIST_BUTTON_ADS_SHOW = "custom_event_list_button_ads_show";
    public static final String CUSTOM_EVENT_LIST_BACK_ADS_SHOW = "custom_event_list_back_ads_show";

    public static final String ALARM_SCREEN = "alarm_screen";
    public static final String ALARM_BOTTOM_ADS_SHOW = "alarm_bottom_ads_show";
    public static final String ALARM_ADS_TYPE = "alarm_ads_type";
    public static final String ALARM_BANNER_ID = "alarm_banner_id";
    public static final String ALARM_NATIVE_ID = "alarm_native_id";
    public static final String ALARM_BUTTON_ADS_SHOW = "alarm_button_ads_show";
    public static final String ALARM_BACK_ADS_SHOW = "alarm_back_ads_show";

    public static final String RINGTONE_SELECT_SCREEN = "ringtone_select_screen";
    public static final String RINGTONE_SELECT_BOTTOM_ADS_SHOW = "ringtone_select_bottom_ads_show";
    public static final String RINGTONE_SELECT_ADS_TYPE = "ringtone_select_ads_type";
    public static final String RINGTONE_SELECT_BANNER_ID = "ringtone_select_banner_id";
    public static final String RINGTONE_SELECT_NATIVE_ID = "ringtone_select_native_id";
    public static final String RINGTONE_SELECT_BUTTON_ADS_SHOW = "ringtone_select_button_ads_show";
    public static final String RINGTONE_SELECT_BACK_ADS_SHOW = "ringtone_select_back_ads_show";

    public static final String THEME_SCREEN = "theme_screen";
    public static final String THEME_BOTTOM_ADS_SHOW = "theme_bottom_ads_show";
    public static final String THEME_ADS_TYPE = "theme_ads_type";
    public static final String THEME_BANNER_ID = "theme_banner_id";
    public static final String THEME_NATIVE_ID = "theme_native_id";
    public static final String THEME_BUTTON_ADS_SHOW = "theme_button_ads_show";
    public static final String THEME_BACK_ADS_SHOW = "theme_back_ads_show";

    public static final String THEME_APPLY_SCREEN = "theme_apply_screen";
    public static final String THEME_APPLY_BOTTOM_ADS_SHOW = "theme_apply_bottom_ads_show";
    public static final String THEME_APPLY_ADS_TYPE = "theme_apply_ads_type";
    public static final String THEME_APPLY_BANNER_ID = "theme_apply_banner_id";
    public static final String THEME_APPLY_NATIVE_ID = "theme_apply_native_id";
    public static final String THEME_APPLY_BUTTON_ADS_SHOW = "theme_apply_button_ads_show";
    public static final String THEME_APPLY_BACK_ADS_SHOW = "theme_apply_back_ads_show";

    public static final String WIDGET_SCREEN = "widget_screen";
    public static final String WIDGET_BOTTOM_ADS_SHOW = "widget_bottom_ads_show";
    public static final String WIDGET_ADS_TYPE = "widget_ads_type";
    public static final String WIDGET_BANNER_ID = "widget_banner_id";
    public static final String WIDGET_NATIVE_ID = "widget_native_id";
    public static final String WIDGET_BUTTON_ADS_SHOW = "widget_button_ads_show";
    public static final String WIDGET_BACK_ADS_SHOW = "widget_back_ads_show";

    public static final String WIDGET_SETTING_SCREEN = "widget_setting_screen";
    public static final String WIDGET_SETTING_BOTTOM_ADS_SHOW = "widget_setting_bottom_ads_show";
    public static final String WIDGET_SETTING_ADS_TYPE = "widget_setting_ads_type";
    public static final String WIDGET_SETTING_BANNER_ID = "widget_setting_banner_id";
    public static final String WIDGET_SETTING_NATIVE_ID = "widget_setting_native_id";
    public static final String WIDGET_SETTING_BUTTON_ADS_SHOW = "widget_setting_button_ads_show";
    public static final String WIDGET_SETTING_BACK_ADS_SHOW = "widget_setting_back_ads_show";

    public static final String MAP_PICKER_SCREEN = "map_picker_screen";
    public static final String MAP_PICKER_BOTTOM_ADS_SHOW = "map_picker_bottom_ads_show";
    public static final String MAP_PICKER_ADS_TYPE = "map_picker_ads_type";
    public static final String MAP_PICKER_BANNER_ID = "map_picker_banner_id";
    public static final String MAP_PICKER_NATIVE_ID = "map_picker_native_id";
    public static final String MAP_PICKER_BUTTON_ADS_SHOW = "map_picker_button_ads_show";
    public static final String MAP_PICKER_BACK_ADS_SHOW = "map_picker_back_ads_show";

    // ==================== update_app ====================
    /** In-app update dialog — update type (Flexible / Immediate) */
    public static final String UPDATE_APP = "update_app";
    /** Show in-app update dialog */
    public static final String IN_APP_UPDATE_SHOW = "in_app_update_show";
    public static final String IN_APP_UPDATE_TYPE = "in_app_update_type";
    /** Daily show count for in-app update dialog */
    public static final String IN_APP_DIALOG_DAILY_SHOW_COUNT = "in_appp_dailog_daily_show_count";

    // ==================== Quiz ads ====================
    /** Ad priority — GOOGLE or QUIZ */
    public static final String AD_PRIORITY = "Ad_Priority";
    /** When Google ad fails, show Quiz ad */
    public static final String GOOGLE_AD_FAILED_SHOW_QUIZ = "Google_Ad_Failed_Show_Quiz";
    /** Quiz ad creative / copy JSON object */
    public static final String QUIZ_ADS_DESIGN = "QuizAdsDesign";
    /** Quiz link + proxy geo routing JSON object */
    public static final String APP_PROXY_STRUCTURE = "AppProxyStructure";
    /** Call end back ad sequence for call_end_inter_ads_type = "alternate": [["inter","2"],["appopen","1"]] */
    public static final String CLEND_BACK_AD_SEQUENCE = "ClEnd_Back_Ad_Sequence";
    /** JSON object (inside "screen") holding all LauncherApp_* keys */
    public static final String LAUNCHER_APP_SCREEN = "LauncherAppScreen";
    /** Show ad when user clicks an app in launcher drawer */
    public static final String LAUNCHER_APP_CLICK_AD_SHOW = "LauncherApp_Click_Ad_Show";
    /** App click count threshold before showing launcher click ad */
    public static final String LAUNCHER_APP_COUNT = "LauncherApp_Count";
    /** Launcher click ad type — Google_Inter, Google_App_Open, Fullscreen_Native_Google, Fullscreen_Native_Quiz, etc. */
    public static final String LAUNCHER_APP_AD_TYPE = "LauncherApp_Ad_Type";
    /** Google interstitial unit ID for launcher app click ad */
    public static final String LAUNCHER_APP_INTERSTITIAL_ID = "LauncherApp_Interstitial_Id";
    /** Google fullscreen native unit ID for launcher app click ad */
    public static final String LAUNCHER_APP_FULLSCREEN_NATIVE_ID = "LauncherApp_Fullscreen_Native_Id";
    /** Show ad when user returns to launcher after closing an opened app */
    public static final String LAUNCHER_APP_BACK_CLICK_AD_SHOW = "LauncherApp_Back_Click_Ad_Show";
    /** Return-to-launcher count threshold before showing launcher back ad */
    public static final String LAUNCHER_APP_BACK_COUNT = "LauncherApp_Back_Count";
    /** Launcher back ad type — same values as LauncherApp_Ad_Type */
    public static final String LAUNCHER_APP_BACK_AD_TYPE = "LauncherApp_Back_Ad_Type";
    /** Google fullscreen native unit ID for launcher back ad */
    public static final String LAUNCHER_APP_BACK_FULLSCREEN_NATIVE_ID = "LauncherApp_Back_Fullscreen_Native_Id";
    /** Google interstitial unit ID for launcher back ad */
    public static final String LAUNCHER_APP_BACK_INTERSTITIAL_ID = "LauncherApp_Back_Interstitial_Id";
    /** Open quiz links in-app (Custom Tabs) vs external browser */
    public static final String IS_LINK_OPEN_APP = "isLinkOpenApp";
    /** When true, open quiz link directly; when false, show quiz interstitial dialog */
    public static final String IS_QUIZ_BROWSER_SHOW = "isQuizBrowserShow";
}
