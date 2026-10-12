package com.iptvplayer.xtreamiptv.myiptvpro.Main.intro;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;

import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.ViewExtKt;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.BaseActivity;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeScreen2;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen;
import com.bumptech.glide.Glide;

import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeLaunchScreen;

import com.iptvplayer.xtreamiptv.myiptvpro.R;

import java.util.List;
import java.util.Random;

public class IntroDesignThirteenScreen extends BaseActivity {

    ImageView img_intro_thirteen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_design_thirteen_screen);
        ViewExtKt.applySystemBarInsets(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), true, true, true);

        img_intro_thirteen = findViewById(R.id.img_intro_thirteen);

        if (AppControlPreference.getInsideHomeCounter() == 1) {
            startActivity(new Intent(IntroDesignThirteenScreen.this, HomeLaunchScreen.class)
                    .addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK));
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backClick();
            }
        });

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            InterstitialAdScreen.reso__Fullscreen_ADS_Display(IntroDesignThirteenScreen.this, new InterstitialAdScreen.OnCompeteAds() {
                @Override
                public void onCompeteAds(boolean b) {

                    if (AppControlPreference.getDefaultPermission()) {
                        if (AppControlPreference.getDefaultPermissionCancel()) {
                            startActivity(new Intent(IntroDesignThirteenScreen.this, HomeScreen2.class));
                        } else {
                            if (AppControlPreference.getInsideHomeCount()) {

                                AppControlPreference.setIntroLauncher(false);
                                AppControlPreference.setIntroCounts(true);
                                Intent intent = new Intent(IntroDesignThirteenScreen.this, HomeLaunchScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AppControlPreference.setIntroLauncher(false);
                                startActivity(new Intent(IntroDesignThirteenScreen.this, HomeLaunchScreen.class));
                            }
                        }
                    } else {

                            startActivity(new Intent(IntroDesignThirteenScreen.this, HomeScreen2.class));

                    }
                }
            }, false);
        });

        List<String> bannerList = AppControlPreference.getQuizBannerShow();

        if (AppControlPreference.getis_intro_adults_image()) {
            if (bannerList != null && !bannerList.isEmpty()) {
                Random random = new Random();
                String randomBannerUrl = bannerList.get(random.nextInt(bannerList.size()));

                Glide.with(this)
                        .load(randomBannerUrl)
                        .into(img_intro_thirteen);
            }
        } else {
            img_intro_thirteen.setImageResource(R.drawable.ic_intro_img_3);
        }
        img_intro_thirteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InterstitialAdScreen.reso__Fullscreen_ADS_Display(IntroDesignThirteenScreen.this, new InterstitialAdScreen.OnCompeteAds() {
                    @Override
                    public void onCompeteAds(boolean b) {


                    }
                },true);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppControllerManege.removeListener();
    }

    private void backClick() {
        InterstitialAdScreen.reso__Back_Fullscreen_ADS_Display(this, new InterstitialAdScreen.OnCompeteAds() {
                    @Override
                    public void onCompeteAds(boolean b) {
                        finish();
                    }
                }
        );
    }
}