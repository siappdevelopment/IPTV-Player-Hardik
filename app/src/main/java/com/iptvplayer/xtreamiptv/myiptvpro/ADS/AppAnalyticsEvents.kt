package com.iptvplayer.xtreamiptv.myiptvpro.ADS

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.iptvplayer.xtreamiptv.myiptvpro.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Firebase / GA4 analytics catalog.
 *
 * - Section A: Exact names from Event_Planner_Task_Tracker_Developer_Event_Guide.pdf
 * - Section B: App-specific screens/actions not in the PDF (same naming rules:
 *   lowercase + underscore, object_action / object_action_result)
 */
object AppAnalyticsEvents {

//    private const val TAG = "AppAnalyticsEvents"

    // ═══════════════════════════════════════════════════════════════════
    // A) PDF — Core flow / screens (exact)
    // ═══════════════════════════════════════════════════════════════════
    const val APP_OPEN = "app_open"
    const val ONBOARDING_VIEW = "onboarding_view"
    const val ONBOARDING_COMPLETE = "onboarding_complete"
    const val POLICY_VIEW = "policy_view"
    const val LANGUAGE_VIEW = "language_view"
    const val PERMISSION_VIEW = "permission_view"
    const val PERMISSION_GRANTED = "permission_granted"
    const val SET_DEFAULT_HOME_VIEW = "set_default_home_view"
    const val SET_DEFAULT_HOME_CLICK = "set_default_home_click"
    const val DEFAULT_HOME_SET_SUCCESS = "default_home_set_success"
    const val DEFAULT_HOME_SET_CANCEL = "default_home_set_cancel"
    const val LAUNCHER_HOME_VIEW = "launcher_home_view"
    const val HOME_VIEW = "home_view"
    const val CALENDAR_VIEW = "calendar_view"

    // PDF — Event / Task
    const val EVENT_CREATE_START = "event_create_start"
    const val EVENT_CREATE_SUCCESS = "event_create_success"
    const val EVENT_CREATE_FAIL = "event_create_fail"
    const val EVENT_EDIT = "event_edit"
    const val EVENT_DELETE = "event_delete"
    const val TASK_CREATE_START = "task_create_start"
    const val TASK_CREATE_SUCCESS = "task_create_success"
    const val TASK_CREATE_FAIL = "task_create_fail"
    const val TASK_EDIT = "task_edit"
    const val TASK_COMPLETE = "task_complete"
    const val TASK_DELETE = "task_delete"

    // PDF — Permission outcomes
    const val NOTIFICATION_PERMISSION_GRANTED = "notification_permission_granted"
    const val PHONE_PERMISSION_GRANTED = "phone_permission_granted"
    const val PHONE_STATE_PERMISSION_GRANTED = "phone_state_permission_granted"
    const val CALENDAR_PERMISSION_GRANTED = "calendar_permission_granted"
    const val OVERLAY_PERMISSION_GRANTED = "overlay_permission_granted"

    // PDF — App lifecycle
    const val APP_BACKGROUND = "app_background"
    const val APP_FOREGROUND = "app_foreground"
    const val APP_DATA_CLEARED = "app_data_cleared"

    // PDF — Ads
    const val BANNER_LOAD = "banner_load"
    const val BANNER_IMPRESSION = "banner_impression"
    const val BANNER_CLICK = "banner_click"
    const val BANNER_FAIL = "banner_fail"
    const val NATIVE_LOAD = "native_load"
    const val NATIVE_IMPRESSION = "native_impression"
    const val NATIVE_CLICK = "native_click"
    const val NATIVE_FAIL = "native_fail"
    const val INTERSTITIAL_LOAD = "interstitial_load"
    const val INTERSTITIAL_IMPRESSION = "interstitial_impression"
    const val INTERSTITIAL_CLICK = "interstitial_click"
    const val INTERSTITIAL_FAIL = "interstitial_fail"
    const val INTERSTITIAL_PRELOAD = "interstitial_preload"
    const val REWARDED_LOAD = "rewarded_load"
    const val REWARDED_IMPRESSION = "rewarded_impression"
    const val REWARDED_COMPLETE = "rewarded_complete"
    const val REWARDED_FAIL = "rewarded_fail"
    const val SPLASH_INTERSTITIAL_PRELOAD = "splash_interstitial_preload"
    const val DEFAULT_PERMISSION_INTERSTITIAL_LOAD = "default_permission_interstitial_load"
    const val DEFAULT_PERMISSION_INTERSTITIAL_IMPRESSION = "default_permission_interstitial_impression"
    const val DEFAULT_PERMISSION_INTERSTITIAL_FAIL = "default_permission_interstitial_fail"
    const val APP_OPEN_AD_LOAD = "app_open_ad_load"
    const val APP_OPEN_AD_IMPRESSION = "app_open_ad_impression"
    const val APP_OPEN_AD_FAIL = "app_open_ad_fail"

    // ═══════════════════════════════════════════════════════════════════
    // B) App-specific screens (not in PDF)
    // ═══════════════════════════════════════════════════════════════════
    const val SPLASH_VIEW = "splash_view"
    const val ONBOARDING_STEP_VIEW = "onboarding_step_view"
    const val OVERLAY_PERMISSION_VIEW = "overlay_permission_view"
    const val TASK_VIEW = "task_view"
    const val TASK_DETAIL_VIEW = "task_detail_view"
    const val TASK_ADD_VIEW = "task_add_view"
    const val EVENT_DETAIL_VIEW = "event_detail_view"
    const val EVENT_ADD_VIEW = "event_add_view"
    const val MEMO_VIEW = "memo_view"
    const val MEMO_ADD_VIEW = "memo_add_view"
    const val MEMO_DETAIL_VIEW = "memo_detail_view"
    const val MINE_VIEW = "mine_view"
    const val SETTINGS_VIEW = "settings_view"
    const val SETTINGS_LANGUAGE_VIEW = "settings_language_view"
    const val THEME_VIEW = "theme_view"
    const val THEME_APPLY_VIEW = "theme_apply_view"
    const val AGENDA_VIEW = "agenda_view"
    const val CUSTOM_EVENT_LIST_VIEW = "custom_event_list_view"
    const val WIDGET_VIEW = "widget_view"
    const val WIDGET_SETTING_VIEW = "widget_setting_view"
    const val ALARM_VIEW = "alarm_view"
    const val RINGTONE_SELECT_VIEW = "ringtone_select_view"
    const val MAP_PICKER_VIEW = "map_picker_view"
    const val CALL_END_VIEW = "call_end_view"
    const val LAUNCHER_SETTINGS_VIEW = "launcher_settings_view"
    const val LAUNCHER_APPS_VIEW = "launcher_apps_view"
    const val LAUNCHER_RECENT_VIEW = "launcher_recent_view"
    const val COMING_SOON_VIEW = "coming_soon_view"
    const val MORE_FEATURES_VIEW = "more_features_view"

    // ═══════════════════════════════════════════════════════════════════
    // B) App-specific actions (not in PDF)
    // ═══════════════════════════════════════════════════════════════════
    const val LANGUAGE_SELECT = "language_select"
    const val LANGUAGE_CONTINUE = "language_continue"
    const val POLICY_AGREE = "policy_agree"
    const val MEMO_CREATE_START = "memo_create_start"
    const val MEMO_CREATE_SUCCESS = "memo_create_success"
    const val MEMO_CREATE_FAIL = "memo_create_fail"
    const val MEMO_EDIT = "memo_edit"
    const val MEMO_DELETE = "memo_delete"
    const val MEMO_SHARE = "memo_share"
    const val MEMO_PIN = "memo_pin"
    const val MEMO_ARCHIVE = "memo_archive"
    const val THEME_APPLY_SUCCESS = "theme_apply_success"
    const val THEME_SELECT = "theme_select"
    const val SETTINGS_LANGUAGE_CLICK = "settings_language_click"
    const val SETTINGS_NOTIFICATION_CLICK = "settings_notification_click"
    const val SETTINGS_SHARE_CLICK = "settings_share_click"
    const val SETTINGS_RATE_CLICK = "settings_rate_click"
    const val SETTINGS_PRIVACY_CLICK = "settings_privacy_click"
    const val SETTINGS_RATE_SUBMIT = "settings_rate_submit"
    const val WIDGET_ADD_CLICK = "widget_add_click"
    const val WIDGET_ADD_SUCCESS = "widget_add_success"
    const val AGENDA_CREATE_EVENT_CLICK = "agenda_create_event_click"
    const val DRAWER_HOLIDAYS_CLICK = "drawer_holidays_click"
    const val DRAWER_EVENTS_CLICK = "drawer_events_click"
    const val DRAWER_WIDGETS_CLICK = "drawer_widgets_click"
    const val DRAWER_THEMES_CLICK = "drawer_themes_click"
    const val DRAWER_SETTINGS_CLICK = "drawer_settings_click"
    const val DRAWER_REFRESH_CLICK = "drawer_refresh_click"
    const val CALENDAR_MODE_CHANGE = "calendar_mode_change"
    const val TAB_SWITCH = "tab_switch"
    const val CALL_END_ACTION = "call_end_action"
    const val CALL_END_CLOSE = "call_end_close"
    const val CALL_END_DIAL = "call_end_dial"
    const val CALL_END_TAB_SWITCH = "call_end_tab_switch"
    const val CALL_SHOW_VIEW = "call_show_view"
    const val NEW_MESSAGE_VIEW = "new_message_view"
    const val NEW_REMIND_VIEW = "new_remind_view"
    const val OVERLAY_PERMISSION_ALLOW = "overlay_permission_allow"
    const val OVERLAY_PERMISSION_DENY = "overlay_permission_deny"
    const val ALARM_DISMISS = "alarm_dismiss"
    const val ALARM_SNOOZE = "alarm_snooze"
    const val RINGTONE_SELECT = "ringtone_select"
    const val MAP_LOCATION_SELECT = "map_location_select"
    const val LAUNCHER_APP_OPEN = "launcher_app_open"
    const val LAUNCHER_APP_UNINSTALL = "launcher_app_uninstall"
    const val ONBOARDING_NEXT_CLICK = "onboarding_next_click"
    const val PERMISSION_ALLOW_CLICK = "permission_allow_click"
    const val SETTINGS_LANGUAGE_CONTINUE = "settings_language_continue"
    const val LAUNCHER_SETTINGS_SORT_CHANGE = "launcher_settings_sort_change"
    const val LAUNCHER_SETTINGS_LABEL_TOGGLE = "launcher_settings_label_toggle"
    const val WIDGET_SETTING_SAVE = "widget_setting_save"
    const val CUSTOM_EVENT_CREATE_CLICK = "custom_event_create_click"
    const val PARAM_PACKAGE_NAME = "package_name"
    const val PARAM_LANGUAGE_CODE = "language_code"
    const val PARAM_ACTION = "action"

    // Param keys (PDF suggested)
    const val PARAM_SCREEN_NAME = "screen_name"
    const val PARAM_SOURCE = "source"
    const val PARAM_PLACEMENT = "placement"
    const val PARAM_PERMISSION_TYPE = "permission_type"
    const val PARAM_RESULT = "result"
    const val PARAM_EVENT_TYPE = "event_type"
    const val PARAM_REMINDER_TYPE = "reminder_type"
    const val PARAM_WIDGET_TYPE = "widget_type"
    const val PARAM_THEME_ID = "theme_id"
    const val PARAM_TAB = "tab"
    const val PARAM_MODE = "mode"
    const val PARAM_STEP = "step"

    @JvmStatic
    @JvmOverloads
    fun track(context: Context?, eventName: String, params: Bundle? = null) {
        if (context == null || eventName.isBlank()) return
        try {
            val bundle = params ?: Bundle()
            if (bundle.isEmpty) {
                bundle.putBoolean(eventName, true)
            }
            if (BuildConfig.DEBUG) {
//                Log.d(TAG, "track: $eventName params=$bundle")
            }
            FirebaseAnalytics.getInstance(context.applicationContext).logEvent(eventName, bundle)
        } catch (e: Exception) {
//            Log.e(TAG, "Failed to log event: $eventName", e)
        }
    }

    @JvmStatic
    fun track(context: Context?, eventName: String, key: String, value: String) {
        track(context, eventName, Bundle().apply { putString(key, value) })
    }

    @JvmStatic
    fun trackWithPlacement(context: Context?, eventName: String, placement: String) {
        track(context, eventName, PARAM_PLACEMENT, placement)
    }

    /** Named screen event + GA4 screen_view (PDF KEEP). */
    @JvmStatic
    fun trackScreen(context: Context?, eventName: String, screenName: String) {
        if (context == null) return
        track(context, eventName, Bundle().apply { putString(PARAM_SCREEN_NAME, screenName) })
        trackScreenView(context, screenName)
    }

    @JvmStatic
    fun trackScreenView(context: Context?, screenName: String) {
        if (context == null || screenName.isBlank()) return
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
            }
//            if (BuildConfig.DEBUG) Log.d(TAG, "screen_view: $screenName")
            FirebaseAnalytics.getInstance(context.applicationContext)
                .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        } catch (e: Exception) {
//            Log.e(TAG, "Failed to log screen_view: $screenName", e)
        }
    }

    /** Called from ActivityStackTracker when any Activity is displayed. */
    @JvmStatic
    fun trackActivityDisplayed(activity: Activity?) {
        if (activity == null) return
        when (activity.javaClass.simpleName) {
            "SplashActivity", "BaseSplashActivity" ->
                trackScreen(activity, SPLASH_VIEW, "splash")
            "LanguageActivity" ->
                trackScreen(activity, LANGUAGE_VIEW, "language")
            "Intro1Activity" ->
                trackScreen(activity, ONBOARDING_VIEW, "onboarding_1")
            "Intro2Activity" -> {
                track(activity, ONBOARDING_STEP_VIEW, Bundle().apply {
                    putString(PARAM_SCREEN_NAME, "onboarding_2")
                    putInt(PARAM_STEP, 2)
                })
                trackScreenView(activity, "onboarding_2")
            }
            "Intro3Activity" -> {
                track(activity, ONBOARDING_STEP_VIEW, Bundle().apply {
                    putString(PARAM_SCREEN_NAME, "onboarding_3")
                    putInt(PARAM_STEP, 3)
                })
                trackScreenView(activity, "onboarding_3")
            }
            "PrivacyPolicySetting" ->
                trackScreen(activity, POLICY_VIEW, "policy")
            "PermissionActivity" ->
                trackScreen(activity, PERMISSION_VIEW, "permission")
            "DefaultAppActivity" ->
                trackScreen(activity, SET_DEFAULT_HOME_VIEW, "set_default_home")
            "HomeActivity" ->
                trackScreen(activity, HOME_VIEW, "home")
            "LauncherHomeActivity" ->
                trackScreen(activity, LAUNCHER_HOME_VIEW, "launcher_home")
            "OverlayPermissionActivity" ->
                trackScreen(activity, OVERLAY_PERMISSION_VIEW, "overlay_permission")
            "EventAddActivity" ->
                trackScreen(activity, EVENT_ADD_VIEW, "event_add")
            "EventDetailActivity" ->
                trackScreen(activity, EVENT_DETAIL_VIEW, "event_detail")
            "AddTaskActivity" ->
                trackScreen(activity, TASK_ADD_VIEW, "task_add")
            "TaskDetailActivity" ->
                trackScreen(activity, TASK_DETAIL_VIEW, "task_detail")
            "AddMemoActivity" ->
                trackScreen(activity, MEMO_ADD_VIEW, "memo_add")
            "MemoDetailsActivity" ->
                trackScreen(activity, MEMO_DETAIL_VIEW, "memo_detail")
            "SettingActivity" ->
                trackScreen(activity, SETTINGS_VIEW, "settings")
            "SettingLanguageActivity" ->
                trackScreen(activity, SETTINGS_LANGUAGE_VIEW, "settings_language")
            "ThemeActivity" ->
                trackScreen(activity, THEME_VIEW, "theme")
            "ThemeApplyActivity" ->
                trackScreen(activity, THEME_APPLY_VIEW, "theme_apply")
            "AgendaActivity" ->
                trackScreen(activity, AGENDA_VIEW, "agenda")
            "CustomEventListActivity" ->
                trackScreen(activity, CUSTOM_EVENT_LIST_VIEW, "custom_event_list")
            "WidgetActivity" ->
                trackScreen(activity, WIDGET_VIEW, "widget")
            "WidgetSettingActivity" ->
                trackScreen(activity, WIDGET_SETTING_VIEW, "widget_setting")
            "AlarmActivity" ->
                trackScreen(activity, ALARM_VIEW, "alarm")
            "RingtonSelectActivity" ->
                trackScreen(activity, RINGTONE_SELECT_VIEW, "ringtone_select")
            "MapPickerActivity" ->
                trackScreen(activity, MAP_PICKER_VIEW, "map_picker")
            "CallEndActivity" ->
                trackScreen(activity, CALL_END_VIEW, "call_end")
            "LauncherSettingsActivity" ->
                trackScreen(activity, LAUNCHER_SETTINGS_VIEW, "launcher_settings")
            "CommingActivity" ->
                trackScreen(activity, COMING_SOON_VIEW, "coming_soon")
            else -> {
                val screen = toSnakeCase(activity.javaClass.simpleName.removeSuffix("Activity"))
                trackScreenView(activity, screen)
            }
        }
    }

    /** Home / launcher tabs & fragments. */
    @JvmStatic
    fun trackHomeTabDisplayed(context: Context?, tab: String) {
        if (context == null || tab.isBlank()) return
        when (tab) {
            "calendar" -> trackScreen(context, CALENDAR_VIEW, "calendar")
            "task" -> trackScreen(context, TASK_VIEW, "task")
            "memo" -> trackScreen(context, MEMO_VIEW, "memo")
            "mine" -> trackScreen(context, MINE_VIEW, "mine")
            "launcher_home" -> trackScreen(context, LAUNCHER_HOME_VIEW, "launcher_home")
            "launcher_apps" -> trackScreen(context, LAUNCHER_APPS_VIEW, "launcher_apps")
            "launcher_recent" -> trackScreen(context, LAUNCHER_RECENT_VIEW, "launcher_recent")
            else -> trackScreenView(context, tab)
        }
        track(context, TAB_SWITCH, PARAM_TAB, tab)
    }

    private fun toSnakeCase(value: String): String {
        if (value.isBlank()) return "unknown"
        return value.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
    }
}
