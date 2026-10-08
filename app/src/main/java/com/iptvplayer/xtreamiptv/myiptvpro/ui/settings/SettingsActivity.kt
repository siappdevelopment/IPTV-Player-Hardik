package com.iptvplayer.xtreamiptv.myiptvpro.ui.settings

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppLanguages
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivitySettingsBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemSettingRowBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemSettingSwitchBinding
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.Dialogs
import com.iptvplayer.xtreamiptv.myiptvpro.ui.language.LanguageActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Messages
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import kotlinx.coroutines.launch

/** Language, playback options, playlist management and about (Settings in the design). */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val prefs by lazy { container.preferences }
    private lateinit var languageRow: ItemSettingRowBinding

    private val languagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val name = result.data?.getStringExtra(LanguageActivity.EXTRA_LANGUAGE_NAME).orEmpty()
            Messages.show(binding.root, getString(R.string.toast_language_set, name))
            renderLanguage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.settingsRoot.applySystemBarInsets()
        binding.btnBack.setOnClickListener { finish() }

        setupRows()
        renderLanguage()
    }

    private fun row(row: ItemSettingRowBinding, @DrawableRes icon: Int, @StringRes title: Int, onClick: () -> Unit) {
        row.ivIcon.setImageResource(icon)
        row.tvTitle.setText(title)
        row.root.setOnClickListener { onClick() }
    }

    private fun toggle(
        row: ItemSettingSwitchBinding,
        @DrawableRes icon: Int,
        @StringRes title: Int,
        initial: Boolean,
        onChange: (Boolean) -> Unit,
    ) {
        row.ivIcon.setImageResource(icon)
        row.tvTitle.setText(title)
        row.switchView.isChecked = initial
        row.root.setOnClickListener {
            val value = !row.switchView.isChecked
            row.switchView.isChecked = value
            onChange(value)
        }
    }

    private fun setupRows() {
        val b = binding
        languageRow = ItemSettingRowBinding.bind(b.rowLanguage.root)
        row(languageRow, R.drawable.ic_translate, R.string.settings_language) {
            languagePicker.launch(
                Intent(this, LanguageActivity::class.java).putExtra(LanguageActivity.EXTRA_FROM_SETTINGS, true),
            )
        }
        languageRow.tvValue.show(true)

        toggle(
            ItemSettingSwitchBinding.bind(b.rowResume.root), R.drawable.ic_play_arrow, R.string.settings_resume,
            prefs.resumeLastChannel,
        ) { prefs.resumeLastChannel = it }
        toggle(
            ItemSettingSwitchBinding.bind(b.rowHardware.root), R.drawable.ic_memory, R.string.settings_hardware,
            prefs.hardwareDecoding,
        ) { prefs.hardwareDecoding = it }

        row(ItemSettingRowBinding.bind(b.rowManage.root), R.drawable.ic_video_library, R.string.settings_manage_playlists) {
            finish() // the Playlist tab is where playlists are managed
        }
        row(ItemSettingRowBinding.bind(b.rowRefresh.root), R.drawable.ic_sync, R.string.settings_refresh_playlists) {
            SettingsDialogs.refresh(this, binding.root)
        }
        val clearHistory = ItemSettingRowBinding.bind(b.rowClearHistory.root)
        row(clearHistory, R.drawable.ic_history_toggle_off, R.string.settings_clear_history) {
            Dialogs.confirm(
                this, getString(R.string.dialog_clear_history_title), getString(R.string.dialog_clear_history_desc),
                R.string.action_clear,
            ) {
                lifecycleScope.launch {
                    container.channelRepository.clearRecents()
                    Messages.show(binding.root, R.string.toast_history_cleared)
                }
            }
        }
        clearHistory.ivIcon.setBackgroundResource(R.drawable.bg_tile_error)
        clearHistory.ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_error))

        row(ItemSettingRowBinding.bind(b.rowPrivacy.root), R.drawable.ic_shield, R.string.settings_privacy) {
            startActivity(Intent(this, PrivacyActivity::class.java))
        }
        row(ItemSettingRowBinding.bind(b.rowRate.root), R.drawable.ic_star, R.string.settings_rate) {
            SettingsDialogs.rate(this, binding.root)
        }
        row(ItemSettingRowBinding.bind(b.rowShare.root), R.drawable.ic_share, R.string.settings_share) {
            SettingsDialogs.share(this, binding.root)
        }
        val about = ItemSettingRowBinding.bind(b.rowAbout.root)
        row(about, R.drawable.ic_info, R.string.settings_about) {
            SettingsDialogs.about(this, binding.root)
        }
        about.tvValue.show(true)
        about.tvValue.text = getString(
            R.string.about_short_version,
            runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull().orEmpty(),
        )
    }

    private fun renderLanguage() {
        languageRow.tvValue.text = AppLanguages.byCode(prefs.languageCode).englishName
    }
}
