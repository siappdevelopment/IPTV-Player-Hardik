package com.iptvplayer.xtreamiptv.myiptvpro.utils

import android.content.Context
import com.iptvplayer.xtreamiptv.myiptvpro.R

/** "just now", "5m ago", "2h ago", "Yesterday", "3d ago". */
fun Context.relativeTime(millis: Long, now: Long = System.currentTimeMillis()): String {
    val minutes = ((now - millis) / 60_000L).coerceAtLeast(0)
    if (minutes < 1) return getString(R.string.time_just_now)
    if (minutes < 60) return getString(R.string.time_minutes_ago, minutes.toInt())
    val hours = (minutes + 30) / 60
    if (hours < 24) return getString(R.string.time_hours_ago, hours.toInt())
    val days = (hours + 12) / 24
    return if (days <= 1L) getString(R.string.time_yesterday) else getString(R.string.time_days_ago, days.toInt())
}
