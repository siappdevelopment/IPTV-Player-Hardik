package com.iptvplayer.xtreamiptv.myiptvpro.Main.model

data class WidgetInfo(
    val widgetId: Int,
    val providerPackage: String,
    val providerClass: String,
    val cellX: Int,
    val cellY: Int,
    val spanX: Int,
    val spanY: Int,
    val pageIndex: Int = 0,
    val isCustom: Boolean = false,
    val customType: String? = null
)
