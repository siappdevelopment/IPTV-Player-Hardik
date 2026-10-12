package com.iptvplayer.xtreamiptv.myiptvpro.Main.intro

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeLaunchScreen
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Adapters.LanguageAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen.OnCompeteAds
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityIntroOneScreenBinding
import com.iptvplayer.xtreamiptv.myiptvpro.Main.model.LanguageModel
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.BaseActivity
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeScreen2

class IntroOneScreen : BaseActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: LanguageAdapter
    lateinit var binding: ActivityIntroOneScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroOneScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.rvLanguage)

        val languageList = listOf(
            LanguageModel(R.drawable.ic_lan_english, "English", "en", "(English)"),
            LanguageModel(R.drawable.ic_lan_spanish, "Spanish", "es", "(Español)"),
            LanguageModel(R.drawable.ic_lan_arabic, "Arabic", "ar", "(عربي)"),
            LanguageModel(R.drawable.ic_lan_french, "French", "fr", "(Français)"),
            LanguageModel(R.drawable.ic_lan_portuguage, "Portuguese", "pt", "(Português)"),
            LanguageModel(R.drawable.ic_lan_german, "German", "de", "(Deutsch)"),
            LanguageModel(R.drawable.ic_lan_japanise, "Japanese", "ja", "(日本語)"),
            LanguageModel(R.drawable.ic_persian, "Persian", "fa", "(فارسی)"),
            LanguageModel(R.drawable.ic_italian, "Italian", "it", "(Italiano)"),
            LanguageModel(R.drawable.ic_lan_russian, "Russian", "ru", "(Русский)")
        )

        adapter = LanguageAdapter(languageList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })

        binding.btnContinue.setOnClickListener {
            getPermissions {
                goToNextScreen()
            }

        }

    }

    private fun goToNextScreen() {
        InterstitialAdScreen.reso__Fullscreen_ADS_Display(
            this@IntroOneScreen,
            object : OnCompeteAds {
                override fun onCompeteAds(b: Boolean) {
                    if (AppControlPreference.getDefaultPermission()) {
                        if (AppControlPreference.getDefaultPermissionCancel()) {
                            if (AppControlPreference.getreso__Intro_Screen_Display() == 1) {
                                startActivity(Intent(this@IntroOneScreen, HomeScreen2::class.java))
                            } else {
                                startActivity(Intent(this@IntroOneScreen, IntroTwoActivity::class.java))
                            }
                        } else {
                            if (AppControlPreference.getInsideHomeCount()) {
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 1) {
                                    AppControlPreference.setIntroLauncher(false)
                                    AppControlPreference.setIntroCounts(true)
                                    val intent =
                                        Intent(this@IntroOneScreen, HomeLaunchScreen::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    startActivity(
                                        Intent(
                                            this@IntroOneScreen,
                                            IntroTwoActivity::class.java
                                        )
                                    )
                                }
                            } else {
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 1) {
                                    AppControlPreference.setIntroLauncher(false)
                                    val intent =
                                        Intent(this@IntroOneScreen, HomeLaunchScreen::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)

                                } else {
                                    startActivity(
                                        Intent(
                                            this@IntroOneScreen,
                                            IntroTwoActivity::class.java
                                        ))
                                }
                            }
                        }
                    } else {
                        if (AppControlPreference.getreso__Intro_Screen_Display() == 1) {


                            startActivity(
                                Intent(
                                    this@IntroOneScreen,
                                    HomeScreen2::class.java
                                )
                            )


                        } else {
                            startActivity(Intent(this@IntroOneScreen, IntroTwoActivity::class.java))

                        }
                    }
                }
            },
            false
        )
    }


    private fun getPermissions(onComplete: () -> Unit) {
        val permissions = ArrayList<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.isEmpty()) {
            onComplete()
            return
        }

        Dexter.withActivity(this)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    onComplete()

                    if (report.isAnyPermissionPermanentlyDenied) {
                        onComplete()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppControllerManege.removeListener()
    }
}