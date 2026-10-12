package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen.reso__Ads_Show_Check;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

@SuppressWarnings("all")
public class NativeSmallView {

    public static Context reso__context;
    public static int reso__Bottom_Array_Pos = 0;
    public static int reso__Ads_Array_Bottom_Pos = 0;
    public static NativeAd reso__Bottom_Admob_Native_Banner;
    public static boolean reso__Fail_Array_Bottom_Ids = false;
    public static String reso__Ads_Bottom_Native = "";
    public static boolean reso__Fb_Fail_Bottom_Array_Id = false;
    public static AdView reso__Adview;

    public static void reso__Bottom_Ads_Random_Ids() {
        try {

            if (AppControlPreference.getreso__Admob_Native_Custom_Ids_List() != null && AppControlPreference.getreso__Admob_Native_Custom_Ids_List().size() != 0 && AppControlPreference.getreso__Admob_Native_Custom_Ids_List().size() != reso__Bottom_Array_Pos) {
                reso__Fail_Array_Bottom_Ids = true;
                AppControlPreference.setreso__Second_Native_Unit_Id(AppControlPreference.getreso__Admob_Native_Custom_Ids_List().get(reso__Bottom_Array_Pos));
                reso__Bottom_Array_Pos = reso__Bottom_Array_Pos + 1;
            } else {
                reso__Fail_Array_Bottom_Ids = false;

            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reso__Admob_Bottom_ADS(final Context context, LinearLayout linearLayout, LinearLayout lnr_view) {

        reso__Bottom_Ads_Random_Ids();
        if (!reso__Fail_Array_Bottom_Ids) {

            if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                if (AppControlPreference.getreso__Is_Quiz_Display() && AppControlPreference.getreso__Is_Quiz_Banner()) {
                    lnr_view.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    QuizAdManege.reso__Quiz_Banner_Ads(context, linearLayout);
                } else {
                    linearLayout.setVisibility(View.GONE);
                    lnr_view.setVisibility(View.GONE);
                }
            } else {

                if (AppControlPreference.getreso__Is_Quiz_Display() && AppControlPreference.getreso__Is_Quiz_Banner()) {
                    QuizAdManege.reso__Quiz_Banner_Ads(context, linearLayout);
                } else {

                }

            }

            return;
        }
        reso__Ads_Bottom_Native = "";
        reso__Ads_Bottom_Native = AppControlPreference.getreso__Second_Native_Unit_Id();

        AdLoader.Builder builder = new AdLoader.Builder(context, reso__Ads_Bottom_Native)
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

                        if (reso__Bottom_Admob_Native_Banner != null) {
                            reso__Bottom_Admob_Native_Banner = null;
                        }
                        reso__Bottom_Admob_Native_Banner = nativeAd;

                        if (AppControlPreference.getreso__Ads_One_By_One_Ids()) {
                            if (AppControlPreference.getreso__Admob_Native_Custom_Ids_List() != null && AppControlPreference.getreso__Admob_Native_Custom_Ids_List().size() != 0 && AppControlPreference.getreso__Admob_Native_Custom_Ids_List().size() == reso__Bottom_Array_Pos) {
                                reso__Bottom_Array_Pos = 0;
                            }
                        } else {
                            reso__Bottom_Array_Pos = 0;
                        }

                        if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                            if (reso__Bottom_Admob_Native_Banner != null) {
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(NativeSmallView.reso__context.LAYOUT_INFLATER_SERVICE);
                                NativeAdView adView = (NativeAdView) inflater.inflate(R.layout.reso_ads_native_baner_load, null);
                                lnr_view.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                                reso__Big_Native_Adview_Display(reso__Bottom_Admob_Native_Banner, adView, false);

                                linearLayout.removeAllViews();
                                linearLayout.addView(adView);
                            }
                        }

                    }
                });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                if (reso__Bottom_Admob_Native_Banner != null) {
                    reso__Bottom_Admob_Native_Banner = null;
                }

                reso__Admob_Bottom_ADS(context, linearLayout, lnr_view);
            }

            @Override
            public void onAdClicked() {
                AppControllerManege.reso__is_Fast_Start = true;
            }
        }).build();

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        adLoader.loadAd(adRequest);
    }

    public static void reso__Admob_Banner_Explicate(Context context, LinearLayout lnr_adview, LinearLayout ll_label) {

        reso__Banner_Random_Ids();
        if (!reso__Fb_Fail_Bottom_Array_Id) {

            if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                if (AppControlPreference.getreso__Is_Quiz_Display() && AppControlPreference.getreso__Is_Quiz_Banner()) {
                    lnr_adview.setVisibility(View.GONE);
                    ll_label.setVisibility(View.VISIBLE);
                    QuizAdManege.reso__Quiz_Banner_Ads(context, ll_label);
                } else {
                    ll_label.setVisibility(View.GONE);
                    lnr_adview.setVisibility(View.GONE);
                }
            } else {
                if (AppControlPreference.getreso__Is_Quiz_Display() && AppControlPreference.getreso__Is_Quiz_Banner()) {
                    QuizAdManege.reso__Quiz_Banner_Ads(context, ll_label);
                } else {

                }
            }

            return;
        }

        lnr_adview.removeAllViews();


        reso__Adview = new AdView(context);
        reso__Adview.setAdUnitId(AppControlPreference.getreso__Admob_Banner_Ads_Id());
        lnr_adview.addView(reso__Adview);
        com.google.android.gms.ads.AdSize adSize = reso__get_AdS_Dimension((Activity) context);
        reso__Adview.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        reso__Adview.loadAd(adRequest);

        FirebaseAnalytics firebaseAnalytics;
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        reso__Adview.setOnPaidEventListener(new OnPaidEventListener() {
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

        reso__Adview.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                lnr_adview.setVisibility(View.VISIBLE);
                ll_label.setVisibility(View.GONE);

                if (AppControlPreference.getreso__Ads_One_By_One_Ids()) {
                    if (AppControlPreference.getreso__Admob_Banner_Ids_List() != null && AppControlPreference.getreso__Admob_Banner_Ids_List().size() != 0 && AppControlPreference.getreso__Admob_Banner_Ids_List().size() == reso__Ads_Array_Bottom_Pos) {
                        reso__Ads_Array_Bottom_Pos = 0;
                    }
                } else {
                    reso__Ads_Array_Bottom_Pos = 0;
                }

            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                reso__Admob_Banner_Explicate(context, lnr_adview, ll_label);


            }

            @Override
            public void onAdOpened() {


            }

            @Override
            public void onAdClicked() {
                reso__Ads_Show_Check(false);
            }

            @Override
            public void onAdClosed() {


            }
        });


    }

    public static void reso__Bottom_ADS_Display(final Context context, final LinearLayout linearLayout, final LinearLayout lnr_view) {

        NativeSmallView.reso__context = context;
        reso__Bottom_Array_Pos = 0;

        if (AppControlPreference.getreso__Ads_Free()) {
            lnr_view.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            return;
        }


        if (AppControlPreference.getreso__Small_Ads_Type().equals("banner")) {
            lnr_view.setVisibility(View.VISIBLE);
            reso__Admob_Banner_Explicate(context, linearLayout, lnr_view);
        } else {
            if (AppControlPreference.getreso__Ads_Type().equals("Load")) {
                reso__Admob_Bottom_ADS(context, linearLayout, lnr_view);
            } else {
                reso__Bottom_ADS_Load(context, linearLayout, lnr_view);
            }
        }


    }

    public static void reso__Bottom_ADS_Load(final Context context, final LinearLayout linearLayout, final LinearLayout lnr_view) {
        if (AppControlPreference.getreso__Admob_Native_Custom_Ids_List() != null && AppControlPreference.getreso__Admob_Native_Custom_Ids_List().size() != 0) {

        }
        if (reso__Bottom_Admob_Native_Banner != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(NativeSmallView.reso__context.LAYOUT_INFLATER_SERVICE);
            NativeAdView adView = (NativeAdView) inflater.inflate(R.layout.reso_ads_native_baner_load, null);
            lnr_view.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            reso__Big_Native_Adview_Display(reso__Bottom_Admob_Native_Banner, adView, false);

            linearLayout.removeAllViews();
            linearLayout.addView(adView);

        } else {

            if (AppControlPreference.getreso__Is_Quiz_Display() && AppControlPreference.getreso__Is_Quiz_Banner()) {
                lnr_view.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                QuizAdManege.reso__Quiz_Banner_Ads(context, linearLayout);
            } else {
                linearLayout.setVisibility(View.GONE);
                lnr_view.setVisibility(View.GONE);
            }

        }
        reso__Admob_Bottom_ADS(context, linearLayout, lnr_view);
    }

    public static void reso__Big_Native_Adview_Display(NativeAd nativeAd, NativeAdView adView, boolean flag) {

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
            //((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
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
//            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    public static com.google.android.gms.ads.AdSize reso__get_AdS_Dimension(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return com.google.android.gms.ads.AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public static void reso__Banner_Random_Ids() {
        try {
            if (AppControlPreference.getreso__Admob_Banner_Ids_List() != null && AppControlPreference.getreso__Admob_Banner_Ids_List().size() != 0 && AppControlPreference.getreso__Admob_Banner_Ids_List().size() != reso__Ads_Array_Bottom_Pos) {
                reso__Fb_Fail_Bottom_Array_Id = true;
                AppControlPreference.setreso__Admob_Banner_Ads_Id(AppControlPreference.getreso__Admob_Banner_Ids_List().get(reso__Ads_Array_Bottom_Pos));
                reso__Ads_Array_Bottom_Pos = reso__Ads_Array_Bottom_Pos + 1;
            } else {
                reso__Fb_Fail_Bottom_Array_Id = false;
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
