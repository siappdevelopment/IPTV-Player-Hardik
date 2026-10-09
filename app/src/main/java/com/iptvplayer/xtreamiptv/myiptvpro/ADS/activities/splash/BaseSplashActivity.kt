package com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.splash

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.VpnHelper
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSAppManage
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSAppStarting
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSBannerSmall
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSConsentManager
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSMainClass
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSNativeDisplay
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ManegeUtilsView
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.utils.LocaleAwareAppCompatActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

abstract class BaseSplashActivity : LocaleAwareAppCompatActivity() {

    private lateinit var version: String
    override fun shouldAutoRefreshLocale(): Boolean = false
    abstract fun initActivity()
    private var hasStartedSplashFlow = false
    private var waitingForVpn = false
    private var referrerTimeoutHandler: Handler? = null

    abstract fun navigateFromSplash()

    override fun onCreate(savedInstanceState: Bundle?) {
        ADSMainClass.initAppContext(applicationContext)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_splash)

        // Fetch FCM Token immediately on startup
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM_TOKEN", "Startup FCM Token: $token")
                } else {
                    Log.e("FCM_TOKEN", "Startup FCM Token fetch failed", task.exception)
                }
            }
        FirebaseApp.initializeApp(this)
        ADSAppManage.FastStart = true
        ADSAppStarting.ADSIsShowing = false
        ADSAppManage.AppStartingScreenOpen = false

        ManegeUtilsView.isUtilsManege(this)

        val versionName: String? =
            getPackageManager().getPackageInfo(getPackageName(), 0).versionName
        val result = versionName?.replace(".", "_")

        version = ManegeUtilsView.ssfsfsfsf + result

        startSplashFlowWhenVpnOff()
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

    override fun onResume() {
        try {
            super.onResume()
            if (waitingForVpn && !hasStartedSplashFlow) {
                startSplashFlowWhenVpnOff()
            }
        } catch (e: ClassCastException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        referrerTimeoutHandler?.removeCallbacksAndMessages(null)
        referrerTimeoutHandler = null
        VpnHelper.dismissDialog()
        super.onDestroy()
    }

    private fun startSplashFlowWhenVpnOff() {
        if (hasStartedSplashFlow || isFinishing || isDestroyed) return

        if (VpnHelper.isVpnActive(this)) {
            waitingForVpn = true
            VpnHelper.ensureVpnDisabled(this) {
                startSplashFlowWhenVpnOff()
            }
            return
        }

        waitingForVpn = false
        hasStartedSplashFlow = true
        VpnHelper.dismissDialog()
        fetchInstallReferrerThen {
            showing_to_all_data()
        }
    }

    /**
     * Remote config chooses paid_user vs normal_user from the install referrer.
     * That read is async, so config must not be applied until it is stored.
     */
    private fun fetchInstallReferrerThen(onFinished: () -> Unit) {
        if (isFinishing || isDestroyed) return

        var finished = false
        val timeoutHandler = Handler(Looper.getMainLooper())
        referrerTimeoutHandler = timeoutHandler
        fun finish() {
            if (finished || isFinishing || isDestroyed) return
            finished = true
            timeoutHandler.removeCallbacksAndMessages(null)
            referrerTimeoutHandler = null
            onFinished()
        }

        timeoutHandler.postDelayed({ finish() }, 3000L)

        val referrerClient = InstallReferrerClient.newBuilder(this).build()
        try {
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        try {
                            val referrerUrl = referrerClient.installReferrer.installReferrer
                            if (!referrerUrl.isNullOrBlank()) {
                                ADSMainClass.setReferrerUrl(referrerUrl)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    try {
                        referrerClient.endConnection()
                    } catch (_: Exception) {
                    }
                    runOnUiThread { finish() }
                }

                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    private fun showing_to_all_data() {
        FirebaseRemoteConfigLoader.showingToAllData(
            activity = this,
            source = FirebaseRemoteConfigLoader.Source.SPLASH,
            onNoNetwork = {
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    app_next_screen_showing()
                }, 500)
            },
            onComplete = {
                continueAfterConsent { app_next_screen_showing() }
            },
            onHideSplashBottomAdPlaceholders = {
                hideSplashBottomAdPlaceholders()
            },
        )
    }

    private fun continueAfterConsent(action: () -> Unit) {
        if (isFinishing || isDestroyed) return

        val showFormOnSplash = ADSMainClass.isConsentOnSplash()

        ADSConsentManager.gatherConsent(this, showFormOnSplash) { formShown ->
            if (isFinishing || isDestroyed) return@gatherConsent

            if (showFormOnSplash) {
                if (formShown) {
                    ADSMainClass.setConsentPendingOnLanguage(false)
                } else if (ADSConsentManager.shouldRetryConsentOnLanguage(this)) {
                    ADSMainClass.setConsentPendingOnLanguage(true)

                }
            }

            proceedAfterConsentGathered(action)
        }
    }

    private fun proceedAfterConsentGathered(action: () -> Unit) {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                action()
            }
        }
    }

    protected fun showSplashAdsAndNavigate() {
        if (isFinishing || isDestroyed) return

        if (!ADSMainClass.getBottomSplashShow()) {
            hideSplashBottomAdPlaceholders()
        }

        if (ADSConsentManager.canRequestAds(this) && !ADSMainClass.getAds_Free()) {
            ADSAppManage.initializeMobileAdsIfNeeded(applicationContext) {
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        ADSAppManage.preloadSplashAdsIfNeeded(this)
                        loadSplashBottomAd()
                        app_open_showing()
                    }
                }
            }
        } else {
            hideSplashBottomAdPlaceholders()
            app_open_showing()
        }
    }

    private fun loadSplashBottomAd() {
        if (!ADSMainClass.getBottomSplashShow()) {
            hideSplashBottomAdPlaceholders()
            return
        }

        findViewById<View>(R.id.banner).visibility = View.VISIBLE

        if (ADSMainClass.getSplashAdsType().equals("native")) {
            if (ADSMainClass.isNativeLoad()) {
                ADSNativeDisplay.loadAdmobNativeAdBig(
                    ADSMainClass.getStringValue(ADSMainClass.splash_native_id),
                    findViewById(R.id.flNativeSmallPlaceholder),
                    findViewById(R.id.shimmer_container_banner),
                    "small",
                    this
                )
            } else {
                ADSNativeDisplay.loadSplashBottomNative(
                    ADSMainClass.getStringValue(ADSMainClass.splash_native_id),
                    findViewById(R.id.flNativeSmallPlaceholder),
                    findViewById(R.id.shimmer_container_banner),
                    this
                )
            }
        } else {
            ADSBannerSmall.loadAdMobBanner(
                ADSMainClass.getStringValue(ADSMainClass.splash_banner_id),
                findViewById(R.id.flBannerSmallPlaceholder),
                findViewById(R.id.shimmer_container_banner),
                this,
                "small"
            )
        }
    }

    private fun hideSplashBottomAdPlaceholders() {
        findViewById<ShimmerFrameLayout?>(R.id.shimmer_container_banner)?.let { shimmer ->
            shimmer.stopShimmer()
            shimmer.visibility = View.GONE
        }
        findViewById<View?>(R.id.flNativeSmallPlaceholder)?.visibility = View.GONE
        findViewById<View?>(R.id.flBannerSmallPlaceholder)?.visibility = View.GONE
        findViewById<View?>(R.id.banner)?.visibility = View.GONE
    }

    private fun app_open_showing() {

        if (!ADSMainClass.shouldShowSplashAd()) {
            Log.d("TAG", "app_open_showing    6666 : " + ADSMainClass.getSplashADType())
            navigateFromSplash()
            return
        }

        if (ADSMainClass.getAds_Free()) {
            Log.d("TAG", "app_open_showing    5555 : " + ADSMainClass.getSplashADType())
            navigateFromSplash()
            return
        }


        val splashAdType = ADSMainClass.getSplashADType()
        if (splashAdType.equals("Appopen", ignoreCase = true) ||
            splashAdType.equals("Inter", ignoreCase = true) ||
            splashAdType.equals("Interstitial", ignoreCase = true)
        ) {
            ADSAppStarting.AdsSplashAppStartingDisplay(this@BaseSplashActivity, {
                ADSAppManage.FastStart = true
                navigateFromSplash()
            })
        } else {
            ADSAppManage.FastStart = true
            navigateFromSplash()
        }
    }

    private fun app_next_screen_showing() {
        ADSAppManage.AppStartingScreenOpen = true
        initActivity()
    }
}
