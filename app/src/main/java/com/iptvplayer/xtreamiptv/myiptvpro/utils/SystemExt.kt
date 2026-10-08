package com.iptvplayer.xtreamiptv.myiptvpro.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

/** True when the device has a network that claims internet access. */
fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

/** The display name of a document picked with the system file picker, or null. */
fun Context.queryDisplayName(uri: Uri): String? = try {
    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    }
} catch (e: Exception) {
    null
}

fun Context.copyToClipboard(label: String, text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}

/** Starts [intent]; returns false when no app can handle it. */
fun Context.startActivitySafely(intent: Intent): Boolean = try {
    startActivity(intent)
    true
} catch (e: android.content.ActivityNotFoundException) {
    false
}

/** Builds a ViewModel factory from a lambda, so VMs with constructor arguments stay easy. */
inline fun <reified VM : ViewModel> vmFactory(crossinline create: () -> VM): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
            create() as T
    }
