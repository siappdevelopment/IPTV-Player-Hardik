package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;


import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen.reso__Ads_Show_Check;
import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.QuizAdManege.AppOpenQuizManege;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

import java.util.Random;

public class AppOpenAdView {

    public static OnCompleteAds reso__on_Complete_Ads;
    public static boolean reso__Ads_Loading_Check = true;
    public static String reso__Admob_App_Starting_Ids = "";
    public static int reso__App_Starting_Array_Index_Ids = 0;
    public static boolean reso__App_Starting_Array_Pos_Ids_Fail = false;
    public static AppOpenAd reso__App_Starting_Ids;
    public static AppOpenAd.AppOpenAdLoadCallback reso__Load_Call_Back;
    public static boolean reso__Is_Ad_Showing = true;
    public static Dialog reso__App_Open_Dialog;
    public static LinearLayout reso__LL_Continue;
    public static RelativeLayout reso__RL_Qureka;
    public static Dialog reso__Dialog;
    private static InterstitialAd reso__Fullscreen_ADS;

    public static void reso__App_Starting_Random_Ids() {
        try {
            if (AppControlPreference.getreso__Admob_App_Starting_Ids_List() != null && AppControlPreference.getreso__Admob_App_Starting_Ids_List().size() != 0 && AppControlPreference.getreso__Admob_App_Starting_Ids_List().size() != reso__App_Starting_Array_Index_Ids) {
                reso__App_Starting_Array_Pos_Ids_Fail = true;
                reso__Admob_App_Starting_Ids = AppControlPreference.getreso__Admob_App_Starting_Ids_List().get(reso__App_Starting_Array_Index_Ids);
                AppControlPreference.setreso__Key_App_Starting_ADS(reso__Admob_App_Starting_Ids);
                reso__App_Starting_Array_Index_Ids = reso__App_Starting_Array_Index_Ids + 1;
            } else {
                reso__App_Starting_Array_Index_Ids = 0;
                reso__App_Starting_Array_Pos_Ids_Fail = false;
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reso__AppWithoutDisplay(Activity context, OnCompleteAds onFinishAd, boolean... doShowAds) {
        reso__on_Complete_Ads = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__on_Complete_Ads.onCompleteAds(true);
            return;
        }
        reso__App_Starting_Ads_Load(context);

    }

    public static void reso__App_Starting_Display(Activity context, OnCompleteAds onFinishAd, boolean... doShowAds) {
        reso__on_Complete_Ads = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__on_Complete_Ads.onCompleteAds(true);
            return;
        }
        reso__App_Starting_Ads_Load(context);

    }

    public static void reso___Load_App_Starting_Display(Activity context, OnCompleteAds onFinishAd, boolean... doShowAds) {
        reso__on_Complete_Ads = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__on_Complete_Ads.onCompleteAds(true);
            return;
        }

        reso__Load_App_Open_Ads(context);

    }

    private static void reso__Admob_Fullscreen_ADS(Activity context) {

        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
            LoadingDialogView.reso__Show_Ads_Loading_Dialog(context);
        }
        InterstitialAdScreen.reso__Fullscreen_ADS_Random_Ids();
        if (!InterstitialAdScreen.reso__Fullscreen_ADS_Failed) {
            reso__Ads_Loading_Check = true;

            if (AppControlPreference.getreso__Splash_Ads_Type().equals("Interstitial")) {
                if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
                    AppOpenAdView.reso__App_Starting_Ads_Load(context);
                } else {
                    if (AppControlPreference.getreso__is_Quiz_App_Open_Display()) {
                        if (AppControlPreference.getreso__Is_Quiz_Display()) {
                            if (AppControlPreference.getreso__Splash_Ads_Type().equals("Appopen")) {
                                reso__on_Complete_Ads.onCompleteAds(true);
                            } else {
                                reso__get_Quiz_Full_Ads(context, true);
                            }
                        } else {
                            reso__on_Complete_Ads.onCompleteAds(true);
                        }
                    } else {
                        reso__on_Complete_Ads.onCompleteAds(true);
                    }
                }
            } else {
                LoadingDialogView.reso__Hide_Ads_Loading_Dialog();
                if (AppControlPreference.getreso__is_Quiz_App_Open_Display()) {
                    if (AppControlPreference.getreso__Is_Quiz_Display()) {
                        if (AppControlPreference.getreso__Splash_Quiz_Display()) {
                            AppOpenQuizManege(context, reso__on_Complete_Ads);
                            reso__Ads_Show_Check(false);
                        } else {
                            AppOpenAdView.reso__get_Quiz_App_Open(context, reso__on_Complete_Ads, false);
                        }
                    } else {
                        reso__on_Complete_Ads.onCompleteAds(true);
                    }
                } else {
                    reso__on_Complete_Ads.onCompleteAds(true);
                }
            }
            return;
        }

        String Interstitialad_ids = AppControlPreference.getreso__Admob_Fuulscreen_Ads_Unit_Id();

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        reso__Fullscreen_ADS = null;

        InterstitialAd.load(context, Interstitialad_ids, adRequest,
                new InterstitialAdLoadCallback() {
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

                        reso__Ads_Loading_Check = true;
                        if (AppControlPreference.getreso__Ads_One_By_One_Ids()) {
                            if (AppControlPreference.getreso__Admob_Interstitial_Ads_List() != null && AppControlPreference.getreso__Admob_Interstitial_Ads_List().size() != 0 && AppControlPreference.getreso__Admob_Interstitial_Ads_List().size() == InterstitialAdScreen.reso__Fullscreen_ADS_Pos) {
                                InterstitialAdScreen.reso__Fullscreen_ADS_Pos = 0;
                            }
                        } else {
                            InterstitialAdScreen.reso__Fullscreen_ADS_Pos = 0;
                        }

                        AppOpenAdView.reso__Fullscreen_ADS = interstitialAd;
                        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                            LoadingDialogView.reso__Hide_Ads_Loading_Dialog();
                            AppOpenAdView.reso__Fullscreen_ADS.show((Activity) context);
                            reso__Ads_Show_Check(false);
                        } else {

                            AppOpenAdView.reso__Fullscreen_ADS.show((Activity) context);
                            reso__Ads_Show_Check(false);
                        }
                        reso__on_Ads_Load_Listener(context);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        reso__Admob_Fullscreen_ADS(context);


                    }
                });
    }


    public static void reso__Fullscreen_ADS_Display(Activity context, OnCompleteAds onFinishAd, boolean... doShowAds) {
        reso__on_Complete_Ads = onFinishAd;
        if (AppControlPreference.getreso__Ads_Free()) {
            reso__on_Complete_Ads.onCompleteAds(true);
            return;
        }

        if (AppControlPreference.getreso__Ads_Display_Type().equals("admob")) {
            reso__Admob_Fullscreen_ADS(context);
        }

    }

    public static void reso__on_Ads_Load_Listener(Context context) {
        reso__Fullscreen_ADS.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                reso__Ads_Show_Check(true);
                reso__on_Complete_Ads.onCompleteAds(true);

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

    public static void reso__App_Starting_Ads_Load(Activity context) {

        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
            LoadingDialogView.reso__Show_Ads_Loading_Dialog(context);
        }

        AppOpenAdView.reso__App_Starting_Random_Ids();
        if (!AppOpenAdView.reso__App_Starting_Array_Pos_Ids_Fail) {

            if (AppControlPreference.getreso__Splash_Ads_Type().equals("Appopen")) {
                if (!AppControlPreference.getreso__Ads_Display_Type().equals("facebook")) {
                    reso__Admob_Fullscreen_ADS(context);
                }
            } else {
                if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                    LoadingDialogView.reso__Hide_Ads_Loading_Dialog();
                }

                if (AppControlPreference.getreso__is_Quiz_App_Open_Display()) {
                    if (AppControlPreference.getreso__Is_Quiz_Display()) {
                        if (AppControlPreference.getreso__Splash_Ads_Type().equals("Interstitial")) {
                            reso__get_Quiz_Full_Ads(context, true);
                        } else {
                            reso__on_Complete_Ads.onCompleteAds(true);
                        }
                    } else {
                        reso__on_Complete_Ads.onCompleteAds(true);
                    }
                } else {
                    reso__on_Complete_Ads.onCompleteAds(true);
                }
            }

            return;
        }

        try {

            String app_open_id = AppControlPreference.getreso__Key_App_Starting_ADS();
            AppOpenAdView.reso__Load_Call_Back =
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {

                            FirebaseAnalytics firebaseAnalytics;
                            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                            ad.setOnPaidEventListener(new OnPaidEventListener() {
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

                            AppOpenAdView.reso__App_Starting_Ids = ad;
                            AppOpenAdView.reso__Is_Ad_Showing = false;
                            if (AppControlPreference.getreso__Ads_One_By_One_Ids()) {
                                if (AppControlPreference.getreso__Admob_App_Starting_Ids_List() != null && AppControlPreference.getreso__Admob_App_Starting_Ids_List().size() != 0 && AppControlPreference.getreso__Admob_App_Starting_Ids_List().size() == AppOpenAdView.reso__App_Starting_Array_Index_Ids) {
                                    AppOpenAdView.reso__App_Starting_Array_Index_Ids = 0;
                                }
                            } else {
                                AppOpenAdView.reso__App_Starting_Array_Index_Ids = 0;
                            }

                            if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                                LoadingDialogView.reso__Hide_Ads_Loading_Dialog();
                            }

                            FullScreenContentCallback fullScreenContentCallback =
                                    new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            reso__Is_Ad_Showing = false;
                                            AppOpenAdView.reso__App_Starting_Ids = null;
                                            reso__on_Complete_Ads.onCompleteAds(true);

                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(AdError adError) {

                                            AppOpenAdView.reso__App_Starting_Ids = null;
                                            reso__on_Complete_Ads.onCompleteAds(true);
                                        }

                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            reso__Is_Ad_Showing = true;

                                        }
                                    };

                            reso__App_Starting_Ids.setFullScreenContentCallback(fullScreenContentCallback);
                            reso__App_Starting_Ids.show(context);

                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {

                            AppOpenAdView.reso__App_Starting_Ids = null;
                            AppOpenAdView.reso__Is_Ad_Showing = false;

                            reso__App_Starting_Ads_Load(context);
                        }
                    };

            AdRequest adRequest = new AdRequest.Builder()
                    .build();

            AppOpenAd.load(context, app_open_id, adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, AppOpenAdView.reso__Load_Call_Back);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void reso__Load_App_Open_Ads(Activity context) {

        LoadingDialogView.reso__Show_Ads_Loading_Dialog(context);

        AppOpenAdView.reso__App_Starting_Random_Ids();
        if (!AppOpenAdView.reso__App_Starting_Array_Pos_Ids_Fail) {

            if (AppControlPreference.getreso__Splash_Ads_Type().equals("Appopen")) {
                if (!AppControlPreference.getreso__Ads_Display_Type().equals("facebook")) {
                    reso__Admob_Fullscreen_ADS(context);
                }
            } else {
                LoadingDialogView.reso__Hide_Ads_Loading_Dialog();

                if (AppControlPreference.getreso__is_Quiz_App_Open_Display()) {
                    if (AppControlPreference.getreso__Is_Quiz_Display()) {
                        if (AppControlPreference.getreso__Splash_Ads_Type().equals("Interstitial")) {
                            reso__get_Quiz_Full_Ads(context, true);
                        } else {
                            reso__on_Complete_Ads.onCompleteAds(true);
                        }
                    } else {
                        reso__on_Complete_Ads.onCompleteAds(true);
                    }
                } else {
                    reso__on_Complete_Ads.onCompleteAds(true);
                }
            }

            return;
        }

        try {

            String app_open_id = AppControlPreference.getreso__Key_App_Starting_ADS();
            AppOpenAdView.reso__Load_Call_Back =
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {

                            FirebaseAnalytics firebaseAnalytics;
                            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                            ad.setOnPaidEventListener(new OnPaidEventListener() {
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

                            AppOpenAdView.reso__App_Starting_Ids = ad;
                            AppOpenAdView.reso__Is_Ad_Showing = false;
                            if (AppControlPreference.getreso__Ads_One_By_One_Ids()) {
                                if (AppControlPreference.getreso__Admob_App_Starting_Ids_List() != null && AppControlPreference.getreso__Admob_App_Starting_Ids_List().size() != 0 && AppControlPreference.getreso__Admob_App_Starting_Ids_List().size() == AppOpenAdView.reso__App_Starting_Array_Index_Ids) {
                                    AppOpenAdView.reso__App_Starting_Array_Index_Ids = 0;
                                }
                            } else {
                                AppOpenAdView.reso__App_Starting_Array_Index_Ids = 0;
                            }

                            LoadingDialogView.reso__Hide_Ads_Loading_Dialog();

                            FullScreenContentCallback fullScreenContentCallback =
                                    new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            reso__Is_Ad_Showing = false;
                                            AppOpenAdView.reso__App_Starting_Ids = null;
                                            reso__on_Complete_Ads.onCompleteAds(true);


                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(AdError adError) {


                                            AppOpenAdView.reso__App_Starting_Ids = null;
                                            reso__on_Complete_Ads.onCompleteAds(true);
                                        }

                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            reso__Is_Ad_Showing = true;

                                        }
                                    };

                            reso__App_Starting_Ids.setFullScreenContentCallback(fullScreenContentCallback);
                            reso__App_Starting_Ids.show(context);

                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {

                            AppOpenAdView.reso__App_Starting_Ids = null;
                            AppOpenAdView.reso__Is_Ad_Showing = false;

                            reso__App_Starting_Ads_Load(context);
                        }
                    };

            AdRequest adRequest = new AdRequest.Builder()
                    .build();

            AppOpenAd.load(context, app_open_id, adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, AppOpenAdView.reso__Load_Call_Back);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void reso__get_Quiz_App_Open(Context activity, OnCompleteAds onFinishAds, Boolean chekdata) {
        if (reso__App_Open_Dialog != null)
            if (reso__App_Open_Dialog.isShowing())
                reso__App_Open_Dialog.dismiss();
        reso__App_Open_Dialog = new Dialog(activity);
        reso__App_Open_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reso__App_Open_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reso__App_Open_Dialog.setContentView(R.layout.reso_quiz_dialog_appopen_layout);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        final int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        reso__App_Open_Dialog.getWindow().setLayout(width, height);
        reso__App_Open_Dialog.setCancelable(false);
        reso__App_Open_Dialog.show();
        reso__LL_Continue = reso__App_Open_Dialog.findViewById(R.id.reso__ll_continue);
        reso__RL_Qureka = reso__App_Open_Dialog.findViewById(R.id.reso__rl_qureka);

        ImageView inter_quiz = reso__App_Open_Dialog.findViewById(R.id.reso__inter_quiz);

        int random = new Random().nextInt(3);
        if (random == 1) {
            if (activity instanceof Activity && !((Activity) activity).isFinishing() && !((Activity) activity).isDestroyed()) {
                Glide.with(activity).load(R.drawable.reso_app_open_img_1).into(inter_quiz);

            }
        } else if (random == 2) {
            if (activity instanceof Activity && !((Activity) activity).isFinishing() && !((Activity) activity).isDestroyed()) {
                Glide.with(activity).load(R.drawable.reso_app_open_img_2).into(inter_quiz);

            }
        } else {
            if (activity instanceof Activity && !((Activity) activity).isFinishing() && !((Activity) activity).isDestroyed()) {
                Glide.with(activity).load(R.drawable.reso_app_open_img_3).into(inter_quiz);
            }
        }

        AppOpenAdView.reso__Is_Ad_Showing = true;

        TextView btn_playnow = reso__App_Open_Dialog.findViewById(R.id.reso__btn_playnow);

        btn_playnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizAdManege.EventQuizManege(activity);
            }
        });

        reso__LL_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppOpenAdView.reso__Is_Ad_Showing = false;
                reso__App_Open_Dialog.dismiss();
                onFinishAds.onCompleteAds(true);
                if (chekdata) {
                    onFinishAds.onCompleteAds(true);
                }
            }
        });

        reso__RL_Qureka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizAdManege.EventQuizManege(activity);
            }
        });

        reso__App_Open_Dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    AppOpenAdView.reso__Is_Ad_Showing = false;
                    reso__App_Open_Dialog.dismiss();
                    onFinishAds.onCompleteAds(true);
                    if (chekdata) {
                        onFinishAds.onCompleteAds(true);
                    }
                }
                return true;
            }
        });


    }

    @SuppressLint("SetTextI18n")
    public static void reso__get_Quiz_Full_Ads(Context context, boolean isClick) {

        try {
            if (reso__Dialog != null)
                if (reso__Dialog.isShowing())
                    return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        reso__Dialog = new Dialog(context, android.R.style.Theme_Light);
        reso__Dialog.setContentView(R.layout.reso_custom_quiz_inter);
        reso__Dialog.setCancelable(false);
        CardView cardView = (CardView) reso__Dialog.findViewById(R.id.reso__imgClose);
        LinearLayout linearLayout2 = (LinearLayout) reso__Dialog.findViewById(R.id.reso__llPersonalAdCenter);
        ImageView imageView = (ImageView) reso__Dialog.findViewById(R.id.reso__iv_native_icon);
        TextView textView = (TextView) reso__Dialog.findViewById(R.id.reso__tv_native_title);
        TextView textView2 = (TextView) reso__Dialog.findViewById(R.id.reso__tv_native_des);
        TextView native_ad_call_to_action = (TextView) reso__Dialog.findViewById(R.id.reso__native_ad_call_to_action);
        TextView tvTimer = (TextView) reso__Dialog.findViewById(R.id.reso__tvTimer);
        LinearLayout adCloseBtn = (LinearLayout) reso__Dialog.findViewById(R.id.reso__adCloseBtn);

        tvTimer.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
        adCloseBtn.setVisibility(View.VISIBLE);

        ImageView done1 = reso__Dialog.findViewById(R.id.reso__done1);
        TextView tvTimer1 = reso__Dialog.findViewById(R.id.reso__tvTimer1);

        if (isClick) {
            tvTimer1.setVisibility(View.GONE);
            done1.setVisibility(View.VISIBLE);
        }

        AppOpenAdView.reso__Is_Ad_Showing = true;

        int random = new Random().nextInt(3);

        if (random == 1) {
            textView2.setText(AppControlPreference.getreso__Quiz_Interstitial_Title_List().get(0));
            textView.setText(AppControlPreference.getreso__Quiz_Interstitial_Description_List().get(0));
            if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                Glide.with(context).load(R.drawable.reso_interstitial_img_1).into(imageView);

            }
        } else if (random == 2) {
            textView2.setText(AppControlPreference.getreso__Quiz_Interstitial_Title_List().get(1));
            textView.setText(AppControlPreference.getreso__Quiz_Interstitial_Description_List().get(1));
            if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                Glide.with(context).load(R.drawable.reso_interstitial_img_2).into(imageView);

            }
        } else {
            textView2.setText(AppControlPreference.getreso__Quiz_Interstitial_Title_List().get(2));
            textView.setText(AppControlPreference.getreso__Quiz_Interstitial_Description_List().get(2));
            if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                Glide.with(context).load(R.drawable.reso_interstitial_img_3).into(imageView);

            }
        }


        @SuppressLint("CutPasteId") View findViewById = reso__Dialog.findViewById(R.id.reso__iv_native_icon);
        Animation loadAnimation = AnimationUtils.loadAnimation(findViewById.getContext(), R.anim.reso_zoom_qureka);
        loadAnimation.setFillAfter(true);
        findViewById.startAnimation(loadAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.5f, 1, 0.0f);
        long j2 = (long) com.facebook.ads.AdError.SERVER_ERROR_CODE;
        translateAnimation.setDuration(j2);
        translateAnimation.setFillAfter(true);
        translateAnimation.setFillEnabled(true);
        findViewById.startAnimation(translateAnimation);
        View findViewById2 = reso__Dialog.findViewById(R.id.reso__cvTopAd);
        TranslateAnimation translateAnimation2 = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.5f, 1, 0.0f);
        translateAnimation2.setDuration(j2);
        translateAnimation2.setFillAfter(true);
        translateAnimation2.setFillEnabled(true);
        findViewById2.startAnimation(translateAnimation2);

        TextView txt_adCloseBtn_1 = reso__Dialog.findViewById(R.id.txt_adCloseBtn_1);
        TextView txt_adCloseBtn_2 = reso__Dialog.findViewById(R.id.txt_adCloseBtn_2);
        RelativeLayout relative_main = reso__Dialog.findViewById(R.id.relative_main);

        adCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppOpenAdView.reso__Is_Ad_Showing = false;
                reso__Ads_Show_Check(true);
                reso__Dialog.dismiss();
                if (isClick) {
                    reso__on_Complete_Ads.onCompleteAds(true);
                }
            }
        });

        try {
            native_ad_call_to_action.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            native_ad_call_to_action.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));
            txt_adCloseBtn_1.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Color()));
            txt_adCloseBtn_2.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Color()));
            tvTimer1.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Color()));

            ((TextView) reso__Dialog.findViewById(R.id.txt_tag_AD)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            ((TextView) reso__Dialog.findViewById(R.id.txt_tag_AD)).setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

            relative_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_BG_Color())));
            findViewById2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_BG_Color())));
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        native_ad_call_to_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reso__Ads_Show_Check(false);
                QuizAdManege.EventQuizManege(context);
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppOpenAdView.reso__Is_Ad_Showing = false;
                reso__Dialog.dismiss();
                if (isClick) {
                    reso__on_Complete_Ads.onCompleteAds(true);
                }
            }
        });

        reso__Dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    AppOpenAdView.reso__Is_Ad_Showing = false;
                    reso__Dialog.dismiss();
                    if (isClick) {
                        reso__on_Complete_Ads.onCompleteAds(true);
                    }
                }
                return true;
            }
        });

        reso__Dialog.show();

    }

    public interface OnCompleteAds {
        void onCompleteAds(boolean b);
    }

}
