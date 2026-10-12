package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppControlPreference {

    private static final String reso__Ads_Free = "reso__Ads_Free";
    private static final String reso__Admob_Fuulscreen_Ads_Unit_Id = "reso__Admob_Fuulscreen_Ads_Unit_Id";
    private static final String reso__Admob_Native_Unit_Id = "reso__Admob_Native_Unit_Id";
    private static final String reso__Second_Native_Unit_Id = "reso__Second_Native_Unit_Id";
    private static final String reso__Admob_Banner_Ads_Id = "reso__Admob_Banner_Ads_Id";
    private static final String reso__Key_App_Starting_ADS = "reso__Key_App_Starting_ADS";
    private static final String reso__Native_By_Page = "reso__Native_By_Page";
    private static final String reso__Ads_Display_Type = "reso__Ads_Display_Type";
    private static final String reso__Small_Ads_Type = "reso__Small_Ads_Type";
    private static final String reso__Native_Button_Color = "reso__Native_Button_Color";
    private static final String reso__Native_Button_Text_Color = "reso__Native_Button_Text_Color";
    private static final String reso__Native_BG_Color = "reso__Native_BG_Color";
    private static final String reso__Ads_Click = "reso__Ads_Click";
    private static final String Ad_After_Row = "Ad_After_Row";
    private static final String Quiz_Ads_Click = "Quiz_Ads_Click";
    private static final String App_Open_Ads_Click = "App_Open_Ads_Click";
    private static final String Interstitial_Ads_Click = "Interstitial_Ads_Click";
    private static final String Native_Ads_Click = "Native_Ads_Click";
    private static final String Quiz_Ads_Back_Click = "Quiz_Ads_Back_Click";
    private static final String App_Open_Ads_Back_Click = "App_Open_Ads_Back_Click";
    private static final String Interstitial_Ads_Back_Click = "Interstitial_Ads_Back_Click";
    private static final String Native_Ads_Back_Click = "Native_Ads_Back_Click";
    private static final String reso__Ads_Back_Click = "reso__Ads_Back_Click";
    private static final String Splash_App_Open_Ads_Show = "Splash_App_Open_Ads_Show";
    private static final String reso__Ads_One_By_One_Ids = "reso__Ads_One_By_One_Ids";
    private static final String reso__Ads_Type = "reso__Ads_Type";
    private static final String reso__Splash_Ads_Type = "reso__Splash_Ads_Type";
    private static final String reso__App_Exit_Dialog = "reso__App_Exit_Dialog";
    private static final String reso__Privacy_Policy = "reso__Privacy_Policy";
    private static final String reso__Coming_Soon = "reso__Coming_Soon";
    private static final String reso__Intro_Screen_Display = "reso__Intro_Screen_Display";
    private static final String reso__Is_Quiz_Display = "reso__Is_Quiz_Display";
    private static final String reso__Quiz_Close_Ads_Display = "reso__Quiz_Close_Ads_Display";
    private static final String reso__Is_Quiz_Native = "reso__Is_Quiz_Native";
    private static final String reso__Is_Quiz_Banner = "reso__Is_Quiz_Banner";
    private static final String reso__Splash_Quiz_Display = "reso__Splash_Quiz_Display";
    private static final String reso__Is_Quiz_Web_Interstitial_Display = "reso__Is_Quiz_Web_Interstitial_Display";
    private static final String reso__is_Quiz_App_Open_Display = "reso__is_Quiz_App_Open_Display";
    private static final String reso__Is_Quiz_Fullscreen_Ads_Display = "reso__Is_Quiz_Fullscreen_Ads_Display";
    private static final String reso__Is_Quiz_Header_Display = "reso__Is_Quiz_Header_Display";
    private static final String str_VGR = "str_VGR";
    private static final String str_position_event = "str_position_event";
    private static final String str_swipe_bottom = "str_swipe_bottom";
    private static final String str_default_permission = "str_default_permission";
    private static final String str_app_quiz_direct_play = "str_app_quiz_direct_play";
    private static final String str_app_click_ads_type = "str_app_click_ads_type";
    private static final String str_alternate_ads = "str_alternate_ads";
    private static final String str_AlternativeList = "str_AlternativeList";
    private static final String MultiTypesAdsShow = "MultiTypesAdsShow";
    private static final String AppMultiTypesAdsShow = "AppMultiTypesAdsShow";
    private static final String AdultImagelist = "AdultImagelist";
    private static final String KEY_AD_INDEX = "key_ad_index";
    private static final String is_IsAlternative_ads = "is_IsAlternative_ads";
    private static final String is_IsRandom_ads = "is_IsRandom_ads";
    private static final String is_both_ad_priority = "is_both_ad_priority";
    private static final String is_both_ad_random = "is_both_ad_random";
    private static final String is_intro_launcher = "is_intro_launcher";
    private static final String Native_Load_All_Time = "Native_Load_All_Time";
    private static final String Default_Cancel_To_Next_Pass = "Default_Cancel_To_Next_Pass";
    private static final String DefaultPermissionCancel = "DefaultPermissionCancel";
    public static String reso__Admob_Interstitial_Ads_List = "reso__Admob_Interstitial_Ads_List";
    public static String reso__Admob_Native_Full_Ads_List = "reso__Admob_Native_Full_Ads_List";
    public static String reso__Admob_Native_Custom_Ids_List = "reso__Admob_Native_Custom_Ids_List";
    public static String reso__Admob_Banner_Ids_List = "reso__Admob_Banner_Ids_List";
    public static String reso__Admob_App_Starting_Ids_List = "reso__Admob_App_Starting_Ids_List";
    public static String reso__Quiz_Banner_Description_List = "reso__Quiz_Banner_Description_List";
    public static String reso__Quiz_Banner_Title_List = "reso__Quiz_Banner_Title_List";
    public static String reso__Quiz_Interstitial_Description_List = "reso__Quiz_Interstitial_Description_List";
    public static String reso__Quiz_Interstitial_Title_List = "reso__Quiz_Interstitial_Title_List";
    public static String reso__Quiz_Native_Description_List = "reso__Quiz_Native_Description_List";
    public static String reso__Quiz_Native_Title_List = "reso__Quiz_Native_Title_List";
    public static String reso__Privacy_Policy_Show_One_Time = "reso__Privacy_Policy_Show_One_Time";
    public static String reso__quiz_ads_button = "reso__quiz_ads_button";
    public static String reso__Open_Link_in_App = "reso__Open_Link_in_App";
    public static String reso__one_singnal_string = "reso__one_singnal_string";
    public static String reso__quiz_link_list = "reso__quiz_link_list";
    public static String reso__singlequizurl = "reso__singlequizurl";
    public static String reso__citylist = "reso__citylist";
    public static String reso__statelist = "reso__statelist";
    public static String reso__countrylist = "reso__countrylist";
    public static String reso__Quiz_link_list_exclude = "reso__Quiz_link_list_exclude";
    public static String reso__QuizLinkNormalEvent = "reso__QuizLinkNormalEvent";
    public static String reso__QuizLinkExcludeEvent = "reso__QuizLinkExcludeEvent";
    public static String reso__CheckExclude = "reso__CheckExclude";
    public static String reso__IPChecker = "reso__IPChecker";
    public static String reso__Ip_Checker_baseUrl = "reso__Ip_Checker_baseUrl";
    public static String reso__AudienceCategory = "reso__Interest_AudienceCategory";
    public static String reso__ContentCategory = "reso__ContentCategory";
    public static String reso__CountryInterestList = "reso__CountryInterestList";
    public static String is_quiz_Home = "is_quiz_Home";
    public static String is_quiz_Both_Priority = "is_quiz_Both_Priority";
    public static String launcherEvent = "launcherEvent";
    public static String DynamicFlagCount = "DynamicFlag";
    public static String InsideHomeCount = "InsideHomeCount";
    public static String InsideHomeCounter = "InsideHomeCounter";
    public static String reso__QuizBannerShow_List = "reso__QuizBannerShow_List";
    public static String is_adults_QuizBannerShow = "is_adults_QuizBannerShow";
    public static String is_intro_adults_image = "is_intro_adults_image";
    public static String is_introacreen = "is_introacreen";
    static String Referral_New_URL = "Referral_New_URL";
    private static String quiz_Inter_Priority_count = "quiz_Inter_Priority_count";
    private static String is_quiz_Inter_Priority = "is_quiz_Inter_Priority";
    private static String is_multi_types_ads_Priority = "is_multi_types_ads_Priority";
    private static String is_app_multi_types_ads_Priority = "is_app_multi_types_ads_Priority";
    private static String is_HomePriorityOneAds = "is_HomePriorityOneAds";

    public static boolean getDefaultPermissionCancel() {
        return manegePrefUtils().getBoolean(DefaultPermissionCancel, false);
    }

    public static void setDefaultPermissionCancel(boolean value) {
        manegePrefUtils().edit().putBoolean(DefaultPermissionCancel, value).apply();
    }

    public static boolean getNative_Load_All_Time() {
        return manegePrefUtils().getBoolean(Native_Load_All_Time, false);
    }

    public static void setNative_Load_All_Time(boolean value) {
        manegePrefUtils().edit().putBoolean(Native_Load_All_Time, value).apply();
    }

    public static boolean getDefault_Cancel_To_Next_Pass() {
        return manegePrefUtils().getBoolean(Default_Cancel_To_Next_Pass, false);
    }

    public static void setDefault_Cancel_To_Next_Pass(boolean value) {
        manegePrefUtils().edit().putBoolean(Default_Cancel_To_Next_Pass, value).apply();
    }

    public static boolean getHomePriorityOneAds() {
        return manegePrefUtils().getBoolean(is_HomePriorityOneAds, false);
    }

    public static void setHomePriorityOneAds(boolean value) {
        manegePrefUtils().edit().putBoolean(is_HomePriorityOneAds, value).apply();
    }

    public static boolean getIntroCounts() {
        return manegePrefUtils().getBoolean(is_introacreen, false);
    }

    public static void setIntroCounts(boolean value) {
        manegePrefUtils().edit().putBoolean(is_introacreen, value).apply();
    }

    public static boolean getIntroQuiz() {
        return manegePrefUtils().getBoolean(is_introacreen, false);
    }

    public static void setIntroQuiz(boolean value) {
        manegePrefUtils().edit().putBoolean(is_introacreen, value).apply();
    }

    public static boolean getIs_adults_QuizBannerShow() {
        return manegePrefUtils().getBoolean(is_adults_QuizBannerShow, false);
    }

    public static void setIs_adults_QuizBannerShow(boolean value) {
        manegePrefUtils().edit().putBoolean(is_adults_QuizBannerShow, value).apply();
    }

    public static boolean getis_intro_adults_image() {
        return manegePrefUtils().getBoolean(is_intro_adults_image, false);
    }

    public static void setis_intro_adults_image(boolean value) {
        manegePrefUtils().edit().putBoolean(is_intro_adults_image, value).apply();
    }

    public static List<String> getQuizBannerShow() {
        String Json = manegePrefUtils().getString(reso__QuizBannerShow_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setQuizBannerShow(List<String> admobBannerIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobBannerIdList);
        manegePrefUtils().edit().putString(reso__QuizBannerShow_List, json).apply();
    }

    public static void setis_quiz_Home(boolean value) {
        manegePrefUtils().edit().putBoolean(is_quiz_Home, value).apply();
    }

    public static boolean getis_quiz_Home() {
        return manegePrefUtils().getBoolean(is_quiz_Home, false);
    }

    public static void setis_quiz_Both_Priority(boolean value) {
        manegePrefUtils().edit().putBoolean(is_quiz_Both_Priority, value).apply();
    }

    public static boolean getis_quiz_Both_Priority() {
        return manegePrefUtils().getBoolean(is_quiz_Both_Priority, false);
    }

    public static Integer getquiz_Inter_Priority_count() {
        return manegePrefUtils().getInt(quiz_Inter_Priority_count, 0);
    }

    public static void setquiz_Inter_Priority_count(Integer value) {
        manegePrefUtils().edit().putInt(quiz_Inter_Priority_count, value).apply();
    }

    public static void setis_quiz_Inter_Priority(boolean value) {
        manegePrefUtils().edit().putBoolean(is_quiz_Inter_Priority, value).apply();
    }

    public static boolean getis_quiz_Inter_Priority() {
        return manegePrefUtils().getBoolean(is_quiz_Inter_Priority, false);
    }

    public static void setis_multi_types_ads_Priority(boolean value) {
        manegePrefUtils().edit().putBoolean(is_multi_types_ads_Priority, value).apply();
    }

    public static boolean getis_multi_types_ads_Priority() {
        return manegePrefUtils().getBoolean(is_multi_types_ads_Priority, false);
    }

    public static void setis_app_multi_types_ads_Priority(boolean value) {
        manegePrefUtils().edit().putBoolean(is_app_multi_types_ads_Priority, value).apply();
    }

    public static boolean getis_app_multi_types_ads_Priority() {
        return manegePrefUtils().getBoolean(is_app_multi_types_ads_Priority, false);
    }

    public static boolean getInsideHomeCount() {
        return manegePrefUtils().getBoolean(InsideHomeCount, true);
    }

    public static void setInsideHomeCount(Boolean index) {
        manegePrefUtils().edit().putBoolean(InsideHomeCount, index).apply();
    }

    public static int getInsideHomeCounter() {
        return manegePrefUtils().getInt(InsideHomeCounter, 0);
    }

    public static void setInsideHomeCounter(int index) {
        manegePrefUtils().edit().putInt(InsideHomeCounter, index).apply();
    }

    public static boolean getIntroLauncher() {
        return manegePrefUtils().getBoolean(is_intro_launcher, false);
    }

    public static void setIntroLauncher(Boolean index) {
        manegePrefUtils().edit().putBoolean(is_intro_launcher, index).apply();
    }

    public static boolean getDynamicFlag(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(DynamicFlagCount, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static void setDynamicFlag(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(DynamicFlagCount, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }

    public static int getLauncherEvent() {
        return manegePrefUtils().getInt(launcherEvent, 0);
    }

    public static void setLauncherEvent(int value) {
        manegePrefUtils().edit().putInt(launcherEvent, value).apply();
    }

    public static boolean getRetentionDay(String key) {
        return manegePrefUtils().getBoolean(key, false);
    }

    public static void setRetentionDay(String key, boolean value) {
        manegePrefUtils().edit().putBoolean(key, value).apply();
    }

    public static String getVGR() {
        return manegePrefUtils().getString(str_VGR, "");
    }

    public static void setVGR(String index) {
        manegePrefUtils().edit().putString(str_VGR, index).apply();
    }

    public static boolean getIsBothAdRandomPriority() {
        return manegePrefUtils().getBoolean(is_both_ad_random, false);
    }

    public static void setIsBothAdRandomPriority(Boolean index) {
        manegePrefUtils().edit().putBoolean(is_both_ad_random, index).apply();
    }

    public static boolean getIsBothAdPriority() {
        return manegePrefUtils().getBoolean(is_both_ad_priority, false);
    }

    public static void setIsBothAdPriority(Boolean index) {
        manegePrefUtils().edit().putBoolean(is_both_ad_priority, index).apply();
    }

    public static boolean getIsAlternativeAds() {
        return manegePrefUtils().getBoolean(is_IsAlternative_ads, false);
    }

    public static void setIsAlternativeAds(Boolean index) {
        manegePrefUtils().edit().putBoolean(is_IsAlternative_ads, index).apply();
    }

    public static boolean getIsRandomAds() {
        return manegePrefUtils().getBoolean(is_IsRandom_ads, false);
    }

    public static void setIsRandomAds(Boolean index) {
        manegePrefUtils().edit().putBoolean(is_IsRandom_ads, index).apply();
    }

    public static int getAdIndex() {
        return manegePrefUtils().getInt(KEY_AD_INDEX, 0);
    }

    public static void setAdIndex(int index) {
        manegePrefUtils().edit().putInt(KEY_AD_INDEX, index).apply();
    }

    public static List<String> getAlternativeList() {
        String json = manegePrefUtils().getString(str_AlternativeList, "[]");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setAlternativeList(List<String> countryInterestList) {
        String json = new Gson().toJson(countryInterestList);
        manegePrefUtils().edit().putString(str_AlternativeList, json).apply();
    }

    public static List<String> getMultiTypesAdsShow() {
        String json = manegePrefUtils().getString(MultiTypesAdsShow, "[]");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setMultiTypesAdsShow(List<String> countryInterestList) {
        String json = new Gson().toJson(countryInterestList);
        manegePrefUtils().edit().putString(MultiTypesAdsShow, json).apply();
    }

    public static List<String> getAppMultiTypesAdsShow() {
        String json = manegePrefUtils().getString(AppMultiTypesAdsShow, "[]");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setAppMultiTypesAdsShow(List<String> countryInterestList) {
        String json = new Gson().toJson(countryInterestList);
        manegePrefUtils().edit().putString(AppMultiTypesAdsShow, json).apply();
    }

    public static List<String> getAdultImagelist() {
        String json = manegePrefUtils().getString(AdultImagelist, "[]");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setAdultImagelist(List<String> countryInterestList) {
        String json = new Gson().toJson(countryInterestList);
        manegePrefUtils().edit().putString(AdultImagelist, json).apply();
    }


    public static String getAppClickAdsType() {
        return manegePrefUtils().getString(str_app_click_ads_type, "");
    }

    public static void setAppClickAdsType(String value) {
        manegePrefUtils().edit().putString(str_app_click_ads_type, value).apply();
    }

    public static boolean getQuizDirectPlay() {
        return manegePrefUtils().getBoolean(str_app_quiz_direct_play, false);
    }

    public static void setQuizDirectPlay(boolean value) {
        manegePrefUtils().edit().putBoolean(str_app_quiz_direct_play, value).apply();
    }

    public static int getreso__Alternet_Ads() {
        return manegePrefUtils().getInt(str_alternate_ads, 1);
    }

    public static void setreso__Alternet_Ads(int value) {
        manegePrefUtils().edit().putInt(str_alternate_ads, value).apply();
    }

    public static boolean getDefaultPermission() {
        return manegePrefUtils().getBoolean(str_default_permission, false);
    }

    public static void setDefaultPermission(boolean value) {
        manegePrefUtils().edit().putBoolean(str_default_permission, value).apply();
    }

    public static boolean getBottomEvent() {
        return manegePrefUtils().getBoolean(str_swipe_bottom, false);
    }

    public static void setBottomEvent(boolean value) {
        manegePrefUtils().edit().putBoolean(str_swipe_bottom, value).apply();
    }

    public static boolean getPositionEvent() {
        return manegePrefUtils().getBoolean(str_position_event, false);
    }

    public static void setPositionEvent(boolean value) {
        manegePrefUtils().edit().putBoolean(str_position_event, value).apply();
    }

    public static List<String> getCountryInterestList() {
        String json = manegePrefUtils().getString(reso__CountryInterestList, "[]");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setCountryInterestList(List<String> countryInterestList) {
        String json = new Gson().toJson(countryInterestList);
        manegePrefUtils().edit().putString(reso__CountryInterestList, json).apply();
    }

    public static List<Integer> getContentCategory() {
        String json = manegePrefUtils().getString(reso__ContentCategory, "[]");
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setContentCategory(List<Integer> cityList) {
        String json = new Gson().toJson(cityList);
        manegePrefUtils().edit().putString(reso__ContentCategory, json).apply();
    }

    public static List<Integer> getAudienceCategory() {
        String json = manegePrefUtils().getString(reso__AudienceCategory, "[]");
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setAudienceCategory(List<Integer> cityList) {
        String json = new Gson().toJson(cityList);
        manegePrefUtils().edit().putString(reso__AudienceCategory, json).apply();
    }

    public static String getIp_Checker_baseUrl() {
        return manegePrefUtils().getString(reso__Ip_Checker_baseUrl, "");
    }

    public static void setIp_Checker_baseUrl(String value) {
        manegePrefUtils().edit().putString(reso__Ip_Checker_baseUrl, value).apply();
    }

    public static boolean getIPChecker() {
        return manegePrefUtils().getBoolean(reso__IPChecker, false);
    }

    public static void setIPChecker(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__IPChecker, value).apply();
    }

    public static boolean getCheckExclude() {
        return manegePrefUtils().getBoolean(reso__CheckExclude, false);
    }

    public static void setCheckExclude(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__CheckExclude, value).apply();
    }

    public static Integer getQuizLinkClickEvent() {
        return manegePrefUtils().getInt(reso__QuizLinkNormalEvent, 0);
    }

    public static void setQuizLinkClickEvent(Integer value) {
        manegePrefUtils().edit().putInt(reso__QuizLinkNormalEvent, value).apply();
    }

    public static Integer getQuizLinkExcludeClickEvent() {
        return manegePrefUtils().getInt(reso__QuizLinkExcludeEvent, 0);
    }

    public static void setQuizLinkExcludeClickEvent(Integer value) {
        manegePrefUtils().edit().putInt(reso__QuizLinkExcludeEvent, value).apply();
    }

    public static List<String> getQuizLinkListExclude() {
        String Json = manegePrefUtils().getString(reso__Quiz_link_list_exclude, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> stringArrayList = gson.fromJson(Json, type);
        return stringArrayList;
    }

    public static void setQuizLinkListExclude(List<String> cityList) {
        Gson gson = new Gson();
        String json = gson.toJson(cityList);
        manegePrefUtils().edit().putString(reso__Quiz_link_list_exclude, json).apply();
    }

    public static List<String> getCityList() {
        String Json = manegePrefUtils().getString(reso__citylist, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> stringArrayList = gson.fromJson(Json, type);
        return stringArrayList;
    }

    public static void setCityList(List<String> cityList) {
        Gson gson = new Gson();
        String json = gson.toJson(cityList);
        manegePrefUtils().edit().putString(reso__citylist, json).apply();
    }

    public static List<String> getStateList() {
        String Json = manegePrefUtils().getString(reso__statelist, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> stringArrayList = gson.fromJson(Json, type);
        return stringArrayList;
    }

    public static void setStateList(List<String> stateList) {
        Gson gson = new Gson();
        String json = gson.toJson(stateList);
        manegePrefUtils().edit().putString(reso__statelist, json).apply();
    }

    public static List<String> getCountryList() {
        String Json = manegePrefUtils().getString(reso__countrylist, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> stringArrayList = gson.fromJson(Json, type);
        return stringArrayList;
    }

    public static void setCountryList(List<String> countryList) {
        Gson gson = new Gson();
        String json = gson.toJson(countryList);
        manegePrefUtils().edit().putString(reso__countrylist, json).apply();
    }

    public static String getSingleQuizUrl() {
        return manegePrefUtils().getString(reso__singlequizurl, "");
    }

    public static void setOneSignalString(String value) {
        manegePrefUtils().edit().putString(reso__one_singnal_string, value).apply();
    }

    public static boolean getOpen_Link_in_App() {
        return manegePrefUtils().getBoolean(reso__Open_Link_in_App, false);
    }

    public static void setOpen_Link_in_App(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Open_Link_in_App, value).apply();
    }

    public static boolean getQuizAdsButton() {
        return manegePrefUtils().getBoolean(reso__quiz_ads_button, false);
    }

    public static void setQuizAdsButton(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__quiz_ads_button, value).apply();
    }

    public static List<String> getQuizLinkList() {
        String Json = manegePrefUtils().getString(reso__quiz_link_list, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> stringArrayList = gson.fromJson(Json, type);
        return stringArrayList;
    }

    public static void setQuizLinkList(List<String> quizLinkList) {
        Gson gson = new Gson();
        String json = gson.toJson(quizLinkList);
        manegePrefUtils().edit().putString(reso__quiz_link_list, json).apply();
    }

    public static String getreso__Quiz_Link() {
        if (AppControlPreference.getCheckExclude()) {

            int random = new Random().nextInt(AppControlPreference.getQuizLinkListExclude().size());
            String stringMainLink = AppControlPreference.getQuizLinkListExclude().get(random);
            return stringMainLink;
        } else {
            int random = new Random().nextInt(AppControlPreference.getQuizLinkList().size());
            String stringMainLink = AppControlPreference.getQuizLinkList().get(random);
            return stringMainLink;
        }
    }

    public static void iukeb__setReferrerUrl(String value) {
        manegePrefUtils().edit().putString(Referral_New_URL, value).apply();
    }

    public static String iukeb__getReferrerUrl() {
        return manegePrefUtils().getString(Referral_New_URL, "");
    }

    public static boolean getreso__Privacy_Policy_Show_One_Time() {
        return manegePrefUtils().getBoolean(reso__Privacy_Policy_Show_One_Time, false);
    }

    public static void setreso__Privacy_Policy_Show_One_Time(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Privacy_Policy_Show_One_Time, value).apply();
    }

    public static List<String> getreso__Quiz_Banner_Description_List() {
        String Json = manegePrefUtils().getString(reso__Quiz_Banner_Description_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setreso__Quiz_Banner_Description_List(List<String> admobBannerIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobBannerIdList);
        manegePrefUtils().edit().putString(reso__Quiz_Banner_Description_List, json).apply();
    }


    public static List<String> getreso__Quiz_Banner_Title_List() {
        String Json = manegePrefUtils().getString(reso__Quiz_Banner_Title_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setreso__Quiz_Banner_Title_List(List<String> admobBannerIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobBannerIdList);
        manegePrefUtils().edit().putString(reso__Quiz_Banner_Title_List, json).apply();
    }

    public static List<String> getreso__Quiz_Interstitial_Description_List() {
        String Json = manegePrefUtils().getString(reso__Quiz_Interstitial_Description_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setreso__Quiz_Interstitial_Description_List(List<String> admobBannerIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobBannerIdList);
        manegePrefUtils().edit().putString(reso__Quiz_Interstitial_Description_List, json).apply();
    }

    public static List<String> getreso__Quiz_Interstitial_Title_List() {
        String Json = manegePrefUtils().getString(reso__Quiz_Interstitial_Title_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setreso__Quiz_Interstitial_Title_List(List<String> admobBannerIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobBannerIdList);
        manegePrefUtils().edit().putString(reso__Quiz_Interstitial_Title_List, json).apply();
    }

    public static List<String> getreso__Quiz_Native_Description_List() {
        String Json = manegePrefUtils().getString(reso__Quiz_Native_Description_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setreso__Quiz_Native_Description_List(List<String> admobBannerIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobBannerIdList);
        manegePrefUtils().edit().putString(reso__Quiz_Native_Description_List, json).apply();
    }


    public static List<String> getreso__Quiz_Native_Title_List() {
        String Json = manegePrefUtils().getString(reso__Quiz_Native_Title_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setreso__Quiz_Native_Title_List(List<String> admobBannerIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobBannerIdList);
        manegePrefUtils().edit().putString(reso__Quiz_Native_Title_List, json).apply();
    }


    public static Boolean getreso__Is_Quiz_Header() {
        return manegePrefUtils().getBoolean(reso__Is_Quiz_Header_Display, false);

    }

    public static void setreso__Is_Quiz_Header(Boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Is_Quiz_Header_Display, value).apply();
    }

    public static Boolean getreso__Is_Quiz_Fullscreen_Ads_Display() {
        return manegePrefUtils().getBoolean(reso__Is_Quiz_Fullscreen_Ads_Display, false);

    }

    public static void setreso__Is_Quiz_Fullscreen_Ads_Display(Boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Is_Quiz_Fullscreen_Ads_Display, value).apply();
    }

    public static Boolean getreso__is_Quiz_App_Open_Display() {
        return manegePrefUtils().getBoolean(reso__is_Quiz_App_Open_Display, false);

    }

    public static void setreso__is_Quiz_App_Open_Display(Boolean value) {
        manegePrefUtils().edit().putBoolean(reso__is_Quiz_App_Open_Display, value).apply();
    }

    public static Boolean getreso__Is_Quiz_Web_Interstitial_Show() {
        return manegePrefUtils().getBoolean(reso__Is_Quiz_Web_Interstitial_Display, false);

    }

    public static void setreso__Is_Quiz_Web_Interstitial_Show(Boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Is_Quiz_Web_Interstitial_Display, value).apply();
    }

    public static Boolean getreso__Splash_Quiz_Display() {
        return manegePrefUtils().getBoolean(reso__Splash_Quiz_Display, false);

    }

    public static void setreso__Splash_Quiz_Display(Boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Splash_Quiz_Display, value).apply();
    }

    public static boolean getreso__Is_Quiz_Banner() {
        return manegePrefUtils().getBoolean(reso__Is_Quiz_Banner, false);
    }

    public static void setreso__Is_Quiz_Banner(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Is_Quiz_Banner, value).apply();
    }

    public static boolean getreso__Is_Quiz_Native() {
        return manegePrefUtils().getBoolean(reso__Is_Quiz_Native, false);
    }

    public static void setreso__Is_Quiz_Native(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Is_Quiz_Native, value).apply();
    }

    public static boolean getreso__Quiz_Close_Ads_Display() {
        return manegePrefUtils().getBoolean(reso__Quiz_Close_Ads_Display, false);
    }

    public static boolean getreso__Is_Quiz_Display() {
        return manegePrefUtils().getBoolean(reso__Is_Quiz_Display, false);
    }

    public static void setreso__Is_Quiz_Display(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Is_Quiz_Display, value).apply();
    }

    public static String getreso__Splash_Ads_Type() {
        return manegePrefUtils().getString(reso__Splash_Ads_Type, "");
    }

    public static void setreso__Splash_Ads_Type(String value) {
        manegePrefUtils().edit().putString(reso__Splash_Ads_Type, value).apply();
    }

    private static SharedPreferences manegePrefUtils() {
        return AppControllerManege.resoGetInstance().getSharedPreferences(
                "emotes__diamond", Context.MODE_PRIVATE);
    }

    public static boolean getreso__Ads_Free() {
        return manegePrefUtils().getBoolean(reso__Ads_Free, false);
    }

    public static void setreso__Ads_Free(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Ads_Free, value).apply();
    }

    public static String getreso__Admob_Native_Unit_Id() {
        return manegePrefUtils().getString(reso__Admob_Native_Unit_Id, "");
    }

    public static void setreso__Admob_Native_Unit_Id(String value) {
        manegePrefUtils().edit().putString(reso__Admob_Native_Unit_Id, value).apply();
    }

    public static String getreso__Second_Native_Unit_Id() {
        return manegePrefUtils().getString(reso__Second_Native_Unit_Id, "");
    }

    public static void setreso__Second_Native_Unit_Id(String value) {
        manegePrefUtils().edit().putString(reso__Second_Native_Unit_Id, value).apply();
    }

    public static int getreso__Native_By_Page() {
        return manegePrefUtils().getInt(reso__Native_By_Page, 0);
    }

    public static void setreso__Native_By_Page(int value) {
        manegePrefUtils().edit().putInt(reso__Native_By_Page, value).apply();
    }

    public static String getreso__Ads_Display_Type() {
        return manegePrefUtils().getString(reso__Ads_Display_Type, "");
    }

    public static void setreso__Ads_Display_Type(String value) {
        manegePrefUtils().edit().putString(reso__Ads_Display_Type, value).apply();
    }

    public static String getreso__Ads_Type() {
        return manegePrefUtils().getString(reso__Ads_Type, "");
    }

    public static void setreso__Ads_Type(String value) {
        manegePrefUtils().edit().putString(reso__Ads_Type, value).apply();
    }

    public static String getreso__Small_Ads_Type() {
        return manegePrefUtils().getString(reso__Small_Ads_Type, "native_banner");
    }

    public static void setreso__Small_Ads_Type(String value) {
        manegePrefUtils().edit().putString(reso__Small_Ads_Type, value).apply();
    }

    public static String getreso__Native_Button_Color() {
        return manegePrefUtils().getString(reso__Native_Button_Color, "");
    }

    public static void setreso__Native_Button_Color(String value) {
        manegePrefUtils().edit().putString(reso__Native_Button_Color, value).apply();
    }

    public static String getreso__Native_Button_Text_Color() {
        return manegePrefUtils().getString(reso__Native_Button_Text_Color, "");
    }

    public static void setreso__Native_Button_Text_Color(String value) {
        manegePrefUtils().edit().putString(reso__Native_Button_Text_Color, value).apply();
    }

    public static String getreso__Native_BG_Color() {
        return manegePrefUtils().getString(reso__Native_BG_Color, "");
    }

    public static void setreso__Native_BG_Color(String value) {
        manegePrefUtils().edit().putString(reso__Native_BG_Color, value).apply();
    }

    public static String getreso__Admob_Banner_Ads_Id() {
        return manegePrefUtils().getString(reso__Admob_Banner_Ads_Id, "");
    }

    public static void setreso__Admob_Banner_Ads_Id(String value) {
        manegePrefUtils().edit().putString(reso__Admob_Banner_Ads_Id, value).apply();
    }

    public static String getreso__Admob_Fuulscreen_Ads_Unit_Id() {
        return manegePrefUtils().getString(reso__Admob_Fuulscreen_Ads_Unit_Id, "");
    }

    public static void setreso__Admob_Fuulscreen_Ads_Unit_Id(String value) {
        manegePrefUtils().edit().putString(reso__Admob_Fuulscreen_Ads_Unit_Id, value).apply();
    }

    public static int getreso__Ads_Back_Click() {
        return manegePrefUtils().getInt(reso__Ads_Back_Click, 0);
    }

    public static void setreso__Ads_Back_Click(int value) {
        manegePrefUtils().edit().putInt(reso__Ads_Back_Click, value).apply();
    }

    public static int getreso__Ads_Click() {
        return manegePrefUtils().getInt(reso__Ads_Click, 0);
    }

    public static void setreso__Ads_Click(int value) {
        manegePrefUtils().edit().putInt(reso__Ads_Click, value).apply();
    }

    public static int getAd_After_Row() {
        return manegePrefUtils().getInt(Ad_After_Row, 0);
    }

    public static void setAd_After_Row(int value) {
        manegePrefUtils().edit().putInt(Ad_After_Row, value).apply();
    }

    public static int get_Quiz_Ads_Click() {
        return manegePrefUtils().getInt(Quiz_Ads_Click, 0);
    }

    public static void set_Quiz_Ads_Click(int value) {
        manegePrefUtils().edit().putInt(Quiz_Ads_Click, value).apply();
    }

    public static int get_App_Open_Ads_Click() {
        return manegePrefUtils().getInt(App_Open_Ads_Click, 0);
    }

    public static void set_App_Open_Ads_Click(int value) {
        manegePrefUtils().edit().putInt(App_Open_Ads_Click, value).apply();
    }

    public static int get_Interstitial_Ads_Click() {
        return manegePrefUtils().getInt(Interstitial_Ads_Click, 0);
    }

    public static void set_Interstitial_Ads_Click(int value) {
        manegePrefUtils().edit().putInt(Interstitial_Ads_Click, value).apply();
    }

    public static int get_Native_Ads_Click() {
        return manegePrefUtils().getInt(Native_Ads_Click, 0);
    }

    public static void set_Native_Ads_Click(int value) {
        manegePrefUtils().edit().putInt(Native_Ads_Click, value).apply();
    }

    public static int get_Quiz_Ads_Back_Click() {
        return manegePrefUtils().getInt(Quiz_Ads_Back_Click, 0);
    }

    public static void set_Quiz_Ads_Back_Click(int value) {
        manegePrefUtils().edit().putInt(Quiz_Ads_Back_Click, value).apply();
    }

    public static int get_App_Open_Ads_Back_Click() {
        return manegePrefUtils().getInt(App_Open_Ads_Back_Click, 0);
    }

    public static void set_App_Open_Ads_Back_Click(int value) {
        manegePrefUtils().edit().putInt(App_Open_Ads_Back_Click, value).apply();
    }

    public static int get_Interstitial_Ads_Back_Click() {
        return manegePrefUtils().getInt(Interstitial_Ads_Back_Click, 0);
    }

    public static void set_Interstitial_Ads_Back_Click(int value) {
        manegePrefUtils().edit().putInt(Interstitial_Ads_Back_Click, value).apply();
    }

    public static int get_Native_Ads_Back_Click() {
        return manegePrefUtils().getInt(Native_Ads_Back_Click, 0);
    }

    public static void set_Native_Ads_Back_Click(int value) {
        manegePrefUtils().edit().putInt(Native_Ads_Back_Click, value).apply();
    }

    public static String getreso__Key_App_Starting_ADS() {
        return manegePrefUtils().getString(reso__Key_App_Starting_ADS, "");
    }

    public static void setreso__Key_App_Starting_ADS(String value) {
        manegePrefUtils().edit().putString(reso__Key_App_Starting_ADS, value).apply();
    }

    public static boolean getSplash_App_Open_Ads_Show() {
        return manegePrefUtils().getBoolean(Splash_App_Open_Ads_Show, false);
    }

    public static void setSplash_App_Open_Ads_Show(boolean value) {
        manegePrefUtils().edit().putBoolean(Splash_App_Open_Ads_Show, value).apply();
    }

    public static boolean getreso__Ads_One_By_One_Ids() {
        return manegePrefUtils().getBoolean(reso__Ads_One_By_One_Ids, false);
    }

    public static void setreso__Ads_One_By_One_Ids(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Ads_One_By_One_Ids, value).apply();
    }

    public static String getreso__Privacy_Policy() {
        return manegePrefUtils().getString(reso__Privacy_Policy, "");
    }

    public static void setreso__Privacy_Policy(String value) {
        manegePrefUtils().edit().putString(reso__Privacy_Policy, value).apply();
    }


    public static boolean getreso__Coming_Soon() {
        return manegePrefUtils().getBoolean(reso__Coming_Soon, false);
    }

    public static void setreso__Coming_Soon(boolean value) {
        manegePrefUtils().edit().putBoolean(reso__Coming_Soon, value).apply();
    }

    public static Integer getreso__Intro_Screen_Display() {
        return manegePrefUtils().getInt(reso__Intro_Screen_Display, 0);
    }

    public static void setreso__Intro_Screen_Display(Integer value) {
        manegePrefUtils().edit().putInt(reso__Intro_Screen_Display, value).apply();
    }

    public static List<String> getreso__Admob_App_Starting_Ids_List() {
        String Json = manegePrefUtils().getString(reso__Admob_App_Starting_Ids_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setreso__Admob_App_Starting_Ids_List(List<String> admobBannerIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobBannerIdList);
        manegePrefUtils().edit().putString(reso__Admob_App_Starting_Ids_List, json).apply();
    }

    public static List<String> getreso__Admob_Banner_Ids_List() {
        String Json = manegePrefUtils().getString(reso__Admob_Banner_Ids_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setreso__Admob_Banner_Ids_List(List<String> admobBannerIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobBannerIdList);
        manegePrefUtils().edit().putString(reso__Admob_Banner_Ids_List, json).apply();
    }

    public static List<String> getreso__Admob_Native_Custom_Ids_List() {
        String Json = manegePrefUtils().getString(reso__Admob_Native_Custom_Ids_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(Json, type);
        return arrayList;
    }

    public static void setreso__Admob_Native_Custom_Ids_List(List<String> admobNativeCustomIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobNativeCustomIdList);
        manegePrefUtils().edit().putString(reso__Admob_Native_Custom_Ids_List, json).apply();
    }

    public static List<String> getreso__Admob_Native_Full_Ads_List() {
        String Json = manegePrefUtils().getString(reso__Admob_Native_Full_Ads_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> stringArrayList = gson.fromJson(Json, type);
        return stringArrayList;
    }

    public static void setreso__Admob_Native_Full_Ads_List(List<String> admobNativeIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobNativeIdList);
        manegePrefUtils().edit().putString(reso__Admob_Native_Full_Ads_List, json).apply();
    }

    public static List<String> getreso__Admob_Interstitial_Ads_List() {
        String Json = manegePrefUtils().getString(reso__Admob_Interstitial_Ads_List, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> stringArrayList = gson.fromJson(Json, type);
        return stringArrayList;
    }

    public static void setreso__Admob_Interstitial_Ads_List(List<String> admobInterstitialIdList) {
        Gson gson = new Gson();
        String json = gson.toJson(admobInterstitialIdList);
        manegePrefUtils().edit().putString(reso__Admob_Interstitial_Ads_List, json).apply();
    }

    public static void setreso__App_Exit_Dialog(Boolean value) {
        manegePrefUtils().edit().putBoolean(reso__App_Exit_Dialog, value).apply();
    }

    public static Boolean reso__is_Exit_Dialog() {
        return manegePrefUtils().getBoolean(reso__App_Exit_Dialog, true);
    }
}

