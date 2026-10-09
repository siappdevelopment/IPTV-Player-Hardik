package com.iptvplayer.xtreamiptv.myiptvpro.ADS.models

import androidx.annotation.StringRes

data class LanguageModel(
    val flag: Int,
    @StringRes val nameLocalRes: Int,
    @StringRes val nameEnglishRes: Int,
    @StringRes val countryNameRes: Int,
    val code: String,
    var isSelected: Boolean = false,
)
