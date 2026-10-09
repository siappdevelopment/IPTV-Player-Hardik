package com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdPlacement {

    public static String termsConditions = "";

    public static String getTermsConditions() {
        return termsConditions;
    }

    public static void setTermsConditions(String termsConditions) {
        AdPlacement.termsConditions = termsConditions;
    }

    public static AdSize getAdaptiveAdSize(Context context) {
        DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();
        float density = outMetrics.density;
        int adWidth = (int) (outMetrics.widthPixels / density);
        if (context instanceof Activity) {
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
        }
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    @Deprecated
    public static AdSize getAdaptiveAdSize(Activity activity) {
        return getAdaptiveAdSize((Context) activity);
    }

    public static void logAdRevenue(Context context, AdValue adValue) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        double revenue = adValue.getValueMicros() / 1_000_000.0;
        String currency = adValue.getCurrencyCode();
        Bundle adRevenueParams = new Bundle();
        adRevenueParams.putString(FirebaseAnalytics.Param.AD_PLATFORM, "Google Ad Manager");
        adRevenueParams.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        adRevenueParams.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, adRevenueParams);
    }

}