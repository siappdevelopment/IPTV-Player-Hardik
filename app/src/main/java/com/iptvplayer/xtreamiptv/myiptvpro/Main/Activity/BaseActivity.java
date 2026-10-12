package com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.QuizAdManege;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        if (QuizAdManege.isQuizOpen) {

            QuizAdManege.isQuizOpen = false;


                if (QuizAdManege.pendingQuizCallback != null) {

                    QuizAdManege.pendingQuizCallback
                            .onCompeteAds(true);

                }

        }
    }
}
