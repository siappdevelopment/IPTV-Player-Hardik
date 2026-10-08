package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import androidx.annotation.DrawableRes
import com.iptvplayer.xtreamiptv.myiptvpro.R

/** Well-known categories, in the order the design lists them. Stored values are these exact names. */
object Categories {
    val known = listOf(
        "Entertainment", "Family", "Sports", "Movies", "Documentary", "Lifestyle",
        "News", "Music", "Kids", "Comedy", "General",
    )

    /** Sort key: known categories first (design order), then everything else alphabetically. */
    fun sortKey(name: String): String {
        val index = known.indexOfFirst { it.equals(name, ignoreCase = true) }
        return if (index >= 0) "0%02d".format(index) else "1" + name.lowercase()
    }

    @DrawableRes
    fun icon(name: String): Int = when (name.lowercase()) {
        "entertainment" -> R.drawable.ic_theaters
        "family" -> R.drawable.ic_family_restroom
        "sports" -> R.drawable.ic_sports_soccer
        "movies" -> R.drawable.ic_movie
        "documentary" -> R.drawable.ic_forest
        "lifestyle" -> R.drawable.ic_restaurant
        "news" -> R.drawable.ic_newspaper
        "music" -> R.drawable.ic_music_note
        "kids" -> R.drawable.ic_toys
        "comedy" -> R.drawable.ic_sentiment_very_satisfied
        else -> R.drawable.ic_live_tv
    }

    /** Artwork glyph for a playlist card: a category icon when the name suggests one. */
    @DrawableRes
    fun playlistIcon(name: String): Int {
        val match = known.firstOrNull { name.contains(it, ignoreCase = true) }
        return if (match != null) icon(match) else R.drawable.ic_video_library
    }
}
