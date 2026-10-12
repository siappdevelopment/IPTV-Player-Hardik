package com.iptvplayer.xtreamiptv.myiptvpro.Main.intro

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.BaseActivity
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeLaunchScreen
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeScreen2
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen.OnCompeteAds
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityIntroTwoBinding
import kotlin.jvm.java

class IntroTwoActivity :  BaseActivity() {

    lateinit var binding: ActivityIntroTwoBinding
    var is_selected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backClick()
            }
        })

        binding.startScreenBtn.setOnClickListener {
            if (is_selected) {
                InterstitialAdScreen.reso__Fullscreen_ADS_Display(
                    this@IntroTwoActivity,
                    object : OnCompeteAds {
                        override fun onCompeteAds(b: Boolean) {
                            if (AppControlPreference.getDefaultPermission()) {
                                if (AppControlPreference.getDefaultPermissionCancel()) {
                                    if (AppControlPreference.getreso__Intro_Screen_Display() == 2) {
                                        startActivity(Intent(this@IntroTwoActivity, HomeScreen2::class.java))
                                    } else {
                                        startActivity(Intent(this@IntroTwoActivity, IntroDesignThreeScreen::class.java))
                                    }
                                } else {
                                    if (AppControlPreference.getInsideHomeCount()) {
                                        if (AppControlPreference.getreso__Intro_Screen_Display() == 2) {
                                            AppControlPreference.setIntroLauncher(false)
                                            AppControlPreference.setIntroCounts(true)
                                            val intent = Intent(this@IntroTwoActivity, HomeLaunchScreen::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        } else {
                                            startActivity(Intent(this@IntroTwoActivity, IntroDesignThreeScreen::class.java))
                                        }
                                    } else {
                                        if (AppControlPreference.getreso__Intro_Screen_Display() == 2) {
                                            AppControlPreference.setIntroLauncher(false)
                                            val intent = Intent(this@IntroTwoActivity, HomeLaunchScreen::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        } else {
                                            startActivity(Intent(this@IntroTwoActivity, IntroDesignThreeScreen::class.java))
                                        }
                                    }
                                }
                            } else {
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 2) {

                                    startActivity(
                                        Intent(
                                            this@IntroTwoActivity,
                                            HomeScreen2::class.java
                                        )
                                    )


                                } else {
                                    startActivity(
                                        Intent(
                                            this@IntroTwoActivity,
                                            IntroDesignThreeScreen::class.java
                                        )
                                    )

                                }
                            }
                        }
                    }, false
                )
            } else {
                Toast.makeText(
                    this@IntroTwoActivity,
                    "Please select an option",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.cnLayout1.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd1)
        }

        binding.cnLayout2.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd2)
        }

        binding.cnLayout3.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd3)
        }

        binding.cnLayout4.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd4)
        }

        binding.cnLayout5.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd5)
        }

        binding.cnLayout6.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd6)
        }

        binding.cnLayout7.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd7)
        }

        binding.cnLayout8.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd8)
        }

        binding.cnLayout9.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd9)
        }

        binding.cnLayout10.setOnClickListener {
            is_selected = true
            unselection()
            Selection(binding.introEnd10)
        }

    }

    private fun unselection() {
        binding.introEnd1.setImageResource(R.drawable.ic_unselected_lang)
        binding.introEnd2.setImageResource(R.drawable.ic_unselected_lang)
        binding.introEnd3.setImageResource(R.drawable.ic_unselected_lang)
        binding.introEnd4.setImageResource(R.drawable.ic_unselected_lang)
        binding.introEnd5.setImageResource(R.drawable.ic_unselected_lang)
        binding.introEnd6.setImageResource(R.drawable.ic_unselected_lang)
        binding.introEnd7.setImageResource(R.drawable.ic_unselected_lang)
        binding.introEnd8.setImageResource(R.drawable.ic_unselected_lang)
        binding.introEnd9.setImageResource(R.drawable.ic_unselected_lang)
        binding.introEnd10.setImageResource(R.drawable.ic_unselected_lang)
    }

    private fun Selection(imageView: ImageView) {
        imageView.setImageResource(R.drawable.ic_selected_lang)
    }

    override fun onResume() {
        super.onResume()
        unselection()
        is_selected = false
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppControllerManege.removeListener()
    }

    private fun backClick() {
        InterstitialAdScreen.reso__Back_Fullscreen_ADS_Display(this, object : OnCompeteAds {
            override fun onCompeteAds(b: Boolean) {
                finish()
            }
        })
    }
}