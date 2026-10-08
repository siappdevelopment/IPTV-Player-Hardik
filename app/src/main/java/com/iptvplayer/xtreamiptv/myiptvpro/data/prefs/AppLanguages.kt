package com.iptvplayer.xtreamiptv.myiptvpro.data.prefs

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/** One entry of the language picker: BCP-47 [code], two-letter [short] tile text, native and English names. */
data class AppLanguage(val code: String, val short: String, val nativeName: String, val englishName: String)

object AppLanguages {
    val all = listOf(
        AppLanguage("en", "EN", "English", "English"),
        AppLanguage("hi", "HI", "हिन्दी", "Hindi"),
        AppLanguage("es", "ES", "Español", "Spanish"),
        AppLanguage("pt", "PT", "Português", "Portuguese"),
        AppLanguage("ar", "AR", "العربية", "Arabic"),
        AppLanguage("fr", "FR", "Français", "French"),
        AppLanguage("in", "ID", "Bahasa Indonesia", "Indonesian"),
        AppLanguage("tr", "TR", "Türkçe", "Turkish"),
        AppLanguage("de", "DE", "Deutsch", "German"),
        AppLanguage("ru", "RU", "Русский", "Russian"),
        AppLanguage("bn", "BN", "বাংলা", "Bengali"),
        AppLanguage("ur", "UR", "اردو", "Urdu"),
    )

    fun byCode(code: String): AppLanguage = all.firstOrNull { it.code == code } ?: all.first()

    /** Applies the language app-wide (AppCompat persists it and recreates visible activities). */
    fun apply(code: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
    }
}
