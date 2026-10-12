package com.iptvplayer.xtreamiptv.myiptvpro.Main.Other

import android.R
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.iptvplayer.xtreamiptv.myiptvpro.Main.model.WidgetInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iptvplayer.xtreamiptv.myiptvpro.Main.model.ShortcutInfo

class WidgetManager(private val context: Context) {
    
    private val appContext = context.applicationContext
    private val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(appContext)
    private val appWidgetHost: AppWidgetHost = LauncherAppWidgetHost(appContext, APPWIDGET_HOST_ID)
    private val prefs: SharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val APPWIDGET_HOST_ID = 1024
        private const val PREFS_NAME = "launcher_widgets"
        private const val KEY_WIDGETS = "widgets"
        private const val KEY_SHORTCUTS = "shortcuts"
    }

    // Custom Host to return our Safe View
    private class LauncherAppWidgetHost(context: Context, hostId: Int) : AppWidgetHost(context, hostId) {
        override fun onCreateView(
            context: Context,
            appWidgetId: Int,
            appWidget: AppWidgetProviderInfo
        ): AppWidgetHostView {
            return SafeAppWidgetHostView(context)
        }
    }

    // Safe Wrapper for Widget View
    private class SafeAppWidgetHostView(context: Context) : AppWidgetHostView(context) {
        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            try {
                super.onLayout(changed, left, top, right, bottom)
            } catch (e: Exception) {
                // Catch layout crashes from buggy widgets
                e.printStackTrace()
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            try {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            } catch (e: Exception) {
                 // Catch measure crashes (like the fontVariationSettings one)
                e.printStackTrace()
                // Provide a safe fallback dimension to avoid "0 size" issues if needed
                 setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
            }
        }
        
        // Optionally wrap dispatchDraw if drawing crashes occur
        /*
        override fun dispatchDraw(canvas: android.graphics.Canvas?) {
            try {
                super.dispatchDraw(canvas)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        */
    }
    
    fun startListening() {
        try {
            appWidgetHost.startListening()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun stopListening() {
        try {
            appWidgetHost.stopListening()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun allocateWidgetId(): Int {
        return appWidgetHost.allocateAppWidgetId()
    }
    
    fun bindWidget(widgetId: Int, provider: AppWidgetProviderInfo): Boolean {
        return try {
            val canBind = appWidgetManager.bindAppWidgetIdIfAllowed(
                widgetId,
                provider.provider
            )
            canBind
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun createWidgetView(widgetId: Int): AppWidgetHostView? {
        return createWidgetView(widgetId, null, null, null, null)
    }


    fun createWidgetView(
        widgetId: Int,
        minWidthDp: Int?,
        minHeightDp: Int?,
        maxWidthDp: Int?,
        maxHeightDp: Int?
    ): AppWidgetHostView? {
        return try {
            val providerInfo = appWidgetManager.getAppWidgetInfo(widgetId)
                ?: return null // Widget not ready yet (e.g. just after bind)
            val hostView = appWidgetHost.createView(context, widgetId, providerInfo)
            hostView.setAppWidget(widgetId, providerInfo)
            if (minWidthDp != null && minHeightDp != null && maxWidthDp != null && maxHeightDp != null) {
                val opts = Bundle().apply {
                    putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, minWidthDp)
                    putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, minHeightDp)
                    putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, maxWidthDp)
                    putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, maxHeightDp)
                }
                hostView.updateAppWidgetSize(opts, minWidthDp, minHeightDp, maxWidthDp, maxHeightDp)
            }
            hostView
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Notify that widget data changed so the provider can refresh content (e.g. list widgets). */
    fun notifyWidgetDataChanged(widgetId: Int) {
        try {
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.list)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun deleteWidget(widgetId: Int) {
        try {
            if (widgetId != 99999) {
                appWidgetHost.deleteAppWidgetId(widgetId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun saveWidgets(widgets: List<WidgetInfo>) {
        val json = gson.toJson(widgets)
        prefs.edit().putString(KEY_WIDGETS, json).apply()
    }
    
    fun loadWidgets(): List<WidgetInfo> {
        val json = prefs.getString(KEY_WIDGETS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<WidgetInfo>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun saveShortcuts(shortcuts: List<ShortcutInfo>) {
        val json = gson.toJson(shortcuts)
        prefs.edit().putString(KEY_SHORTCUTS, json).apply()
    }

    fun loadShortcuts(): List<ShortcutInfo> {
        val json = prefs.getString(KEY_SHORTCUTS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<ShortcutInfo>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Helper to update position of a specific widget
    fun updateWidgetPosition(widgetId: Int, cellX: Int, cellY: Int, pageIndex: Int) {
        val widgets = loadWidgets().toMutableList()
        val index = widgets.indexOfFirst { it.widgetId == widgetId }
        if (index != -1) {
            val old = widgets[index]
            widgets[index] = old.copy(cellX = cellX, cellY = cellY, pageIndex = pageIndex)
            saveWidgets(widgets)
        }
    }

    // Helper to update position of a specific shortcut
    fun updateShortcutPosition(packageName: String, cellX: Int, cellY: Int, pageIndex: Int) {
        val shortcuts = loadShortcuts().toMutableList()
        val index = shortcuts.indexOfFirst { it.packageName == packageName }
        if (index != -1) {
            shortcuts[index] = ShortcutInfo(packageName, cellX, cellY, pageIndex)
            saveShortcuts(shortcuts)
        }
    }

    fun removeShortcut(packageName: String) {
        val shortcuts = loadShortcuts().toMutableList()
        shortcuts.removeAll { it.packageName == packageName }
        saveShortcuts(shortcuts)
    }

    fun updateWidgetSpan(widgetId: Int, spanX: Int, spanY: Int) {
        val widgets = loadWidgets().toMutableList()
        val index = widgets.indexOfFirst { it.widgetId == widgetId }
        if (index != -1) {
            val old = widgets[index]
            widgets[index] = old.copy(spanX = spanX, spanY = spanY)
            saveWidgets(widgets)
        }
    }

    fun removeWidget(widgetId: Int) {
        val widgets = loadWidgets().toMutableList()
        widgets.removeAll { it.widgetId == widgetId }
        saveWidgets(widgets)
        deleteWidget(widgetId)
    }

    fun cleanupUninstalledApps() {
        val shortcuts = loadShortcuts().toMutableList()
        val pm = context.packageManager
        val removed = shortcuts.removeAll { shortcut ->
            try {
                pm.getPackageInfo(shortcut.packageName, 0)
                false
            } catch (e: Exception) {
                true // Package not found
            }
        }
        if (removed) {
            saveShortcuts(shortcuts)
        }
    }

    fun seedDefaultShortcuts() {
        if (prefs.getBoolean("seeded_home_icons_v7", false)) {
            seedCustomWidget()
            return
        }

        val shortcuts = loadShortcuts().toMutableList()
        val pm = context.packageManager

        // Settings, Gallery, Store, Clock
        val targets = listOf(
            Intent(Settings.ACTION_SETTINGS) to listOf("com.android.settings"),
            Intent(Intent.ACTION_VIEW).apply { type = "image/*" } to listOf("com.google.android.apps.photos"),
            Intent(Intent.ACTION_MAIN).apply {
                setPackage("com.android.vending")
                addCategory(Intent.CATEGORY_LAUNCHER)
            } to listOf("com.android.vending"),
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")) to
                    listOf("com.android.chrome")
        )

        val rowToUse = 5

        for (i in targets.indices) {
            val (intent, fallbacks) = targets[i]
            var resolvedPkg: String? = null

            val acts = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (acts.isNotEmpty()) {
                resolvedPkg = acts[0].activityInfo.packageName
            }

            if (resolvedPkg == null) {
                for (pkg in fallbacks) {
                    try {
                        pm.getPackageInfo(pkg, 0)
                        resolvedPkg = pkg
                        break
                    } catch (e: Exception) {}
                }
            }

            if (resolvedPkg != null) {
                shortcuts.removeAll { it.packageName == resolvedPkg && it.pageIndex == 0 }
                shortcuts.add(ShortcutInfo(resolvedPkg, i, rowToUse, 0))
            }
        }

        saveShortcuts(shortcuts)
        prefs.edit().putBoolean("seeded_home_icons_v7", true).apply()

        seedCustomWidget()
    }

    private fun seedCustomWidget() {
        if (prefs.getBoolean("seeded_custom_google_widget_v1", false)) return
        
        val widgets = loadWidgets().toMutableList()
        
        // Remove existing custom search widgets to avoid duplicates
        widgets.removeAll { it.isCustom && it.customType == "google_search_clock" }
        
        // Add custom widget at top of Page 0
        // Span 4x3 (occupies top 3 rows)
        widgets.add(
            WidgetInfo(
                widgetId = 99999, // Custom ID for local identification
                providerPackage = "custom",
                providerClass = "google_search_clock",
                cellX = 0,
                cellY = 0,
                spanX = 4,
                spanY = 3,
                pageIndex = 0,
                isCustom = true,
                customType = "google_search_clock"
            )
        )
        
        saveWidgets(widgets)
        prefs.edit().putBoolean("seeded_custom_google_widget_v1", true).apply()
    }

    fun getAppWidgetHost(): AppWidgetHost = appWidgetHost
    
    fun getAppWidgetManager(): AppWidgetManager = appWidgetManager
}
