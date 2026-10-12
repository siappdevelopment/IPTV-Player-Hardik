package com.iptvplayer.xtreamiptv.myiptvpro.Main.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PreferenceDayCycle {

    private static final String PREF_NAME = "AppRetentionPrefe";
    private static final String FIRST_DAY = "first_day";
    private static final String DAY_COUNT = "day_count";

    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static void checkAndUpdateDayCount(Context context) {

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String currentDateStr = sdf.format(new Date());
        String firstDayStr = prefs.getString(FIRST_DAY, null);

        if (firstDayStr == null) {
            editor.putString(FIRST_DAY, currentDateStr);
            editor.putInt(DAY_COUNT, 1);
            editor.apply();
        } else {
            try {
                Date firstDate = sdf.parse(firstDayStr);
                Date currentDate = sdf.parse(currentDateStr);

                if (firstDate != null && currentDate != null) {

                    long diff = currentDate.getTime() - firstDate.getTime();

                    int daysPassed = (int) (diff / (1000 * 60 * 60 * 24)) + 1;

                    if (daysPassed > 30) daysPassed = 30;

                    editor.putInt(DAY_COUNT, daysPassed);
                    editor.apply();
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getDayCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(DAY_COUNT, 1);
    }
}
