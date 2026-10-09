package com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.TypedValue;

import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.iptvplayer.xtreamiptv.myiptvpro.utils.ThemeManager;

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.model.AppsModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Utils {
    private static final String PREF_KEY_INTRO_COMPLETED = "introCompleted";
    private static final String PREF_KEY_APP_LANGUAGE_NEW = "appLanguageNew";
    private static final String PREF_KEY_LABEL_VISIBILITY = "labelVisibility";
    private static final String PREF_KEY_APP_ICON_SIZE = "appIconSize";
    private static final String PREF_KEY_APP_LABEL_SIZE = "appLabelSize";
    private static final String PREF_KEY_APP_SERIALIZE = "appSerialize";
    public static boolean isFromContacts;

    public static void applyStoredLocale(Context context) {
        applyLocaleWithoutRecreate(context, getAppLanguageNew(context));
    }

    public static void setLocale(Context context, String languageCode) {
        applyLocaleWithoutRecreate(context, languageCode);
    }

    @SuppressWarnings("deprecation")
    public static void applyLocaleWithoutRecreate(Context context, String languageCode) {
        if (languageCode == null || languageCode.isEmpty()) {
            languageCode = "en";
        }
        Locale locale = Locale.forLanguageTag(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static Context wrapContext(Context context) {
        String languageCode = getAppLanguageNew(context);
        if (languageCode == null || languageCode.isEmpty()) {
            languageCode = "en";
        }
        Locale locale = Locale.forLanguageTag(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }
        return context.createConfigurationContext(configuration);
    }

    public static void setIntroCompleted(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("introCompleted", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(PREF_KEY_INTRO_COMPLETED, value).apply();
    }

    public static boolean getIntroCompleted(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("introCompleted", MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_KEY_INTRO_COMPLETED, false);
    }

    public static void setAppLanguageNew(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appLanguageNew", MODE_PRIVATE);
        sharedPreferences.edit().putString(PREF_KEY_APP_LANGUAGE_NEW, value).commit();
    }

    public static String getAppLanguageNew(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appLanguageNew", MODE_PRIVATE);
        return sharedPreferences.getString(PREF_KEY_APP_LANGUAGE_NEW, "en");
    }

    public static void setLabelVisibility(Context context, Boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("labelVisibility", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(PREF_KEY_LABEL_VISIBILITY, value).apply();
    }

    public static Boolean getLabelVisibility(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("labelVisibility", MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_KEY_LABEL_VISIBILITY, true);
    }

    public static void setAppIconSize(Context context, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appIconSize", MODE_PRIVATE);
        sharedPreferences.edit().putInt(PREF_KEY_APP_ICON_SIZE, value).apply();
    }

    public static int getAppIconSize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appIconSize", MODE_PRIVATE);
        return sharedPreferences.getInt(PREF_KEY_APP_ICON_SIZE, 60);
    }

    public static void setAppLabelSize(Context context, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appLabelSize", MODE_PRIVATE);
        sharedPreferences.edit().putInt(PREF_KEY_APP_LABEL_SIZE, value).apply();
    }

    public static int getAppLabelSize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appLabelSize", MODE_PRIVATE);
        return sharedPreferences.getInt(PREF_KEY_APP_LABEL_SIZE, 12);
    }

    public static void setAppSerialize(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appSerialize", MODE_PRIVATE);
        sharedPreferences.edit().putString(PREF_KEY_APP_SERIALIZE, value).commit();
    }

    public static String getAppSerialize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appSerialize", MODE_PRIVATE);
        return sharedPreferences.getString(PREF_KEY_APP_SERIALIZE, "a");
    }

    public static void sortAppsList(Context context, List<AppsModel> list) {
        if (context == null || list == null || list.size() < 2) {
            return;
        }
        String serialize = getAppSerialize(context);
        Comparator<AppsModel> comparator;
        if ("d".equals(serialize)) {
            comparator = (a, b) -> appName(b).compareToIgnoreCase(appName(a));
        } else if ("i".equals(serialize)) {
            comparator = (a, b) -> Long.compare(b.getInstallTime(), a.getInstallTime());
        } else {
            comparator = (a, b) -> appName(a).compareToIgnoreCase(appName(b));
        }
        synchronized (list) {
            list.sort(comparator);
        }
    }

    private static String appName(AppsModel model) {
        String name = model != null ? model.getAppName() : null;
        return name != null ? name : "";
    }

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void trackScreen(Context context, String screenName) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(screenName, true);
            FirebaseAnalytics.getInstance(context).logEvent(screenName, bundle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCompletingDefaultAppSetup(Context context, boolean value) {
        // commit(): LauncherHome may open immediately after role grant and must see this flag.
        context.getApplicationContext().getSharedPreferences("defaultAppFlow", MODE_PRIVATE)
                .edit().putBoolean("completingDefaultAppSetup", value).commit();
    }

    public static boolean isCompletingDefaultAppSetup(Context context) {
        return context.getApplicationContext().getSharedPreferences("defaultAppFlow", MODE_PRIVATE)
                .getBoolean("completingDefaultAppSetup", false);
    }

    /**
     * Set when the system delivers a HOME intent to our real launcher during default setup
     * (user tapped Always). Survives RoleManager lag on Android 12.
     */
    public static void setLauncherChosenDuringSetup(Context context, boolean value) {
        context.getApplicationContext().getSharedPreferences("defaultAppFlow", MODE_PRIVATE)
                .edit().putBoolean("launcherChosenDuringSetup", value).commit();
    }

    public static boolean isLauncherChosenDuringSetup(Context context) {
        return context.getApplicationContext().getSharedPreferences("defaultAppFlow", MODE_PRIVATE)
                .getBoolean("launcherChosenDuringSetup", false);
    }

    /** True when this app should be treated as the home launcher (role, preferred, or Always just chosen). */
    public static boolean isDefaultHomeAppOrChosen(Context context) {
        return isDefaultHomeApp(context) || isLauncherChosenDuringSetup(context);
    }

    /** Avoid re-sampling wallpaper on every page swipe / status-bar update. */
    private static final long WALLPAPER_DARK_CACHE_MS = 60_000L;
    private static Boolean cachedWallpaperDark;
    private static long wallpaperDarkCachedAtMs;

    public static boolean isWallpaperDark(Context context) {
        long now = SystemClock.elapsedRealtime();
        if (cachedWallpaperDark != null
                && (now - wallpaperDarkCachedAtMs) < WALLPAPER_DARK_CACHE_MS) {
            return cachedWallpaperDark;
        }
        boolean dark;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                WallpaperColors colors = WallpaperManager.getInstance(context)
                        .getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
                if (colors != null) {
                    int hints = colors.getColorHints();
                    dark = (hints & WallpaperColors.HINT_SUPPORTS_DARK_TEXT) == 0;
                    cachedWallpaperDark = dark;
                    wallpaperDarkCachedAtMs = now;
                    return dark;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dark = isWallpaperDarkFromBitmap(context);
        cachedWallpaperDark = dark;
        wallpaperDarkCachedAtMs = now;
        return dark;
    }

    private static boolean isWallpaperDarkFromBitmap(Context context) {
        try {
            Drawable drawable = WallpaperManager.getInstance(context).getDrawable();
            if (drawable == null) {
                return true;
            }

            Bitmap bitmap = drawableToBitmap(drawable);
            int sampleWidth = Math.min(bitmap.getWidth(), 64);
            int sampleHeight = Math.min(bitmap.getHeight(), 64);
            Bitmap sample = Bitmap.createScaledBitmap(bitmap, sampleWidth, sampleHeight, true);

            long totalLuminance = 0;
            int pixelCount = sampleWidth * sampleHeight;
            for (int x = 0; x < sampleWidth; x++) {
                for (int y = 0; y < sampleHeight; y++) {
                    int pixel = sample.getPixel(x, y);
                    totalLuminance += (long) (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel));
                }
            }

            if (sample != bitmap) {
                sample.recycle();
            }
            return ((double) totalLuminance / pixelCount) < 128;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                return bitmap;
            }
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            width = 64;
            height = 64;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * True when this app is the device Home/launcher.
     * Checks RoleManager (API 29+) and PackageManager preferred HOME — some OEMs
     * (Samsung Android 12) update preferred activity before {@code isRoleHeld}.
     */
    public static boolean isDefaultHomeApp(Context context) {
        if (context == null) {
            return false;
        }
        if (isRoleHomeHeld(context)) {
            return true;
        }
        return isPreferredHomePackage(context);
    }

    /** RoleManager ROLE_HOME held (API 29+). */
    public static boolean isRoleHomeHeld(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return false;
        }
        try {
            RoleManager roleManager = context.getSystemService(RoleManager.class);
            return roleManager != null
                    && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)
                    && roleManager.isRoleHeld(RoleManager.ROLE_HOME);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * True when PackageManager resolves HOME to this package (not the system chooser).
     */
    public static boolean isPreferredHomePackage(Context context) {
        ResolveInfo resolveInfo = resolvePreferredHome(context);
        if (resolveInfo == null || resolveInfo.activityInfo == null) {
            return false;
        }
        String pkg = resolveInfo.activityInfo.packageName;
        String name = resolveInfo.activityInfo.name;
        if (isHomeChooserComponent(pkg, name)) {
            return false;
        }
        return context.getPackageName().equals(pkg);
    }

    /**
     * True when some other launcher is already the preferred HOME app.
     * In that case bare {@code ACTION_MAIN + CATEGORY_HOME} opens that launcher
     * instead of a chooser — use RoleManager or Home Settings instead.
     */
    public static boolean hasOtherPreferredHomeApp(Context context) {
        ResolveInfo resolveInfo = resolvePreferredHome(context);
        if (resolveInfo == null || resolveInfo.activityInfo == null) {
            return false;
        }
        String pkg = resolveInfo.activityInfo.packageName;
        String name = resolveInfo.activityInfo.name;
        if (isHomeChooserComponent(pkg, name)) {
            return false;
        }
        return !context.getPackageName().equals(pkg);
    }

    private static ResolveInfo resolvePreferredHome(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            return context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isHomeChooserComponent(String packageName, String className) {
        return isChooserOrResolverComponent(packageName, className);
    }

    /**
     * True when PackageManager would open the system “Select App” / resolver UI.
     */
    public static boolean isChooserOrResolverComponent(@Nullable String packageName,
                                                       @Nullable String className) {
        if (packageName == null) {
            return true;
        }
        if ("android".equals(packageName)
                || "com.android.internal.app".equals(packageName)
                || "com.samsung.android.intentresolver".equals(packageName)
                || "com.android.intentresolver".equals(packageName)
                || "com.hihonor.android.internal.app".equals(packageName)) {
            return true;
        }
        if (className == null) {
            return false;
        }
        return className.contains("ResolverActivity")
                || className.contains("ChooserActivity")
                || className.contains("IntentResolver")
                || className.contains("SemResolver")
                || className.contains("ResolverWrapper");
    }

    /**
     * Preferred (user-selected default) activity for an intent, or null if unset / chooser.
     */
    @Nullable
    public static ResolveInfo resolvePreferredActivity(Context context, @Nullable Intent intent) {
        if (context == null || intent == null) {
            return null;
        }
        try {
            ResolveInfo resolved = context.getPackageManager()
                    .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolved == null || resolved.activityInfo == null) {
                return null;
            }
            String pkg = resolved.activityInfo.packageName;
            String name = resolved.activityInfo.name;
            if (isChooserOrResolverComponent(pkg, name)) {
                return null;
            }
            if (context.getPackageName().equals(pkg)) {
                return null;
            }
            return resolved;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Opens a specific app by package (and optional activity class). Never starts the
     * system app-chooser / resolver.
     */
    public static boolean launchAppDirect(Context context,
                                          @Nullable String packageName,
                                          @Nullable String className) {
        if (context == null || packageName == null || packageName.isEmpty()) {
            return false;
        }
        // Specific launcher activity (app drawer icon) — open that component first.
        if (className != null && !className.isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(new ComponentName(packageName, className));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(intent);
                return true;
            } catch (Exception ignored) {
            }
        }
        // Normal package launch (respects the app’s main entry).
        try {
            Intent launch = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launch != null) {
                // Rebuild as a fully explicit component Intent so OEMs never show a chooser.
                ComponentName component = launch.getComponent();
                Intent start;
                if (component != null) {
                    start = new Intent(Intent.ACTION_MAIN);
                    start.addCategory(Intent.CATEGORY_LAUNCHER);
                    start.setComponent(component);
                } else {
                    start = new Intent(launch);
                    start.setPackage(packageName);
                }
                start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(start);
                return true;
            }
        } catch (Exception ignored) {
        }
        // Last resort: bare component (for handlers without a LAUNCHER activity).
        if (className != null && !className.isEmpty()) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, className));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(intent);
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public static boolean launchAppDirect(Context context, @Nullable String packageName) {
        return launchAppDirect(context, packageName, null);
    }

    /**
     * Opens the user’s preferred/default handler for {@code intent} when one is set.
     * Returns false if no preferred app (would show chooser) so callers can fall back.
     */
    public static boolean launchPreferredHandler(Context context, @Nullable Intent intent) {
        ResolveInfo preferred = resolvePreferredActivity(context, intent);
        if (preferred == null || preferred.activityInfo == null) {
            return false;
        }
        String pkg = preferred.activityInfo.packageName;
        String cls = preferred.activityInfo.name;
        // Start the exact preferred activity (user’s “Always” choice) with this intent.
        try {
            Intent start = new Intent(intent);
            start.setClassName(pkg, cls);
            start.setPackage(pkg);
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(start);
            return true;
        } catch (Exception ignored) {
        }
        // Fall back to opening the app’s main entry.
        return launchAppDirect(context, pkg, null);
    }

    @Nullable
    public static String packageOfPreferredHandler(Context context, @Nullable Intent intent) {
        ResolveInfo preferred = resolvePreferredActivity(context, intent);
        if (preferred == null || preferred.activityInfo == null) {
            return null;
        }
        return preferred.activityInfo.packageName;
    }

    /**
     * Picks one concrete package for {@code ACTION_MAIN + category} without opening
     * the Select App dialog. Prefers the device default when set, then preferred
     * packages, then a system app handler.
     */
    @Nullable
    public static String resolveCategoryPackage(Context context,
                                                String category,
                                                @Nullable String[] preferredPackages) {
        if (context == null || category == null) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(category);

        // 1) User’s already-selected default for this category — highest priority.
        String preferred = packageOfPreferredHandler(context, intent);
        if (preferred != null) {
            return preferred;
        }

        PackageManager pm = context.getPackageManager();
        String selfPackage = context.getPackageName();

        // 2) Known packages (OEM / common).
        if (preferredPackages != null) {
            for (String pkg : preferredPackages) {
                if (pkg == null || selfPackage.equals(pkg)) {
                    continue;
                }
                if (pm.getLaunchIntentForPackage(pkg) != null) {
                    return pkg;
                }
            }
        }

        // 3) Any non-chooser handler; prefer system apps.
        try {
            List<ResolveInfo> matches = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL);
            String fallback = null;
            for (ResolveInfo info : matches) {
                if (info == null || info.activityInfo == null) {
                    continue;
                }
                String pkg = info.activityInfo.packageName;
                String name = info.activityInfo.name;
                if (isChooserOrResolverComponent(pkg, name) || selfPackage.equals(pkg)) {
                    continue;
                }
                boolean system = false;
                try {
                    ApplicationInfo appInfo = info.activityInfo.applicationInfo;
                    system = appInfo != null
                            && (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                } catch (Exception ignored) {
                }
                if (system) {
                    return pkg;
                }
                if (fallback == null) {
                    fallback = pkg;
                }
            }
            return fallback;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Resolve package for an arbitrary intent: user default first, then preferred list,
     * then first non-chooser match.
     */
    @Nullable
    public static String resolveIntentPackage(Context context,
                                              Intent intent,
                                              @Nullable String[] preferredPackages) {
        if (context == null || intent == null) {
            return null;
        }
        String preferred = packageOfPreferredHandler(context, intent);
        if (preferred != null) {
            return preferred;
        }

        PackageManager pm = context.getPackageManager();
        String selfPackage = context.getPackageName();

        if (preferredPackages != null) {
            for (String pkg : preferredPackages) {
                if (pkg == null || selfPackage.equals(pkg)) {
                    continue;
                }
                if (pm.getLaunchIntentForPackage(pkg) != null) {
                    return pkg;
                }
            }
        }

        try {
            List<ResolveInfo> matches = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL);
            String fallback = null;
            for (ResolveInfo info : matches) {
                if (info == null || info.activityInfo == null) {
                    continue;
                }
                String pkg = info.activityInfo.packageName;
                String name = info.activityInfo.name;
                if (isChooserOrResolverComponent(pkg, name) || selfPackage.equals(pkg)) {
                    continue;
                }
                boolean system = false;
                try {
                    ApplicationInfo appInfo = info.activityInfo.applicationInfo;
                    system = appInfo != null
                            && (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                } catch (Exception ignored) {
                }
                if (system) {
                    return pkg;
                }
                if (fallback == null) {
                    fallback = pkg;
                }
            }
            return fallback;
        } catch (Exception e) {
            return null;
        }
    }

    /** Alias for {@link #isDefaultHomeApp(Context)} — used by Home custom dialog flow. */
    public static boolean isAppDefaultLauncher(Context context) {
        return isDefaultHomeApp(context);
    }

    /**
     * Recents (overview) visibility:
     * - Default launcher set → hide app from Recents
     * - Not default launcher → show app in Recents
     */
    public static void applyRecentsVisibility(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            boolean exclude = isDefaultHomeApp(activity);
            ActivityManager am =
                    (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                return;
            }
            List<ActivityManager.AppTask> tasks = am.getAppTasks();
            if (tasks == null) {
                return;
            }
            for (ActivityManager.AppTask task : tasks) {
                if (task != null) {
                    task.setExcludeFromRecents(exclude);
                }
            }
        } catch (Exception ignored) {
        }
    }
}