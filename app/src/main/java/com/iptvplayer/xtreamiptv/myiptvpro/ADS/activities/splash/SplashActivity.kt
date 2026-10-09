package com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.splash

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.PermissionFirebaseEvents
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSMainClass
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common.Utils
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation.StartupFlowManager
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Common

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseSplashActivity() {
    private var isProceeding = false
    private var hasNavigated = false

    private fun makeFullScreenImmersive() {

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            val flags =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            window.decorView.systemUiVisibility = flags
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }

    override fun onResume() {
        super.onResume()
        makeFullScreenImmersive()
        Utils.applyRecentsVisibility(this)
    }

    override fun initActivity() {
        Common.hideSystemUI(this)
        makeFullScreenImmersive()
        ADSMainClass.updateConsecutiveStreak(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = getWindow().getInsetsController()
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.navigationBars())
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
            }
        }
        ADSMainClass.initAppContext(this)
        AppAnalyticsEvents.track(this, AppAnalyticsEvents.APP_OPEN)
        proceed()
    }

    private fun proceed() {
        if (isProceeding) return
        isProceeding = true
        showSplashAdsAndNavigate()
    }

    override fun navigateFromSplash() {
        if (hasNavigated) return
        hasNavigated = true
        PermissionFirebaseEvents.syncGrantedEvents(this)
        StartupFlowManager.startStartupFlow(this)
    }

}
