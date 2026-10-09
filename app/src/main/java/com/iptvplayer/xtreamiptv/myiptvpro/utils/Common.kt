package com.iptvplayer.xtreamiptv.myiptvpro.utils

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSAppManage
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation.StartupFlowManager
import com.iptvplayer.xtreamiptv.myiptvpro.R
import java.util.Calendar

object Common {

    fun openHomeActivity(activity: Activity, tab: String? = null) {
        val intent = Intent(activity, StartupFlowManager.homeDestinationClass()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            tab?.let { putExtra("open_tab", it) }
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
        activity.finishAndRemoveTask()
    }

    fun hideSystemUI(activity: Activity) {
        activity.window?.decorView?.post {
            activity.window?.let { window ->
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                controller.hide(WindowInsetsCompat.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    fun hideSystemNavigationBar(activity: Activity?) {
        activity ?: return
        hideSystemUI(activity)
        activity.window?.let { window ->
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    fun applyImmersiveFlagsToPopupRoot(contentView: View?) {
        contentView ?: return
        contentView.post {
            var root: View = contentView
            while (root.parent is View) {
                root = root.parent as View
            }
            @Suppress("DEPRECATION")
            val flags = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
            @Suppress("DEPRECATION")
            root.systemUiVisibility = flags
            val lp = root.layoutParams
            if (lp is WindowManager.LayoutParams) {
                @Suppress("DEPRECATION")
                lp.systemUiVisibility = flags
                try {
                    val wm = root.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    wm.updateViewLayout(root, lp)
                } catch (_: Exception) {
                }
            }
        }
    }

    fun attachKeepSystemNavHidden(
        activity: Activity?,
        shouldKeepHidden: () -> Boolean
    ): () -> Unit {
        val decor = activity?.window?.decorView ?: return {}
        @Suppress("DEPRECATION")
        val listener = View.OnSystemUiVisibilityChangeListener {
            if (shouldKeepHidden()) {
                hideSystemNavigationBar(activity)
            }
        }
        @Suppress("DEPRECATION")
        decor.setOnSystemUiVisibilityChangeListener(listener)
        return {
            @Suppress("DEPRECATION")
            decor.setOnSystemUiVisibilityChangeListener(null)
        }
    }

    fun showSystemUI(activity: Activity) {
        activity.window?.decorView?.post {
            activity.window?.let { window ->
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                controller.show(WindowInsetsCompat.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }


    private var lastClickTime = 0L

    fun isValidClick(delay: Long = 500L): Boolean {
        val currentTime = System.currentTimeMillis()

        return if (currentTime - lastClickTime >= delay) {
            lastClickTime = currentTime
            true
        } else {
            false
        }
    }
}