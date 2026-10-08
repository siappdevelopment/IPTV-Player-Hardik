package com.iptvplayer.xtreamiptv.myiptvpro.ui.player

import android.content.Intent

/** Where the player takes its channel queue from. */
sealed interface PlayerSource {
    data class Playlist(val id: Long, val name: String) : PlayerSource
    data class Category(val name: String) : PlayerSource
    data object AllChannels : PlayerSource
    data object Recent : PlayerSource
    data object Favorites : PlayerSource
    /** Just the one channel (e.g. a history row whose playlist is gone). */
    data object Single : PlayerSource

    fun writeTo(intent: Intent): Intent = intent.apply {
        when (val s = this@PlayerSource) {
            is Playlist -> { putExtra(KEY_TYPE, "playlist"); putExtra(KEY_ID, s.id); putExtra(KEY_NAME, s.name) }
            is Category -> { putExtra(KEY_TYPE, "category"); putExtra(KEY_NAME, s.name) }
            AllChannels -> putExtra(KEY_TYPE, "all")
            Recent -> putExtra(KEY_TYPE, "recent")
            Favorites -> putExtra(KEY_TYPE, "favorites")
            Single -> putExtra(KEY_TYPE, "single")
        }
    }

    companion object {
        private const val KEY_TYPE = "src_type"
        private const val KEY_ID = "src_id"
        private const val KEY_NAME = "src_name"

        fun readFrom(intent: Intent): PlayerSource = when (intent.getStringExtra(KEY_TYPE)) {
            "playlist" -> Playlist(intent.getLongExtra(KEY_ID, -1L), intent.getStringExtra(KEY_NAME).orEmpty())
            "category" -> Category(intent.getStringExtra(KEY_NAME).orEmpty())
            "all" -> AllChannels
            "recent" -> Recent
            "favorites" -> Favorites
            else -> Single
        }
    }
}
