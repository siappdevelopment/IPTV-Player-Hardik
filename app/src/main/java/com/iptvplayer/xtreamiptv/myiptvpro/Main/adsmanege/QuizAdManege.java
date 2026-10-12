package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import static android.view.View.VISIBLE;
import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen.reso__Ads_Show_Check;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.ads.AdError;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.SplashActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

import org.json.JSONObject;

import java.util.List;
import java.util.Random;


@SuppressWarnings("all")
public class QuizAdManege {

    private static final float reso__Blur_Radius = 25f;
    public static Dialog reso__Dialog;
    public static Boolean reso__Ads_Clicked_Quiz = true;
    public static Boolean reso__Quiz_Click = false;
    public static TextView reso__Quiz_Close_Text;
    public static TextView reso__Tv_Timer1;
    public static ImageView reso__Done1;
    public static LinearLayout reso__LL_View;
    public static CardView reso__Card_View;
    public static int QuizClickEvent = 0;
    public static String kuyfvaa = "T9kB3eYz%s";
    public static String shjdnjs = "u2JxL8aV%s";
    public static String jskdsnk = "f7PoNQrM%s";
    public static String bjjsdbd = "Qm3vWxN8%s";
    public static String bsjdbjb = "YbZ49KrA%s";
    public static String husuhds = "Vc8oTu12%s";
    public static String sdsjjdb = "LrQzCw17%s";
    public static String shjdhhj = "M3tYzF8j%s";
    public static String sdjnjjs = "nXPuBvL4%s";
    public static String jsdsjbd = "AjrL8zCw%s";
    public static String dfdfdob = "Lrrtop17";
    public static String wrgfhhj = "M3tskdsm";
    public static String qwefjjs = "nhwhdbns";
    public static String htfdjbd = "Adkmfkdm";
    public static String dsdsdss = "G33FU9C1%s";
    public static InterstitialAdScreen.OnCompeteAds pendingQuizCallback;
    public static boolean isQuizOpen = false;

    public static void reso__Quiz_Interstitial_ADS(Context context, InterstitialAdScreen.OnCompeteAds onFinishAds, boolean isClick) {

        try {
            if (reso__Dialog != null)
                if (reso__Dialog.isShowing())
                    return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (AppControlPreference.getreso__Is_Quiz_Display()) {
            if (AppControlPreference.getreso__Is_Quiz_Web_Interstitial_Show()) {
                if (isClick) {


                    InterQuizManege(context, onFinishAds);
                    reso__Ads_Show_Check(false);
                    reso__Ads_Clicked_Quiz = false;

                }

            } else if (!AppControlPreference.getreso__Is_Quiz_Web_Interstitial_Show()) {
                reso__Dialog = new Dialog(context, android.R.style.Theme_Light);
                reso__Dialog.setContentView(R.layout.reso_quiz_interstitial_layout);
                reso__Dialog.setCancelable(false);
                reso__Card_View = (CardView) reso__Dialog.findViewById(R.id.reso__iv_Close);
                reso__LL_View = (LinearLayout) reso__Dialog.findViewById(R.id.reso__LL_adCloseBtn);
                LinearLayout linearLayout2 = (LinearLayout) reso__Dialog.findViewById(R.id.reso__LLPerAdCenter);
                ImageView imageView = (ImageView) reso__Dialog.findViewById(R.id.reso__img_native_logo);
                TextView textView = (TextView) reso__Dialog.findViewById(R.id.reso__Text_native_title);
                TextView textView2 = (TextView) reso__Dialog.findViewById(R.id.reso__text_native_des);
                TextView txt_download = (TextView) reso__Dialog.findViewById(R.id.reso__Text_download);

                int download = new Random().nextInt(100000);
                txt_download.setText(String.valueOf(download) + "+ user");


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


                View findViewById = reso__Dialog.findViewById(R.id.reso__img_native_logo);
                Animation loadAnimation = AnimationUtils.loadAnimation(findViewById.getContext(), R.anim.reso_zoom_qureka);
                loadAnimation.setFillAfter(true);
                findViewById.startAnimation(loadAnimation);
                TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.5f, 1, 0.0f);
                long j2 = (long) AdError.SERVER_ERROR_CODE;
                translateAnimation.setDuration(j2);
                translateAnimation.setFillAfter(true);
                translateAnimation.setFillEnabled(true);
                findViewById.startAnimation(translateAnimation);
                View findViewById2 = reso__Dialog.findViewById(R.id.reso__Card_TopAd);
                TranslateAnimation translateAnimation2 = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.5f, 1, 0.0f);
                translateAnimation2.setDuration(j2);
                translateAnimation2.setFillAfter(true);
                translateAnimation2.setFillEnabled(true);
                findViewById2.startAnimation(translateAnimation2);

                reso__Quiz_Close_Text = reso__Dialog.findViewById(R.id.reso__TextTimer);
                reso__Tv_Timer1 = reso__Dialog.findViewById(R.id.reso__tvTimer1);
                reso__Done1 = reso__Dialog.findViewById(R.id.reso__done1);

                TextView native_ad_call_to_action = reso__Dialog.findViewById(R.id.reso__iv_native_ad_call_to_action);
                TextView reso__LL_adCloseBtn = reso__Dialog.findViewById(R.id.reso__LL_adCloseBtn);
                TextView txt_adCloseBtn_1 = reso__Dialog.findViewById(R.id.txt_adCloseBtn_1);
                TextView txt_adCloseBtn_2 = reso__Dialog.findViewById(R.id.txt_adCloseBtn_2);
                RelativeLayout relative_main = reso__Dialog.findViewById(R.id.relative_main);

                try {
                    native_ad_call_to_action.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
                    reso__LL_adCloseBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
                    native_ad_call_to_action.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));
                    txt_adCloseBtn_1.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Color()));
                    txt_adCloseBtn_2.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Color()));
                    reso__Tv_Timer1.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Color()));

                    ((TextView) reso__Dialog.findViewById(R.id.txt_tag_AD)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
                    ((TextView) reso__Dialog.findViewById(R.id.txt_tag_AD)).setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

                    relative_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_BG_Color())));
                    findViewById2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_BG_Color())));
                } catch (NullPointerException n) {
                    n.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                reso__LL_View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (reso__Done1.getVisibility() == VISIBLE) {
                            reso__Ads_Show_Check(true);
                            reso__Ads_Clicked_Quiz = true;
                            reso__Dialog.dismiss();
                            if (isClick) {
                                onFinishAds.onCompeteAds(true);
                            }
                        }
                    }
                });

                native_ad_call_to_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reso__Ads_Show_Check(false);
                        EventQuizManege(context);
                    }
                });

                reso__Ads_Show_Check(false);
                ExitDialogViewAd.reso__Quiz_Timer_Dialog();

                reso__Card_View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (reso__Quiz_Click) {
                            if (AppControlPreference.getreso__Quiz_Close_Ads_Display() && reso__Ads_Clicked_Quiz) {
                                reso__Ads_Clicked_Quiz = false;
                                reso__Ads_Show_Check(false);
                                EventQuizManege(context);
                            } else {
                                reso__Ads_Show_Check(true);
                                reso__Ads_Clicked_Quiz = true;
                                reso__Dialog.dismiss();
                                if (isClick) {
                                    onFinishAds.onCompeteAds(true);
                                }
                            }
                        }
                    }
                });


                reso__Dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            reso__Ads_Clicked_Quiz = true;
                            reso__Dialog.dismiss();
                            reso__Ads_Show_Check(true);
                            if (isClick) {
                                onFinishAds.onCompeteAds(true);
                            }
                        }
                        return true;
                    }
                });

                reso__Dialog.show();
            }
        }

    }

    public static void reso__quiz_Native_Ads(Context context, LinearLayout linearLayout, Boolean type) {

        View layout2;
        if (type) {
            layout2 = LayoutInflater.from(context).inflate(R.layout.reso_quiz_small_native_layout, linearLayout, false);
        } else {
            layout2 = LayoutInflater.from(context).inflate(R.layout.reso_quiz_ad_native_cal, linearLayout, false);

            ImageView quiz_logo_image = layout2.findViewById(R.id.quiz_logo_image);
            if (AppControlPreference.getIs_adults_QuizBannerShow()) {
                int nextInt = new Random().nextInt(AppControlPreference.getQuizBannerShow().size());
                String str = AppControlPreference.getQuizBannerShow().get(nextInt);
                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(str).into(quiz_logo_image);

                }
            } else {
                int random = new Random().nextInt(3);

                if (random == 1) {
                    if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                        Glide.with(context).load(R.drawable.reso_banner_img_1).into(quiz_logo_image);

                    }
                } else if (random == 2) {
                    if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                        Glide.with(context).load(R.drawable.reso_banner_img_2).into(quiz_logo_image);

                    }
                } else {
                    if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                        Glide.with(context).load(R.drawable.reso_banner_img_3).into(quiz_logo_image);

                    }
                }
            }

        }


        TextView tv_text_ad_name = layout2.findViewById(R.id.reso__tv_text_ad_name);
        TextView tv_text_ad_desc = layout2.findViewById(R.id.reso__tv_text_ad_desc);
        ImageView iv_nats_round = layout2.findViewById(R.id.reso__iv_nats_round);
        TextView ln_playnow = layout2.findViewById(R.id.reso__ln_playnow);
        int random = new Random().nextInt(3);
        if (random == 1) {
            tv_text_ad_name.setText(AppControlPreference.getreso__Quiz_Native_Title_List().get(0));
            tv_text_ad_desc.setText(AppControlPreference.getreso__Quiz_Native_Description_List().get(0));

        } else if (random == 2) {
            tv_text_ad_name.setText(AppControlPreference.getreso__Quiz_Native_Title_List().get(1));
            tv_text_ad_desc.setText(AppControlPreference.getreso__Quiz_Native_Description_List().get(1));


        } else {
            tv_text_ad_name.setText(AppControlPreference.getreso__Quiz_Native_Title_List().get(2));
            tv_text_ad_desc.setText(AppControlPreference.getreso__Quiz_Native_Description_List().get(2));

        }

        if (AppControlPreference.getIs_adults_QuizBannerShow()) {
            int nextInt = new Random().nextInt(AppControlPreference.getQuizBannerShow().size());
            String str = AppControlPreference.getQuizBannerShow().get(nextInt);
            if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                Glide.with(context).load(str).into(iv_nats_round);

            }
        } else {
            if (random == 1) {

                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(R.drawable.reso_native_img_1).into(iv_nats_round);

                }
            } else if (random == 2) {

                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(R.drawable.reso_native_img_2).into(iv_nats_round);

                }

            } else {

                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(R.drawable.reso_native_img_3).into(iv_nats_round);

                }

            }

        }


        try {
            ln_playnow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            ln_playnow.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

            ((TextView) layout2.findViewById(R.id.txt_tag_AD)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            ((TextView) layout2.findViewById(R.id.txt_tag_AD)).setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

            layout2.findViewById(R.id.ll_native_bg).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_BG_Color())));
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ln_playnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reso__Ads_Show_Check(false);
                EventQuizManege(context);

            }
        });

        if (linearLayout != null) {
            linearLayout.removeAllViews();
            linearLayout.addView(layout2);
        }
    }

    public static Bitmap reso__blur(Bitmap image, Context context) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(reso__Blur_Radius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    @SuppressLint("SetTextI18n")
    public static void reso__Quiz_Banner_Ads(Context context, LinearLayout lnr_view) {

        View layout2 = LayoutInflater.from(context).inflate(R.layout.reso_quiz_banner, lnr_view, false);
        AppCompatButton playNowLL = layout2.findViewById(R.id.reso__btnInsatll);

        TextView tv_text_ad_name = layout2.findViewById(R.id.reso__tv_text_ad_name);
        TextView tv_text_ad_desc = layout2.findViewById(R.id.reso__tv_text_ad_desc);
        ImageView iv_banner_round = layout2.findViewById(R.id.reso__iv_banner_round);

        LinearLayout ll_native_bg = layout2.findViewById(R.id.ll_native_bg);
        int random = new Random().nextInt(3);

        if (random == 1) {
            tv_text_ad_name.setText(AppControlPreference.getreso__Quiz_Banner_Title_List().get(0));
            tv_text_ad_desc.setText(AppControlPreference.getreso__Quiz_Banner_Description_List().get(0));

        } else if (random == 2) {
            tv_text_ad_name.setText(AppControlPreference.getreso__Quiz_Banner_Title_List().get(1));
            tv_text_ad_desc.setText(AppControlPreference.getreso__Quiz_Banner_Description_List().get(1));

        } else {
            tv_text_ad_name.setText(AppControlPreference.getreso__Quiz_Banner_Title_List().get(2));
            tv_text_ad_desc.setText(AppControlPreference.getreso__Quiz_Banner_Description_List().get(2));

        }
        if (AppControlPreference.getIs_adults_QuizBannerShow()) {
            int nextInt = new Random().nextInt(AppControlPreference.getQuizBannerShow().size());
            String str = AppControlPreference.getQuizBannerShow().get(nextInt);
            if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                Glide.with(context).load(str).into(iv_banner_round);

            }
        } else {
            if (random == 1) {

                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(R.drawable.reso_banner_img_1).into(iv_banner_round);

                }
            } else if (random == 2) {

                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(R.drawable.reso_banner_img_2).into(iv_banner_round);

                }
            } else {
                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(R.drawable.reso_banner_img_3).into(iv_banner_round);

                }
            }
        }


        try {
            playNowLL.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            playNowLL.setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

            ((TextView) layout2.findViewById(R.id.txt_tag_AD)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_Button_Color())));
            ((TextView) layout2.findViewById(R.id.txt_tag_AD)).setTextColor(Color.parseColor(AppControlPreference.getreso__Native_Button_Text_Color()));

            ll_native_bg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(AppControlPreference.getreso__Native_BG_Color())));
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        playNowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reso__Ads_Show_Check(false);
                EventQuizManege(context);
            }
        });


        if (lnr_view != null) {
            lnr_view.removeAllViews();
            lnr_view.addView(layout2);
        }

    }

    public static void reso__Quiz_Header(ImageView imageView, Context context) {

        ObjectAnimator flipAnimation = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 360f);
        flipAnimation.setDuration(2200);
        flipAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        flipAnimation.setInterpolator(new android.view.animation.LinearInterpolator());
        flipAnimation.start();
        if (AppControlPreference.getIs_adults_QuizBannerShow()) {
            int nextInt = new Random().nextInt(AppControlPreference.getQuizBannerShow().size());
            String str = AppControlPreference.getQuizBannerShow().get(nextInt);
            if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                Glide.with(context).load(str).into(imageView);

            }
        } else {
            int random = new Random().nextInt(3);
            if (random == 1) {
                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(R.drawable.reso_banner_img_1).into(imageView);

                }
            } else if (random == 2) {
                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(R.drawable.reso_banner_img_2).into(imageView);

                }
            } else {
                if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    Glide.with(context).load(R.drawable.reso_banner_img_3).into(imageView);

                }
            }
        }


    }

    public static void EventQuizManege(Context context) {

        if (AppControlPreference.getCheckExclude()) {
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle newBundle = new Bundle();
            newBundle.putBoolean("browser_show", true);
            firebaseAnalytics.logEvent(context.getString(R.string.browser_user_show), newBundle);
            if (AppControlPreference.getOpen_Link_in_App()) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    Bundle bundle = new Bundle();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        bundle.putBinder("android.support.customtabs.extra.SESSION", (IBinder) null);
                    }
                    intent.putExtras(bundle);
                    intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", context.getResources().getColor(R.color.color_primary));
                    intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                    intent.setData(Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                    context.startActivity(intent, (Bundle) null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(context.getResources().getColor(R.color.color_primary));
                    CustomTabsIntent build = builder.build();
                    build.intent.setFlags(268435456);
                    build.launchUrl(context, Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {

            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle newBundle = new Bundle();
            newBundle.putBoolean("browser_show", true);
            firebaseAnalytics.logEvent(context.getString(R.string.browser_user_show), newBundle);
            if (AppControlPreference.getOpen_Link_in_App()) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    Bundle bundle = new Bundle();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        bundle.putBinder("android.support.customtabs.extra.SESSION", (IBinder) null);
                    }
                    intent.putExtras(bundle);
                    intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", context.getResources().getColor(R.color.color_primary));
                    intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                    intent.setData(Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                    context.startActivity(intent, (Bundle) null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(context.getResources().getColor(R.color.color_primary));
                    CustomTabsIntent build = builder.build();
                    build.intent.setFlags(268435456);
                    build.launchUrl(context, Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }


    }

    public static void InterQuizManege(Context context, InterstitialAdScreen.OnCompeteAds onFinishAds) {

        if (AppControlPreference.getCheckExclude()) {
            if (!AppControlPreference.getreso__Quiz_Link().equals("")) {
                if (QuizClickEvent == AppControlPreference.getQuizLinkExcludeClickEvent()) {
                    QuizClickEvent = 0;
                    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                    Bundle newBundle = new Bundle();
                    newBundle.putBoolean("browser_show", true);
                    firebaseAnalytics.logEvent(context.getString(R.string.browser_user_show), newBundle);
                    reso__Ads_Show_Check(false);
                    reso__Ads_Clicked_Quiz = false;
//                    onFinishAds.onCompeteAds(true);
                    pendingQuizCallback = onFinishAds;
                    isQuizOpen = true;
                    if (AppControlPreference.getOpen_Link_in_App()) {
                        try {
                            Intent intent = new Intent("android.intent.action.VIEW");
                            Bundle bundle = new Bundle();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                bundle.putBinder("android.support.customtabs.extra.SESSION", (IBinder) null);
                            }
                            intent.putExtras(bundle);
                            intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", context.getResources().getColor(R.color.color_primary));
                            intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                            intent.setData(Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                            context.startActivity(intent, (Bundle) null);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            builder.setToolbarColor(context.getResources().getColor(R.color.color_primary));
                            CustomTabsIntent build = builder.build();
                            build.intent.setFlags(268435456);
                            build.launchUrl(context, Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
//                    onFinishAds.onCompeteAds(true);
                    QuizClickEvent++;
                    pendingQuizCallback = onFinishAds;
                    isQuizOpen = true;
                }
            } else {
//                onFinishAds.onCompeteAds(true);
                pendingQuizCallback = onFinishAds;
                isQuizOpen = true;
            }


        } else {
            if (!AppControlPreference.getreso__Quiz_Link().equals("")) {
                if (QuizClickEvent == AppControlPreference.getQuizLinkClickEvent()) {
                    QuizClickEvent = 0;


                    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                    Bundle newBundle = new Bundle();
                    newBundle.putBoolean("browser_show", true);
                    firebaseAnalytics.logEvent(context.getString(R.string.browser_user_show), newBundle);
                    reso__Ads_Show_Check(false);
                    reso__Ads_Clicked_Quiz = false;
//                    onFinishAds.onCompeteAds(true);
                    pendingQuizCallback = onFinishAds;
                    isQuizOpen = true;
                    if (AppControlPreference.getOpen_Link_in_App()) {
                        try {
                            Intent intent = new Intent("android.intent.action.VIEW");
                            Bundle bundle = new Bundle();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                bundle.putBinder("android.support.customtabs.extra.SESSION", (IBinder) null);
                            }
                            intent.putExtras(bundle);
                            intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", context.getResources().getColor(R.color.color_primary));
                            intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                            intent.setData(Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                            context.startActivity(intent, (Bundle) null);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            builder.setToolbarColor(context.getResources().getColor(R.color.color_primary));
                            CustomTabsIntent build = builder.build();
                            build.intent.setFlags(268435456);
                            build.launchUrl(context, Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } else {
                    QuizClickEvent++;
//                    onFinishAds.onCompeteAds(true);
                    pendingQuizCallback = onFinishAds;
                    isQuizOpen = true;

                }
            } else {
//                onFinishAds.onCompeteAds(true);
                pendingQuizCallback = onFinishAds;
                isQuizOpen = true;

            }
        }
    }

    public static void fetchIpDetails(Context context, IpCheckCallback callback) {
        if (AppControlPreference.getIPChecker()) {
            List<String> citylink_list = AppControlPreference.getCityList();
            List<String> statelink_list = AppControlPreference.getStateList();
            List<String> countrylink_list = AppControlPreference.getCountryList();

            String baseUrl = AppControlPreference.getIp_Checker_baseUrl();

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    baseUrl,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.optString("status", "");

                                if (!"success".equals(status)) {
                                    callback.onResult(false);
                                    return;
                                }

                                String cityName    = response.optString("city", "");
                                String stateName   = response.optString("regionName", "");
                                String countryName = response.optString("country", "");

                                boolean cityFound = false;
                                for (String city : citylink_list) {
                                    if (!city.isEmpty() && cityName.equals(city)) {
                                        cityFound = true;
                                        break;
                                    }
                                }

                                boolean stateFound = false;
                                for (String state : statelink_list) {
                                    if (!state.isEmpty() && stateName.equals(state)) {
                                        stateFound = true;
                                        break;
                                    }
                                }

                                boolean countryFound = false;
                                for (String country : countrylink_list) {
                                    if (!country.isEmpty() && countryName.equals(country)) {
                                        countryFound = true;
                                        break;
                                    }
                                }

                                if (countryFound || stateFound || cityFound) {
                                    callback.onResult(true);
                                } else {
                                    callback.onResult(false);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                callback.onResult(false);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            callback.onResult(false);
                        }
                    }
            );

            queue.add(jsonRequest);
        } else {
            callback.onResult(false);
        }
    }

    public static void AppOpenQuizManege(Context context, AppOpenAdView.OnCompleteAds reso__on_Complete_Ads) {

        if (AppControlPreference.getCheckExclude()) {
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle newBundle = new Bundle();
            newBundle.putBoolean("browser_show", true);
            firebaseAnalytics.logEvent(context.getString(R.string.browser_user_show), newBundle);
            newBundle.putBoolean("is_exclude", true);
            firebaseAnalytics.logEvent(context.getString(R.string.is_user_exclude), newBundle);
            SplashActivity.reso__Quiz_Show = true;
            if (AppControlPreference.getOpen_Link_in_App()) {
                try {

                    Intent intent = new Intent("android.intent.action.VIEW");
                    Bundle bundle = new Bundle();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        bundle.putBinder("android.support.customtabs.extra.SESSION", (IBinder) null);
                    }
                    intent.putExtras(bundle);
                    intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", context.getResources().getColor(R.color.color_primary));
                    intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                    intent.setData(Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                    context.startActivity(intent, (Bundle) null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(context.getResources().getColor(R.color.color_primary));
                    CustomTabsIntent build = builder.build();
                    build.intent.setFlags(268435456);
                    build.launchUrl(context, Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            FirebaseAnalytics instance = FirebaseAnalytics.getInstance(context);
            Bundle bundle1 = new Bundle();

            bundle1.putBoolean("is_nonexclude", true);
            instance.logEvent(context.getString(R.string.is_user_nonexclude), bundle1);

            if (!AppControlPreference.getreso__Quiz_Link().equals("")) {
                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle newBundle = new Bundle();
                newBundle.putBoolean("browser_show", true);
                firebaseAnalytics.logEvent(context.getString(R.string.browser_user_show), newBundle);
                SplashActivity.reso__Quiz_Show = true;
                if (AppControlPreference.getOpen_Link_in_App()) {
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        Bundle bundle = new Bundle();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            bundle.putBinder("android.support.customtabs.extra.SESSION", (IBinder) null);
                        }
                        intent.putExtras(bundle);
                        intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", context.getResources().getColor(R.color.color_primary));
                        intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                        intent.setData(Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                        context.startActivity(intent, (Bundle) null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        builder.setToolbarColor(context.getResources().getColor(R.color.color_primary));
                        CustomTabsIntent build = builder.build();
                        build.intent.setFlags(268435456);
                        build.launchUrl(context, Uri.parse(AppControlPreference.getreso__Quiz_Link()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                reso__on_Complete_Ads.onCompleteAds(true);
            }
        }
    }

    public interface IpCheckCallback {
        void onResult(boolean isCheckIp);
    }
}
