package com.iptvplayer.xtreamiptv.myiptvpro.ui.language

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppLanguages
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityLanguageBinding
import com.iptvplayer.xtreamiptv.myiptvpro.ui.onboarding.OnboardingActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.utils.onTrimmedTextChanged
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show

/** Language picker. First launch continues to onboarding; from Settings it returns there. */
class LanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageBinding
    private val fromSettings by lazy { intent.getBooleanExtra(EXTRA_FROM_SETTINGS, false) }
    private var query = ""
    private lateinit var selectedCode: String

    private val adapter = LanguageAdapter { language ->
        selectedCode = language.code
        bindSelection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.languageRoot.applySystemBarInsets()

        selectedCode = savedInstanceState?.getString(STATE_SELECTED) ?: container.preferences.languageCode
        binding.btnBack.show(fromSettings)
        if (fromSettings) binding.btnContinue.setText(R.string.action_save)
        binding.btnBack.setOnClickListener { finish() }
        binding.rvLanguages.layoutManager = LinearLayoutManager(this)
        binding.rvLanguages.adapter = adapter
        binding.etSearch.onTrimmedTextChanged {
            query = it
            submit()
        }
        binding.btnContinue.setOnClickListener { confirm() }
        submit()
        bindSelection()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SELECTED, selectedCode)
    }

    private fun submit() {
        adapter.submitList(
            AppLanguages.all.filter {
                query.isEmpty() ||
                    it.nativeName.contains(query, ignoreCase = true) ||
                    it.englishName.contains(query, ignoreCase = true)
            },
        )
    }

    private fun bindSelection() {
        adapter.selectedCode = selectedCode
    }

    private fun confirm() {
        val language = AppLanguages.byCode(selectedCode)
        container.preferences.languageCode = language.code
        if (fromSettings) {
            setResult(RESULT_OK, Intent().putExtra(EXTRA_LANGUAGE_NAME, language.nativeName))
            finish()
        } else {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }
        AppLanguages.apply(language.code)
    }

    companion object {
        const val EXTRA_FROM_SETTINGS = "from_settings"
        const val EXTRA_LANGUAGE_NAME = "language_name"
        private const val STATE_SELECTED = "selected_language"
    }
}
