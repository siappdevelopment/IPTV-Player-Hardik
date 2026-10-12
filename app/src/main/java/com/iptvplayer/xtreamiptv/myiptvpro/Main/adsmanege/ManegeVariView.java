package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.ManegeParameter.difineEgf;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.iptvplayer.xtreamiptv.myiptvpro.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ManegeVariView {
    public static String checkedInstallReferrer = "checkedInstallReferrer";
    public static String asadpass;
    public static String sdasdada;
    public static String grdswf1;
    public static String grdswf2;
    public static String grdswf3;
    public static String grdswf4;
    public static String grdswf5;
    public static String grdswf6;
    public static String grdswf7;
    public static String grdswf8;
    public static String grdswf9;
    public static String grdswf10;
    public static String skkdmss;
    public static String skdnsms;
    public static String snjndnn;
    public static String smdmsmd;
    static String sdswf = "Vk";
    static String zsd = "X1";
    static String qwza = sdswf + zsd;
    static List<String> listStrPos2 = new ArrayList<>();
    static List<String> listStrPos6 = new ArrayList<>();
    static String ushdd = "U2F";

    static List<String> reso__Quiz_banner_description_list = new ArrayList<>();
    static List<String> reso__banner_image_list = new ArrayList<>();
    static List<String> reso__Quiz_banner_title_list = new ArrayList<>();
    static List<String> reso__Quiz_interstitial_description_list = new ArrayList<>();
    static String skndn = "sdG";
    static List<String> reso__Quiz_interstitial_title_list = new ArrayList<>();
    static List<String> reso__Quiz_native_description_list = new ArrayList<>();
    static List<String> reso__Quiz_native_title_list = new ArrayList<>();
    static List<String> reso__bannerAdUnitIds = new ArrayList<>();
    static List<String> reso__nativeAdUnitIds = new ArrayList<>();
    static List<String> reso__bottomNativeAdUnitIds = new ArrayList<>();
    static List<String> reso__interstitialAdUnitIds = new ArrayList<>();
    static List<String> reso__appOpenAdUnitIds = new ArrayList<>();
    static String ksgqwl = ushdd + skndn;
    static List<Integer> reso__Audience_Category = new ArrayList<>();
    static List<Integer> reso__Content_Category = new ArrayList<>();
    static List<String> reso__setCountryInterestList = new ArrayList<>();
    static List<String> alternative_ads_typeList = new ArrayList<>();
    static List<String> multi_types_ads_show_List = new ArrayList<>();
    static List<String> multi_types_ads_show_List_marketing = new ArrayList<>();
    static List<String> app_multi_types_ads_show_List = new ArrayList<>();
    static List<String> app_multi_types_ads_show_List_marketing = new ArrayList<>();
    static List<String> adult_image_ad_click = new ArrayList<>();
    static List<String> adult_image_ad_click_Marketing = new ArrayList<>();


    private static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static void manegeParameter(Context context, FirebaseRemoteConfig utils) throws JSONException {
        int versionCode = getAppVersionCode(context);
        String versionKey = ManegeParameter.ssfsfsfsf + versionCode;
        String jsonString = utils.getString(versionKey);
        String vadl;
        String gval;
        String tval;
        String sdcx;
        String csdv;
        String xcxc;
        String tygf;
        String fgbf;
        String fgff;
        String gfgr;
        String nttv;

        String sqsa;
        String rtwy;
        String sdwd;
        String qsga;

        try {
            vadl = difineEgf(ManegeParameter.kscns, ManegeVariView.grdswf1);
            gval = difineEgf(ManegeParameter.suhdj, ManegeVariView.grdswf2);
            tval = difineEgf(ManegeParameter.jskdk, ManegeVariView.grdswf3);
            sdcx = difineEgf(ManegeParameter.didjs, ManegeVariView.grdswf4);
            csdv = difineEgf(ManegeParameter.hksdn, ManegeVariView.grdswf5);
            xcxc = difineEgf(ManegeParameter.nsjdn, ManegeVariView.grdswf6);
            tygf = difineEgf(ManegeParameter.sdnks, ManegeVariView.grdswf7);
            fgbf = difineEgf(ManegeParameter.snjdj, ManegeVariView.grdswf8);
            fgff = difineEgf(ManegeParameter.sbdjh, ManegeVariView.grdswf9);
            gfgr = difineEgf(ManegeParameter.hsdgh, ManegeVariView.grdswf10);
            nttv = "quiz_native_structure";

            sqsa = difineEgf(ManegeParameter.qwert, ManegeVariView.skkdmss);
            rtwy = difineEgf(ManegeParameter.asdfg, ManegeVariView.skdnsms);
            sdwd = difineEgf(ManegeParameter.zxcvb, ManegeVariView.snjndnn);
            qsga = difineEgf(ManegeParameter.poiuy, ManegeVariView.smdmsmd);


        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
        JSONObject root = new JSONObject(jsonString);

        String AD_BUTTON_COLOR = root.getString(ManegeParameter.Native_Ads_Button_Color);
        String AD_BUTTON_TEXT_COLOR = root.getString(ManegeParameter.Native_Ads_Button_Text_Color);
        String AD_BG_COLOR = root.getString(ManegeParameter.Native_Ads_BG_Color);
        String AD_BUTTON_COLOR_Marketing = root.getString(ManegeParameter.Native_Ads_Button_Color_Marketing);
        String AD_BUTTON_TEXT_COLOR_Marketing = root.getString(ManegeParameter.Native_Ads_Button_Text_Color_Marketing);
        String AD_BG_COLOR_Marketing = root.getString(ManegeParameter.Native_Ads_BG_Color_Marketing);
        String AD_SHOW_TYPE = root.getString(ManegeParameter.Ads_Show_Type);
        String AD_LOAD_TYPE = root.getString(ManegeParameter.Ads_Load_Type);
        String APPLICATION_ID = root.getString(ManegeParameter.Application_Id);
        String APPLICATION_TOKEN = root.getString(ManegeParameter.Application_Token);
        String BOTTOM_ADS_TYPE = root.getString(ManegeParameter.Bottom_Ads_Type);
        String SPLASH_AD_LOAD_TYPE = root.getString(ManegeParameter.Splash_Ads_Load_Type);
        String PRIVACY_POLICY = root.getString(ManegeParameter.Privacy_Policy);

        String setAppClickAdsType = root.getString(ManegeParameter.setAppClickAdsType);

        Long AD_CLICK = root.getLong(ManegeParameter.Ads_Click);
        Long Ad_After_Row = root.getLong(ManegeParameter.Ad_After_Row);
        Long Ad_After_Row_Marketing = root.getLong(ManegeParameter.Ad_After_Row_Marketing);
        Long AD_CLICK_Marketing = root.getLong(ManegeParameter.Ads_Click_Marketing);
        Long app_ad_click_priority = root.getLong(ManegeParameter.app_ad_click_priority);
        Long BACK_AD_CLICK = root.getLong(ManegeParameter.Ads_Back_Click);
        Long BACK_AD_CLICK_Marketing = root.getLong(ManegeParameter.Ads_Back_Click_Marketing);
        Long NATIVE_BY_PAGE = root.getLong(ManegeParameter.Ads_Native_By_Page);
        Long SCREEN_SHOW = root.getLong(ManegeParameter.Intro_Screen_Show);
        Long Intro_Screen_Show_Marketing = root.getLong(ManegeParameter.Intro_Screen_Show_Marketing);
        Long app_ad_home_count = root.getLong(ManegeParameter.app_ad_home_count);

        JSONObject click_Manage_pro_root = new JSONObject(jsonString);
        JSONObject click_Manage_json_qui = click_Manage_pro_root.getJSONObject("click_manage");

        Long Quiz_Ads_Click = click_Manage_json_qui.getLong(ManegeParameter.Quiz_Ads_Click);
        Long Quiz_Ads_Click_Marketing = click_Manage_json_qui.getLong(ManegeParameter.Quiz_Ads_Click_Marketing);
        Long App_Open_Ads_Click = click_Manage_json_qui.getLong(ManegeParameter.App_Open_Ads_Click);
        Long App_Open_Ads_Click_Marketing = click_Manage_json_qui.getLong(ManegeParameter.App_Open_Ads_Click_Marketing);
        Long Interstitial_Ads_Click = click_Manage_json_qui.getLong(ManegeParameter.Interstitial_Ads_Click);
        Long Interstitial_Ads_Click_Marketing = click_Manage_json_qui.getLong(ManegeParameter.Interstitial_Ads_Click_Marketing);
        Long Native_Ads_Click = click_Manage_json_qui.getLong(ManegeParameter.Native_Ads_Click);
        Long Native_Ads_Click_Marketing = click_Manage_json_qui.getLong(ManegeParameter.Native_Ads_Click_Marketing);

        Long Quiz_Ads_Back_Click = click_Manage_json_qui.getLong(ManegeParameter.Quiz_Ads_Back_Click);
        Long Quiz_Ads_Back_Click_Marketing = click_Manage_json_qui.getLong(ManegeParameter.Quiz_Ads_Back_Click_Marketing);
        Long App_Open_Ads_Back_Click = click_Manage_json_qui.getLong(ManegeParameter.App_Open_Ads_Back_Click);
        Long App_Open_Ads_Back_Click_Marketing = click_Manage_json_qui.getLong(ManegeParameter.App_Open_Ads_Back_Click_Marketing);
        Long Interstitial_Ads_Back_Click = click_Manage_json_qui.getLong(ManegeParameter.Interstitial_Ads_Back_Click);
        Long Interstitial_Ads_Back_Click_Marketing = click_Manage_json_qui.getLong(ManegeParameter.Interstitial_Ads_Back_Click_Marketing);
        Long Native_Ads_Back_Click = click_Manage_json_qui.getLong(ManegeParameter.Native_Ads_Back_Click);
        Long Native_Ads_Back_Click_Marketing = click_Manage_json_qui.getLong(ManegeParameter.Native_Ads_Back_Click_Marketing);


        Boolean Splash_App_Open_Ads_Show = root.getBoolean(ManegeParameter.Splash_App_Open_Ads_Show);
        Boolean Splash_App_Open_Ads_Show_Marketing = root.getBoolean(ManegeParameter.Splash_App_Open_Ads_Show_Marketing);
        Boolean AD_ONE_BY_ONE_ID = root.getBoolean(ManegeParameter.Ads_One_By_One_Id);
        Boolean COMING_SOON = root.getBoolean(ManegeParameter.Coming_Soon_Screen);
        Boolean EXIT_DIALOG = root.getBoolean(ManegeParameter.App_Exit_Dialog);
        Boolean FIREBASE_ANALYTICS = root.getBoolean(ManegeParameter.Firebase_Analytics);
        Boolean app_quiz_direct_play = root.getBoolean(ManegeParameter.app_quiz_direct_play);
        String setVGR = root.getString(ManegeParameter.quiz_base_url);


        Boolean AD_BLOCKER = root.getBoolean(ManegeParameter.Ads_Blocker);
        Boolean Native_Load_All_Time = root.getBoolean(ManegeParameter.Native_Load_All_Time);
        Boolean Default_Cancel_To_Next_Pass = root.getBoolean(ManegeParameter.Default_Cancel_To_Next_Pass);
        Boolean Default_Cancel_To_Next_Pass_Marketing = root.getBoolean(ManegeParameter.Default_Cancel_To_Next_Pass_Marketing);
        Boolean privacy_policy_show_one_time = root.getBoolean(ManegeParameter.privacy_policy_show_one_time);
        Boolean Open_Link_in_App = root.getBoolean(ManegeParameter.Open_Link_in_App);
        Boolean app_default_permission = root.getBoolean(ManegeParameter.app_default_permission);
        Boolean app_default_permission_marketing = root.getBoolean(ManegeParameter.app_default_permission_marketing);

        Boolean is_random_ads = root.getBoolean(ManegeParameter.is_random_ads);
        Boolean is_alternative_ads = root.getBoolean(ManegeParameter.is_alternative_ads);
        Boolean str_BothAdPriority = root.getBoolean(ManegeParameter.is_both_ad_priority);
        Boolean is_both_random_priority = root.getBoolean(ManegeParameter.is_both_random_priority);


        String FB_referrer_uri = root.getString(ManegeParameter.FB_referrer_uri);
        String Instagram_referrer_uri = root.getString(ManegeParameter.InstagramReferralUrl);
        String marketing_referrer_uri = root.getString(ManegeParameter.marketing_referrer_uri);
        String google_referrer_uri = root.getString(ManegeParameter.google_referrer_uri);
        String One_Signal_String = root.getString(ManegeParameter.One_Signal_String);

        JSONObject pro_root = new JSONObject(jsonString);
        JSONObject json_qui = pro_root.getJSONObject(sqsa);

        Boolean is_quiz = json_qui.getBoolean(ManegeParameter.Is_Quiz);
        Boolean Is_Quiz_Native = json_qui.getBoolean(ManegeParameter.Is_Quiz_Native);
        Boolean Is_Marketing_Quiz = json_qui.getBoolean(ManegeParameter.Is_Marketing_Quiz);
        Boolean is_inside_home_count = pro_root.getBoolean(ManegeParameter.is_inside_home_count);
        Boolean is_inside_home_count_marketing = pro_root.getBoolean(ManegeParameter.is_inside_home_count_marketing);
        Boolean Is_Quiz_Banner = json_qui.getBoolean(ManegeParameter.Is_Quiz_Banner);
        Boolean splash_quiz_show = json_qui.getBoolean(ManegeParameter.Splash_Quiz_Show);
        Boolean Is_Quiz_Web_Interstitial_Show = json_qui.getBoolean(ManegeParameter.Is_Quiz_Web_Interstitial_Show);
        Boolean Is_Quiz_App_Open_Show = json_qui.getBoolean(ManegeParameter.Is_Quiz_App_Open_Show);
        Boolean Is_Quiz_Interstitial_Show = json_qui.getBoolean(ManegeParameter.Is_Quiz_Interstitial_Show);
        Boolean Is_Quiz_Header = json_qui.getBoolean(ManegeParameter.Is_Quiz_Header);

        Boolean is_quiz_Home = root.getBoolean(ManegeParameter.is_quiz_Home);
        Boolean is_quiz_Home_Marketing = root.getBoolean(ManegeParameter.is_quiz_Home_Marketing);
        Boolean is_quiz_Both_Priority = root.getBoolean(ManegeParameter.is_quiz_Both_Priority);
        Boolean is_quiz_Both_Priority_Marketing = root.getBoolean(ManegeParameter.is_quiz_Both_Priority_Marketing);
        Boolean is_quiz_Inter_Priority = root.getBoolean(ManegeParameter.is_quiz_Inter_Priority);
        Boolean is_HomePriorityOneAds = root.getBoolean(ManegeParameter.is_HomePriorityOneAds);
        Boolean is_HomePriorityOneAds_Marketing = root.getBoolean(ManegeParameter.is_HomePriorityOneAds_Marketing);
        Boolean is_multi_types_ads_Priority = root.getBoolean(ManegeParameter.is_multi_types_ads_Priority);
        Boolean is_multi_types_ads_Priority_Marketing = root.getBoolean(ManegeParameter.is_multi_types_ads_Priority_Marketing);
        Boolean is_app_multi_types_ads_Priority = root.getBoolean(ManegeParameter.is_app_multi_types_ads_Priority);
        Boolean is_app_multi_types_ads_Priority_Marketing = root.getBoolean(ManegeParameter.is_app_multi_types_ads_Priority_Marketing);
        Long quiz_Inter_Priority_count = root.getLong(ManegeParameter.quiz_Inter_Priority_count);

        try {
            JSONArray alternative_ads_type = root.getJSONArray(ManegeParameter.app_alternative_ads_type);
            for (int i = 0; i < alternative_ads_type.length(); i++) {
                String contentCatString = alternative_ads_type.getString(i);
                alternative_ads_typeList.add(contentCatString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray multi_types_ads_show = root.getJSONArray(ManegeParameter.multi_types_ads_show);
            for (int i = 0; i < multi_types_ads_show.length(); i++) {
                String multiTypesAdsShowString = multi_types_ads_show.getString(i);
                multi_types_ads_show_List.add(multiTypesAdsShowString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray multi_types_ads_show_Marketing = root.getJSONArray(ManegeParameter.multi_types_ads_show_Marketing);
            for (int i = 0; i < multi_types_ads_show_Marketing.length(); i++) {
                String multiTypesAdsShowMarketingString = multi_types_ads_show_Marketing.getString(i);
                multi_types_ads_show_List_marketing.add(multiTypesAdsShowMarketingString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray app_multi_types_ads_show = root.getJSONArray(ManegeParameter.app_multi_types_ads_show);
            for (int i = 0; i < app_multi_types_ads_show.length(); i++) {
                String appMultiTypesAdsShowString = app_multi_types_ads_show.getString(i);
                app_multi_types_ads_show_List.add(appMultiTypesAdsShowString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray app_multi_types_ads_show_Marketing = root.getJSONArray(ManegeParameter.app_multi_types_ads_show_Marketing);
            for (int i = 0; i < app_multi_types_ads_show_Marketing.length(); i++) {
                String appMultiTypesAdsShowMarketingString = app_multi_types_ads_show_Marketing.getString(i);
                app_multi_types_ads_show_List_marketing.add(appMultiTypesAdsShowMarketingString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            JSONArray app_multi_types_ads_show = root.getJSONArray(ManegeParameter.adult_image_ad_click);
            for (int i = 0; i < app_multi_types_ads_show.length(); i++) {
                String appMultiTypesAdsShowString = app_multi_types_ads_show.getString(i);
                adult_image_ad_click.add(appMultiTypesAdsShowString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray app_multi_types_ads_show_Marketing = root.getJSONArray(ManegeParameter.adult_image_ad_click_Marketing);
            for (int i = 0; i < app_multi_types_ads_show_Marketing.length(); i++) {
                String appMultiTypesAdsShowMarketingString = app_multi_types_ads_show_Marketing.getString(i);
                adult_image_ad_click_Marketing.add(appMultiTypesAdsShowMarketingString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setquiz_Inter_Priority_count(Integer.valueOf(Math.toIntExact(quiz_Inter_Priority_count)));
        AppControlPreference.setis_quiz_Inter_Priority(is_quiz_Inter_Priority);


        AppControlPreference.setAlternativeList(alternative_ads_typeList);


        JSONObject main = new JSONObject(jsonString);
        JSONObject pro_json = main.getJSONObject(rtwy);


        Long app_pro_xy_Quiz_ClickEvent = pro_json.getLong(fgff);
        Long app_pro_xy_Quiz_Exclude_ClickEvent = pro_json.getLong(gfgr);

        Boolean app_button_ad_show = root.getBoolean(ManegeParameter.app_button_ad_show);
        Boolean app_button_ad_show_Marketing = root.getBoolean(ManegeParameter.app_button_ad_show_Marketing);
        AppControlPreference.setQuizLinkClickEvent(Math.toIntExact(app_pro_xy_Quiz_ClickEvent));
        AppControlPreference.setQuizLinkExcludeClickEvent(Math.toIntExact(app_pro_xy_Quiz_Exclude_ClickEvent));


        String marketingUrl = iukeb__printSubsInDelimiters2(AppControlPreference.iukeb__getReferrerUrl());
        String facebook_user = iukeb__extractValueBeforeFirstAmpersand(AppControlPreference.iukeb__getReferrerUrl());
        String instagram_user = iukeb__extractValueBeforeFirstAmpersand(AppControlPreference.iukeb__getReferrerUrl());
        String newstring = iukeb__printSubsInDelimiters1(AppControlPreference.iukeb__getReferrerUrl());
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);

        JSONObject quizAdsDesign = root.getJSONObject(qsga);

        Boolean is_intro_adults_image = quizAdsDesign.getBoolean(ManegeParameter.is_intro_adults_image);
        Boolean is_intro_adults_image_marketing = quizAdsDesign.getBoolean(ManegeParameter.is_intro_adults_image_marketing);

        Boolean is_adults_quiz_banner = quizAdsDesign.getBoolean(ManegeParameter.is_adults_quiz_banner);
        Boolean is_adults_quiz_banner_marketing = quizAdsDesign.getBoolean(ManegeParameter.is_adults_quiz_banner_marketing);

        if (newstring.equals(google_referrer_uri) || facebook_user.equals(FB_referrer_uri) || instagram_user.equals(Instagram_referrer_uri) || marketingUrl.equals(marketing_referrer_uri)) {
            if (AppControlPreference.getCheckExclude()) {
                Bundle newBundle = new Bundle();
                newBundle.putBoolean("is_user", true);
                firebaseAnalytics.logEvent(context.getString(R.string.is_user_basic), newBundle);
                AppControlPreference.setQuizAdsButton(app_button_ad_show);
                AppControlPreference.setis_quiz_Home(is_quiz_Home);
                AppControlPreference.setis_quiz_Both_Priority(is_quiz_Both_Priority);
                AppControlPreference.setreso__Is_Quiz_Display(is_quiz);
                AppControlPreference.setInsideHomeCount(is_inside_home_count);
                AppControlPreference.setreso__Intro_Screen_Display(Integer.valueOf(Math.toIntExact(SCREEN_SHOW)));
                AppControlPreference.setIs_adults_QuizBannerShow(is_adults_quiz_banner);
                AppControlPreference.setis_intro_adults_image(is_intro_adults_image);

                AppControlPreference.setMultiTypesAdsShow(multi_types_ads_show_List);
                AppControlPreference.setAdultImagelist(adult_image_ad_click);
                AppControlPreference.setAppMultiTypesAdsShow(app_multi_types_ads_show_List);
                AppControlPreference.setis_multi_types_ads_Priority(is_multi_types_ads_Priority);
                AppControlPreference.setis_app_multi_types_ads_Priority(is_app_multi_types_ads_Priority);
                AppControlPreference.setHomePriorityOneAds(is_HomePriorityOneAds);
                AppControlPreference.setDefaultPermission(app_default_permission);
                AppControlPreference.setDefault_Cancel_To_Next_Pass(Default_Cancel_To_Next_Pass);
                AppControlPreference.setreso__Native_Button_Color(AD_BUTTON_COLOR);
                AppControlPreference.setreso__Native_Button_Text_Color(AD_BUTTON_TEXT_COLOR);
                AppControlPreference.setreso__Native_BG_Color(AD_BG_COLOR);
                AppControlPreference.setSplash_App_Open_Ads_Show(Splash_App_Open_Ads_Show);
                AppControlPreference.setreso__Ads_Click(Math.toIntExact(AD_CLICK));
                AppControlPreference.setreso__Ads_Back_Click(Math.toIntExact(BACK_AD_CLICK));

                AppControlPreference.setAd_After_Row(Math.toIntExact(Ad_After_Row));

                AppControlPreference.set_Quiz_Ads_Click(Math.toIntExact(Quiz_Ads_Click));
                AppControlPreference.set_App_Open_Ads_Click(Math.toIntExact(App_Open_Ads_Click));
                AppControlPreference.set_Interstitial_Ads_Click(Math.toIntExact(Interstitial_Ads_Click));
                AppControlPreference.set_Native_Ads_Click(Math.toIntExact(Native_Ads_Click));

                AppControlPreference.set_Quiz_Ads_Back_Click(Math.toIntExact(Quiz_Ads_Back_Click));
                AppControlPreference.set_App_Open_Ads_Back_Click(Math.toIntExact(App_Open_Ads_Back_Click));
                AppControlPreference.set_Interstitial_Ads_Back_Click(Math.toIntExact(Interstitial_Ads_Back_Click));
                AppControlPreference.set_Native_Ads_Back_Click(Math.toIntExact(Native_Ads_Back_Click));

            } else {
                Bundle newBundle = new Bundle();
                newBundle.putBoolean("is_user", true);
                firebaseAnalytics.logEvent(context.getString(R.string.is_user_advanced), newBundle);
                AppControlPreference.setQuizAdsButton(app_button_ad_show_Marketing);
                AppControlPreference.setreso__Is_Quiz_Display(Is_Marketing_Quiz);
                AppControlPreference.setInsideHomeCount(is_inside_home_count_marketing);
                AppControlPreference.setreso__Intro_Screen_Display(Integer.valueOf(Math.toIntExact(Intro_Screen_Show_Marketing)));

                AppControlPreference.setis_quiz_Home(is_quiz_Home_Marketing);
                AppControlPreference.setis_quiz_Both_Priority(is_quiz_Both_Priority_Marketing);
                AppControlPreference.setIs_adults_QuizBannerShow(is_adults_quiz_banner_marketing);
                AppControlPreference.setis_intro_adults_image(is_intro_adults_image_marketing);
                AppControlPreference.setAdultImagelist(adult_image_ad_click_Marketing);
                AppControlPreference.setMultiTypesAdsShow(multi_types_ads_show_List_marketing);
                AppControlPreference.setAppMultiTypesAdsShow(app_multi_types_ads_show_List_marketing);
                AppControlPreference.setis_multi_types_ads_Priority(is_multi_types_ads_Priority_Marketing);
                AppControlPreference.setis_app_multi_types_ads_Priority(is_app_multi_types_ads_Priority_Marketing);
                AppControlPreference.setHomePriorityOneAds(is_HomePriorityOneAds_Marketing);
                AppControlPreference.setDefaultPermission(app_default_permission_marketing);
                AppControlPreference.setDefault_Cancel_To_Next_Pass(Default_Cancel_To_Next_Pass_Marketing);
                AppControlPreference.setreso__Native_Button_Color(AD_BUTTON_COLOR_Marketing);
                AppControlPreference.setreso__Native_Button_Text_Color(AD_BUTTON_TEXT_COLOR_Marketing);
                AppControlPreference.setreso__Native_BG_Color(AD_BG_COLOR_Marketing);
                AppControlPreference.setSplash_App_Open_Ads_Show(Splash_App_Open_Ads_Show_Marketing);
                AppControlPreference.setreso__Ads_Click(Math.toIntExact(AD_CLICK_Marketing));
                AppControlPreference.setreso__Ads_Back_Click(Math.toIntExact(BACK_AD_CLICK_Marketing));

                AppControlPreference.setAd_After_Row(Math.toIntExact(Ad_After_Row_Marketing));

                AppControlPreference.set_Quiz_Ads_Click(Math.toIntExact(Quiz_Ads_Click_Marketing));
                AppControlPreference.set_App_Open_Ads_Click(Math.toIntExact(App_Open_Ads_Click_Marketing));
                AppControlPreference.set_Interstitial_Ads_Click(Math.toIntExact(Interstitial_Ads_Click_Marketing));
                AppControlPreference.set_Native_Ads_Click(Math.toIntExact(Native_Ads_Click_Marketing));

                AppControlPreference.set_Quiz_Ads_Back_Click(Math.toIntExact(Quiz_Ads_Back_Click_Marketing));
                AppControlPreference.set_App_Open_Ads_Back_Click(Math.toIntExact(App_Open_Ads_Back_Click_Marketing));
                AppControlPreference.set_Interstitial_Ads_Back_Click(Math.toIntExact(Interstitial_Ads_Back_Click_Marketing));
                AppControlPreference.set_Native_Ads_Back_Click(Math.toIntExact(Native_Ads_Back_Click_Marketing));

            }


        } else {
            Bundle newBundle = new Bundle();
            newBundle.putBoolean("is_user", true);
            firebaseAnalytics.logEvent(context.getString(R.string.is_user_basic), newBundle);
            AppControlPreference.setQuizAdsButton(app_button_ad_show);
            AppControlPreference.setis_quiz_Home(is_quiz_Home);
            AppControlPreference.setis_quiz_Both_Priority(is_quiz_Both_Priority);
            AppControlPreference.setreso__Is_Quiz_Display(is_quiz);
            AppControlPreference.setInsideHomeCount(is_inside_home_count);
            AppControlPreference.setreso__Intro_Screen_Display(Integer.valueOf(Math.toIntExact(SCREEN_SHOW)));
            AppControlPreference.setIs_adults_QuizBannerShow(is_adults_quiz_banner);
            AppControlPreference.setis_intro_adults_image(is_intro_adults_image);
            AppControlPreference.setAdultImagelist(adult_image_ad_click);

            AppControlPreference.setMultiTypesAdsShow(multi_types_ads_show_List);
            AppControlPreference.setAppMultiTypesAdsShow(app_multi_types_ads_show_List);
            AppControlPreference.setis_multi_types_ads_Priority(is_multi_types_ads_Priority);
            AppControlPreference.setis_app_multi_types_ads_Priority(is_app_multi_types_ads_Priority);
            AppControlPreference.setHomePriorityOneAds(is_HomePriorityOneAds);
            AppControlPreference.setDefaultPermission(app_default_permission);
            AppControlPreference.setDefault_Cancel_To_Next_Pass(Default_Cancel_To_Next_Pass);
            AppControlPreference.setreso__Native_Button_Color(AD_BUTTON_COLOR);
            AppControlPreference.setreso__Native_Button_Text_Color(AD_BUTTON_TEXT_COLOR);
            AppControlPreference.setreso__Native_BG_Color(AD_BG_COLOR);
            AppControlPreference.setSplash_App_Open_Ads_Show(Splash_App_Open_Ads_Show);
            AppControlPreference.setreso__Ads_Click(Math.toIntExact(AD_CLICK));
            AppControlPreference.setreso__Ads_Back_Click(Math.toIntExact(BACK_AD_CLICK));

            AppControlPreference.setAd_After_Row(Math.toIntExact(Ad_After_Row));

            AppControlPreference.set_Quiz_Ads_Click(Math.toIntExact(Quiz_Ads_Click));
            AppControlPreference.set_App_Open_Ads_Click(Math.toIntExact(App_Open_Ads_Click));
            AppControlPreference.set_Interstitial_Ads_Click(Math.toIntExact(Interstitial_Ads_Click));
            AppControlPreference.set_Native_Ads_Click(Math.toIntExact(Native_Ads_Click));

            AppControlPreference.set_Quiz_Ads_Back_Click(Math.toIntExact(Quiz_Ads_Back_Click));
            AppControlPreference.set_App_Open_Ads_Back_Click(Math.toIntExact(App_Open_Ads_Back_Click));
            AppControlPreference.set_Interstitial_Ads_Back_Click(Math.toIntExact(Interstitial_Ads_Back_Click));
            AppControlPreference.set_Native_Ads_Back_Click(Math.toIntExact(Native_Ads_Back_Click));

        }
        if (AppControlPreference.getInsideHomeCount() && AppControlPreference.getreso__Intro_Screen_Display() >= 1) {
            AppControlPreference.setIntroLauncher(true);
        } else {
            AppControlPreference.setIntroLauncher(false);

        }
        AppControlPreference.setIsRandomAds(is_random_ads);
        AppControlPreference.setInsideHomeCounter(Math.toIntExact(app_ad_home_count));
        AppControlPreference.setIsAlternativeAds(is_alternative_ads);
        AppControlPreference.setVGR(setVGR);
        AppControlPreference.setIsBothAdPriority(str_BothAdPriority);
        AppControlPreference.setIsBothAdRandomPriority(is_both_random_priority);
        AppControlPreference.setreso__Alternet_Ads(Math.toIntExact(app_ad_click_priority));
        AppControlPreference.setAppClickAdsType(setAppClickAdsType);
        AppControlPreference.setQuizDirectPlay(app_quiz_direct_play);
        AppControlPreference.setreso__Is_Quiz_Banner(Is_Quiz_Banner);
        AppControlPreference.setreso__Is_Quiz_Native(Is_Quiz_Native);
        AppControlPreference.setNative_Load_All_Time(Native_Load_All_Time);
        AppControlPreference.setreso__Splash_Quiz_Display(splash_quiz_show);
        AppControlPreference.setreso__Is_Quiz_Web_Interstitial_Show(Is_Quiz_Web_Interstitial_Show);
        AppControlPreference.setreso__is_Quiz_App_Open_Display(Is_Quiz_App_Open_Show);
        AppControlPreference.setreso__Is_Quiz_Fullscreen_Ads_Display(Is_Quiz_Interstitial_Show);
        AppControlPreference.setreso__Is_Quiz_Header(Is_Quiz_Header);
        AppControlPreference.setOneSignalString(One_Signal_String);
        AppControlPreference.setOpen_Link_in_App(Open_Link_in_App);
        AppControlPreference.setreso__Ads_Display_Type(AD_SHOW_TYPE);
        AppControlPreference.setreso__Ads_Type(AD_LOAD_TYPE);
        AppControlPreference.setreso__Small_Ads_Type(BOTTOM_ADS_TYPE);




        AppControlPreference.setreso__Native_By_Page(Math.toIntExact(NATIVE_BY_PAGE));

        reso__Load_Ads_Id_List(jsonString, sdwd);
        reso__Load_Quiz_Data(jsonString, qsga);
        reso__pro_xy_part(jsonString, vadl, gval, tval, sdcx, csdv, xcxc, rtwy);

        if (!APPLICATION_ID.isEmpty() && !APPLICATION_TOKEN.isEmpty()) {
            reso__Initialize_Application(context, APPLICATION_ID, APPLICATION_TOKEN);
        }


        AppControlPreference.setreso__App_Exit_Dialog(EXIT_DIALOG);
        AppControlPreference.setreso__Coming_Soon(COMING_SOON);
        AppControlPreference.setreso__Privacy_Policy(PRIVACY_POLICY);
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(FIREBASE_ANALYTICS);


        AppControlPreference.setreso__Splash_Ads_Type(SPLASH_AD_LOAD_TYPE);
        AppControlPreference.setreso__Ads_Free(AD_BLOCKER);
        AppControlPreference.setreso__Privacy_Policy_Show_One_Time(privacy_policy_show_one_time);

        AppControlPreference.setreso__Ads_One_By_One_Ids(AD_ONE_BY_ONE_ID);


    }


    private static void reso__Initialize_Application(Context contett, String application_id, String token) {
        FacebookSdk.setApplicationId(application_id);
        FacebookSdk.setClientToken(token);
        FacebookSdk.sdkInitialize(contett);
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        FacebookSdk.setAutoLogAppEventsEnabled(true);

        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
        }
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
        AppEventsLogger logger = AppEventsLogger.newLogger(contett);
        logger.getApplicationId();
    }

    private static void reso__pro_xy_part(String jsonString, String vadl, String gval, String tval, String sdcx, String csdv, String xcxc, String rtwy) throws JSONException {
        JSONObject root = new JSONObject(jsonString);
        JSONObject appProxyStructure = root.getJSONObject(rtwy);


        try {
            JSONArray str2 = appProxyStructure.getJSONArray(gval);
            for (int i = 0; i < str2.length(); i++) {
                String Quiz_link = str2.getString(i);

                listStrPos2.add(Quiz_link);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        AppControlPreference.setQuizLinkList(listStrPos2);


        try {
            JSONArray str6 = appProxyStructure.getJSONArray(xcxc);
            for (int i = 0; i < str6.length(); i++) {
                String excludeLinkList = str6.getString(i);
                listStrPos6.add(excludeLinkList);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        AppControlPreference.setQuizLinkListExclude(listStrPos6);

    }

    public static String iukeb__printSubsInDelimiters1(String str) {
        String ans = "";
        String[] arr = str.split("=", 2);
        ans = arr[0];
        return ans;
    }

    public static String iukeb__printSubsInDelimiters2(String str) {
        String ans = "";
        String[] arr = str.split("=", 2);
        if (arr.length > 1 && arr[1] != null) {
            ans = arr[1];
        } else {
            ans = "";
        }
        return ans;
    }

    private static void reso__Load_Quiz_Data(String jsonString, String qsga) throws JSONException {
        JSONObject root = new JSONObject(jsonString);
        JSONObject quizAdsDesign = root.getJSONObject(qsga);


        try {
            JSONArray is_quiz_banner_images = quizAdsDesign.getJSONArray(ManegeParameter.is_quiz_banner_image);
            for (int i = 0; i < is_quiz_banner_images.length(); i++) {
                String str = is_quiz_banner_images.getString(i);
                reso__banner_image_list.add(str);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setQuizBannerShow(reso__banner_image_list);
        try {
            JSONArray Quiz_banner_description = quizAdsDesign.getJSONArray(ManegeParameter.Quiz_banner_description);
            for (int i = 0; i < Quiz_banner_description.length(); i++) {
                String str_Quiz_banner_description = Quiz_banner_description.getString(i);
                reso__Quiz_banner_description_list.add(str_Quiz_banner_description);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Quiz_Banner_Description_List(reso__Quiz_banner_description_list);


        try {
            JSONArray Quiz_banner_title = quizAdsDesign.getJSONArray(ManegeParameter.Quiz_banner_title);
            for (int i = 0; i < Quiz_banner_title.length(); i++) {
                String str_Quiz_banner_title = Quiz_banner_title.getString(i);
                reso__Quiz_banner_title_list.add(str_Quiz_banner_title);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Quiz_Banner_Title_List(reso__Quiz_banner_title_list);


        try {
            JSONArray Quiz_interstitial_description = quizAdsDesign.getJSONArray(ManegeParameter.Quiz_interstitial_description);
            for (int i = 0; i < Quiz_interstitial_description.length(); i++) {
                String str_Quiz_interstitial_description = Quiz_interstitial_description.getString(i);
                reso__Quiz_interstitial_description_list.add(str_Quiz_interstitial_description);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Quiz_Interstitial_Description_List(reso__Quiz_interstitial_description_list);


        try {
            JSONArray Quiz_interstitial_title = quizAdsDesign.getJSONArray(ManegeParameter.Quiz_interstitial_title);
            for (int i = 0; i < Quiz_interstitial_title.length(); i++) {
                String str_Quiz_interstitial_title = Quiz_interstitial_title.getString(i);
                reso__Quiz_interstitial_title_list.add(str_Quiz_interstitial_title);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Quiz_Interstitial_Title_List(reso__Quiz_interstitial_title_list);

        try {
            JSONArray Quiz_native_description = quizAdsDesign.getJSONArray(ManegeParameter.Quiz_native_description);
            for (int i = 0; i < Quiz_native_description.length(); i++) {
                String str_Quiz_native_description = Quiz_native_description.getString(i);
                reso__Quiz_native_description_list.add(str_Quiz_native_description);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Quiz_Native_Description_List(reso__Quiz_native_description_list);


        try {
            JSONArray Quiz_native_title = quizAdsDesign.getJSONArray(ManegeParameter.Quiz_native_title);
            for (int i = 0; i < Quiz_native_title.length(); i++) {
                String str_Quiz_native_title = Quiz_native_title.getString(i);
                reso__Quiz_native_title_list.add(str_Quiz_native_title);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Quiz_Native_Title_List(reso__Quiz_native_title_list);

    }

    public static String iukeb__extractValueBeforeFirstAmpersand(String input) {
        int index = input.indexOf('&');
        if (index != -1) {
            return input.substring(0, index);
        }
        return "";
    }

    private static void reso__Load_Ads_Id_List(String jsonString, String sdwd) throws JSONException {
        JSONObject root = new JSONObject(jsonString);
        JSONObject googleAds = root.getJSONObject(sdwd);


        try {
            JSONArray AdsAdmobBannerIds = googleAds.getJSONArray(ManegeParameter.Admob_Banner_Id);
            for (int i = 0; i < AdsAdmobBannerIds.length(); i++) {
                String Admob_Banner_Id = AdsAdmobBannerIds.getString(i);
                reso__bannerAdUnitIds.add(Admob_Banner_Id);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Admob_Banner_Ids_List(reso__bannerAdUnitIds);

        try {
            JSONArray AdsAdmobBottomNativeId = googleAds.getJSONArray(ManegeParameter.Admob_Small_Native_Id);
            for (int i = 0; i < AdsAdmobBottomNativeId.length(); i++) {
                String Admob_Bottom_Native_Id = AdsAdmobBottomNativeId.getString(i);
                reso__bottomNativeAdUnitIds.add(Admob_Bottom_Native_Id);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Admob_Native_Custom_Ids_List(reso__bottomNativeAdUnitIds);

        try {
            JSONArray AdsAdmobInterstitialId = googleAds.getJSONArray(ManegeParameter.Admob_Interstitial_Id);
            for (int i = 0; i < AdsAdmobInterstitialId.length(); i++) {
                String Admob_Interstitial_Id = AdsAdmobInterstitialId.getString(i);
                reso__interstitialAdUnitIds.add(Admob_Interstitial_Id);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Admob_Interstitial_Ads_List(reso__interstitialAdUnitIds);

        try {
            JSONArray AdsAdmobNativeId = googleAds.getJSONArray(ManegeParameter.Admob_Native_Id);
            for (int i = 0; i < AdsAdmobNativeId.length(); i++) {
                String Admob_Native_Id = AdsAdmobNativeId.getString(i);
                reso__nativeAdUnitIds.add(Admob_Native_Id);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Admob_Native_Full_Ads_List(reso__nativeAdUnitIds);

        try {
            JSONArray AdsAdmobAppOpenId = googleAds.getJSONArray(ManegeParameter.Admob_App_Open_Id);
            for (int i = 0; i < AdsAdmobAppOpenId.length(); i++) {
                String App_Open_Id = AdsAdmobAppOpenId.getString(i);
                reso__appOpenAdUnitIds.add(App_Open_Id);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setreso__Admob_App_Starting_Ids_List(reso__appOpenAdUnitIds);

        try {
            JSONArray audienceCat = googleAds.getJSONArray(ManegeParameter.Interest_Audience_Category);
            for (int i = 0; i < audienceCat.length(); i++) {
                int audienceCatString = audienceCat.getInt(i);
                reso__Audience_Category.add(audienceCatString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setAudienceCategory(reso__Audience_Category);
        try {
            JSONArray ContentCat = googleAds.getJSONArray(ManegeParameter.Interest_Content_Category);
            for (int i = 0; i < ContentCat.length(); i++) {
                int contentCatString = ContentCat.getInt(i);
                reso__Content_Category.add(contentCatString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setContentCategory(reso__Content_Category);
        try {
            JSONArray countryCat = googleAds.getJSONArray(ManegeParameter.Interest_setCountryInterestList);
            for (int i = 0; i < countryCat.length(); i++) {
                String contentCatString = countryCat.getString(i);
                reso__setCountryInterestList.add(contentCatString);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AppControlPreference.setCountryInterestList(reso__setCountryInterestList);

    }

}
