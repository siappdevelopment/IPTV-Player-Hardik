package com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.permissions

import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.provider.Settings
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.PermissionFirebaseEvents
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.overlayPermission.OverlayPermissionActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSBannerSmall
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSMainClass
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSNativeDisplay
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation.StartupFlowManager
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation.StartupFlowStep
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityPermissionBinding
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Common
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Constance
import com.iptvplayer.xtreamiptv.myiptvpro.utils.LocaleAwareAppCompatActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.PermissionManager
import com.iptvplayer.xtreamiptv.myiptvpro.utils.SharedPreferenceManager

class PermissionActivity : LocaleAwareAppCompatActivity() {
    private lateinit var binding: ActivityPermissionBinding
    private var awaitingOverlayGrant = false
    private var overlayGrantHandled = false
    private var overlayFlowCompleted = false
    private val overlayHandler = Handler(Looper.getMainLooper())

    private var isAutoFlow = false

    /** True from launching a runtime-permission request until its result arrives; blocks re-entrant requests. */
    private var isPermissionRequestInProgress = false

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            isPermissionRequestInProgress = false
            Log.d(TAG, "permission request result received: $results")
            PermissionFirebaseEvents.trackFromPermissionResults(this, results)
            updateCardVisibilities()
            if (isAutoFlow) {
                // One request round already covered every missing runtime permission, so move on to the
                // overlay step. Re-calling requestPermissions() from here was the recursion.
                Log.d(TAG, "auto flow: runtime permissions done, continuing with overlay step")
                checkAndRequestOverlayPermission()
            }
        }

    private val overlaySettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            awaitingOverlayGrant = false
            stopOverlayChecker()
            if (Settings.canDrawOverlays(this)) {
                handleOverlayGranted()
            } else {
                proceedToNextScreen()
            }
        }

    private val overlayChecker = object : Runnable {
        override fun run() {
            if (Settings.canDrawOverlays(this@PermissionActivity)) {
                handleOverlayGranted(fromBackgroundPoll = true)
                return
            }
            overlayHandler.postDelayed(this, OVERLAY_CHECK_INTERVAL_MS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Survive recreation while a request is waiting for its result.
        isPermissionRequestInProgress = savedInstanceState?.getBoolean(STATE_REQUEST_IN_PROGRESS, false) ?: false
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Common.hideSystemUI(this)

        if (ADSMainClass.getPermissionSmallAdsShow()) {
            if (ADSMainClass.getPermissionAdsType().equals("native")) {
                ADSNativeDisplay.loadAdmobNativeAdBig(
                    ADSMainClass.getStringValue(ADSMainClass.SETTING_SCREEN_NATIVE),
                    findViewById(R.id.flNativeSmallPlaceholder),
                    findViewById(R.id.shimmer_container_banner),
                    "small",
                    this
                )
            } else {
                ADSBannerSmall.loadAdMobBanner(
                    ADSMainClass.getStringValue(ADSMainClass.SETTING_SCREEN_BANNER),
                    findViewById(R.id.flBannerSmallPlaceholder),
                    findViewById(R.id.shimmer_container_banner),
                    this,
                    "small"
                )
            }
        } else {
            binding.shimmerContainerBanner.visibility = View.GONE
            binding.flNativeSmallPlaceholder.visibility = View.GONE
            binding.flBannerSmallPlaceholder.visibility = View.GONE
        }

        initView()
    }

    private fun initView() {
        binding.btnAllowPermission.setOnClickListener {
            AppAnalyticsEvents.track(this, AppAnalyticsEvents.PERMISSION_ALLOW_CLICK)
            isAutoFlow = true
            requestPermissions()
        }

        binding.cardNotification.setOnClickListener {
            if (!isNotificationGranted()) {
                AppAnalyticsEvents.track(
                    this,
                    AppAnalyticsEvents.PERMISSION_ALLOW_CLICK,
                    AppAnalyticsEvents.PARAM_PERMISSION_TYPE,
                    "notification"
                )
                isAutoFlow = false
                requestNotificationPermission()
            }
        }

        binding.cardCall.setOnClickListener {
            if (!isCallGranted()) {
                AppAnalyticsEvents.track(
                    this,
                    AppAnalyticsEvents.PERMISSION_ALLOW_CLICK,
                    AppAnalyticsEvents.PARAM_PERMISSION_TYPE,
                    "call"
                )
                isAutoFlow = false
                requestCallPermission()
            }
        }

        binding.cardOverlay.setOnClickListener {
            if (!isOverlayGranted()) {
                AppAnalyticsEvents.track(
                    this,
                    AppAnalyticsEvents.OVERLAY_PERMISSION_ALLOW,
                    AppAnalyticsEvents.PARAM_SOURCE,
                    "permission_screen"
                )
                isAutoFlow = false
                checkAndRequestOverlayPermission()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                overridePendingTransition(0, 0)
                goNext()
            }
        })

        updateCardVisibilities()
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!isCallGranted()) {
            permissionsToRequest.add(Manifest.permission.CALL_PHONE)
            permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isNotificationGranted()) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            launchRuntimePermissions(permissionsToRequest.toTypedArray(), "auto")
        } else {
            checkAndRequestOverlayPermission()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launchRuntimePermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), "notification")
        }
    }

    private fun requestCallPermission() {
        launchRuntimePermissions(
            arrayOf(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE
            ),
            "call"
        )
    }

    /**
     * Single entry point for runtime-permission requests. A request is never launched while another one
     * is still waiting for its result: Activity.requestPermissions() answers such a call synchronously
     * with an empty result, which is what used to drive the infinite recursion.
     */
    private fun launchRuntimePermissions(permissions: Array<String>, source: String) {
        if (isPermissionRequestInProgress) {
            Log.d(TAG, "permission request in progress, ignoring ($source)")
            return
        }
        if (permissions.isEmpty()) return
        Log.d(TAG, "permission request started ($source): ${permissions.joinToString()}")
        isPermissionRequestInProgress = true
        try {
            requestPermissionsLauncher.launch(permissions)
        } catch (e: Exception) {
            isPermissionRequestInProgress = false
            Log.e(TAG, "permission request failed to launch: ${e.message}")
        }
    }

    private fun checkAndRequestOverlayPermission() {
        // Default launcher already set: skip the overlay step and continue with the existing next step.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this) &&
            !com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common.Utils.isDefaultHomeApp(this)
        ) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = Uri.parse("package:$packageName")
            }
            if (intent.resolveActivity(packageManager) != null) {
                launchOverlaySettingsFlow(intent)
            } else {
                val fallbackIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                if (fallbackIntent.resolveActivity(packageManager) != null) {
                    launchOverlaySettingsFlow(fallbackIntent)
                } else if (isAutoFlow) {
                    setResult(RESULT_OK)
                    goNext()
                }
            }
        } else if (isAutoFlow) {
            setResult(RESULT_OK)
            goNext()
        }
    }

    private fun launchOverlaySettingsFlow(settingsIntent: Intent) {
        overlayGrantHandled = false
        overlayFlowCompleted = false
        awaitingOverlayGrant = true
        overlaySettingsLauncher.launch(settingsIntent)
        startOverlayChecker()
        overlayHandler.postDelayed({
            if (awaitingOverlayGrant) {
                startActivity(
                    Intent(this, OverlayPermissionActivity::class.java).apply {
                        putExtra(OverlayPermissionActivity.EXTRA_HOST_TASK_ID, taskId)
                    }
                )
            }
        }, OVERLAY_GUIDE_DELAY_MS)
    }

    private fun startOverlayChecker() {
        stopOverlayChecker()
        overlayHandler.post(overlayChecker)
    }

    private fun stopOverlayChecker() {
        overlayHandler.removeCallbacks(overlayChecker)
    }

    private fun handleOverlayGranted(fromBackgroundPoll: Boolean = false) {
        if (overlayGrantHandled || !Settings.canDrawOverlays(this)) return

        overlayGrantHandled = true
        awaitingOverlayGrant = false
        stopOverlayChecker()
        PermissionFirebaseEvents.trackOverlayGranted(this)

        val isResumed = lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)
        if (fromBackgroundPoll || !isResumed) {
            returnToPermissionScreen()
        } else {
            completeOverlayFlow()
        }
    }

    private fun returnToPermissionScreen() {
        val intent = Intent(this, PermissionActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
            putExtra(EXTRA_OVERLAY_GRANTED, true)
        }
        startActivity(intent)
        try {
            getSystemService(ActivityManager::class.java)
                ?.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME)
        } catch (_: Exception) {
        }
    }

    private fun completeOverlayFlow() {
        proceedToNextScreen()
    }

    private fun proceedToNextScreen() {
        if (overlayFlowCompleted) return
        overlayFlowCompleted = true
        awaitingOverlayGrant = false
        stopOverlayChecker()
        setResult(RESULT_OK)
        goNext()
    }

    override fun onResume() {
        super.onResume()
        PermissionFirebaseEvents.syncGrantedEvents(this)
        updateCardVisibilities()
        if (intent.getBooleanExtra(EXTRA_OVERLAY_GRANTED, false)) {
            intent.removeExtra(EXTRA_OVERLAY_GRANTED)
            if (Settings.canDrawOverlays(this)) {
                completeOverlayFlow()
                return
            } else {
                proceedToNextScreen()
                return
            }
        }
        if (awaitingOverlayGrant) {
            if (Settings.canDrawOverlays(this)) {
                handleOverlayGranted()
            } else {
                startOverlayChecker()
            }
        }
    }

    private fun updateCardVisibilities() {
        binding.swNotification.isChecked = isNotificationGranted()
        binding.swCall.isChecked = isCallGranted()
        binding.swOverlay.isChecked = isOverlayGranted()
        // Same condition that skips the overlay step in checkAndRequestOverlayPermission().
        binding.cardOverlay.visibility =
            if (com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common.Utils.isDefaultHomeApp(this)) View.GONE else View.VISIBLE
    }

    private fun isNotificationGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun isCallGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isOverlayGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(EXTRA_OVERLAY_GRANTED, false)) {
            intent.removeExtra(EXTRA_OVERLAY_GRANTED)
            if (Settings.canDrawOverlays(this)) {
                completeOverlayFlow()
            } else {
                proceedToNextScreen()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_REQUEST_IN_PROGRESS, isPermissionRequestInProgress)
    }

    override fun onDestroy() {
        stopOverlayChecker()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_OVERLAY_GRANTED = "extra_overlay_granted"
        private const val TAG = "PermissionActivity"
        private const val STATE_REQUEST_IN_PROGRESS = "state_permission_request_in_progress"
        private const val OVERLAY_CHECK_INTERVAL_MS = 500L
        private const val OVERLAY_GUIDE_DELAY_MS = 800L
    }

    private fun goNext() {
        if (isFinishing) return

        SharedPreferenceManager.putBoolean(this, Constance.PERMISSION_SCREEN_SHOWN, true)
        PermissionManager.checkAndTrackDefaultDialer(this)

        if (StartupFlowManager.isStartupFlowActive(this)) {
            StartupFlowManager.completeStep(this, StartupFlowStep.PERMISSION)
            return
        }

        startActivity(Intent(this, StartupFlowManager.homeDestinationClass()))
        finish()
    }
}
