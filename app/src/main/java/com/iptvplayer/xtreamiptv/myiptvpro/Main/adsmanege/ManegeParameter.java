package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ManegeParameter {


    static String Native_Ads_Button_Color = "Native_Ads_Button_Color";
    static String Native_Ads_Button_Text_Color = "NativeAdButtonTextColor";
    static String Native_Ads_BG_Color = "Native_Ad_BG_Color";
    static String Native_Ads_Button_Color_Marketing = "Native_Ads_Button_Color_Marketing";
    static String Native_Ads_Button_Text_Color_Marketing = "NativeAdButtonTextColor_Marketing";
    static String Native_Ads_BG_Color_Marketing = "Native_Ad_BG_Color_Marketing";
    static String Ads_Show_Type = "AdsShowType";
    static String Ads_Load_Type = "AdsLoadType";
    static String app_default_permission = "app_default_permission";
    static String app_default_permission_marketing = "app_default_permission_marketing";
    static String Application_Id = "Application_Id";
    static String Application_Token = "Application_Token";
    static String Bottom_Ads_Type = "Bottom_Ads_Type";
    static String Splash_Ads_Load_Type = "Splash_Ads_Load_Type";
    static String Privacy_Policy = "Privacy_Policy";
    static String Ads_Click = "Ads_Click";
    static String Ad_After_Row = "Ad_After_Row";
    static String Ad_After_Row_Marketing = "Ad_After_Row_Marketing";
    static String Ads_Click_Marketing = "Ads_Click_Marketing";
    static String Ads_Back_Click = "Ads_Back_Click";
    static String Ads_Back_Click_Marketing = "Ads_Back_Click_Marketing";
    static String Quiz_Ads_Click = "Quiz_Ads_Click";
    static String Quiz_Ads_Click_Marketing = "Quiz_Ads_Click_Marketing";
    static String App_Open_Ads_Click = "App_Open_Ads_Click";
    static String App_Open_Ads_Click_Marketing = "App_Open_Ads_Click_Marketing";
    static String Interstitial_Ads_Click = "Interstitial_Ads_Click";
    static String Interstitial_Ads_Click_Marketing = "Interstitial_Ads_Click_Marketing";
    static String Native_Ads_Click = "Native_Ads_Click";
    static String Native_Ads_Click_Marketing = "Native_Ads_Click_Marketing";
    static String Quiz_Ads_Back_Click = "Quiz_Ads_Back_Click";
    static String Quiz_Ads_Back_Click_Marketing = "Quiz_Ads_Back_Click_Marketing";
    static String App_Open_Ads_Back_Click = "App_Open_Ads_Back_Click";
    static String App_Open_Ads_Back_Click_Marketing = "App_Open_Ads_Back_Click_Marketing";
    static String Interstitial_Ads_Back_Click = "Interstitial_Ads_Back_Click";
    static String Interstitial_Ads_Back_Click_Marketing = "Interstitial_Ads_Back_Click_Marketing";
    static String Native_Ads_Back_Click = "Native_Ads_Back_Click";
    static String Native_Ads_Back_Click_Marketing = "Native_Ads_Back_Click_Marketing";
    static String Ads_Native_By_Page = "Ads_Native_By_Page";
    static String Intro_Screen_Show = "Intro_Screen_Show";
    static String Intro_Screen_Show_Marketing = "Intro_Screen_Show_Marketing";
    static String app_ad_home_count = "app_ad_home_count";
    static String Splash_App_Open_Ads_Show = "Splash_App_Open_Ads_Show";
    static String Splash_App_Open_Ads_Show_Marketing = "Splash_App_Open_Ads_Show_Marketing";
    static String Ads_One_By_One_Id = "Ads_One_By_One_Id";
    static String Coming_Soon_Screen = "Coming_Soon_Screen";
    static String App_Exit_Dialog = "App_Exit_Dialog";
    static String Firebase_Analytics = "Firebase_Analytics";
    static String Ads_Blocker = "Ads_Blocker";
    static String Native_Load_All_Time = "Native_Load_All_Time";
    static String Default_Cancel_To_Next_Pass = "Default_Cancel_To_Next_Pass";
    static String Default_Cancel_To_Next_Pass_Marketing = "Default_Cancel_To_Next_Pass_Marketing";
    static String privacy_policy_show_one_time = "privacy_policy_show_one_time";
    static String Is_Quiz_Native = "Is_Quiz_Native";
    static String Is_Quiz = "Is_Quiz";
    static String Is_Marketing_Quiz = "Is_Marketing_Quiz";
    static String Is_Quiz_Banner = "Is_Quiz_Banner";
    static String is_inside_home_count_marketing = "is_inside_home_count_marketing";
    static String is_inside_home_count = "is_inside_home_count";
    static String Splash_Quiz_Show = "Splash_Quiz_Show";
    static String Is_Quiz_Web_Interstitial_Show = "Is_Quiz_Web_Interstitial_Show";
    static String Is_Quiz_App_Open_Show = "Is_Quiz_App_Open_Show";
    static String Is_Quiz_Interstitial_Show = "Is_Quiz_Interstitial_Show";
    static String Is_Quiz_Header = "Is_Quiz_Header";
    static String is_quiz_Home = "is_quiz_Home";
    static String is_quiz_Home_Marketing = "is_quiz_Home_Marketing";
    public static String app_pro_xy_list_City = "app_pro_xy_list_City";
    public static String app_pro_xy_list_State = "app_pro_xy_list_State";
    public static String app_pro_xy_list_Country = "app_pro_xy_list_Country";
    public static String app_proxy_structure = "app_proxy_structure";
    public static String app_pro_xy_Check_Ip = "app_pro_xy_Check_Ip";
    public static String app_pro_xy_Ip_Checker_baseUrl = "app_pro_xy_Ip_Checker_baseUrl";
    static String is_quiz_Both_Priority = "is_quiz_Both_Priority";
    static String is_quiz_Both_Priority_Marketing = "is_quiz_Both_Priority_Marketing";
    static String is_quiz_Inter_Priority = "is_quiz_Inter_Priority";
    static String is_HomePriorityOneAds = "is_HomePriorityOneAds";
    static String is_HomePriorityOneAds_Marketing = "is_HomePriorityOneAds_Marketing";
    static String is_multi_types_ads_Priority = "is_multi_types_ads_Priority";
    static String is_multi_types_ads_Priority_Marketing = "is_multi_types_ads_Priority_Marketing";
    static String is_app_multi_types_ads_Priority = "is_app_multi_types_ads_Priority";
    static String is_app_multi_types_ads_Priority_Marketing = "is_app_multi_types_ads_Priority_Marketing";
    static String quiz_Inter_Priority_count = "quiz_Inter_Priority_count";
    static String FB_referrer_uri = "FacebookReferralUrl";
    static String InstagramReferralUrl = "InstagramReferralUrl";
    static String marketing_referrer_uri = "MarketingReferralUrl";
    static String google_referrer_uri = "GoogleReferralUrl";
    static String One_Signal_String = "One_Signal_String";
    static String Open_Link_in_App = "Open_Link_in_App";
    static String app_button_ad_show = "app_button_ad_show";
    static String app_button_ad_show_Marketing = "app_button_ad_show_Marketing";
       static String Interest_Audience_Category = "Interest_Audience_Category";
    static String Interest_Content_Category = "Interest_Content_Category";
    static String Interest_setCountryInterestList = "Interest_Country_Extension";
    public static String kscns;
    public static String suhdj;
    public static String jskdk;
    public static String didjs;
    public static String hksdn;

    static String Admob_Banner_Id = "Admob_Banner_Id";
    static String app_quiz_direct_play = "app_direct_ads_play";
    static String is_random_ads = "is_random_ads";
    static String is_alternative_ads = "is_alternative_ads";
    static String is_both_ad_priority = "is_both_ad_priority";
    static String is_both_random_priority = "is_both_random_priority";
    static String app_alternative_ads_type = "app_alternative_ads_type";
    static String multi_types_ads_show = "multi_types_ads_show";
    static String multi_types_ads_show_Marketing = "multi_types_ads_show_Marketing";
    static String app_multi_types_ads_show = "app_multi_types_ads_show";
    static String app_multi_types_ads_show_Marketing = "app_multi_types_ads_show_Marketing";
    static String adult_image_ad_click = "adult_image_ad_click";
    static String adult_image_ad_click_Marketing = "adult_image_ad_click_Marketing";
    static String app_ad_click_priority = "app_ad_click_priority";
    static String Admob_Small_Native_Id = "Admob_Small_Native_Id";
    static String Admob_Interstitial_Id = "Admob_Interstitial_Id";
    static String Admob_Native_Id = "Admob_Native_Id";
    static String Admob_App_Open_Id = "Admob_App_Open_Id";
    static String Quiz_banner_description = "Quiz_banner_description";
    static String is_quiz_banner_image = "is_quiz_banner_image";
    static String Quiz_banner_title = "Quiz_banner_title";
    static String Quiz_interstitial_description = "Quiz_interstitial_description";
    static String Quiz_interstitial_title = "Quiz_interstitial_title";
    static String Quiz_native_description = "Quiz_native_description";
    static String Quiz_native_title = "Quiz_native_title";
    static String is_adults_quiz_banner_marketing = "is_adults_quiz_banner_marketing";
    static String is_adults_quiz_banner = "is_adults_quiz_banner";

    static String is_intro_adults_image = "is_intro_adults_image";
    static String is_intro_adults_image_marketing = "is_intro_adults_image_marketing";

    public static String Version_Code = "Version_Code";
    public static String Version_Name = "Version_Name";
    public static String Forcly_Update_App = "Forcly_Update_App";
    public static String Update_App = "Update_App";
    public  static String Maintenance_Dialog = "Maintenance_Dialog";
    public static String Update_Coming_Soon = "Update_Coming_Soon";
    public static String App_Update_Link = "App_Update_Link";
    public static String Maintenance_Text = "Maintenance_Text";
    public static String ssfsfsfsf;
    public static String setAppClickAdsType = "app_ad_click_type";
    public static String quiz_base_url = "quiz_base_url";

    public static String nsjdn;
    public static String sdnks;
    public static String snjdj;
    public static String sbdjh;
    public static String hsdgh;

    public static String qwert;
    public static String asdfg;
    public static String zxcvb;
    public static String poiuy;


    static int jas = 256;
    static int sjdh = 128;
    static String sdjbf = "AES/CBC/PKCS7Padding";

    public static String difineEgf(String password, String cipherText) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] ctBytes = com.sun.jersey.core.util.Base64.decode(cipherText.getBytes(hsjdsb));
        byte[] saltBytes = Arrays.copyOfRange(ctBytes, 8, 16);
        byte[] ciphertextBytes = Arrays.copyOfRange(ctBytes, 16, ctBytes.length);
        byte[] key = new byte[jas /8];
        byte[] iv = new byte[sjdh /8];

        EvpKDF(password.getBytes(hsjdsb), jas, sjdh, saltBytes, key, iv);

        Cipher cipher = Cipher.getInstance(sdjbf);
        SecretKey keyS = new SecretKeySpec(key, aesKey);

        cipher.init(Cipher.DECRYPT_MODE, keyS, new IvParameterSpec(iv));
        byte[] plainText = cipher.doFinal(ciphertextBytes);
        return new String(plainText);
    }
    private static byte[] EvpKDF(byte[] password, int keySize, int ivSize, byte[] salt, byte[] resultKey, byte[] resultIv) throws NoSuchAlgorithmException {
        return EvpKDF(password, keySize, ivSize, salt, 1, ahhuhas, resultKey, resultIv);
    }

    private static byte[] EvpKDF(byte[] password, int keySize, int ivSize, byte[] salt, int iterations, String hashAlgorithm, byte[] resultKey, byte[] resultIv) throws NoSuchAlgorithmException {
        keySize = keySize / 32;
        ivSize = ivSize / 32;
        int targetKeySize = keySize + ivSize;
        byte[] derivedBytes = new byte[targetKeySize * 4];
        int numberOfDerivedWords = 0;
        byte[] block = null;
        MessageDigest hasher = MessageDigest.getInstance(hashAlgorithm);
        while (numberOfDerivedWords < targetKeySize) {
            if (block != null) {
                hasher.update(block);
            }
            hasher.update(password);
            block = hasher.digest(salt);
            hasher.reset();

            for (int i = 1; i < iterations; i++) {
                block = hasher.digest(block);
                hasher.reset();
            }

            System.arraycopy(block, 0, derivedBytes, numberOfDerivedWords * 4,
                    Math.min(block.length, (targetKeySize - numberOfDerivedWords) * 4));

            numberOfDerivedWords += block.length/4;
        }

        System.arraycopy(derivedBytes, 0, resultKey, 0, keySize * 4);
        System.arraycopy(derivedBytes, keySize * 4, resultIv, 0, ivSize * 4);

        return derivedBytes;
    }

    static String aesKey = "AES";
    static String hsjdsb = "UTF-8";
    static String ahhuhas = "MD5";

}
