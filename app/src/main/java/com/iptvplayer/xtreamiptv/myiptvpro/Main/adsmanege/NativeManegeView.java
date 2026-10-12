package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;


import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen.reso__Ads_Show_Check;
import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen.reso__On_Compete_Ads_Callback;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.WindowCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@SuppressWarnings("all")
public class NativeManegeView {

    public static int reso__Array_Pos = 0;
    public static boolean reso__Is_Fail_Array_Ids = false;
    public static Context reso__App_Context;
    public static int reso__Native_By_Page = 0;
    public static boolean reso__Is_Dialog_Enabled = true;
    public static String reso__Native_Ads_Type = "";
    public static NativeAd reso__Admob_NativeAd;
    public static String reso__Ads_Show_Type = "small";
    private static boolean isAdHandled = false;

    public static void reso__Display_Bottom_NativeAd(Context context, final LinearLayout linearLayout, final LinearLayout lnr_view, Boolean banner_flag) {

        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
            reso__Load_Admob_Big_NativeAd(context, linearLayout, lnr_view, banner_flag);
            return;
        }

        if (reso__Admob_NativeAd != null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            NativeAdView adView = (NativeAdView) inflater.inflate(R.layout.reso_ads_mid_native_load, null);
            lnr_view.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            reso__Populate_Native_AdView_Fullscreen(reso__Admob_NativeAd, adView, false);
            linearLayout.removeAllViews();
            linearLayout.addView(adView);
            ListViewAdapterView.reso__admob_Native_Hashmap.put(ListViewAdapterView.reso__pos, adView);
        } else {
            if (AppControlPreference.getreso__Is_Quiz_Display() && AppControlPreference.getreso__Is_Quiz_Native()) {
                linearLayout.setVisibility(View.VISIBLE);
                lnr_view.setVisibility(View.GONE);
                QuizAdManege.reso__quiz_Native_Ads(context, linearLayout, banner_flag);
            } else {
                linearLayout.setVisibility(View.GONE);
                lnr_view.setVisibility(View.GONE);
            }

        }
        reso__Load_Admob_Big_NativeAd(context, linearLayout, lnr_view, banner_flag);
    }

    public static void reso__Display_Big_NativeAd(final Context context, final LinearLayout linearLayout, final LinearLayout lnr_view, String ads_type) {
        reso__Ads_Show_Type = ads_type;
        NativeManegeView.reso__App_Context = context;
        reso__Array_Pos = 0;
        if (AppControlPreference.getreso__Ads_Free()) {
            lnr_view.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            return;
        }

        if (reso__Native_By_Page == AppControlPreference.getreso__Native_By_Page()) {
            reso__Native_By_Page = 0;


            if (ads_type.equals("big")) {
                reso__Load_Big_NativeAd(context, linearLayout, lnr_view, false);
            } else {
                reso__Display_Bottom_NativeAd(context, linearLayout, lnr_view, true);
            }


        } else {
            reso__Native_By_Page++;
            lnr_view.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }
    }

    public static void reso__Load_Big_NativeAd(Context context, final LinearLayout linearLayout, final LinearLayout lnr_view, Boolean banner_flag) {

        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {

            if (reso__Is_Dialog_Enabled) {
                reso__Is_Dialog_Enabled = false;
                reso__Load_Admob_Big_NativeAd(context, linearLayout, lnr_view, banner_flag);
            }
            return;
        }

        if (reso__Admob_NativeAd != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            NativeAdView adView;
            if (reso__Ads_Show_Type.equals("big")) {
                adView = (NativeAdView) inflater.inflate(R.layout.reso_ads_big_native_load, null);
            } else {
                adView = (NativeAdView) inflater.inflate(R.layout.reso_ads_mid_native_load, null);
            }

            lnr_view.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            reso__Populate_Native_AdView_Fullscreen(reso__Admob_NativeAd, adView, false);
            linearLayout.removeAllViews();
            linearLayout.addView(adView);
            ListViewAdapterView.reso__admob_Native_Hashmap.put(ListViewAdapterView.reso__pos, adView);
        } else {

            if (AppControlPreference.getreso__Is_Quiz_Display() && AppControlPreference.getreso__Is_Quiz_Native()) {
                linearLayout.setVisibility(View.VISIBLE);
                lnr_view.setVisibility(View.GONE);
                QuizAdManege.reso__quiz_Native_Ads(context, linearLayout, banner_flag);
            } else {
                linearLayout.setVisibility(View.GONE);
                lnr_view.setVisibility(View.GONE);
            }


        }
        if (reso__Is_Dialog_Enabled) {
            reso__Is_Dialog_Enabled = false;
            reso__Load_Admob_Big_NativeAd(context, linearLayout, lnr_view, banner_flag);
        }
    }

    public static void reso__Randomize_Big_NativeAd_Ids() {
        try {
            if (AppControlPreference.getreso__Admob_Native_Full_Ads_List() != null && AppControlPreference.getreso__Admob_Native_Full_Ads_List().size() != 0 && AppControlPreference.getreso__Admob_Native_Full_Ads_List().size() != reso__Array_Pos) {
                reso__Is_Fail_Array_Ids = true;
                AppControlPreference.setreso__Admob_Native_Unit_Id(AppControlPreference.getreso__Admob_Native_Full_Ads_List().get(reso__Array_Pos));
                reso__Array_Pos = reso__Array_Pos + 1;
            } else {
                reso__Is_Fail_Array_Ids = false;
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reso__Load_Admob_Big_NativeAd(final Context context, final LinearLayout linearLayout, final LinearLayout lnr_view, boolean banner_flag) {

        reso__Randomize_Big_NativeAd_Ids();
        if (!reso__Is_Fail_Array_Ids) {
            reso__Is_Dialog_Enabled = true;

            if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                if (AppControlPreference.getreso__Is_Quiz_Display() && AppControlPreference.getreso__Is_Quiz_Native()) {
                    linearLayout.setVisibility(View.VISIBLE);
                    lnr_view.setVisibility(View.GONE);
                    QuizAdManege.reso__quiz_Native_Ads(context, linearLayout, banner_flag);
                } else {
                    linearLayout.setVisibility(View.GONE);
                    lnr_view.setVisibility(View.GONE);
                }
            } else {
                if (AppControlPreference.getreso__Is_Quiz_Display() && AppControlPreference.getreso__Is_Quiz_Native()) {
                    QuizAdManege.reso__quiz_Native_Ads(context, linearLayout, banner_flag);
                }
            }

            return;
        }

        reso__Native_Ads_Type = "";
        reso__Native_Ads_Type = AppControlPreference.getreso__Admob_Native_Unit_Id();
        AdLoader.Builder builder = new AdLoader.Builder(context, reso__Native_Ads_Type)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {

                        FirebaseAnalytics firebaseAnalytics;
                        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                        nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
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

                        reso__Is_Dialog_Enabled = true;
                        if (reso__Admob_NativeAd != null) {
                            reso__Admob_NativeAd = null;
                        }

                        reso__Admob_NativeAd = nativeAd;
                        if (AppControlPreference.getreso__Ads_One_By_One_Ids()) {
                            if (AppControlPreference.getreso__Admob_Native_Full_Ads_List() != null && AppControlPreference.getreso__Admob_Native_Full_Ads_List().size() != 0 && AppControlPreference.getreso__Admob_Native_Full_Ads_List().size() == reso__Array_Pos) {
                                reso__Array_Pos = 0;
                            }
                        } else {
                            reso__Array_Pos = 0;
                        }


                        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                            if (reso__Admob_NativeAd != null) {

                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                NativeAdView adView;
                                if (reso__Ads_Show_Type.equals("big")) {
                                    adView = (NativeAdView) inflater.inflate(R.layout.reso_ads_big_native_load, null);
                                } else {
                                    adView = (NativeAdView) inflater.inflate(R.layout.reso_ads_mid_native_load, null);
                                }

                                lnr_view.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                                reso__Populate_Native_AdView_Fullscreen(reso__Admob_NativeAd, adView, false);

                                linearLayout.removeAllViews();
                                linearLayout.addView(adView);
                                ListViewAdapterView.reso__admob_Native_Hashmap.put(ListViewAdapterView.reso__pos, adView);
                            }
                        }
                    }
                });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        if (reso__Admob_NativeAd != null) {
                            reso__Admob_NativeAd = null;
                        }


                        reso__Load_Admob_Big_NativeAd(context, linearLayout, lnr_view, banner_flag);

                    }

                    @Override
                    public void onAdClicked() {
                        AppControllerManege.reso__is_Fast_Start = true;
                        reso__Ads_Show_Check(false);
                    }
                })
                .build();

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        adLoader.loadAd(adRequest);
    }

    public static void reso__Populate_Native_AdView_Fullscreen(NativeAd nativeAd, NativeAdView adView, boolean flag) {
        com.google.android.gms.ads.nativead.MediaView mediaView = adView.findViewById(R.id.reso__media_view);

        adView.setMediaView(mediaView);
        adView.setHeadlineView(adView.findViewById(R.id.reso__top_line));
        adView.setBodyView(adView.findViewById(R.id.reso__body));
        adView.setCallToActionView(adView.findViewById(R.id.reso__install_button));
        adView.setIconView(adView.findViewById(R.id.reso__logo));
        adView.setPriceView(adView.findViewById(R.id.reso__fees));
        adView.setStarRatingView(adView.findViewById(R.id.reso__rate_stars));
        adView.setStoreView(adView.findViewById(R.id.reso__store));
        adView.setAdvertiserView(adView.findViewById(R.id.reso__advertisment));
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());


        try {
            ((TextView) adView.getCallToActionView()).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            ((TextView) adView.getCallToActionView()).setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

            ((TextView) adView.findViewById(R.id.txt_tag_AD)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            ((TextView) adView.findViewById(R.id.txt_tag_AD)).setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

            adView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_BG_Color())));
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.GONE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            if (flag) {
                adView.getStarRatingView().setVisibility(View.GONE);
            } else {
                adView.getStarRatingView().setVisibility(View.GONE);
            }
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            if (flag) {
                adView.getStarRatingView().setVisibility(View.GONE);
            } else {
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }


        if (nativeAd.getAdvertiser() == null) {
            if (flag) {
                adView.getAdvertiserView().setVisibility(View.GONE);
            } else {
                adView.getAdvertiserView().setVisibility(View.GONE);
            }
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            if (flag) {
                adView.getAdvertiserView().setVisibility(View.GONE);
            } else {
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        }

        adView.setNativeAd(nativeAd);

        MediaContent vc = nativeAd.getMediaContent();

        if (vc.hasVideoContent()) {
            nativeAd.getMediaContent().getVideoController().setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        } else {
            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    public static void showNativeInter(final Activity activity) {
        showNativeInter(activity, reso__Admob_NativeAd);
    }

    public static void showNativeInter(final Activity activity, NativeAd nativeAd) {
        try {
            if (nativeAd == null) {
                if (reso__On_Compete_Ads_Callback != null) {
                    reso__On_Compete_Ads_Callback.onCompeteAds(true);
                }
                return;
            }
            final View overlay = activity.getLayoutInflater()
                    .inflate(R.layout.layout_native_inter_container, null, false);

            final FrameLayout frameLayout = overlay.findViewById(R.id.viewGroup);
            final View btnClose = overlay.findViewById(R.id.ad_close);
            final TextView tvTimer = overlay.findViewById(R.id.ad_timer);

            final ViewGroup parentView = activity.findViewById(android.R.id.content);
            WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), true);

            View oldView = parentView.findViewWithTag("native_full_view");
            if (oldView != null) {
                parentView.removeView(oldView);
            }

            overlay.setTag("native_full_view");
            parentView.addView(overlay, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            overlay.requestApplyInsets();

            overlay.setVisibility(View.VISIBLE);
            tvTimer.setVisibility(View.INVISIBLE);
            btnClose.setOnClickListener(v -> {
                if (reso__On_Compete_Ads_Callback != null) {
                    reso__On_Compete_Ads_Callback.onCompeteAds(true);
                }
                parentView.removeView(overlay);
            });
            showNative(activity, frameLayout, tvTimer, btnClose, parentView, overlay, nativeAd);

        } catch (Exception e) {
        }
    }

    public static void PopulateUnifiedFullNativeAdView(NativeAd nativeAd, NativeAdView adView, boolean flag) {
        com.google.android.gms.ads.nativead.MediaView mediaView = adView.findViewById(R.id.ad_media);

        adView.setMediaView(mediaView);
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());


        try {
            ((TextView) adView.getCallToActionView()).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            ((TextView) adView.getCallToActionView()).setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

            ((TextView) adView.findViewById(R.id.txt_tag_AD)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            ((TextView) adView.findViewById(R.id.txt_tag_AD)).setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

            adView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_BG_Color())));
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

        MediaContent vc = nativeAd.getMediaContent();

        if (vc.hasVideoContent()) {
            nativeAd.getMediaContent().getVideoController().setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        } else {
            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    public static void showNative(Context context, final FrameLayout linearLayout, TextView closeText, View closeButton, ViewGroup decorView, View overlay, NativeAd nativeAd) {
        LayoutInflater inflater = LayoutInflater.from(context);
        NativeAdView adView = (NativeAdView) inflater.inflate(R.layout.admob_big_native_ad, linearLayout, false);
        linearLayout.setVisibility(View.VISIBLE);
        PopulateUnifiedFullNativeAdView(nativeAd, adView, false);
        CloseTimer(closeText, closeButton);
        linearLayout.removeAllViews();
        linearLayout.addView(adView);
        ListViewAdapterView.reso__admob_Native_Hashmap.put(ListViewAdapterView.reso__pos, adView);


    }

    public static void CloseTimer(TextView closeText, View closeButton) {


        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                closeButton.setVisibility(View.INVISIBLE);
                closeText.setVisibility(View.VISIBLE);
                closeText.setText(String.valueOf(sec));
            }

            public void onFinish() {
                closeText.setVisibility(View.INVISIBLE);
                closeButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public static void reso__Admob_Native_Full_ADS_Display(
            Activity activity,
            InterstitialAdScreen.OnCompeteAds onFinishAd
    ) {

        reso__On_Compete_Ads_Callback = onFinishAd;
        isAdHandled = false;

        if (AppControlPreference.getreso__Ads_Free()) {
            completeAdFlow();
            return;
        }

        if (!AppControlPreference.getreso__Ads_Type().equals("Load")
                && reso__Admob_NativeAd != null) {

            if (AppControlPreference.getNative_Load_All_Time()) {

                loadNative(activity, true);

            } else {

                if (!activity.isFinishing() && !activity.isDestroyed()) {

                    isAdHandled = true;

                    showNativeInter(activity, reso__Admob_NativeAd);
                }
            }

            return;
        }

        // Fresh Load
        loadNative(activity, true);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (!isAdHandled && reso__Admob_NativeAd == null) {

                try {
                    LoadingDialogView.reso__Hide_Ads_Loading_Dialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                completeAdFlow();
            }

        }, 3000);
    }

    public static void loadNative(final Activity context) {
        loadNative(context, false);
    }

    public static void loadNative(final Activity context, final boolean isFullDisplay) {


        reso__Randomize_Big_NativeAd_Ids();


        reso__Native_Ads_Type =
                AppControlPreference.getreso__Admob_Native_Unit_Id();

        if (reso__Native_Ads_Type == null
                || reso__Native_Ads_Type.isEmpty()) {

            completeAdFlow();
            return;
        }

        if (isFullDisplay) {

            if (!context.isFinishing() && !context.isDestroyed()) {

                try {
                    LoadingDialogView.reso__Show_Ads_Loading_Dialog(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        AdLoader.Builder builder =
                new AdLoader.Builder(context, reso__Native_Ads_Type)

                        .forNativeAd(nativeAd -> {


                            try {
                                LoadingDialogView.reso__Hide_Ads_Loading_Dialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (nativeAd == null) {

                                completeAdFlow();
                                return;
                            }

                            if (reso__Admob_NativeAd != null) {
                                reso__Admob_NativeAd.destroy();
                            }

                            reso__Admob_NativeAd = nativeAd;

                            // Paid Event
                            FirebaseAnalytics firebaseAnalytics =
                                    FirebaseAnalytics.getInstance(context);

                            nativeAd.setOnPaidEventListener(adValue -> {

                                double revenue =
                                        adValue.getValueMicros() / 1_000_000.0;

                                String currency =
                                        adValue.getCurrencyCode();

                                Bundle bundle = new Bundle();

                                bundle.putString(
                                        FirebaseAnalytics.Param.AD_PLATFORM,
                                        "AdMob"
                                );

                                bundle.putString(
                                        FirebaseAnalytics.Param.CURRENCY,
                                        currency
                                );

                                bundle.putDouble(
                                        FirebaseAnalytics.Param.VALUE,
                                        revenue
                                );

                                firebaseAnalytics.logEvent(
                                        FirebaseAnalytics.Event.AD_IMPRESSION,
                                        bundle
                                );
                            });

                            // Show Full Native
                            if (isFullDisplay) {

                                if (!isAdHandled
                                        && !context.isFinishing()
                                        && !context.isDestroyed()) {

                                    isAdHandled = true;

                                    showNativeInter(context, nativeAd);
                                }
                            }

                            // Reset Array Position
                            if (AppControlPreference.getreso__Ads_One_By_One_Ids()) {

                                if (AppControlPreference
                                        .getreso__Admob_Native_Full_Ads_List() != null
                                        &&
                                        AppControlPreference
                                                .getreso__Admob_Native_Full_Ads_List()
                                                .size() == reso__Array_Pos) {

                                    reso__Array_Pos = 0;
                                }

                            } else {
                                reso__Array_Pos = 0;
                            }
                        });

        AdLoader adLoader =
                builder.withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(
                            @NonNull LoadAdError adError
                    ) {

                        Log.e("NativeAds",
                                "Ad Failed : " + adError.getMessage());

                        try {
                            LoadingDialogView.reso__Hide_Ads_Loading_Dialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (reso__Admob_NativeAd != null) {

                            reso__Admob_NativeAd.destroy();
                            reso__Admob_NativeAd = null;
                        }

                        completeAdFlow();
                    }

                    @Override
                    public void onAdClicked() {


                        try {
                            LoadingDialogView.reso__Hide_Ads_Loading_Dialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        AppControllerManege.reso__is_Fast_Start = true;

                        reso__Ads_Show_Check(false);
                    }

                }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private static void completeAdFlow() {

        if (isAdHandled) {
            return;
        }

        isAdHandled = true;

        try {
            LoadingDialogView.reso__Hide_Ads_Loading_Dialog();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (reso__On_Compete_Ads_Callback != null) {
            reso__On_Compete_Ads_Callback.onCompeteAds(true);
        }
    }
}


