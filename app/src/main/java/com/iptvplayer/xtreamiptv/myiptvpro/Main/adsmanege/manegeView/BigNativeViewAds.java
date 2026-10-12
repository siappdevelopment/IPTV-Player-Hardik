package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.manegeView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.NativeManegeView;

import io.supercharge.shimmerlayout.ShimmerLayout;


public class BigNativeViewAds extends LinearLayout {

    static LinearLayout reso__full_Ll;
    static LinearLayout reso__full_Native_Ll;
    static Context reso__context;

    public BigNativeViewAds(Context reso__context) {
        super(reso__context);
        init(reso__context);
    }

    public BigNativeViewAds(Context reso__context, @Nullable AttributeSet attrs) {
        super(reso__context, attrs);
        init(reso__context);
    }

    public BigNativeViewAds(Context reso__context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(reso__context, attrs, defStyleAttr);
        init(reso__context);
    }

    private void init(Context context) {
        BigNativeViewAds.reso__context = context;
        LayoutInflater li = LayoutInflater.from(context);
        li.inflate(R.layout.reso_ads_big_native_simmer, this);

        ShimmerLayout shimmerText = (ShimmerLayout) findViewById(R.id.reso__shimmer);
//        if (shimmerText != null) {
//            shimmerText.startShimmerAnimation();
//        }

        if (isInEditMode()) {
            return;
        }

        reso__full_Ll = (LinearLayout) findViewById(R.id.reso__linear_full);
        reso__full_Native_Ll = (LinearLayout) findViewById(R.id.reso__linear_Native_big);
        NativeManegeView.reso__Display_Big_NativeAd(context, reso__full_Native_Ll, reso__full_Ll, "big");
    }

}
