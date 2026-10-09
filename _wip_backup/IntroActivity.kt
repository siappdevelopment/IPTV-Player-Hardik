package com.iptvplayer.xtreamiptv.myiptvpro.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityIntroBinding
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show

/** One intro page: art, title, dots, Continue / Next and a bottom ad. Subclasses only set [step]. */
abstract class IntroActivity : AppCompatActivity() {

    /** 1-based page number. */
    protected abstract val step: Int

    private lateinit var binding: ActivityIntroBinding
    private var releaseAd: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.introRoot.applySystemBarInsets()

        val ads = container.ads
        val count = container.adsConfig.onboardingCount
        val arts: List<View> = listOf(binding.art1.root, binding.art2.root, binding.art3.root)
        arts.forEachIndexed { i, art -> art.show(i == step - 1) }
        binding.tvTitle.setText(TITLES[step - 1])
        binding.tvSub.setText(SUBS[step - 1])
        binding.btnNext.setText(if (step >= count) R.string.action_get_started else R.string.action_next)
        setupDots(count)

        releaseAd = ads.loadBottomAd(this, binding.adSlot, binding.adPlaceholder)
        ads.preloadInterstitial()

        binding.btnNext.setOnClickListener {
            binding.btnNext.isEnabled = false
            ads.showInterstitial(this) { IntroNavigation.next(this, step) }
        }
        onBackPressedDispatcher.addCallback(this) {
            if (step == 1) finishAffinity() else finish()
        }
    }

    override fun onDestroy() {
        releaseAd()
        super.onDestroy()
    }

    private fun setupDots(count: Int) {
        repeat(count) { i ->
            val dot = View(this)
            dot.setBackgroundResource(R.drawable.bg_onb_dot)
            dot.isSelected = i == step - 1
            val active = i == step - 1
            val params = LinearLayout.LayoutParams(px(if (active) R.dimen._24sdp else R.dimen._8sdp), px(R.dimen._8sdp))
            params.marginEnd = px(R.dimen._6sdp)
            binding.dots.addView(dot, params)
        }
    }

    private fun px(@DimenRes res: Int) = resources.getDimensionPixelSize(res)

    private companion object {
        val TITLES = intArrayOf(R.string.onb_1_title, R.string.onb_2_title, R.string.onb_3_title)
        val SUBS = intArrayOf(R.string.onb_1_sub, R.string.onb_2_sub, R.string.onb_3_sub)
    }
}

class Intro1Activity : IntroActivity() {
    override val step = 1
}

class Intro2Activity : IntroActivity() {
    override val step = 2
}

class Intro3Activity : IntroActivity() {
    override val step = 3
}
