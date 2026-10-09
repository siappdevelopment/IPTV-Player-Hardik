package com.iptvplayer.xtreamiptv.myiptvpro.ADS

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common.Utils
import com.iptvplayer.xtreamiptv.myiptvpro.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * One-shot Firebase events for permission / default-home outcomes.
 * Event names match Event_Planner_Task_Tracker_Developer_Event_Guide.pdf.
 */
object PermissionFirebaseEvents {

    private const val TAG = "PermissionFirebaseEvents"
    private const val PREFS_NAME = "permission_firebase_events"
    private const val KEY_PREFIX = "fired_"

    const val POST_NOTIFICATIONS_GRANTED = AppAnalyticsEvents.NOTIFICATION_PERMISSION_GRANTED
    const val READ_PHONE_STATE_GRANTED = AppAnalyticsEvents.PHONE_STATE_PERMISSION_GRANTED
    const val CALL_PHONE_GRANTED = AppAnalyticsEvents.PHONE_PERMISSION_GRANTED
    const val GET_ACCOUNTS_GRANTED = AppAnalyticsEvents.PERMISSION_GRANTED
    const val READ_CALENDAR_GRANTED = AppAnalyticsEvents.CALENDAR_PERMISSION_GRANTED
    const val OVERLAY_GRANTED = AppAnalyticsEvents.OVERLAY_PERMISSION_GRANTED
    const val DEFAULT_HOME_APP_SET = AppAnalyticsEvents.DEFAULT_HOME_SET_SUCCESS
    const val DEFAULT_HOME_APP_CANCEL = AppAnalyticsEvents.DEFAULT_HOME_SET_CANCEL

    /** Fire event once. Safe to call from any grant / cancel callback. */
    @JvmStatic
    fun trackOnce(context: Context?, eventName: String) {
        if (context == null || eventName.isBlank()) return

        val appContext = context.applicationContext
        val prefs = prefs(appContext)
        val key = KEY_PREFIX + eventName

        if (prefs.getBoolean(key, false)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Skip (already fired): $eventName")
            }
            return
        }

        prefs.edit().putBoolean(key, true).apply()

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Trigger event [DEBUG]: $eventName")
        } else {
            Log.i(TAG, "Trigger event [RELEASE]: $eventName")
        }

        try {
            val bundle = Bundle().apply { putBoolean(eventName, true) }
            FirebaseAnalytics.getInstance(appContext).logEvent(eventName, bundle)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log Firebase event: $eventName", e)
        }
    }

    @JvmStatic
    fun trackPostNotificationsGranted(context: Context?) {
        trackOnce(context, POST_NOTIFICATIONS_GRANTED)
        trackOnce(context, AppAnalyticsEvents.PERMISSION_GRANTED)
    }

    @JvmStatic
    fun trackReadPhoneStateGranted(context: Context?) {
        trackOnce(context, READ_PHONE_STATE_GRANTED)
        trackOnce(context, AppAnalyticsEvents.PERMISSION_GRANTED)
    }

    @JvmStatic
    fun trackCallPhoneGranted(context: Context?) {
        trackOnce(context, CALL_PHONE_GRANTED)
        trackOnce(context, AppAnalyticsEvents.PERMISSION_GRANTED)
    }

    @JvmStatic
    fun trackGetAccountsGranted(context: Context?) {
        // PDF: use permission_granted + permission_type param (no dedicated accounts event)
        if (context == null) return
        val prefs = prefs(context.applicationContext)
        val key = KEY_PREFIX + "accounts_granted"
        if (prefs.getBoolean(key, false)) return
        prefs.edit().putBoolean(key, true).apply()
        AppAnalyticsEvents.track(
            context,
            AppAnalyticsEvents.PERMISSION_GRANTED,
            Bundle().apply { putString("permission_type", "get_accounts") }
        )
    }

    @JvmStatic
    fun trackReadCalendarGranted(context: Context?) {
        trackOnce(context, READ_CALENDAR_GRANTED)
        trackOnce(context, AppAnalyticsEvents.PERMISSION_GRANTED)
    }

    @JvmStatic
    fun trackOverlayGranted(context: Context?) {
        trackOnce(context, OVERLAY_GRANTED)
        trackOnce(context, AppAnalyticsEvents.PERMISSION_GRANTED)
    }

    @JvmStatic
    fun trackDefaultHomeAppSet(context: Context?) =
        trackOnce(context, DEFAULT_HOME_APP_SET)

    @JvmStatic
    fun trackDefaultHomeAppCancel(context: Context?) =
        trackOnce(context, DEFAULT_HOME_APP_CANCEL)

    /**
     * Maps a runtime permission result map to the matching once-only events.
     * Call from RequestMultiplePermissions / RequestPermission callbacks.
     */
    @JvmStatic
    fun trackFromPermissionResults(context: Context?, results: Map<String, Boolean>?) {
        if (context == null || results.isNullOrEmpty()) return
        results.forEach { (permission, granted) ->
            if (granted) trackAndroidPermission(context, permission)
        }
    }

    /**
     * Sync events for permissions that are already granted (splash / permission screens / home).
     * Safe to call repeatedly — SharedPreferences blocks duplicates.
     */
    @JvmStatic
    fun syncGrantedEvents(context: Context?) {
        if (context == null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            isGranted(context, Manifest.permission.POST_NOTIFICATIONS)
        ) {
            trackPostNotificationsGranted(context)
        }
        if (isGranted(context, Manifest.permission.READ_PHONE_STATE)) {
            trackReadPhoneStateGranted(context)
        }
        if (isGranted(context, Manifest.permission.CALL_PHONE)) {
            trackCallPhoneGranted(context)
        }
        if (isGranted(context, Manifest.permission.GET_ACCOUNTS)) {
            trackGetAccountsGranted(context)
        }
        if (isGranted(context, Manifest.permission.READ_CALENDAR)) {
            trackReadCalendarGranted(context)
        }
        if (isOverlayGranted(context)) {
            trackOverlayGranted(context)
        }
        if (Utils.isDefaultHomeApp(context)) {
            trackDefaultHomeAppSet(context)
        }
    }

    private fun trackAndroidPermission(context: Context, permission: String) {
        when (permission) {
            Manifest.permission.POST_NOTIFICATIONS -> trackPostNotificationsGranted(context)
            Manifest.permission.READ_PHONE_STATE -> trackReadPhoneStateGranted(context)
            Manifest.permission.CALL_PHONE -> trackCallPhoneGranted(context)
            Manifest.permission.GET_ACCOUNTS -> trackGetAccountsGranted(context)
            Manifest.permission.READ_CALENDAR -> trackReadCalendarGranted(context)
            else -> {
                if (BuildConfig.DEBUG) Log.d(TAG, "No mapped event for permission: $permission")
            }
        }
    }

    private fun isOverlayGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    private fun isGranted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
