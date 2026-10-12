package com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.container
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.Dialogs
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.PillSwitch
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.Messages
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch

/** Language, playback options, playlist management and about (Settings in the design). */
class SettingsActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
    }

    private lateinit var binding: ActivitySettingsBinding
    private val prefs by lazy { container.preferences }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.settingsRoot.applySystemBarInsets()
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backClick()
                }
            }
        )

        binding.btnBack.setOnClickListener { backClick() }

        setupRows()
    }

    private fun toggle(
        root: View,
        switchView: PillSwitch,
        initial: Boolean,
        onChange: (Boolean) -> Unit
    ) {
        switchView.isChecked = initial
        root.setOnClickListener {
            val value = !switchView.isChecked
            switchView.isChecked = value
            onChange(value)
        }
    }

    private fun setupRows() {
        val b = binding
        toggle(b.rowResume, b.switchResume, prefs.resumeLastChannel) {
            prefs.resumeLastChannel = it
        }
        toggle(b.rowHardware, b.switchHardware, prefs.hardwareDecoding) {
            prefs.hardwareDecoding = it
        }

        b.rowManage.setOnClickListener {
            InterstitialAdScreen.reso__Back_Fullscreen_ADS_Display(
                this,
                object : InterstitialAdScreen.OnCompeteAds {
                    override fun onCompeteAds(b: Boolean) {
                        finish()
                    }
                }
            )
        }
        b.rowRefresh.setOnClickListener { SettingsDialogs.refresh(this, binding.root) }
        b.rowClearHistory.setOnClickListener {
            Dialogs.confirm(
                this,
                getString(R.string.dialog_clear_history_title),
                getString(R.string.dialog_clear_history_desc),
                R.string.action_clear,
            ) {
                lifecycleScope.launch {
                    container.channelRepository.clearRecents()
                    Messages.show(binding.root, R.string.toast_history_cleared)
                }
            }
        }
        b.rowPrivacy.setOnClickListener {
            InterstitialAdScreen.reso__Fullscreen_ADS_Display(
                this@SettingsActivity,
                object : InterstitialAdScreen.OnCompeteAds {
                    override fun onCompeteAds(b: Boolean) {
                        startActivity(
                            Intent(this@SettingsActivity, PrivacyActivity::class.java)
                        )
                    }
                },
                false
            )
        }
        b.rowRate.setOnClickListener { SettingsDialogs.rate(this, binding.root) }
        b.rowShare.setOnClickListener { SettingsDialogs.share(this, binding.root) }
        b.rowAbout.setOnClickListener { SettingsDialogs.about(this, binding.root) }
        b.tvAboutValue.text = getString(
            R.string.about_short_version,
            runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull()
                .orEmpty(),
        )
    }

    private fun backClick() {
        InterstitialAdScreen.reso__Back_Fullscreen_ADS_Display(
            this,
            object : InterstitialAdScreen.OnCompeteAds {
                override fun onCompeteAds(b: Boolean) {
                    finish()
                }
            }
        )
    }

}
