package com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class AppsModel {
    public String appName;
    public String packageName;
    /** Launcher activity class name; when set, opens this activity directly (no chooser). */
    @Nullable
    public String className;
    public Drawable appIcon;
    public long installTime;

    public AppsModel(String appName, String packageName, Drawable appIcon, long installTime) {
        this(appName, packageName, null, appIcon, installTime);
    }

    public AppsModel(String appName, String packageName, @Nullable String className,
                     Drawable appIcon, long installTime) {
        this.appName = appName;
        this.packageName = packageName;
        this.className = className;
        this.appIcon = appIcon;
        this.installTime = installTime;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    @Nullable
    public String getClassName() {
        return className;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public long getInstallTime() {
        return installTime;
    }
}
