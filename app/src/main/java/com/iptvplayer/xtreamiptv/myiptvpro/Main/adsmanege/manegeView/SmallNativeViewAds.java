package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.manegeView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.NativeSmallView;

import io.supercharge.shimmerlayout.ShimmerLayout;


public class SmallNativeViewAds extends LinearLayout {


    static Context reso__context;

    public SmallNativeViewAds(Context reso__context) {
        super(reso__context);
        init(reso__context);
    }

    public SmallNativeViewAds(Context reso__context, @Nullable AttributeSet attrs) {
        super(reso__context, attrs);
        init(reso__context);
    }

    public SmallNativeViewAds(Context reso__context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(reso__context, attrs, defStyleAttr);
        init(reso__context);
    }

    private void init(Context context) {
        SmallNativeViewAds.reso__context = context;
        LayoutInflater li = LayoutInflater.from(context);
        li.inflate(R.layout.reso_ads_small_ntv_ban_simmer, this);

        ShimmerLayout shimmerText = (ShimmerLayout) findViewById(R.id.reso__shimmer);
//        if (shimmerText != null) {
//            shimmerText.startShimmerAnimation();
//        }

        LinearLayout llline_full = (LinearLayout) findViewById(R.id.reso__linear_bottom_full);
        LinearLayout llnative_full = (LinearLayout) findViewById(R.id.reso__linear_bottom_ads);
        NativeSmallView.reso__Bottom_ADS_Display(context, llnative_full, llline_full);
    }


}
