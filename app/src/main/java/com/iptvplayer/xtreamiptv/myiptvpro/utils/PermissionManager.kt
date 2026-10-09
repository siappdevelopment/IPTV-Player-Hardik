package com.iptvplayer.xtreamiptv.myiptvpro.utils

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents
import android.app.AppOpsManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telecom.TelecomManager
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSUtilitis

object PermissionManager {

    fun isDefaultDialer(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
        } else {
            val telecomManager =
                context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val defaultDialer = telecomManager.defaultDialerPackage
            context.packageName == defaultDialer
        }
    }

    fun checkAndTrackDefaultDialer(context: Context) {
        if (isDefaultDialer(context)) {
            val isTracked = SharedPreferenceManager.getBoolean(context, "is_default_allow_tracked", false)
            if (!isTracked) {
                AppAnalyticsEvents.track(
                    context,
                    AppAnalyticsEvents.PERMISSION_GRANTED,
                    "permission_type",
                    "default_dialer"
                )
                SharedPreferenceManager.putBoolean(context, "is_default_allow_tracked", true)
            }
        }
    }

    fun isPermissionDialogShown(context: Context): Boolean {
        return SharedPreferenceManager.getBoolean(context, Constance.PERMISSION_DIALOG_SHOWN, false)
    }

    fun setPermissionDialogShown(context: Context) {
        SharedPreferenceManager.putBoolean(context, Constance.PERMISSION_DIALOG_SHOWN, true)
    }

    fun needsOverlayPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !hasOverlayPermission(context) &&
                !SharedPreferenceManager.getBoolean(context, Constance.OVERLAY_PERMISSION_SKIP)
    }

    @JvmStatic
    fun getOverlaySettingsIntent(context: Context): Intent {
        return Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.fromParts("package", context.packageName, null)
        )
    }

    @JvmStatic
    fun hasOverlayPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true

        // 1. Standard check
        if (Settings.canDrawOverlays(context)) return true

        // 2. AppOps fallback
        try {
            val appOps =
                context.applicationContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                android.os.Process.myUid(),
                context.packageName
            )
            if (mode == AppOpsManager.MODE_ALLOWED) return true

            // 3. Exception-based check for Android 8 (more aggressive)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                try {
                    appOps.checkOp(
                        AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                        android.os.Process.myUid(),
                        context.packageName
                    )
                    return true
                } catch (_: SecurityException) {
                    // Permission truly denied
                }
            }

            // 4. Numeric fallback via reflection
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                val method = appOps.javaClass.getMethod(
                    "checkOpNoThrow",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
                val reflectionMode = method.invoke(
                    appOps,
                    24, // OP_SYSTEM_ALERT_WINDOW
                    android.os.Process.myUid(),
                    context.packageName
                ) as Int
                return reflectionMode == AppOpsManager.MODE_ALLOWED
            }
        } catch (_: Exception) {
        }

        return false
    }
}