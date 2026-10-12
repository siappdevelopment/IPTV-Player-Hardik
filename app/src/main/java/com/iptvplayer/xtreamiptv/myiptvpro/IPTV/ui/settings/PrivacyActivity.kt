package com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityPrivacyBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemPolicyCardBinding

/** The privacy policy as the design's stack of cards (static content bundled in strings). */
class PrivacyActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        val binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.privacyRoot.applySystemBarInsets()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backClick()
                }
            }
        )

        binding.btnBack.setOnClickListener { backClick() }

        val titles = resources.getStringArray(R.array.privacy_titles)
        val bodies = resources.getStringArray(R.array.privacy_bodies)
        val icons = resources.obtainTypedArray(R.array.privacy_icons)
        val inflater = LayoutInflater.from(this)
        titles.forEachIndexed { index, title ->
            val card = ItemPolicyCardBinding.inflate(inflater, binding.llPolicy, false)
            card.ivIcon.setImageResource(icons.getResourceId(index, 0))
            card.tvTitle.text = title
            card.tvBody.text = bodies[index]
            binding.llPolicy.addView(card.root)
        }
        icons.recycle()
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
