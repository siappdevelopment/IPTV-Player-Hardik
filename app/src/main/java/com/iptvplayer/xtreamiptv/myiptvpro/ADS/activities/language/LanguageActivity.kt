package com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.language

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.adapter.LanguageAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSAppManage
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSBannerAdaptive
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSConsentManager
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSInterDisplay
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSInterDisplayClick
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSMainClass
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSMainClass.INTER_FIRST_TIME
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSNativeDisplay
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSUtilitis
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.models.LanguageModel
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation.StartupFlowManager
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation.StartupFlowStep
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.Theme.ThemeManager
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityLanguageBinding
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Constance
import com.iptvplayer.xtreamiptv.myiptvpro.utils.LanguageManager
import com.iptvplayer.xtreamiptv.myiptvpro.utils.LocaleAwareAppCompatActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.LocaleHelper
import com.iptvplayer.xtreamiptv.myiptvpro.utils.OnClickHandler
import com.iptvplayer.xtreamiptv.myiptvpro.utils.SharedPreferenceManager

class LanguageActivity : LocaleAwareAppCompatActivity(), OnClickHandler {

    private lateinit var binding: ActivityLanguageBinding
    private lateinit var adapter: LanguageAdapter
    private var showIcon = false
    private var tempSelectedLanguageCode = ""
    private var languageAdsLoaded = false

    override fun shouldAutoRefreshLocale(): Boolean = false

    private val languageList = mutableListOf(
        LanguageModel(
            R.drawable.ic_lang_english,
            R.string.lang_en_local,
            R.string.lang_en_english,
            R.string.lang_en_country,
            "en"
        ),
        LanguageModel(
            R.drawable.ic_lang_english_uk,
            R.string.lang_en_gb_local,
            R.string.lang_en_gb_english,
            R.string.lang_en_gb_country,
            "en-GB"
        ),
        LanguageModel(
            R.drawable.ic_lang_india,
            R.string.lang_hi_local,
            R.string.lang_hi_english,
            R.string.lang_hi_country,
            "hi"
        ),
        LanguageModel(
            R.drawable.ic_lang_russian,
            R.string.lang_ru_local,
            R.string.lang_ru_english,
            R.string.lang_ru_country,
            "ru"
        ),
        LanguageModel(
            R.drawable.ic_lang_italian,
            R.string.lang_it_local,
            R.string.lang_it_english,
            R.string.lang_it_country,
            "it"
        ),
        LanguageModel(
            R.drawable.ic_lang_french,
            R.string.lang_fr_local,
            R.string.lang_fr_english,
            R.string.lang_fr_country,
            "fr"
        ),
        LanguageModel(
            R.drawable.ic_lang_spanish,
            R.string.lang_es_local,
            R.string.lang_es_english,
            R.string.lang_es_country,
            "es"
        ),
        LanguageModel(
            R.drawable.ic_lang_japanese,
            R.string.lang_ja_local,
            R.string.lang_ja_english,
            R.string.lang_ja_country,
            "ja"
        ),
        LanguageModel(
            R.drawable.ic_lang_korean,
            R.string.lang_ko_local,
            R.string.lang_ko_english,
            R.string.lang_ko_country,
            "ko"
        ),
        LanguageModel(
            R.drawable.ic_lang_german,
            R.string.lang_de_local,
            R.string.lang_de_english,
            R.string.lang_de_country,
            "de"
        ),
        LanguageModel(
            R.drawable.ic_lang_chinese,
            R.string.lang_zh_local,
            R.string.lang_zh_english,
            R.string.lang_zh_country,
            "zh"
        ),
        LanguageModel(
            R.drawable.ic_lang_thai,
            R.string.lang_th_local,
            R.string.lang_th_english,
            R.string.lang_th_country,
            "th"
        ),
        LanguageModel(
            R.drawable.ic_lang_greek,
            R.string.lang_el_local,
            R.string.lang_el_english,
            R.string.lang_el_country,
            "el"
        ),
        LanguageModel(
            R.drawable.ic_lang_portuguese,
            R.string.lang_pt_local,
            R.string.lang_pt_english,
            R.string.lang_pt_country,
            "pt"
        ),
        LanguageModel(
            R.drawable.ic_lang_brazil,
            R.string.lang_pt_br_local,
            R.string.lang_pt_br_english,
            R.string.lang_pt_br_country,
            "pt-BR"
        ),
        LanguageModel(
            R.drawable.ic_lang_dutch,
            R.string.lang_nl_local,
            R.string.lang_nl_english,
            R.string.lang_nl_country,
            "nl"
        ),
        LanguageModel(
            R.drawable.ic_lang_filipino,
            R.string.lang_fil_local,
            R.string.lang_fil_english,
            R.string.lang_fil_country,
            "fil"
        ),
        LanguageModel(
            R.drawable.ic_lang_turkish,
            R.string.lang_tr_local,
            R.string.lang_tr_english,
            R.string.lang_tr_country,
            "tr"
        ),
        LanguageModel(
            R.drawable.ic_lang_indonesian,
            R.string.lang_id_local,
            R.string.lang_id_english,
            R.string.lang_id_country,
            "id"
        ),
        LanguageModel(
            R.drawable.ic_lang_africa,
            R.string.lang_af_local,
            R.string.lang_af_english,
            R.string.lang_af_country,
            "af"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showIcon = intent.getBooleanExtra("language", false)
        tempSelectedLanguageCode = LocaleHelper.readSavedLanguage(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language)
        ThemeManager.applyTheme(this)
        initView()
        if (ADSMainClass.shouldShowConsentOnLanguage()) {
            continueAfterConsent { loadLanguageAds() }
        } else {
            initializeAdsAndLoad { loadLanguageAds() }
        }
    }

    private fun initializeAdsAndLoad(action: () -> Unit) {
        if (ADSConsentManager.canRequestAds(this) && !ADSMainClass.getAds_Free()) {
            ADSAppManage.initializeMobileAdsIfNeeded(applicationContext) {
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) action()
                }
            }
        } else {
            runOnUiThread {
                if (!isFinishing && !isDestroyed) action()
            }
        }
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
        if (ADSConsentManager.canRequestAds(this) && !ADSMainClass.getAds_Free()) {
            ADSAppManage.initializeMobileAdsIfNeeded(applicationContext) {
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) action()
                }
            }
        } else {
            runOnUiThread {
                if (!isFinishing && !isDestroyed) action()
            }
        }
    }

    private fun hideLanguageAdPlaceholders() {
        findViewById<View>(R.id.shimmer_container_small).visibility = View.GONE
        findViewById<View>(R.id.cardView).visibility = View.GONE
        findViewById<View>(R.id.flNativeSmallPlaceholder).visibility = View.GONE
        findViewById<View>(R.id.flBannerSmallPlaceholder).visibility = View.GONE
    }

    private fun loadLanguageAds() {
        if (languageAdsLoaded) return
        if (!ADSMainClass.getLanguageScreenBottomAdShow()) {
            hideLanguageAdPlaceholders()
            return
        }
        if (!ADSConsentManager.canRequestAds(this) || ADSMainClass.getAds_Free()) {
            hideLanguageAdPlaceholders()
            return
        }
        languageAdsLoaded = true
        if (ADSMainClass.getLanguageAdsType().equals("native")) {
            findViewById<View>(R.id.flBannerSmallPlaceholder).visibility = View.GONE
            Log.d("asasalspaslas", "loadLanguageAds: ")
            ADSNativeDisplay.loadAdmobNativeAdBig(
                ADSMainClass.getStringValue(ADSMainClass.LANGUAGE_SCREEN_NATIVE),
                findViewById(R.id.flNativeSmallPlaceholder),
                findViewById(R.id.shimmer_container_small),
                "big",
                this
            )
        } else {
            findViewById<View>(R.id.cardView).visibility = View.GONE
            findViewById<View>(R.id.flNativeSmallPlaceholder).visibility = View.GONE
            ADSBannerAdaptive.loadAdMobBanner(
                ADSMainClass.getStringValue(ADSMainClass.LANGUAGE_SCREEN_BANNER),
                findViewById(R.id.flBannerSmallPlaceholder),
                findViewById(R.id.shimmer_container_small),
                this,
                "small"
            )
        }
    }

    private fun initView() {
        binding.onClickHandler = this
        val currentLangCode = LocaleHelper.readSavedLanguage(this)
        val isAlreadyLoggedIn = SharedPreferenceManager.getBoolean(this, Constance.IS_LOG_IN)

        languageList.forEach { it.isSelected = false }
        if (currentLangCode.isNotEmpty()) {
            languageList.forEach {
                it.isSelected =
                    LanguageManager.normalizeLanguageCode(it.code) == currentLangCode
            }
        } else if (isAlreadyLoggedIn) {
            languageList.find { it.code == "en" }?.isSelected = true
        }

        if (ADSMainClass.getLanguageInterAdsShow() && ADSMainClass.isInterPreLoad()
            && ADSUtilitis.IsNetworkConnected(this)
        ) {
            ADSInterDisplay.preloadInterstitialAd(
                this,
                ADSMainClass.getStringValue(ADSMainClass.INTER_FIRST_TIME)
            )
        }

        adapter = LanguageAdapter(languageList) { selected ->
            tempSelectedLanguageCode = selected.code
            AppAnalyticsEvents.track(
                this,
                AppAnalyticsEvents.LANGUAGE_SELECT,
                AppAnalyticsEvents.PARAM_LANGUAGE_CODE,
                selected.code
            )
        }
        binding.rvLanguages.adapter = adapter
        binding.rvLanguages.layoutManager = LinearLayoutManager(this)

        if (adapter.getSelectedPosition() != -1) {
            tempSelectedLanguageCode = adapter.getSelectedLanguage().code
        }

        binding.ivBack.visibility = if (showIcon) View.VISIBLE else View.GONE
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        })
        binding.ivBack.setOnClickListener {
            onBack()

        }
    }

    /**
     * Apply language without AppCompat setApplicationLocales and without recreating Home
     * in the background (Samsung Android 15 brings Home to front if Home.recreate() is called).
     */
    private fun applySelectedLanguage(languageCode: String) {
        LocaleHelper.setLocale(this, languageCode)
        SharedPreferenceManager.putBoolean(this, Constance.IS_LOG_IN, true)
    }

    private fun navigateAfterLanguageSelected() {
        if (isFinishing || isDestroyed) return
        if (tempSelectedLanguageCode.isBlank()) {
            Toast.makeText(this, getString(R.string.please_select_language), Toast.LENGTH_SHORT)
                .show()
            return
        }

        applySelectedLanguage(tempSelectedLanguageCode)
        AppAnalyticsEvents.track(
            this,
            AppAnalyticsEvents.LANGUAGE_CONTINUE,
            "language_code",
            tempSelectedLanguageCode
        )

        if (showIcon) {
            // Settings → Language → Done: stay in Settings stack. Never open Home.
            finish()
            return
        }
        // First-install flow only
        StartupFlowManager.completeStep(this, StartupFlowStep.LANGUAGE)
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

    override fun onClick(view: View) {
        if (!isValidClick()) return
        when (view.id) {
            binding.cvDone.id -> {
                if (adapter.getSelectedPosition() == -1) {
                    Toast.makeText(
                        this,
                        getString(R.string.please_select_language),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                tempSelectedLanguageCode = adapter.getSelectedLanguage().code

                if (LanguageManager.isSameLanguage(this, tempSelectedLanguageCode)
                    && SharedPreferenceManager.getBoolean(this, Constance.IS_LOG_IN)
                ) {
                    if (showIcon) {
                        finish()
                    } else {
                        StartupFlowManager.completeStep(this, StartupFlowStep.LANGUAGE)
                    }
                    return
                }

                if (ADSMainClass.getLanguageInterAdsShow()) {
                    ADSInterDisplay.ADSInterstitialShowing(
                        this@LanguageActivity,
                        ADSMainClass.getStringValue(INTER_FIRST_TIME),
                        { _ -> navigateAfterLanguageSelected() },
                        ADSMainClass.getLanguageInterAdsShow()
                    )
                } else {
                    navigateAfterLanguageSelected()
                }
            }

        }
    }

    fun onBack() {
        overridePendingTransition(0, 0)
        if (showIcon) {
            ADSInterDisplayClick.ADSBackDisplayInterstitial(
                this@LanguageActivity,
                ADSMainClass.getStringValue(ADSMainClass.INTER_SECOND_TIME),
                { _ -> finish() },
                ADSMainClass.getLanguageInterAdsShow()
            )
            return
        }
        finishAffinity()
    }

    override fun onResume() {
        try {
            super.onResume()
        } catch (e: ClassCastException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        hideSystemUI(this)
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

    override fun onStop() {
        super.onStop()
        try {
            ADSUtilitis.MassageBoxFullDismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        try {
            ADSUtilitis.MassageBoxFullDismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
