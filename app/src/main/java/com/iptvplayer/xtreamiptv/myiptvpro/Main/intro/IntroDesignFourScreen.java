package com.iptvplayer.xtreamiptv.myiptvpro.Main.intro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeLaunchScreen;

import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.BaseActivity;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeScreen2;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen;
import com.iptvplayer.xtreamiptv.myiptvpro.R;
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityIntroDesignFourScreenBinding;


public class IntroDesignFourScreen extends BaseActivity {

    boolean isSelectedIntro = false;
    ActivityIntroDesignFourScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroDesignFourScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backClick();
            }
        });

        findViewById(R.id.start_screen_btn).setOnClickListener(v -> {
            if (isSelectedIntro) {
                InterstitialAdScreen.reso__Fullscreen_ADS_Display(IntroDesignFourScreen.this, new InterstitialAdScreen.OnCompeteAds() {
                    @Override
                    public void onCompeteAds(boolean b) {

                        if (AppControlPreference.getDefaultPermission()) {
                            if (AppControlPreference.getDefaultPermissionCancel()) {
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 4) {
                                    startActivity(new Intent(IntroDesignFourScreen.this, HomeScreen2.class));
                                } else {
                                    startActivity(new Intent(IntroDesignFourScreen.this, IntroDesignFiveScreen.class));
                                }
                            } else {
                                if (AppControlPreference.getInsideHomeCount()) {
                                    if (AppControlPreference.getreso__Intro_Screen_Display() == 4) {

                                        AppControlPreference.setIntroLauncher(false);
                                        AppControlPreference.setIntroCounts(true);

                                        Intent intent = new Intent(IntroDesignFourScreen.this, HomeLaunchScreen.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                    } else {
                                        startActivity(new Intent(IntroDesignFourScreen.this, IntroDesignFiveScreen.class));

                                    }
                                } else {
                                    if (AppControlPreference.getreso__Intro_Screen_Display() == 4) {
                                        AppControlPreference.setIntroLauncher(false);
                                        Intent intent = new Intent(IntroDesignFourScreen.this, HomeLaunchScreen.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                    } else {
                                        startActivity(new Intent(IntroDesignFourScreen.this, IntroDesignFiveScreen.class));
                                    }
                                }
                            }
                        } else {
                            if (AppControlPreference.getreso__Intro_Screen_Display() == 4) {

                                    startActivity(new Intent(IntroDesignFourScreen.this, HomeScreen2.class));


                            } else {
                                startActivity(new Intent(IntroDesignFourScreen.this, IntroDesignFiveScreen.class));
                            }
                        }
                    }
                }, false);
            } else {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
            }
        });

        binding.cnLayout1.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd1);
        });

        binding.cnLayout2.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd2);
        });

        binding.cnLayout3.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd3);
        });

        binding.cnLayout4.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd4);
        });

        binding.cnLayout5.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd5);
        });

        binding.cnLayout6.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd6);
        });

        binding.cnLayout7.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd7);
        });

        binding.cnLayout8.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd8);
        });

        binding.cnLayout9.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd9);
        });

        binding.cnLayout10.setOnClickListener(view -> {
            isSelectedIntro = true;
            unSelectedIntro();
            selectedIntro(binding.introEnd10);
        });

    }

    private void selectedIntro(ImageView image1) {
        image1.setImageResource(R.drawable.ic_selected_lang);
    }

    private void unSelectedIntro() {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        unSelectedIntro();
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