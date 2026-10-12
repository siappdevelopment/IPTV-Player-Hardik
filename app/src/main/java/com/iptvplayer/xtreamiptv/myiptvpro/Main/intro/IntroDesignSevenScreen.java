package com.iptvplayer.xtreamiptv.myiptvpro.Main.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;

import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.BaseActivity;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeScreen2;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen;
import com.bumptech.glide.Glide;

import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeLaunchScreen;

import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityIntroDesignSevenScreenBinding;

import java.util.List;
import java.util.Random;

public class IntroDesignSevenScreen extends BaseActivity {

    boolean isSelectedIntro = false;

    ActivityIntroDesignSevenScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroDesignSevenScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backClick();
            }
        });

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (!isSelectedIntro) {
                android.widget.Toast.makeText(this, "Please select an option", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            InterstitialAdScreen.reso__Fullscreen_ADS_Display(IntroDesignSevenScreen.this, new InterstitialAdScreen.OnCompeteAds() {
                @Override
                public void onCompeteAds(boolean b) {
                    if (AppControlPreference.getDefaultPermission()) {
                        if (AppControlPreference.getDefaultPermissionCancel()) {
                            if (AppControlPreference.getreso__Intro_Screen_Display() == 7) {
                                startActivity(new Intent(IntroDesignSevenScreen.this, HomeScreen2.class));
                            } else {
                                startActivity(new Intent(IntroDesignSevenScreen.this, IntroDesignEightScreen.class));
                            }
                        } else {
                            if (AppControlPreference.getInsideHomeCount()) {
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 7) {

                                    AppControlPreference.setIntroLauncher(false);
                                    AppControlPreference.setIntroCounts(true);

                                    Intent intent = new Intent(IntroDesignSevenScreen.this, HomeLaunchScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    startActivity(new Intent(IntroDesignSevenScreen.this, IntroDesignEightScreen.class));
                                }
                            } else {
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 7) {
                                    AppControlPreference.setIntroLauncher(false);
                                    Intent intent = new Intent(IntroDesignSevenScreen.this, HomeLaunchScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } else {
                                    startActivity(new Intent(IntroDesignSevenScreen.this, IntroDesignEightScreen.class));
                                }
                            }
                        }
                    } else {
                        if (AppControlPreference.getreso__Intro_Screen_Display() == 7) {

                                startActivity(new Intent(IntroDesignSevenScreen.this, HomeScreen2.class));


                        } else {
                            startActivity(new Intent(IntroDesignSevenScreen.this, IntroDesignEightScreen.class));
                        }
                    }
                }
            }, false);
        });

        setupOptions();
    }

    // Single-choice option cards; Next requires a selection (same rule as the other question screens).
    private void setupOptions() {
        final android.view.View[] rows = {
                binding.cnLayout1, binding.cnLayout2, binding.cnLayout3, binding.cnLayout4, binding.cnLayout5,
                binding.cnLayout6, binding.cnLayout7, binding.cnLayout8, binding.cnLayout9, binding.cnLayout10};
        final android.widget.ImageView[] ends = {
                binding.introEnd1, binding.introEnd2, binding.introEnd3, binding.introEnd4, binding.introEnd5,
                binding.introEnd6, binding.introEnd7, binding.introEnd8, binding.introEnd9, binding.introEnd10};
        for (int i = 0; i < rows.length; i++) {
            final int position = i;
            rows[i].setOnClickListener(view -> {
                for (android.widget.ImageView end : ends) {
                    end.setImageResource(R.drawable.ic_unselected_lang);
                }
                ends[position].setImageResource(R.drawable.ic_selected_lang);
                isSelectedIntro = true;
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.introEnd1.setImageResource(R.drawable.ic_unselected_lang);
        binding.introEnd2.setImageResource(R.drawable.ic_unselected_lang);
        binding.introEnd3.setImageResource(R.drawable.ic_unselected_lang);
        binding.introEnd4.setImageResource(R.drawable.ic_unselected_lang);
        binding.introEnd5.setImageResource(R.drawable.ic_unselected_lang);
        binding.introEnd6.setImageResource(R.drawable.ic_unselected_lang);
        binding.introEnd7.setImageResource(R.drawable.ic_unselected_lang);
        binding.introEnd8.setImageResource(R.drawable.ic_unselected_lang);
        binding.introEnd9.setImageResource(R.drawable.ic_unselected_lang);
        binding.introEnd10.setImageResource(R.drawable.ic_unselected_lang);
        isSelectedIntro = false;
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