package com.iptvplayer.xtreamiptv.myiptvpro.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityOnboardingBinding
import com.iptvplayer.xtreamiptv.myiptvpro.ui.main.MainActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show

/** Four intro steps. Skip / Get Started marks onboarding complete so it never repeats. */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private var step = 0
    private lateinit var arts: List<View>
    private val dots = mutableListOf<View>()

    private val titles = intArrayOf(R.string.onb_1_title, R.string.onb_2_title, R.string.onb_3_title, R.string.onb_4_title)
    private val subs = intArrayOf(R.string.onb_1_sub, R.string.onb_2_sub, R.string.onb_3_sub, R.string.onb_4_sub)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.onboardingRoot.applySystemBarInsets()

        step = savedInstanceState?.getInt(STATE_STEP) ?: 0
        arts = listOf(binding.art1.root, binding.art2.root, binding.art3.root, binding.art4.root)
        repeat(titles.size) { dots.add(addDot()) }

        binding.btnNext.setOnClickListener { if (step < titles.lastIndex) show(step + 1) else finishOnboarding() }
        binding.btnSkip.setOnClickListener { finishOnboarding() }
        onBackPressedDispatcher.addCallback(this) {
            if (step > 0) show(step - 1) else finish()
        }
        show(step)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_STEP, step)
    }

    private fun addDot(): View {
        val dot = View(this)
        dot.setBackgroundResource(R.drawable.bg_onb_dot)
        val params = android.widget.LinearLayout.LayoutParams(px(R.dimen._8sdp), px(R.dimen._8sdp))
        params.marginEnd = px(R.dimen._6sdp)
        binding.dots.addView(dot, params)
        return dot
    }

    private fun show(newStep: Int) {
        step = newStep
        arts.forEachIndexed { i, art -> art.show(i == step) }
        binding.tvTitle.setText(titles[step])
        binding.tvSub.setText(subs[step])
        val last = step == titles.lastIndex
        binding.btnSkip.show(!last)
        binding.btnNext.setText(if (last) R.string.action_get_started else R.string.action_next)
        dots.forEachIndexed { i, dot ->
            dot.isSelected = i == step
            dot.layoutParams = dot.layoutParams.apply { width = px(if (i == step) R.dimen._24sdp else R.dimen._8sdp) }
        }
    }

    private fun finishOnboarding() {
        container.preferences.onboardingDone = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun px(@androidx.annotation.DimenRes res: Int) = resources.getDimensionPixelSize(res)

    private companion object {
        const val STATE_STEP = "step"
    }
}
