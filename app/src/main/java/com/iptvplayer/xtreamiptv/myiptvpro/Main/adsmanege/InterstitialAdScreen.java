package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;


import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.getAdIndex;
import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.getAlternativeList;
import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.setAdIndex;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

@SuppressWarnings("all")
public class InterstitialAdScreen {

    public static int reso__Ads_Click = 0;
    public static int Quiz_Ads_Click = 0;
    public static int App_Open_Ads_Click = 0;
    public static int Interstitial_Ads_Click = 0;
    public static int Native_Ads_Click = 0;
    public static int Quiz_Ads_Back_Click = 0;
    public static int App_Open_Ads_Back_Click = 0;
    public static int Interstitial_Ads_Back_Click = 0;
    public static int Native_Ads_Back_Click = 0;
    public static int reso__Ads_Back_Click = 0;
    public static Boolean reso__Is_Ads_Show_Enabled = false;
    public static int reso__Fullscreen_ADS_Pos = 0;
    public static boolean reso__Fullscreen_ADS_Failed = false;
    public static OnCompeteAds reso__On_Compete_Ads_Callback;
    public static boolean reso__Is_Ads_Procurement_Enabled = true;
    public static int priority_inter_ads_count = 0;
    private static InterstitialAd reso__Fullscreen_ADS;

    public static void reso__Fullscreen_ADS_Display(Activity context, OnCompeteAds onFinishAd, boolean b, boolean... doShowAds) {

        reso__On_Compete_Ads_Callback = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__On_Compete_Ads_Callback.onCompeteAds(true);
            return;
        }

        if (AppControlPreference.getis_quiz_Home()) {
            if (AppControlPreference.getis_quiz_Inter_Priority()) {
                if (priority_inter_ads_count == AppControlPreference.getquiz_Inter_Priority_count()) {
                    priority_inter_ads_count = 0;
                    if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                        reso__Admob_Fullscreen_ADS(context);
                    }
                } else {
                    QuizAdManege.reso__Quiz_Interstitial_ADS(context, reso__On_Compete_Ads_Callback, true);
                    priority_inter_ads_count++;
                }
            } else {
                QuizAdManege.reso__Quiz_Interstitial_ADS(context, reso__On_Compete_Ads_Callback, true);
            }
        } else if (b) {
            showAdsSequentially(context, AppControlPreference.getAdultImagelist(), 0, reso__On_Compete_Ads_Callback);
        } else if (AppControlPreference.getis_multi_types_ads_Priority()) {
            showAdsSequentially(context, AppControlPreference.getMultiTypesAdsShow(), 0, reso__On_Compete_Ads_Callback);
        } else if (AppControlPreference.getis_quiz_Both_Priority()) {
            QuizAdManege.reso__Quiz_Interstitial_ADS(context, new OnCompeteAds() {
                @Override
                public void onCompeteAds(boolean b) {
                    if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                        reso__Admob_Fullscreen_ADS(context);
                    } else {

                    }
                }
            }, true);
        } else {
            if (InterstitialAdScreen.reso__Ads_Click == AppControlPreference.getreso__Ads_Click()) {
                InterstitialAdScreen.reso__Ads_Click = 0;
                InterstitialAdScreen.reso__Fullscreen_ADS_Pos = 0;

                if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                    reso__Admob_Fullscreen_ADS(context);
                } else {

                }

            } else {
                reso__On_Compete_Ads_Callback.onCompeteAds(true);
                InterstitialAdScreen.reso__Ads_Click++;
            }
        }
    }

    public static void reso__App_Fullscreen_ADS_Display(Activity context, OnCompeteAds onFinishAd, boolean b, boolean... doShowAds) {

        reso__On_Compete_Ads_Callback = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__On_Compete_Ads_Callback.onCompeteAds(true);
            return;
        }

        if (AppControlPreference.getis_quiz_Home()) {
            if (AppControlPreference.getis_quiz_Inter_Priority()) {
                if (priority_inter_ads_count == AppControlPreference.getquiz_Inter_Priority_count()) {
                    priority_inter_ads_count = 0;
                    if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                        reso__Admob_Fullscreen_ADS(context);
                    }
                } else {
                    QuizAdManege.reso__Quiz_Interstitial_ADS(context, reso__On_Compete_Ads_Callback, true);
                    priority_inter_ads_count++;
                }
            } else {
                QuizAdManege.reso__Quiz_Interstitial_ADS(context, reso__On_Compete_Ads_Callback, true);
            }
        } else if (b) {
            showAdsSequentially(context, AppControlPreference.getAdultImagelist(), 0, reso__On_Compete_Ads_Callback);
        } else if (AppControlPreference.getis_app_multi_types_ads_Priority()) {
            showAdsSequentially(context, AppControlPreference.getAppMultiTypesAdsShow(), 0, reso__On_Compete_Ads_Callback);
        } else if (AppControlPreference.getis_quiz_Both_Priority()) {
            QuizAdManege.reso__Quiz_Interstitial_ADS(context, new OnCompeteAds() {
                @Override
                public void onCompeteAds(boolean b) {
                    if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                        reso__Admob_Fullscreen_ADS(context);
                    } else {

                    }
                }
            }, true);
        } else {
            if (InterstitialAdScreen.reso__Ads_Click == AppControlPreference.getreso__Ads_Click()) {
                InterstitialAdScreen.reso__Ads_Click = 0;
                InterstitialAdScreen.reso__Fullscreen_ADS_Pos = 0;

                if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                    reso__Admob_Fullscreen_ADS(context);
                } else {

                }
            } else {
                reso__On_Compete_Ads_Callback.onCompeteAds(true);
                InterstitialAdScreen.reso__Ads_Click++;
            }
        }
    }

    public static void showAdsSequentially(Activity activity, List<String> adsList, int index, OnCompeteAds reso__On_Compete_Ads_Callback) {

        if (index >= adsList.size()) {

            if (reso__On_Compete_Ads_Callback != null) {

                reso__On_Compete_Ads_Callback.onCompeteAds(true);
            }
            return;
        }

        String adType = adsList.get(index);

        switch (adType) {

            case "Quiz":
                if (InterstitialAdScreen.Quiz_Ads_Click == AppControlPreference.get_Quiz_Ads_Click()) {
                    InterstitialAdScreen.Quiz_Ads_Click = 0;

                    QuizAdManege.reso__Quiz_Interstitial_ADS(activity, new OnCompeteAds() {
                        @Override
                        public void onCompeteAds(boolean b) {
                            showAdsSequentially(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                        }
                    }, true);

                } else {
                    showAdsSequentially(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                    InterstitialAdScreen.Quiz_Ads_Click++;
                }
                break;

            case "Appopen":
                if (InterstitialAdScreen.App_Open_Ads_Click == AppControlPreference.get_App_Open_Ads_Click()) {
                    InterstitialAdScreen.App_Open_Ads_Click = 0;

                    AppOpenAdView.reso___Load_App_Starting_Display((Activity) activity, new AppOpenAdView.OnCompleteAds() {
                        @Override
                        public void onCompleteAds(boolean b) {
                            showAdsSequentially(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                        }
                    });
                } else {
                    showAdsSequentially(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                    InterstitialAdScreen.App_Open_Ads_Click++;
                }
                break;

            case "Interstitial":
                if (InterstitialAdScreen.Interstitial_Ads_Click == AppControlPreference.get_Interstitial_Ads_Click()) {
                    InterstitialAdScreen.Interstitial_Ads_Click = 0;

                    InterstitialAdScreen.reso__FullScreenInterstailOnly_Sequentially((Activity) activity, new OnCompeteAds() {
                        @Override
                        public void onCompeteAds(boolean b) {
                            showAdsSequentially(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                        }
                    });
                } else {
                    showAdsSequentially(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                    InterstitialAdScreen.Interstitial_Ads_Click++;
                }
                break;

            case "Native":
                if (InterstitialAdScreen.Native_Ads_Click == AppControlPreference.get_Native_Ads_Click()) {
                    InterstitialAdScreen.Native_Ads_Click = 0;

                    NativeManegeView.reso__Admob_Native_Full_ADS_Display((Activity) activity, new OnCompeteAds() {
                        @Override
                        public void onCompeteAds(boolean b) {
                            showAdsSequentially(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                        }
                    });
                } else {
                    showAdsSequentially(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                    InterstitialAdScreen.Native_Ads_Click++;
                }
                break;

            default:
                showAdsSequentially(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                break;
        }
    }

    public static void showAdsSequentially_back(Activity activity, List<String> adsList, int index, OnCompeteAds reso__On_Compete_Ads_Callback) {

        if (index >= adsList.size()) {

            if (reso__On_Compete_Ads_Callback != null) {

                reso__On_Compete_Ads_Callback.onCompeteAds(true);
            }
            return;
        }

        String adType = adsList.get(index);

        switch (adType) {

            case "Quiz":
                if (InterstitialAdScreen.Quiz_Ads_Back_Click == AppControlPreference.get_Quiz_Ads_Back_Click()) {
                    InterstitialAdScreen.Quiz_Ads_Back_Click = 0;

                    QuizAdManege.reso__Quiz_Interstitial_ADS(activity, new OnCompeteAds() {
                        @Override
                        public void onCompeteAds(boolean b) {
                            showAdsSequentially_back(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                        }
                    }, true);

                } else {
                    showAdsSequentially_back(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                    InterstitialAdScreen.Quiz_Ads_Back_Click++;
                }
                break;

            case "Appopen":
                if (InterstitialAdScreen.App_Open_Ads_Back_Click == AppControlPreference.get_App_Open_Ads_Back_Click()) {
                    InterstitialAdScreen.App_Open_Ads_Back_Click = 0;

                    AppOpenAdView.reso___Load_App_Starting_Display((Activity) activity, new AppOpenAdView.OnCompleteAds() {
                        @Override
                        public void onCompleteAds(boolean b) {
                            showAdsSequentially_back(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                        }
                    });
                } else {
                    showAdsSequentially_back(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                    InterstitialAdScreen.App_Open_Ads_Back_Click++;
                }
                break;

            case "Interstitial":
                if (InterstitialAdScreen.Interstitial_Ads_Back_Click == AppControlPreference.get_Interstitial_Ads_Back_Click()) {
                    InterstitialAdScreen.Interstitial_Ads_Back_Click = 0;

                    InterstitialAdScreen.reso__FullScreenInterstailOnly_Sequentially((Activity) activity, new OnCompeteAds() {
                        @Override
                        public void onCompeteAds(boolean b) {
                            showAdsSequentially_back(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                        }
                    });
                } else {
                    showAdsSequentially_back(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                    InterstitialAdScreen.Interstitial_Ads_Back_Click++;
                }
                break;

            case "Native":
                if (InterstitialAdScreen.Native_Ads_Back_Click == AppControlPreference.get_Native_Ads_Back_Click()) {
                    InterstitialAdScreen.Native_Ads_Back_Click = 0;

                    NativeManegeView.reso__Admob_Native_Full_ADS_Display((Activity) activity, new OnCompeteAds() {
                        @Override
                        public void onCompeteAds(boolean b) {
                            showAdsSequentially_back(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                        }
                    });
                } else {
                    showAdsSequentially_back(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                    InterstitialAdScreen.Native_Ads_Back_Click++;
                }
                break;

            default:
                showAdsSequentially_back(activity, adsList, index + 1, reso__On_Compete_Ads_Callback);
                break;
        }
    }

    public static void reso__FullScreenInterstailOnly(Activity context, OnCompeteAds onFinishAd, boolean... doShowAds) {

        reso__On_Compete_Ads_Callback = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__On_Compete_Ads_Callback.onCompeteAds(true);
            return;
        }

        if (InterstitialAdScreen.reso__Ads_Click == AppControlPreference.getreso__Ads_Click()) {
            InterstitialAdScreen.reso__Ads_Click = 0;
            InterstitialAdScreen.reso__Fullscreen_ADS_Pos = 0;


            if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                reso__Admob_FullScreenInterstailOnly(context);
            }

        } else {
            reso__On_Compete_Ads_Callback.onCompeteAds(true);
            InterstitialAdScreen.reso__Ads_Click++;
        }
    }

    public static void reso__FullScreenInterstailOnly_Sequentially(Activity context, OnCompeteAds onFinishAd, boolean... doShowAds) {

        reso__On_Compete_Ads_Callback = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__On_Compete_Ads_Callback.onCompeteAds(true);
            return;
        }


        if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
            reso__Admob_FullScreenInterstailOnly(context);
        }
    }

    public static String getNextAdType() {

        List<String> list = getAlternativeList();

        // Check null or empty
        if (list == null || list.isEmpty()) {
            return "Interstitial"; // fallback
        }

        int index = getAdIndex();

        // Reset index if out of bounds
        if (index >= list.size()) {
            index = 0;
        }

        String adType = list.get(index);

        // Save next index
        setAdIndex(index + 1);

        return adType;
    }

    public static void reso__FullScreenQuizAd(Activity context, OnCompeteAds onFinishAd, boolean... doShowAds) {

        reso__On_Compete_Ads_Callback = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__On_Compete_Ads_Callback.onCompeteAds(true);
            return;
        }

        if (InterstitialAdScreen.reso__Ads_Click == AppControlPreference.getreso__Ads_Click()) {
            InterstitialAdScreen.reso__Ads_Click = 0;
            InterstitialAdScreen.reso__Fullscreen_ADS_Pos = 0;


            QuizAdManege.reso__Quiz_Interstitial_ADS(context, reso__On_Compete_Ads_Callback, true);


        } else {
            reso__On_Compete_Ads_Callback.onCompeteAds(true);
            InterstitialAdScreen.reso__Ads_Click++;
        }
    }

    public static void reso__Admob_FullScreenInterstailOnly(Activity context) {

        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {

            if (reso__Is_Ads_Procurement_Enabled) {
                reso__Is_Ads_Procurement_Enabled = false;
                reso__Admob_Fullscreen_Only(context);

            }
            return;
        }

        if (reso__Fullscreen_ADS != null) {

            reso__Ads_Show_Check(false);
            reso__Fullscreen_ADS.show(context);

        } else {

            reso__On_Compete_Ads_Callback.onCompeteAds(true);


        }

        if (reso__Is_Ads_Procurement_Enabled) {
            reso__Is_Ads_Procurement_Enabled = false;
            reso__Admob_Fullscreen_Only(context);
        }
    }

    public static void reso__Admob_Fullscreen_ADS(Activity context) {

        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {

            if (reso__Is_Ads_Procurement_Enabled) {
                reso__Is_Ads_Procurement_Enabled = false;
                reso__Admob_Fullscreen_ADS_Load(context);

            }
            return;
        }

        if (reso__Fullscreen_ADS != null) {

            reso__Ads_Show_Check(false);
            reso__Fullscreen_ADS.show(context);

        } else {
            if (AppControlPreference.getreso__Is_Quiz_Display()) {
                if (AppControlPreference.getreso__Is_Quiz_Fullscreen_Ads_Display()) {
                    QuizAdManege.reso__Quiz_Interstitial_ADS(context, reso__On_Compete_Ads_Callback, true);
                } else {
                    reso__On_Compete_Ads_Callback.onCompeteAds(true);
                }
            } else {
                reso__On_Compete_Ads_Callback.onCompeteAds(true);
            }

        }

        if (reso__Is_Ads_Procurement_Enabled) {
            reso__Is_Ads_Procurement_Enabled = false;
            reso__Admob_Fullscreen_ADS_Load(context);
        }
    }

    public static void reso__Back_Fullscreen_ADS_Display(Activity context, OnCompeteAds onFinishAd, boolean... doShowAds) {
        reso__On_Compete_Ads_Callback = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__On_Compete_Ads_Callback.onCompeteAds(true);
            return;
        }

        if (AppControlPreference.getis_quiz_Home()) {
            if (AppControlPreference.getis_quiz_Inter_Priority()) {
                if (priority_inter_ads_count == AppControlPreference.getquiz_Inter_Priority_count()) {
                    priority_inter_ads_count = 0;
                    if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                        reso__Admob_Fullscreen_ADS(context);
                    }
                } else {
                    QuizAdManege.reso__Quiz_Interstitial_ADS(context, reso__On_Compete_Ads_Callback, true);
                    priority_inter_ads_count++;
                }
            } else {
                QuizAdManege.reso__Quiz_Interstitial_ADS(context, reso__On_Compete_Ads_Callback, true);
            }
        } else if (AppControlPreference.getis_multi_types_ads_Priority()) {
            showAdsSequentially_back(context, AppControlPreference.getMultiTypesAdsShow(), 0, reso__On_Compete_Ads_Callback);
        } else if (AppControlPreference.getis_quiz_Both_Priority()) {
            QuizAdManege.reso__Quiz_Interstitial_ADS(context, new OnCompeteAds() {
                @Override
                public void onCompeteAds(boolean b) {

                    if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                        reso__Admob_Fullscreen_ADS(context);
                    } else {

                    }
                }
            }, true);
        } else {
            if (InterstitialAdScreen.reso__Ads_Back_Click == AppControlPreference.getreso__Ads_Back_Click()) {
                InterstitialAdScreen.reso__Ads_Back_Click = 0;
                InterstitialAdScreen.reso__Fullscreen_ADS_Pos = 0;

                if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                    reso__Admob_Fullscreen_ADS(context);
                } else {

                }
            } else {
                reso__On_Compete_Ads_Callback.onCompeteAds(true);
                InterstitialAdScreen.reso__Ads_Back_Click++;
            }
        }
    }

    public static void reso__Ads_Show_Check(Boolean aBoolean) {

        if (aBoolean) {
            AppOpenAdView.reso__Is_Ad_Showing = false;
            AppControllerManege.reso__is_Fast_Start = false;
            reso__Is_Ads_Show_Enabled = true;
        } else {
            AppOpenAdView.reso__Is_Ad_Showing = true;
            reso__Is_Ads_Show_Enabled = false;
            AppControllerManege.reso__is_Fast_Start = true;
        }

    }

    public static void reso__Fullscreen_ADS_Random_Ids() {
        try {
            if (AppControlPreference.getreso__Admob_Interstitial_Ads_List() != null && AppControlPreference.getreso__Admob_Interstitial_Ads_List().size() != 0 && AppControlPreference.getreso__Admob_Interstitial_Ads_List().size() != reso__Fullscreen_ADS_Pos) {
                reso__Fullscreen_ADS_Failed = true;
                AppControlPreference.setreso__Admob_Fuulscreen_Ads_Unit_Id(AppControlPreference.getreso__Admob_Interstitial_Ads_List().get(reso__Fullscreen_ADS_Pos));
                reso__Fullscreen_ADS_Pos = reso__Fullscreen_ADS_Pos + 1;
            } else {
                reso__Fullscreen_ADS_Failed = false;
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reso__Admob_Fullscreen_ADS_Load(Activity context) {
        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
            LoadingDialogView.reso__Show_Ads_Loading_Dialog(context);
        }

        InterstitialAdScreen.reso__Fullscreen_ADS_Random_Ids();
        if (!InterstitialAdScreen.reso__Fullscreen_ADS_Failed) {
            reso__Is_Ads_Procurement_Enabled = true;
            if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                LoadingDialogView.reso__Hide_Ads_Loading_Dialog();

                if (AppControlPreference.getreso__Is_Quiz_Display()) {
                    if (AppControlPreference.getreso__Is_Quiz_Fullscreen_Ads_Display()) {
                        QuizAdManege.reso__Quiz_Interstitial_ADS(context, reso__On_Compete_Ads_Callback, true);
                    } else {
                        reso__On_Compete_Ads_Callback.onCompeteAds(true);
                    }
                } else {
                    reso__On_Compete_Ads_Callback.onCompeteAds(true);
                }

            }

            return;
        }


        String Interstitialad_ids = AppControlPreference.getreso__Admob_Fuulscreen_Ads_Unit_Id();

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        reso__Fullscreen_ADS = null;

        reso__Fullscreen_ADS.load(context, Interstitialad_ids, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                FirebaseAnalytics firebaseAnalytics;
                firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                interstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(AdValue adValue) {
                        double revenue = adValue.getValueMicros() / 1_000_000.0;
                        String currency = adValue.getCurrencyCode();
                        Bundle adRevenueParams = new Bundle();
                        adRevenueParams.putString(FirebaseAnalytics.Param.AD_PLATFORM, "Google Ad Manager");
                        adRevenueParams.putString(FirebaseAnalytics.Param.CURRENCY, currency);
                        adRevenueParams.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, adRevenueParams);
                    }
                });

                reso__Is_Ads_Procurement_Enabled = true;
                if (AppControlPreference.getreso__Ads_One_By_One_Ids()) {
                    if (AppControlPreference.getreso__Admob_Interstitial_Ads_List() != null && AppControlPreference.getreso__Admob_Interstitial_Ads_List().size() != 0 && AppControlPreference.getreso__Admob_Interstitial_Ads_List().size() == reso__Fullscreen_ADS_Pos) {
                        reso__Fullscreen_ADS_Pos = 0;
                    }
                } else {
                    reso__Fullscreen_ADS_Pos = 0;
                }

                InterstitialAdScreen.reso__Fullscreen_ADS = interstitialAd;
                if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                    LoadingDialogView.reso__Hide_Ads_Loading_Dialog();

                    if (AppControllerManege.reso__Is_Screen_Check_Enabled) {
                        AppControllerManege.reso__Is_Screen_Check_Enabled = false;

                        InterstitialAdScreen.reso__Fullscreen_ADS.show((Activity) context);
                        InterstitialAdScreen.reso__Ads_Show_Check(false);
                    }

                }
                reso__On_Content_Listener(context);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                reso__Admob_Fullscreen_ADS_Load(context);

            }
        });
    }

    public static void reso__Admob_Fullscreen_Only(Activity context) {
        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
            LoadingDialogView.reso__Show_Ads_Loading_Dialog(context);
        }

        InterstitialAdScreen.reso__Fullscreen_ADS_Random_Ids();


        String Interstitialad_ids = AppControlPreference.getreso__Admob_Fuulscreen_Ads_Unit_Id();

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        reso__Fullscreen_ADS = null;

        reso__Fullscreen_ADS.load(context, Interstitialad_ids, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                FirebaseAnalytics firebaseAnalytics;
                firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                interstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(AdValue adValue) {
                        double revenue = adValue.getValueMicros() / 1_000_000.0;
                        String currency = adValue.getCurrencyCode();
                        Bundle adRevenueParams = new Bundle();
                        adRevenueParams.putString(FirebaseAnalytics.Param.AD_PLATFORM, "Google Ad Manager");
                        adRevenueParams.putString(FirebaseAnalytics.Param.CURRENCY, currency);
                        adRevenueParams.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, adRevenueParams);
                    }
                });

                reso__Is_Ads_Procurement_Enabled = true;
                if (AppControlPreference.getreso__Ads_One_By_One_Ids()) {
                    if (AppControlPreference.getreso__Admob_Interstitial_Ads_List() != null && AppControlPreference.getreso__Admob_Interstitial_Ads_List().size() != 0 && AppControlPreference.getreso__Admob_Interstitial_Ads_List().size() == reso__Fullscreen_ADS_Pos) {
                        reso__Fullscreen_ADS_Pos = 0;
                    }
                } else {
                    reso__Fullscreen_ADS_Pos = 0;
                }

                InterstitialAdScreen.reso__Fullscreen_ADS = interstitialAd;
                if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                    LoadingDialogView.reso__Hide_Ads_Loading_Dialog();

                    if (AppControllerManege.reso__Is_Screen_Check_Enabled) {
                        AppControllerManege.reso__Is_Screen_Check_Enabled = false;

                        InterstitialAdScreen.reso__Fullscreen_ADS.show((Activity) context);
                        InterstitialAdScreen.reso__Ads_Show_Check(false);
                    }

                }
                reso__On_Content_Interstail_Only(context);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                reso__Admob_Fullscreen_ADS_Load(context);

            }
        });
    }

    public static void reso__On_Content_Listener(Context context) {
        reso__Fullscreen_ADS.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                reso__Ads_Show_Check(true);
                reso__On_Compete_Ads_Callback.onCompeteAds(true);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
            }

            @Override
            public void onAdImpression() {
            }

            @Override
            public void onAdShowedFullScreenContent() {
            }
        });
    }

    public static void reso__On_Content_Interstail_Only(Context context) {
        reso__Fullscreen_ADS.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdDismissedFullScreenContent() {


                reso__Ads_Show_Check(true);
                reso__On_Compete_Ads_Callback.onCompeteAds(true);


            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
            }

            @Override
            public void onAdImpression() {
            }

            @Override
            public void onAdShowedFullScreenContent() {
            }
        });
    }

    public interface OnCompeteAds {
        void onCompeteAds(boolean b);
    }

}
