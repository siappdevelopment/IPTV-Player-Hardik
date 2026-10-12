package com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.BaseActivity
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeScreen2
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.ExitDialogViewAd
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.Main.intro.IntroOneScreen
import com.google.firebase.analytics.FirebaseAnalytics


class PermissionActivity : BaseActivity() {

    private lateinit var btnPermission: TextView

    private val launcherRoleResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val firebaseAnalytics = FirebaseAnalytics.getInstance(this@PermissionActivity)

        when (result.resultCode) {

            RESULT_OK -> {

                if (isDefaultLauncher()) {

                    val allowBundle = Bundle()
                    allowBundle.putBoolean("default_permission_allow", true)

                    firebaseAnalytics.logEvent(
                        "default_permission_allow",
                        allowBundle
                    )

                    getSharedPreferences("launcher_pref", MODE_PRIVATE)
                        .edit()
                        .putBoolean("open_intro_after_launcher", true)
                        .apply()

                    finish()
                }
            }

            RESULT_CANCELED -> {

                val cancelBundle = Bundle()
                cancelBundle.putBoolean("default_permission_cancel", true)

                firebaseAnalytics.logEvent(
                    "default_permission_cancel",
                    cancelBundle
                )

                if (AppControlPreference.getDefault_Cancel_To_Next_Pass()) {
                    InterstitialAdScreen.reso__Fullscreen_ADS_Display(
                        this@PermissionActivity,
                        object :
                            InterstitialAdScreen.OnCompeteAds {
                            override fun onCompeteAds(b: Boolean) {
                                AppControlPreference.setDefaultPermissionCancel(true)
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 0) {
                                    startActivity(
                                        Intent(
                                            this@PermissionActivity,
                                            HomeScreen2::class.java
                                        )
                                    )
                                } else {
                                    startActivity(
                                        Intent(
                                            this@PermissionActivity,
                                            IntroOneScreen::class.java
                                        )
                                    )
                                }
                            }
                        },
                        false
                    )
                }
            }

            else -> {

                val deniedBundle = Bundle()
                deniedBundle.putBoolean("default_permission_denied", true)

                firebaseAnalytics.logEvent(
                    "default_permission_denied",
                    deniedBundle
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_permission)
        btnPermission = findViewById(R.id.btnPermission)
        btnPermission.setOnClickListener {
            setPermissionDefault(this, this@PermissionActivity)

        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backClick()
            }
        })
    }

    private fun isDefaultLauncher(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            ?: return false
        return resolveInfo.activityInfo?.packageName == packageName
    }

    fun setPermissionDefault(context: Context, permissionActivity: PermissionActivity?) {
        permissionActivity!!.getSharedPreferences("launcher_pref", MODE_PRIVATE)
            .edit()
            .putBoolean("open_intro_after_launcher", true)
            .apply()

        if (permissionActivity == null) {
            return
        }
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                val roleManager: RoleManager? = context.getSystemService(RoleManager::class.java)
                if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {

                    val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
                    val newBundle = Bundle()
                    newBundle.putBoolean("default_permission_allow", true)
                    firebaseAnalytics.logEvent("default_permission_allow", newBundle)
                    permissionActivity.launcherRoleResult.launch(
                        roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                    )
                    return
                }
                val intent1 = Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS")
                if (intent1.resolveActivity(context.packageManager) != null) {
                    intentPermission(permissionActivity, intent1)
                    return
                }
                settingPermission(context, permissionActivity)
                return
            }
            val intent2 = Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS")
            val bundle = Bundle()
            bundle.putString(":settings:fragment_args_key", "default_home_app")
            intent2.putExtra(":settings:show_fragment_args", bundle)
            intentPermission(permissionActivity, intent2)
        } catch (unused: Exception) {
            Toast.makeText(
                context,
                "Could not open launcher settings directly, please select this app as default launcher manually from settings.",
                Toast.LENGTH_LONG
            ).show()
            settingPermission(context, permissionActivity)
        }
    }

    fun settingPermission(context: Context, cVar: PermissionActivity?) {
        val intent = Intent("android.settings.SETTINGS")
        if (intent.resolveActivity(context.packageManager) != null) {
            intentPermission(cVar!!, intent)
            return
        }
        Toast.makeText(context, "Could not open settings.", Toast.LENGTH_SHORT).show()
    }

    fun intentPermission(activity: PermissionActivity, intent: Intent?) {
        try {
            if (intent != null) {
                activity.launcherRoleResult.launch(intent)
            }
        } catch (exception: IllegalStateException) {
            Log.e(
                "LauncherDefaultSetter",
                "Activity is likely destroyed; cannot launch result.",
                exception
            )
        } catch (exception: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppControllerManege.removeListener()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
    }

    var isFlag: Boolean = false

    private fun backClick() {
        if (AppControlPreference.reso__is_Exit_Dialog()) {
            ExitDialogViewAd.reso__App_Exit_Dialog(this@PermissionActivity)
        } else {
            if (isFlag) {
                finishAffinity()
            } else {
                isFlag = true
                Toast.makeText(this, "Please tap again!", Toast.LENGTH_SHORT).show()
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        isFlag = false
                    }
                }, 3000)
            }
        }
    }
}
