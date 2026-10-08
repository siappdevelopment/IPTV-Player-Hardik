package com.iptvplayer.xtreamiptv.myiptvpro.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivitySplashBinding
import com.iptvplayer.xtreamiptv.myiptvpro.ui.language.LanguageActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.main.MainActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Brand moment. First launch → Language → Onboarding → Playlists; returning users go straight to
 * the app (and to the last channel when "Resume last channel" is on).
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.splashRoot.applySystemBarInsets()

        lifecycleScope.launch {
            delay(SPLASH_MS)
            route()
            finish()
        }
    }

    private fun route() {
        val prefs = container.preferences
        if (!prefs.onboardingDone) {
            startActivity(Intent(this, LanguageActivity::class.java))
            return
        }
        startActivity(Intent(this, MainActivity::class.java))
        if (prefs.resumeLastChannel) {
            prefs.lastChannel()?.let { (channel, playlistId) ->
                val source = if (playlistId != null) PlayerSource.Playlist(playlistId, "") else PlayerSource.Single
                startActivity(PlayerActivity.intent(this, source, channel))
            }
        }
    }

    private companion object {
        const val SPLASH_MS = 1800L
    }
}
