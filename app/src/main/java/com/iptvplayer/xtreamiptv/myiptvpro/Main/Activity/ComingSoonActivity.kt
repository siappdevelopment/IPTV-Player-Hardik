package com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity

import android.os.Bundle
import android.widget.ImageView
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege
import com.iptvplayer.xtreamiptv.myiptvpro.R

class ComingSoonActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coming_soon)

        findViewById<ImageView>(R.id.close_coming).setOnClickListener {
            finishAffinity()
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
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