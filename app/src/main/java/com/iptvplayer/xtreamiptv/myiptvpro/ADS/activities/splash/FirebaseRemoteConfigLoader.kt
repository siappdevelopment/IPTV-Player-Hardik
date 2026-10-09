package com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.splash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSMainClass
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.AdPlacement
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSUtilitis
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ManegeUtilsView
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation.StartupFlowManager
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder

object FirebaseRemoteConfigLoader {

    enum class Source {
        SPLASH,
        LAUNCHER_HOME,
    }

    @JvmStatic
    fun showingToAllData(
        activity: Activity,
        source: Source,
        onNoNetwork: () -> Unit = {},
        onUpdateComingSoon: () -> Unit = {},
        onComplete: () -> Unit = {},
        onHideSplashBottomAdPlaceholders: (() -> Unit)? = null,
    ) {
        if (!ADSUtilitis.IsNetworkConnected(activity)) {
            onNoNetwork()
            return
        }

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.default_config)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    try {
                        applyRemoteConfig(
                            context = activity,
                            remoteConfig = remoteConfig,
                            onHideSplashBottomAdPlaceholders = onHideSplashBottomAdPlaceholders,
                        )

                        onComplete()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onComplete()
                    }
                } else {
                    onComplete()
                }
            }
    }

    private fun resolveVersionKey(context: Context): String {
        ManegeUtilsView.isUtilsManege(context)
        val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        val result = versionName?.replace(".", "_")
        return ManegeUtilsView.ssfsfsfsf + result
    }

    private fun applyRemoteConfig(

        context: Context,
        remoteConfig: FirebaseRemoteConfig,
        onHideSplashBottomAdPlaceholders: (() -> Unit)?,
    ): Boolean {
        val versionKey = resolveVersionKey(context)
        val remoteConfigValue = remoteConfig.getString(versionKey)
        val rootConfigJson: JSONObject = when {
            remoteConfigValue.trim().startsWith("[") ->
                JSONArray(remoteConfigValue).getJSONObject(0)

            else ->
                JSONObject(remoteConfigValue)
        }

        val referrerUrl = ADSMainClass.getReferrerUrl().orEmpty()

        val configJson: JSONObject =
            if (isPaidMarketingReferrer(context, referrerUrl)) {
                rootConfigJson.getJSONObject(GlobalParameterManage.PAID_USER)
            } else {
                rootConfigJson.getJSONObject(GlobalParameterManage.NORMAL_USER)
            }

        val privacy_policy = configJson.getString(GlobalParameterManage.APP_PRIVACY_POLICY)
        val splash_ads_type = configJson.getString(GlobalParameterManage.SPLASH_AD_LOAD_TYPE)
        val ads_click = configJson.getLong(GlobalParameterManage.ADS_CLICK)
        val back_click = configJson.getLong(GlobalParameterManage.BACKS_CLICK)
        val native_by_page = configJson.getLong(GlobalParameterManage.NATIVE_BY_PAGE)
        val ads_blocker = configJson.getBoolean(GlobalParameterManage.ADS_BLOCKS)
        val coming_soon = configJson.getBoolean(GlobalParameterManage.COMING_SOON)
        val exit_ads = configJson.getBoolean(GlobalParameterManage.EXIT_DIALOG)
        val firebaseanalytics = configJson.getBoolean(GlobalParameterManage.FIREBASE_ANALYTICS)
        val update_coming_soon = configJson.getBoolean(GlobalParameterManage.UPDATE_COMING_SOON)
        val defualt_launcher_show = when {
            configJson.has(GlobalParameterManage.DEFAULT_LAUNCHER_SHOW) ->
                configJson.optBoolean(GlobalParameterManage.DEFAULT_LAUNCHER_SHOW, true)
            configJson.has(GlobalParameterManage.DEFUALT_LAUNCHER_SHOW) ->
                configJson.optBoolean(GlobalParameterManage.DEFUALT_LAUNCHER_SHOW, true)
            else -> true
        }
        ADSMainClass.setDefaultLauncherShow(defualt_launcher_show)
        val consent_form_show_screen =
            configJson.getString(GlobalParameterManage.consent_form_show_screen)

        ADSMainClass.setConsentScreenShow(consent_form_show_screen);
        val screen = configJson.getJSONObject(GlobalParameterManage.SCREEN)

        val adsLayout = screen.getJSONObject(GlobalParameterManage.ADS_LAYOUT)
        val lightMode = adsLayout.getJSONObject(GlobalParameterManage.LIGHT_MODE)
        val button_margin = adsLayout.getString(GlobalParameterManage.BUTTON_MARGIN)

        val lightNativeButtonColor =
            lightMode.getString(GlobalParameterManage.NATIVE_ADS_BUTTON_COLOR)

        val lightNativeBackgroundColor =
            lightMode.getString(GlobalParameterManage.NATIVE_AD_BACKGROUND_COLOR)

        val lightNativeAllTextColor =
            lightMode.getString(GlobalParameterManage.NATIVE_AD_ALL_TEXT_COLOR)

        val lightNativeButtonTextColor =
            lightMode.getString(GlobalParameterManage.NATIVE_AD_BUTTON_TEXT_COLOR)

        val darkMode = adsLayout.getJSONObject(GlobalParameterManage.DARK_MODE)

        val darkNativeButtonColor =
            darkMode.getString(GlobalParameterManage.NATIVE_ADS_BUTTON_COLOR)

        val darkNativeBackgroundColor =
            darkMode.getString(GlobalParameterManage.NATIVE_AD_BACKGROUND_COLOR)

        val darkNativeAllTextColor =
            darkMode.getString(GlobalParameterManage.NATIVE_AD_ALL_TEXT_COLOR)

        val darkNativeButtonTextColor =
            darkMode.getString(GlobalParameterManage.NATIVE_AD_BUTTON_TEXT_COLOR)

        ADSMainClass.setButtonMargins(button_margin);
        ADSMainClass.setLightNativeButtonColor(lightNativeButtonColor)
        ADSMainClass.setLightNativeBackgroundColor(lightNativeBackgroundColor)
        ADSMainClass.setLightNativeAllTextColor(lightNativeAllTextColor)
        ADSMainClass.setLightNativeButtonTextColor(lightNativeButtonTextColor)

        ADSMainClass.setDarkNativeButtonColor(darkNativeButtonColor)
        ADSMainClass.setDarkNativeBackgroundColor(darkNativeBackgroundColor)
        ADSMainClass.setDarkNativeAllTextColor(darkNativeAllTextColor)
        ADSMainClass.setDarkNativeButtonTextColor(darkNativeButtonTextColor)

        // Splash Screen
        val splash_screen = screen.getJSONObject(GlobalParameterManage.SPLASH_SCREEN)
        val splah_app_open_ads_show =
            splash_screen.getBoolean(GlobalParameterManage.SPLASH_APP_OPEN_ADS_SHOW)
        val splash_app_open_after_count =
            splash_screen.getInt(GlobalParameterManage.SPLASH_APP_OPEN_AFTER_COUNT)
        val app_open_show_in_background =
            splash_screen.getBoolean(GlobalParameterManage.APP_OPEN_SHOW_IN_BACKGROUND)
        val app_open_daily_show_count =
            splash_screen.getInt(GlobalParameterManage.APP_OPEN_DAILY_SHOW_COUNT)
        val app_open_id = splash_screen.getString(GlobalParameterManage.APP_OPEN_ID)
        val splash_banner_id =
            splash_screen.getString(GlobalParameterManage.SPLASH_BANNER_ID)
        val splash_native_id = splash_screen.getString(GlobalParameterManage.SPLASH_NATIVE_ID)
        val splash_ads_types = splash_screen.getString(GlobalParameterManage.SPLASH_ADS_TYPE)
        val splash_screen_bottom_ad_show =
            splash_screen.getBoolean(GlobalParameterManage.SPLASH_SCREEN_BOTTOM_AD_SHOW)
        ADSMainClass.setSplashAdsType(splash_ads_types)
        ADSMainClass.setSplashAppOpenAfterCount(splash_app_open_after_count)
        ADSMainClass.setAppOpenBackgroundShow(app_open_show_in_background)
        ADSMainClass.setAppOpenAdDailyLimit(app_open_daily_show_count)
        ADSMainClass.setBottomSplashShow(splash_screen_bottom_ad_show)
        if (!splash_screen_bottom_ad_show) {
            onHideSplashBottomAdPlaceholders?.invoke()
        }
        ADSMainClass.setSplashAppOpenShow(splah_app_open_ads_show)

        // Language Screen
        val language_screen = screen.getJSONObject(GlobalParameterManage.LANGUAGE_SCREEN)
        val splash_to_language =
            language_screen.getBoolean(GlobalParameterManage.SPLASH_TO_LANGUAGE)
        val language_screen_bottom_ad_show =
            language_screen.getBoolean(GlobalParameterManage.LANGUAGE_SCREEN_BOTTOM_AD_SHOW)
        val language_ads_type = language_screen.getString(GlobalParameterManage.LANGUAGE_ADS_TYPE)
        val language_inter_ads_show =
            language_screen.getBoolean(GlobalParameterManage.LANGUAGE_INTER_ADS_SHOW)
        val language_banner_id = language_screen.getString(GlobalParameterManage.LANGUAGE_BANNER_ID)
        val language_native_id = language_screen.getString(GlobalParameterManage.LANGUAGE_NATIVE_ID)

        ADSMainClass.setSplashToLanguage(splash_to_language)
        ADSMainClass.setLanguageScreenBottomAdShow(language_screen_bottom_ad_show)
        ADSMainClass.setLanguageAdsType(language_ads_type)
        ADSMainClass.setLanguageInterAdsShow(language_inter_ads_show)

        // Onboarding Intro Screen
        val onboarding_intro = screen.getJSONObject(GlobalParameterManage.ONBOARDING_INTRO)
        val onboarding_count_show =
            onboarding_intro.getInt(GlobalParameterManage.ONBOARDING_COUNT_SHOW)
        val onboarding_ads_type =
            onboarding_intro.getString(GlobalParameterManage.ONBOARDING_ADS_TYPE)
        val onboarding_inter_ads_show =
            onboarding_intro.getBoolean(GlobalParameterManage.ONBOARDING_INTER_ADS_SHOW)
        val onboarding_screen_bottom_ad_show =
            onboarding_intro.getBoolean(GlobalParameterManage.ONBOARDING_SCREEN_BOTTOM_AD_SHOW)

        val onboarding_native_id =
            onboarding_intro.getString(GlobalParameterManage.ONBOARDING_NATIVE_ID)
        val onboarding_banner_id =
            onboarding_intro.getString(GlobalParameterManage.ONBOARDING_BANNER_ID)
        ADSMainClass.setOnboardingAdsType(onboarding_ads_type)
        ADSMainClass.setOnboardingInterAdsShow(onboarding_inter_ads_show)
        ADSMainClass.setOnboardingScreenBottomAdShow(onboarding_screen_bottom_ad_show)
        ADSMainClass.setOnboardingCountShow(onboarding_count_show)

        // Permission Screen
        val permission_screen = screen.getJSONObject(GlobalParameterManage.PERMISSION_SCREEN)
        val permission_small_ads_show =
            permission_screen.optBoolean(GlobalParameterManage.PERMISSION_SMALL_ADS_SHOW, false)
        val permission_ads_type =
            permission_screen.getString(GlobalParameterManage.PERMISSION_ADS_TYPE)
        val permission_native_id =
            permission_screen.getString(GlobalParameterManage.PERMISSION_NATIVE_ID)
        val permission_banner_id =
            permission_screen.getString(GlobalParameterManage.PERMISSION_BANNER_ID)

        ADSMainClass.setPermissionSmallAdsShow(permission_small_ads_show)
        ADSMainClass.setPermissionAdsType(permission_ads_type)

        // Default Permission Screen (Set as Default bottom ads)
        val default_permission_screem =
            screen.optJSONObject(GlobalParameterManage.DEFAULT_PERMISSION_SCREEM)
        val default_permission_bottom_ads_show =
            default_permission_screem?.optBoolean(
                GlobalParameterManage.DEFAULT_PERMISSION_BOTTOM_ADS_SHOW,
                true
            ) ?: true
        val default_permission_ads_type =
            default_permission_screem?.optString(
                GlobalParameterManage.DEFAULT_PERMISSION_ADS_TYPE,
                "banner"
            ) ?: "banner"
        val default_permission_banner_id =
            default_permission_screem?.optString(
                GlobalParameterManage.DEFAULT_PERMISSION_BANNER_ID,
                ""
            ) ?: ""
        val default_permission_native_id =
            default_permission_screem?.optString(
                GlobalParameterManage.DEFAULT_PERMISSION_NATIVE_ID,
                ""
            ) ?: ""
        val default_permission_button_ads_show =
            default_permission_screem?.optBoolean(
                GlobalParameterManage.DEFAULT_PERMISSION_BUTTON_ADS_SHOW,
                false
            ) ?: false
        val default_permission_button_ads_type =
            default_permission_screem?.optString(
                GlobalParameterManage.DEFAULT_PERMISSION_BUTTON_ADS_TYPE,
                "Appopen"
            ) ?: "Appopen"
        ADSMainClass.setDefaultPermissionBottomAdsShow(default_permission_bottom_ads_show)
        ADSMainClass.setDefaultPermissionAdsType(default_permission_ads_type)
        ADSMainClass.setDefaultPermissionButtonAdsShow(default_permission_button_ads_show)
        ADSMainClass.setDefaultPermissionButtonAdsType(default_permission_button_ads_type)

        // Home Screen
        val home_screen = screen.getJSONObject(GlobalParameterManage.HOME_SCREEN)
        val home_screen_bottom_show =
            home_screen.getBoolean(GlobalParameterManage.HOME_SCREEN_BOTTOM_SHOW)
        val home_screen_bottom_ads_refresh =
            home_screen.optBoolean(GlobalParameterManage.HOME_SCREEN_BOTTOM_ADS_REFRESH, false)
        val home_screen_bottom_ads_refresh_time =
            home_screen.optString(GlobalParameterManage.HOME_SCREEN_BOTTOM_ADS_REFRESH_TIME, "30")
                .toIntOrNull()
                ?: home_screen.optInt(GlobalParameterManage.HOME_SCREEN_BOTTOM_ADS_REFRESH_TIME, 30)
        val home_screen_ads_type = home_screen.getString(GlobalParameterManage.HOME_SCREEN_ADS_TYPE)
        val home_banner_id = home_screen.getString(GlobalParameterManage.HOME_BANNER_ID)
        val home_native_id = home_screen.getString(GlobalParameterManage.HOME_NATIVE_ID)
        val home_bottom_nav_inter_show =
            home_screen.optBoolean(GlobalParameterManage.HOME_BOTTOM_NAV_INTER_SHOW, false)
        val home_bottom_count = home_screen.optInt(GlobalParameterManage.HOME_BOTTOM_COUNT, 2)
        val custom_after_hour_show =
            home_screen.optString(GlobalParameterManage.CUSTOM_AFTER_HOUR_SHOW, "2")
                .toIntOrNull()
                ?: home_screen.optInt(GlobalParameterManage.CUSTOM_AFTER_HOUR_SHOW, 2)
        val home_button_ads_show =
            home_screen.optBoolean(GlobalParameterManage.HOME_BUTTON_ADS_SHOW, true)

        ADSMainClass.setHomeScreenBottomShow(home_screen_bottom_show)
        ADSMainClass.setHomeScreenBottomAdsRefresh(home_screen_bottom_ads_refresh)
        ADSMainClass.setHomeScreenBottomAdsRefreshTime(home_screen_bottom_ads_refresh_time)
        ADSMainClass.setHomeScreenAdsType(home_screen_ads_type)
        ADSMainClass.setHomeBottomNavInterShow(home_bottom_nav_inter_show)
        ADSMainClass.setHomeBottomCount(home_bottom_count)
        ADSMainClass.setCustomAfterHourShow(custom_after_hour_show)
        ADSMainClass.setActivityButtonAdsShow(
            ADSMainClass.SCREEN_HOME,
            home_button_ads_show
        )

        // Launcher Home Screen
        val launcher_home_screen = screen.getJSONObject(GlobalParameterManage.LAUNCHER_HOME_SCREEN)
        val swipe_inter_show =
            launcher_home_screen.getBoolean(GlobalParameterManage.SWIPE_INTER_SHOW)
        val swipe_inter_count =
            launcher_home_screen.getInt(GlobalParameterManage.SWIPE_INTER_COUNT)
        val launcher_ads_type =
            launcher_home_screen.getString(GlobalParameterManage.LAUNCHER_ADS_TYPE)
        val launcher_banner_id =
            launcher_home_screen.getString(GlobalParameterManage.LAUNCHER_BANNER_ID)
        val launcher_native_id =
            launcher_home_screen.getString(GlobalParameterManage.LAUNCHER_NATIVE_ID)
        val bottom_drawer_ads_show =
            launcher_home_screen.getBoolean(GlobalParameterManage.BOTTOM_DRAWER_ADS_SHOW)

        ADSMainClass.setLauncherHomeSwipeInterShow(swipe_inter_show)
        ADSMainClass.setLauncherHomeSwipeInterCount(swipe_inter_count)
        ADSMainClass.setLauncherHomeAdsType(launcher_ads_type)
        ADSMainClass.setLauncherBottomDrawerAdsShow(bottom_drawer_ads_show)

        // Recent Launcher Screen
        val recent_launcher_screen =
            screen.optJSONObject(GlobalParameterManage.RECENT_LAUNCHER_SCREEN)
        val recent_bottom_ads_show = recent_launcher_screen?.optBoolean(
            GlobalParameterManage.RECENT_BOTTOM_ADS_SHOW,
            false
        ) ?: false
        val recent_ads_type = recent_launcher_screen?.optString(
            GlobalParameterManage.RECENT_ADS_TYPE,
            "banner"
        ) ?: "banner"
        val recent_banner_id = recent_launcher_screen?.optString(
            GlobalParameterManage.RECENT_BANNER_ID,
            ""
        ) ?: ""
        val recent_native_id = recent_launcher_screen?.optString(
            GlobalParameterManage.RECENT_NATIVE_ID,
            ""
        ) ?: ""
        ADSMainClass.setRecentLauncherBottomAdsShow(recent_bottom_ads_show)
        ADSMainClass.setRecentLauncherAdsType(recent_ads_type)


        // Setting Screen
        val setting_screen = screen.getJSONObject(GlobalParameterManage.SETTING_SCREEN)
        val setting_bottom_ads_show =
            setting_screen.getBoolean(GlobalParameterManage.SETTING_BOTTOM_ADS_SHOW)
        val setting_ads_type = setting_screen.getString(GlobalParameterManage.SETTING_ADS_TYPE)
        val setting_banner_id = setting_screen.getString(GlobalParameterManage.SETTING_BANNER_ID)
        val setting_native_id = setting_screen.getString(GlobalParameterManage.SETTING_NATIVE_ID)
        val setting_button_ads_show = setting_screen.optBoolean(
            GlobalParameterManage.SETTING_BUTTON_ADS_SHOW,
            true
        )
        val setting_back_ads_show = setting_screen.optBoolean(
            GlobalParameterManage.SETTING_BACK_ADS_SHOW,
            true
        )
        ADSMainClass.setSettingBottomAdsShow(setting_bottom_ads_show)
        ADSMainClass.setSettingAdsType(setting_ads_type)
        ADSMainClass.setActivityButtonAdsShow(
            ADSMainClass.SCREEN_SETTING,
            setting_button_ads_show
        )
        ADSMainClass.setActivityBackAdsShow(
            ADSMainClass.SCREEN_SETTING,
            setting_back_ads_show
        )

        // Launcher Setting Screen
        val launcher_setting_screen =
            screen.optJSONObject(GlobalParameterManage.LAUNCHER_SETTING_SCREEN)
        val launcher_setting_back_ads_show = launcher_setting_screen?.optBoolean(
            GlobalParameterManage.LAUNCHER_SETTING_BACK_ADS_SHOW,
            true
        ) ?: true
        val launcher_setting_bottom_ads_show = launcher_setting_screen?.optBoolean(
            GlobalParameterManage.LAUNCHER_SETTING_BOTTOM_ADS_SHOW,
            true
        ) ?: true
        val launcher_setting_ads_type = launcher_setting_screen?.optString(
            GlobalParameterManage.LAUNCHER_SETTING_ADS_TYPE,
            "banner"
        ) ?: "banner"
        val launcher_setting_banner_id = launcher_setting_screen?.optString(
            GlobalParameterManage.LAUNCHER_SETTING_BANNER_ID,
            ""
        ) ?: ""
        val launcher_setting_native_id = launcher_setting_screen?.optString(
            GlobalParameterManage.LAUNCHER_SETTING_NATIVE_ID,
            ""
        ) ?: ""

        ADSMainClass.setLauncherSettingBackAdsShow(launcher_setting_back_ads_show)
        ADSMainClass.setLauncherSettingBottomAdsShow(launcher_setting_bottom_ads_show)
        ADSMainClass.setLauncherSettingAdsType(launcher_setting_ads_type)


        // Call End Screen
        val callend_screen = screen.getJSONObject(GlobalParameterManage.CALLEND_SCREEN)

        val is_callend_show = callend_screen.getBoolean(GlobalParameterManage.IS_CALLEND_SHOW)
        val is_callend_bottom_ad_show =
            callend_screen.getBoolean(GlobalParameterManage.IS_CALLEND_BOTTOM_AD_SHOW)
        val callend_bottom_ads_type =
            callend_screen.getString(GlobalParameterManage.CALLEND_BOTTOM_ADS_TYPE)
        val callend_banner_ad_id =
            callend_screen.getString(GlobalParameterManage.CALLEND_BANNER_AD_ID)
        val callend_native_ad_id =
            callend_screen.getString(GlobalParameterManage.CALLEND_NATIVE_AD_ID)
        val notification_install_days =
            callend_screen.getInt(GlobalParameterManage.NOTIFICATION_INSTALL_DAYS)
        val notification_call_install_days =
            callend_screen.getInt(GlobalParameterManage.NOTIFICATION_CALL_INSTALL_DAYS)
        val notification_call_overlay_install_days =
            callend_screen.getInt(GlobalParameterManage.NOTIFICATION_CALL_OVERLAY_INSTALL_DAYS)
        val country_get_with_ip =
            callend_screen.getBoolean(GlobalParameterManage.COUNTRY_GET_WITH_IP)

        ADSMainClass.setIsShowCallEnd(is_callend_show)
        ADSMainClass.setCallEndBottomAdsShow(is_callend_bottom_ad_show)
        ADSMainClass.setCallEndBottomAdsType(callend_bottom_ads_type)
        ADSMainClass.setNotificationInstallDays(notification_install_days)
        ADSMainClass.setNotificationCallInstallDays(notification_call_install_days)
        ADSMainClass.setNotificationCallOverlayInstallDays(
            notification_call_overlay_install_days
        )
        val all_allow_notif =
            callend_screen.optBoolean(GlobalParameterManage.ALL_ALLOW_PERMISSION_SHOW_FB_NOTIFICATION)
        ADSMainClass.setAllAllowPermissionShowFbNotification(all_allow_notif)
        val close_button_show_on_full_native_ads =
            callend_screen.getBoolean(GlobalParameterManage.CLOSE_BUTTON_SHOW_ON_FULL_NATIVE_ADS)
        ADSMainClass.setCloseButtonShowOnFullNativeAds(
            close_button_show_on_full_native_ads
        )
        ADSMainClass.setCountryGetWithIp(country_get_with_ip)

        val notification_country_list = ArrayList<String>()
        val notification_country_array =
            callend_screen.getJSONArray(GlobalParameterManage.NOTIFICATION_COUNTRY)
        for (i in 0 until notification_country_array.length()) {
            notification_country_list.add(notification_country_array.getString(i))
        }
        ADSMainClass.setNotificationCountries(notification_country_list)

        val notification_call_country_list = ArrayList<String>()
        val notification_call_country_array =
            callend_screen.getJSONArray(GlobalParameterManage.NOTIFICATION_CALL_COUNTRY)
        for (i in 0 until notification_call_country_array.length()) {
            notification_call_country_list.add(
                notification_call_country_array.getString(
                    i
                )
            )
        }
        ADSMainClass.setNotificationCallCountries(notification_call_country_list)

        val notification_call_overlay_country_list = ArrayList<String>()
        val notification_call_overlay_country_array =
            callend_screen.getJSONArray(GlobalParameterManage.NOTIFICATION_CALL_OVERLAY_COUNTRY)
        for (i in 0 until notification_call_overlay_country_array.length()) {
            notification_call_overlay_country_list.add(
                notification_call_overlay_country_array.getString(i)
            )
        }
        ADSMainClass.setNotificationCallOverlayCountries(
            notification_call_overlay_country_list
        )


        val call_end_bacK_inter =
            callend_screen.getJSONObject(GlobalParameterManage.CALL_END_BACK_INTER)
        val call_end_inter_ads_show =
            call_end_bacK_inter.getBoolean(GlobalParameterManage.CALL_END_INTER_ADS_SHOW)
        val notification_screen_back_ads_show =
            call_end_bacK_inter.getBoolean(GlobalParameterManage.NOTIFICATION_SCREEN_BACK_ADS_SHOW)
        val call_end_inter_ads_type =
            call_end_bacK_inter.getString(GlobalParameterManage.CALL_END_INTER_ADS_TYPE)
        val call_end_inter_day_count =
            call_end_bacK_inter.getLong(GlobalParameterManage.CALL_END_INTER_DAY_COUNT)
        val callend_again_open_count =
            call_end_bacK_inter.getLong(GlobalParameterManage.CALLEND_AGAIN_OPEN_COUNT)
        val call_end_inter_active_total_show_count =
            call_end_bacK_inter.getLong(GlobalParameterManage.CALL_END_INTER_ACTIVE_TOTAL_SHOW_COUNT)
        val callend_inter_ad_id =
            call_end_bacK_inter.getString(GlobalParameterManage.CALLEND_INTER_AD_ID)


        val call_end_inter_ads_show_country = ArrayList<String>()
        try {
            val countryArray =
                call_end_bacK_inter.getJSONArray(GlobalParameterManage.CALL_END_INTER_ADS_SHOW_COUNTRY)
            for (i in 0 until countryArray.length()) {
                call_end_inter_ads_show_country.add(countryArray.getString(i))
            }
        } catch (e: Exception) {
        }

        ADSMainClass.setCallEndAdCountries(call_end_inter_ads_show_country)
        ADSMainClass.setCallEndInterAdsShow(call_end_inter_ads_show)
        ADSMainClass.setNotificationScreenBackAdsShow(
            notification_screen_back_ads_show
        )
        ADSMainClass.setCallEndInterAdsType(call_end_inter_ads_type)
        AdPlacement.applyClEndBackConfig(
            if (call_end_bacK_inter.has(GlobalParameterManage.CLEND_BACK_AD_SEQUENCE)) call_end_bacK_inter
            else callend_screen
        )
        ADSMainClass.setCallEndInterDayCount(
            Math.toIntExact(
                call_end_inter_day_count
            )
        )
        ADSMainClass.setCallEndAgainOpenCount(
            Math.toIntExact(
                callend_again_open_count
            )
        )

        ADSMainClass.setCallEndInterShowCount(
            Math.toIntExact(
                call_end_inter_active_total_show_count
            )
        )

        val other_screen = screen.getJSONObject(GlobalParameterManage.OTHER_SCREEN)
        val inter_first_time = other_screen.getString(GlobalParameterManage.INTER_FIRST_TIME)
        val inter_second_time = other_screen.getString(GlobalParameterManage.INTER_SECOND_TIME)
        val other_native_id = other_screen.getString(GlobalParameterManage.OTHER_NATIVE_ID)
        val other_banner_id = other_screen.getString(GlobalParameterManage.OTHER_BANNER_ID)
        // Root inter_ads_load_type controls Splash + all app/onboarding interstitials.
        // other_screen.inter_ads_load_type overrides only when explicitly set.
        val splash_inter_ads_load_type =
            configJson.optString(GlobalParameterManage.INTER_ADS_LOAD_TYPE, "PreLoad").trim()
        val other_screen_inter_load_type =
            other_screen.optString(GlobalParameterManage.OTHER_INTER_ADS_LOAD_TYPE, "").trim()
        val app_inter_ads_load_type =
            if (other_screen_inter_load_type.isNotEmpty()) {
                other_screen_inter_load_type
            } else {
                splash_inter_ads_load_type
            }
        android.util.Log.d(
            "ADS_INTER",
            "Firebase loadType root=[" + splash_inter_ads_load_type
                    + "] other_screen=[" + other_screen_inter_load_type
                    + "] appApplied=[" + app_inter_ads_load_type
                    + "] inter_first_time=[" + inter_first_time + "]"
        )
        val native_ads_load_type =
            configJson.optString(GlobalParameterManage.NATIVE_ADS_LOAD_TYPE, "PreLoad").trim()
        val banner_ads_load_type =
            configJson.optString(GlobalParameterManage.BANNER_ADS_LOAD_TYPE, "PreLoad").trim()
        val appopen_ads_load_type =
            configJson.optString(GlobalParameterManage.APPOPEN_ADS_LOAD_TYPE, "PreLoad").trim()
        val other_bottom_ads_type =
            other_screen.getString(GlobalParameterManage.OTHER_BOTTOM_ADS_TYPE)
        val other_bottom_ads_show =
            other_screen.getBoolean(GlobalParameterManage.OTHER_BOTTOM_ADS_SHOW)
        val inter_ads_show = other_screen.getBoolean(GlobalParameterManage.INTER_ADS_SHOW)
        val inter_ads_show_on_back =
            other_screen.getBoolean(GlobalParameterManage.INTER_ADS_SHOW_ON_BACK)
        val exit_native_id = other_screen.getString(GlobalParameterManage.EXIT_NATIVE_ID)
        ADSMainClass.setInterAdsShow(inter_ads_show)
        ADSMainClass.setSplashInterAdsLoadType(splash_inter_ads_load_type)
        ADSMainClass.setInterAdsLoadType(app_inter_ads_load_type)
        ADSMainClass.setNativeAdsLoadType(native_ads_load_type)
        ADSMainClass.setBannerAdsLoadType(banner_ads_load_type)
        ADSMainClass.setAppOpenAdsLoadType(appopen_ads_load_type)
        ADSMainClass.setInterAdsOnBackShow(inter_ads_show_on_back)
        ADSMainClass.setOtherAdsType(other_bottom_ads_type)
        ADSMainClass.setOtherAdsShow(other_bottom_ads_show)

        parseAdditionalScreenBottomAds(
            screen,
            other_bottom_ads_show,
            other_bottom_ads_type,
            other_banner_id,
            other_native_id
        )

        val update_app = screen.getJSONObject(GlobalParameterManage.UPDATE_APP)
        val in_app_update_show = update_app.getBoolean(GlobalParameterManage.IN_APP_UPDATE_SHOW)
        val in_app_update_type = update_app.getString(GlobalParameterManage.IN_APP_UPDATE_TYPE)
        val in_appp_dailog_daily_show_count =
            update_app.getInt(GlobalParameterManage.IN_APP_DIALOG_DAILY_SHOW_COUNT)

        ADSMainClass.setInAppUpdateShow(in_app_update_show)
        ADSMainClass.setInAppUpdateType(in_app_update_type)
        ADSMainClass.setInApppDialogDailyShowCount(in_appp_dailog_daily_show_count)


        val overlay_notification_show =
            screen.optBoolean(
                GlobalParameterManage.OVERLAY_PERMISSION_NOTIFICATION_SHOW,
                configJson.optBoolean(
                    GlobalParameterManage.OVERLAY_PERMISSION_NOTIFICATION_SHOW,
                    false
                )
            )
        ADSMainClass.setOverlayPermissionNotificationShow(overlay_notification_show)

        val overlay_notification_days =
            screen.optInt(
                GlobalParameterManage.OVERLAY_PERMISSION_NOTIFICATION_DAYS_SHOW_COUNT,
                configJson.optInt(
                    GlobalParameterManage.OVERLAY_PERMISSION_NOTIFICATION_DAYS_SHOW_COUNT,
                    0
                )
            )
        ADSMainClass.setOverlayPermissionNotificationDaysShowCount(
            overlay_notification_days
        )

        ADSMainClass.setStringValue(ADSMainClass.PERMISSION_SCREEN_NATIVE, permission_native_id)
        ADSMainClass.setStringValue(ADSMainClass.PERMISSION_SCREEN_BANNER, permission_banner_id)

        ADSMainClass.setStringValue(ADSMainClass.DEFAULT_PERMISSION_SCREEN_BANNER, default_permission_banner_id)
        ADSMainClass.setStringValue(ADSMainClass.DEFAULT_PERMISSION_SCREEN_NATIVE, default_permission_native_id)

        ADSMainClass.setStringValue(ADSMainClass.splash_banner_id, splash_banner_id)
        ADSMainClass.setStringValue(ADSMainClass.splash_native_id, splash_native_id)

        ADSMainClass.setStringValue(ADSMainClass.onboarding_banner_id, onboarding_banner_id)
        ADSMainClass.setStringValue(ADSMainClass.onboarding_native_id, onboarding_native_id)

        ADSMainClass.setStringValue(ADSMainClass.LANGUAGE_SCREEN_NATIVE, language_native_id)
        ADSMainClass.setStringValue(ADSMainClass.LANGUAGE_SCREEN_BANNER, language_banner_id)

        ADSMainClass.setStringValue(ADSMainClass.HOME_SCREEN_NATIVE, home_native_id)
        ADSMainClass.setStringValue(ADSMainClass.HOME_SCREEN_BANNER, home_banner_id)

        ADSMainClass.setStringValue(ADSMainClass.LAUNCHER_HOME_SCREEN_NATIVE, launcher_native_id)
        ADSMainClass.setStringValue(ADSMainClass.LAUNCHER_HOME_SCREEN_BANNER, launcher_banner_id)

        ADSMainClass.setStringValue(ADSMainClass.RECENT_LAUNCHER_SCREEN_NATIVE, recent_native_id)
        ADSMainClass.setStringValue(ADSMainClass.RECENT_LAUNCHER_SCREEN_BANNER, recent_banner_id)

        ADSMainClass.setStringValue(ADSMainClass.SETTING_SCREEN_NATIVE, setting_native_id)
        ADSMainClass.setStringValue(ADSMainClass.SETTING_SCREEN_BANNER, setting_banner_id)

        ADSMainClass.setStringValue(ADSMainClass.LAUNCHER_SETTING_SCREEN_NATIVE, launcher_setting_native_id)
        ADSMainClass.setStringValue(ADSMainClass.LAUNCHER_SETTING_SCREEN_BANNER, launcher_setting_banner_id)

        ADSMainClass.setStringValue(ADSMainClass.CALL_END_Native, callend_native_ad_id)
        ADSMainClass.setStringValue(ADSMainClass.CALL_END_BANNER, callend_banner_ad_id)
        ADSMainClass.setStringValue(ADSMainClass.CALL_END_Inter, callend_inter_ad_id)

        ADSMainClass.setStringValue(ADSMainClass.APP_OPEN_ID, app_open_id)
        ADSMainClass.setStringValue(ADSMainClass.INTER_FIRST_TIME, inter_first_time)
        ADSMainClass.setStringValue(ADSMainClass.INTER_SECOND_TIME, inter_second_time)
        ADSMainClass.setStringValue(ADSMainClass.OTHER_SCREEN_NATIVE, other_native_id)
        ADSMainClass.setStringValue(ADSMainClass.OTHER_SCREEN_BANNER, other_banner_id)
        ADSMainClass.setStringValue(ADSMainClass.EXIT_SCREEN_NATIVE, exit_native_id)

        ADSMainClass.setExitAds(exit_ads)
        ADSMainClass.setComingSoon(coming_soon)
        ADSMainClass.setPrivacyPolicy(privacy_policy)
        FirebaseAnalytics.getInstance(context)
            .setAnalyticsCollectionEnabled(firebaseanalytics)
        ADSMainClass.setSplashADType(splash_ads_type)
        ADSMainClass.setAds_Free(ads_blocker)

        ADSMainClass.setAdsClick(Math.toIntExact(ads_click))
        ADSMainClass.setAdsBackClick(Math.toIntExact(back_click))
        ADSMainClass.setNativeByPage(Math.toIntExact(native_by_page))

        parseStartupFlowConfig(context, configJson)
        parseQuizAdsConfig(context, configJson)
        return update_coming_soon
    }

    private fun decodeReferrer(referrerUrl: String): String {
        return try {
            URLDecoder.decode(referrerUrl, Charsets.UTF_8.name())
        } catch (_: Exception) {
            referrerUrl
        }
    }

    /**
     * Paid traffic can appear anywhere in the Play install referrer, including
     * percent-encoded values and parameters that are not first in the query.
     */
    private fun isPaidMarketingReferrer(context: Context, referrerUrl: String): Boolean {
        if (referrerUrl.isBlank()) return false
        val decoded = decodeReferrer(referrerUrl).trim()
        if (decoded.isBlank()) return false

        val gclidKey = context.getString(R.string.markrting_converter1)
        val facebook = context.getString(R.string.utm_source_apps_facebook_com)
        val instagram = context.getString(R.string.utm_source_apps_instagram_com)
        val marketing = context.getString(R.string.utm_source_marketing)

        return decoded.contains("$gclidKey=", ignoreCase = true) ||
            decoded.contains(facebook, ignoreCase = true) ||
            decoded.contains(instagram, ignoreCase = true) ||
            decoded.contains(marketing, ignoreCase = true)
    }

    private data class ScreenBottomAdConfig(
        val show: Boolean,
        val type: String,
        val bannerId: String,
        val nativeId: String,
        val buttonAdsShow: Boolean,
        val backAdsShow: Boolean,
    )

    private fun parseScreenBottomAdConfig(
        screen: JSONObject,
        screenKey: String,
        showKey: String,
        typeKey: String,
        bannerKey: String,
        nativeKey: String,
        buttonShowKey: String,
        backShowKey: String,
        fallbackShow: Boolean,
        fallbackType: String,
        fallbackBannerId: String,
        fallbackNativeId: String,
    ): ScreenBottomAdConfig {
        val screenObject = screen.optJSONObject(screenKey)
        return ScreenBottomAdConfig(
            show = screenObject?.optBoolean(showKey, fallbackShow) ?: fallbackShow,
            type = screenObject?.optString(typeKey, fallbackType) ?: fallbackType,
            bannerId = screenObject?.optString(bannerKey, fallbackBannerId) ?: fallbackBannerId,
            nativeId = screenObject?.optString(nativeKey, fallbackNativeId) ?: fallbackNativeId,
            buttonAdsShow = screenObject?.optBoolean(buttonShowKey, true) ?: true,
            backAdsShow = screenObject?.optBoolean(backShowKey, true) ?: true,
        )
    }

    private data class AdditionalScreenBottomAdSpec(
        val screenPrefix: String,
        val screenKey: String,
        val showKey: String,
        val typeKey: String,
        val bannerKey: String,
        val nativeKey: String,
        val buttonShowKey: String,
        val backShowKey: String,
    )

    private fun applyScreenBottomAdConfig(
        config: ScreenBottomAdConfig,
        screenPrefix: String,
    ) {
        ADSMainClass.setActivityBottomAdsShow(screenPrefix, config.show)
        ADSMainClass.setActivityAdsType(screenPrefix, config.type)
        ADSMainClass.setActivityBannerId(screenPrefix, config.bannerId)
        ADSMainClass.setActivityNativeId(screenPrefix, config.nativeId)
        ADSMainClass.setActivityButtonAdsShow(screenPrefix, config.buttonAdsShow)
        ADSMainClass.setActivityBackAdsShow(screenPrefix, config.backAdsShow)
    }

    private fun parseAdditionalScreenBottomAds(
        screen: JSONObject,
        fallbackShow: Boolean,
        fallbackType: String,
        fallbackBannerId: String,
        fallbackNativeId: String,
    ) {
        val additionalScreens = listOf(
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_AGENDA,
                GlobalParameterManage.AGENDA_SCREEN,
                GlobalParameterManage.AGENDA_BOTTOM_ADS_SHOW,
                GlobalParameterManage.AGENDA_ADS_TYPE,
                GlobalParameterManage.AGENDA_BANNER_ID,
                GlobalParameterManage.AGENDA_NATIVE_ID,
                GlobalParameterManage.AGENDA_BUTTON_ADS_SHOW,
                GlobalParameterManage.AGENDA_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_EVENT_ADD,
                GlobalParameterManage.EVENT_ADD_SCREEN,
                GlobalParameterManage.EVENT_ADD_BOTTOM_ADS_SHOW,
                GlobalParameterManage.EVENT_ADD_ADS_TYPE,
                GlobalParameterManage.EVENT_ADD_BANNER_ID,
                GlobalParameterManage.EVENT_ADD_NATIVE_ID,
                GlobalParameterManage.EVENT_ADD_BUTTON_ADS_SHOW,
                GlobalParameterManage.EVENT_ADD_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_EVENT_DETAIL,
                GlobalParameterManage.EVENT_DETAIL_SCREEN,
                GlobalParameterManage.EVENT_DETAIL_BOTTOM_ADS_SHOW,
                GlobalParameterManage.EVENT_DETAIL_ADS_TYPE,
                GlobalParameterManage.EVENT_DETAIL_BANNER_ID,
                GlobalParameterManage.EVENT_DETAIL_NATIVE_ID,
                GlobalParameterManage.EVENT_DETAIL_BUTTON_ADS_SHOW,
                GlobalParameterManage.EVENT_DETAIL_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_ADD_TASK,
                GlobalParameterManage.ADD_TASK_SCREEN,
                GlobalParameterManage.ADD_TASK_BOTTOM_ADS_SHOW,
                GlobalParameterManage.ADD_TASK_ADS_TYPE,
                GlobalParameterManage.ADD_TASK_BANNER_ID,
                GlobalParameterManage.ADD_TASK_NATIVE_ID,
                GlobalParameterManage.ADD_TASK_BUTTON_ADS_SHOW,
                GlobalParameterManage.ADD_TASK_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_TASK_DETAIL,
                GlobalParameterManage.TASK_DETAIL_SCREEN,
                GlobalParameterManage.TASK_DETAIL_BOTTOM_ADS_SHOW,
                GlobalParameterManage.TASK_DETAIL_ADS_TYPE,
                GlobalParameterManage.TASK_DETAIL_BANNER_ID,
                GlobalParameterManage.TASK_DETAIL_NATIVE_ID,
                GlobalParameterManage.TASK_DETAIL_BUTTON_ADS_SHOW,
                GlobalParameterManage.TASK_DETAIL_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_ADD_MEMO,
                GlobalParameterManage.ADD_MEMO_SCREEN,
                GlobalParameterManage.ADD_MEMO_BOTTOM_ADS_SHOW,
                GlobalParameterManage.ADD_MEMO_ADS_TYPE,
                GlobalParameterManage.ADD_MEMO_BANNER_ID,
                GlobalParameterManage.ADD_MEMO_NATIVE_ID,
                GlobalParameterManage.ADD_MEMO_BUTTON_ADS_SHOW,
                GlobalParameterManage.ADD_MEMO_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_MEMO_DETAILS,
                GlobalParameterManage.MEMO_DETAILS_SCREEN,
                GlobalParameterManage.MEMO_DETAILS_BOTTOM_ADS_SHOW,
                GlobalParameterManage.MEMO_DETAILS_ADS_TYPE,
                GlobalParameterManage.MEMO_DETAILS_BANNER_ID,
                GlobalParameterManage.MEMO_DETAILS_NATIVE_ID,
                GlobalParameterManage.MEMO_DETAILS_BUTTON_ADS_SHOW,
                GlobalParameterManage.MEMO_DETAILS_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_CUSTOM_EVENT_LIST,
                GlobalParameterManage.CUSTOM_EVENT_LIST_SCREEN,
                GlobalParameterManage.CUSTOM_EVENT_LIST_BOTTOM_ADS_SHOW,
                GlobalParameterManage.CUSTOM_EVENT_LIST_ADS_TYPE,
                GlobalParameterManage.CUSTOM_EVENT_LIST_BANNER_ID,
                GlobalParameterManage.CUSTOM_EVENT_LIST_NATIVE_ID,
                GlobalParameterManage.CUSTOM_EVENT_LIST_BUTTON_ADS_SHOW,
                GlobalParameterManage.CUSTOM_EVENT_LIST_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_ALARM,
                GlobalParameterManage.ALARM_SCREEN,
                GlobalParameterManage.ALARM_BOTTOM_ADS_SHOW,
                GlobalParameterManage.ALARM_ADS_TYPE,
                GlobalParameterManage.ALARM_BANNER_ID,
                GlobalParameterManage.ALARM_NATIVE_ID,
                GlobalParameterManage.ALARM_BUTTON_ADS_SHOW,
                GlobalParameterManage.ALARM_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_RINGTONE_SELECT,
                GlobalParameterManage.RINGTONE_SELECT_SCREEN,
                GlobalParameterManage.RINGTONE_SELECT_BOTTOM_ADS_SHOW,
                GlobalParameterManage.RINGTONE_SELECT_ADS_TYPE,
                GlobalParameterManage.RINGTONE_SELECT_BANNER_ID,
                GlobalParameterManage.RINGTONE_SELECT_NATIVE_ID,
                GlobalParameterManage.RINGTONE_SELECT_BUTTON_ADS_SHOW,
                GlobalParameterManage.RINGTONE_SELECT_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_THEME,
                GlobalParameterManage.THEME_SCREEN,
                GlobalParameterManage.THEME_BOTTOM_ADS_SHOW,
                GlobalParameterManage.THEME_ADS_TYPE,
                GlobalParameterManage.THEME_BANNER_ID,
                GlobalParameterManage.THEME_NATIVE_ID,
                GlobalParameterManage.THEME_BUTTON_ADS_SHOW,
                GlobalParameterManage.THEME_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_THEME_APPLY,
                GlobalParameterManage.THEME_APPLY_SCREEN,
                GlobalParameterManage.THEME_APPLY_BOTTOM_ADS_SHOW,
                GlobalParameterManage.THEME_APPLY_ADS_TYPE,
                GlobalParameterManage.THEME_APPLY_BANNER_ID,
                GlobalParameterManage.THEME_APPLY_NATIVE_ID,
                GlobalParameterManage.THEME_APPLY_BUTTON_ADS_SHOW,
                GlobalParameterManage.THEME_APPLY_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_WIDGET,
                GlobalParameterManage.WIDGET_SCREEN,
                GlobalParameterManage.WIDGET_BOTTOM_ADS_SHOW,
                GlobalParameterManage.WIDGET_ADS_TYPE,
                GlobalParameterManage.WIDGET_BANNER_ID,
                GlobalParameterManage.WIDGET_NATIVE_ID,
                GlobalParameterManage.WIDGET_BUTTON_ADS_SHOW,
                GlobalParameterManage.WIDGET_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_WIDGET_SETTING,
                GlobalParameterManage.WIDGET_SETTING_SCREEN,
                GlobalParameterManage.WIDGET_SETTING_BOTTOM_ADS_SHOW,
                GlobalParameterManage.WIDGET_SETTING_ADS_TYPE,
                GlobalParameterManage.WIDGET_SETTING_BANNER_ID,
                GlobalParameterManage.WIDGET_SETTING_NATIVE_ID,
                GlobalParameterManage.WIDGET_SETTING_BUTTON_ADS_SHOW,
                GlobalParameterManage.WIDGET_SETTING_BACK_ADS_SHOW,
            ),
            AdditionalScreenBottomAdSpec(
                ADSMainClass.SCREEN_MAP_PICKER,
                GlobalParameterManage.MAP_PICKER_SCREEN,
                GlobalParameterManage.MAP_PICKER_BOTTOM_ADS_SHOW,
                GlobalParameterManage.MAP_PICKER_ADS_TYPE,
                GlobalParameterManage.MAP_PICKER_BANNER_ID,
                GlobalParameterManage.MAP_PICKER_NATIVE_ID,
                GlobalParameterManage.MAP_PICKER_BUTTON_ADS_SHOW,
                GlobalParameterManage.MAP_PICKER_BACK_ADS_SHOW,
            ),
        )

        for (spec in additionalScreens) {
            applyScreenBottomAdConfig(
                parseScreenBottomAdConfig(
                    screen,
                    spec.screenKey,
                    spec.showKey,
                    spec.typeKey,
                    spec.bannerKey,
                    spec.nativeKey,
                    spec.buttonShowKey,
                    spec.backShowKey,
                    fallbackShow,
                    fallbackType,
                    fallbackBannerId,
                    fallbackNativeId,
                ),
                spec.screenPrefix,
            )
        }
    }

    private fun parseQuizAdsConfig(context: Context, configJson: JSONObject) {
        try {
            if (configJson.has(GlobalParameterManage.AD_PRIORITY)
                || configJson.has(GlobalParameterManage.GOOGLE_AD_FAILED_SHOW_QUIZ)
            ) {
                ADSMainClass.applyQuizAdsConfig(configJson)
            }
            if (configJson.has(GlobalParameterManage.LAUNCHER_APP_CLICK_AD_SHOW)
                || configJson.has(GlobalParameterManage.LAUNCHER_APP_COUNT)
                || configJson.has(GlobalParameterManage.LAUNCHER_APP_AD_TYPE)
                || configJson.has(GlobalParameterManage.LAUNCHER_APP_INTERSTITIAL_ID)
                || configJson.has(GlobalParameterManage.LAUNCHER_APP_FULLSCREEN_NATIVE_ID)
                || configJson.has(GlobalParameterManage.LAUNCHER_APP_BACK_CLICK_AD_SHOW)
                || configJson.has(GlobalParameterManage.LAUNCHER_APP_BACK_COUNT)
                || configJson.has(GlobalParameterManage.LAUNCHER_APP_BACK_AD_TYPE)
                || configJson.has(GlobalParameterManage.LAUNCHER_APP_BACK_INTERSTITIAL_ID)
                || configJson.has(GlobalParameterManage.LAUNCHER_APP_BACK_FULLSCREEN_NATIVE_ID)
                || configJson.has(GlobalParameterManage.IS_LINK_OPEN_APP)
            ) {
                ADSMainClass.applyLauncherAppConfig(configJson)
            }
            val launcherAppScreen =
                configJson.optJSONObject(GlobalParameterManage.SCREEN)
                    ?.optJSONObject(GlobalParameterManage.LAUNCHER_APP_SCREEN)
                    ?: configJson.optJSONObject(GlobalParameterManage.LAUNCHER_APP_SCREEN)
            if (launcherAppScreen != null) {
                ADSMainClass.applyLauncherAppConfig(launcherAppScreen)
            }
            val quizAdsDesign = configJson.optJSONObject(GlobalParameterManage.QUIZ_ADS_DESIGN)
            if (quizAdsDesign != null) {
                // Merge root priority flags into design object when present on root
                if (configJson.has(GlobalParameterManage.AD_PRIORITY)) {
                    quizAdsDesign.put(
                        GlobalParameterManage.AD_PRIORITY,
                        configJson.optString(GlobalParameterManage.AD_PRIORITY, "")
                    )
                }
                if (configJson.has(GlobalParameterManage.GOOGLE_AD_FAILED_SHOW_QUIZ)) {
                    quizAdsDesign.put(
                        GlobalParameterManage.GOOGLE_AD_FAILED_SHOW_QUIZ,
                        configJson.optBoolean(
                            GlobalParameterManage.GOOGLE_AD_FAILED_SHOW_QUIZ,
                            false
                        )
                    )
                }
                ADSMainClass.applyQuizAdsConfig(quizAdsDesign)
            }
            val appProxy = configJson.optJSONObject(GlobalParameterManage.APP_PROXY_STRUCTURE)
            if (appProxy != null) {
                ADSMainClass.applyAppProxyConfig(appProxy)
            }
        } catch (e: Exception) {
            Log.e("QuizAds", "parseQuizAdsConfig: ${e.message}")
        }
    }

    private fun parseStartupFlowConfig(context: Context, configJson: JSONObject) {
        val flowSteps = mutableListOf<String>()
        val flowArray = configJson.optJSONArray(GlobalParameterManage.SHOW_INTRO_SCREEN_FLOW)
        if (flowArray != null) {
            for (i in 0 until flowArray.length()) {
                flowSteps.add(flowArray.getString(i))
            }
        }
        StartupFlowManager.saveFlow(context, flowSteps)
    }
}

