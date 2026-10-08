package com.iptvplayer.xtreamiptv.myiptvpro.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityPrivacyBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemPolicyCardBinding
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge

/** The privacy policy as the design's stack of cards (static content bundled in strings). */
class PrivacyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        val binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.privacyRoot.applySystemBarInsets()
        binding.btnBack.setOnClickListener { finish() }

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
}
