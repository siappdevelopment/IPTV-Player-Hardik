package com.iptvplayer.xtreamiptv.myiptvpro.Main.Other

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PackageReceiver(private val onPackageChanged: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_PACKAGE_REMOVED || 
            action == Intent.ACTION_PACKAGE_ADDED || 
            action == Intent.ACTION_PACKAGE_REPLACED) {
            
            val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
            if (action == Intent.ACTION_PACKAGE_REMOVED && isReplacing) {
                return
            }
            
            onPackageChanged()
        }
    }
}
