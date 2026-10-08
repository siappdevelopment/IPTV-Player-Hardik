package com.iptvplayer.xtreamiptv.myiptvpro.utils

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge

/**
 * Edge-to-edge with light system-bar icons. The app is dark-only, so the bar style must not
 * follow the phone's light/dark setting (that would give dark icons on a dark background).
 */
fun ComponentActivity.enableAppEdgeToEdge() {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
    )
}
