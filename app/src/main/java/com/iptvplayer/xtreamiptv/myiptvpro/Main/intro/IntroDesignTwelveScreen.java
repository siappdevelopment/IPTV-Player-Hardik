package com.iptvplayer.xtreamiptv.myiptvpro.Main.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;

import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.ViewExtKt;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.BaseActivity;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeScreen2;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen;
import com.bumptech.glide.Glide;

import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeLaunchScreen;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.intro.IntroDesignThirteenScreen;
import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityIntroDesignTwelveScreenBinding;

import java.util.List;
import java.util.Random;

public class IntroDesignTwelveScreen extends BaseActivity {

    ActivityIntroDesignTwelveScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroDesignTwelveScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewExtKt.applySystemBarInsets(binding.getRoot(), true, true, true);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backClick();
            }
        });

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            InterstitialAdScreen.reso__Fullscreen_ADS_Display(IntroDesignTwelveScreen.this, new InterstitialAdScreen.OnCompeteAds() {
                @Override
                public void onCompeteAds(boolean b) {

                    if (AppControlPreference.getDefaultPermission()) {
                        if (AppControlPreference.getDefaultPermissionCancel()) {
                            if (AppControlPreference.getreso__Intro_Screen_Display() == 12) {
                                startActivity(new Intent(IntroDesignTwelveScreen.this, HomeScreen2.class));
                            } else {
                                startActivity(new Intent(IntroDesignTwelveScreen.this, IntroDesignThirteenScreen.class));
                            }
                        } else {
                            if (AppControlPreference.getInsideHomeCount()) {
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 12) {

                                    AppControlPreference.setIntroLauncher(false);
                                    AppControlPreference.setIntroCounts(true);

                                    Intent intent = new Intent(IntroDesignTwelveScreen.this, HomeLaunchScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    startActivity(new Intent(IntroDesignTwelveScreen.this, IntroDesignThirteenScreen.class));

                                }
                            } else {
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 12) {
                                    AppControlPreference.setIntroLauncher(false);
                                    Intent intent = new Intent(IntroDesignTwelveScreen.this, HomeLaunchScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    startActivity(new Intent(IntroDesignTwelveScreen.this, IntroDesignThirteenScreen.class));
                                }
                            }
                        }
                    } else {
                        if (AppControlPreference.getreso__Intro_Screen_Display() == 12) {


                        startActivity(new Intent(IntroDesignTwelveScreen.this, HomeScreen2.class));


                        } else {
                            startActivity(new Intent(IntroDesignTwelveScreen.this, IntroDesignThirteenScreen.class));
                        }
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
                        .into(binding.imgIntroTwelve);
            }
        } else {
            binding.imgIntroTwelve.setImageResource(R.drawable.ic_intro_img_2);
        }
        binding.imgIntroTwelve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InterstitialAdScreen.reso__Fullscreen_ADS_Display(IntroDesignTwelveScreen.this, new InterstitialAdScreen.OnCompeteAds() {
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