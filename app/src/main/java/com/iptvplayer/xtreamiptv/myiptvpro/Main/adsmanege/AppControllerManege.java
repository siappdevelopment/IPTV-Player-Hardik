package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.onesignal.notifications.INotificationClickEvent;
import com.onesignal.notifications.INotificationClickListener;
import com.onesignal.notifications.INotificationLifecycleListener;
import com.onesignal.notifications.INotificationWillDisplayEvent;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AppControllerManege extends Application {

    public static boolean reso__is_Fast_Start = false;
    public static boolean reso__Is_Splash_Initiated = false;
    public static boolean reso__Is_Screen_Check_Enabled = false;
    private static AppControllerManege instanceManage = new AppControllerManege();
    private int reso__Initialization_Count = 0;
    public static FirebaseRemoteConfig firebaseRemoteConfig;
    private static final List<OnConfigChangedListener> listeners =
            new ArrayList<>();

    public static AppControllerManege resoGetInstance() {
        if (instanceManage == null) {
            instanceManage = new AppControllerManege();
        }
        return instanceManage;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instanceManage = this;
        setAppOpenAds();
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this, "543b29b1-5595-41c5-be06-5f8da195f758");
        OneSignal.getNotifications().addClickListener(new INotificationClickListener() {
            @Override
            public void onClick(@NonNull INotificationClickEvent iNotificationClickEvent) {

                String url = iNotificationClickEvent
                        .getNotification()
                        .getLaunchURL();

                try {
                    Intent intent;

                    if (url != null && url.startsWith("tel:")) {
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(url));
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    }

                    intent.setComponent(null);
                    intent.setPackage(null);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(Intent.createChooser(intent, "Open with"));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        OneSignal.getNotifications().addForegroundLifecycleListener(new INotificationLifecycleListener() {
            @Override
            public void onWillDisplay(@NonNull INotificationWillDisplayEvent event) {

                syncFirebaseConfig_instant_value();
                AppControllerManege.addListener(listener);

                event.getNotification().display();
            }
        });

    }

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final OnConfigChangedListener listener = key -> {
        mainHandler.post(() -> {

            syncFirebaseConfig_instant_value();

        });
    };


    public static void removeListener() {
        listeners.remove(listeners);
    }

    public static void syncFirebaseConfig_instant_value() {

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        firebaseRemoteConfig.setDefaultsAsync(R.xml.hrse_firebase_default_config);


        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if (task.isSuccessful()) {
                    try {
                        ManegeVariView.manegeParameter(instanceManage, firebaseRemoteConfig);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                } else {
                    Toast.makeText(instanceManage,
                            "Something Went Wrong",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public static void addListener(OnConfigChangedListener listener) {
        listeners.add(listener);
    }

    public interface OnConfigChangedListener {
        void onChanged(String key);
    }

    private void setAppOpenAds() {

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {

                reso__Is_Screen_Check_Enabled = true;

                if (!AppControlPreference.getreso__Coming_Soon()) {
                    if (reso__Is_Splash_Initiated) {
                        if (reso__Initialization_Count == 0) {
                            if (!reso__is_Fast_Start) {
                                if (DevelSystemViewAd.reso__Is_Debuggle(activity)) {

                                    if (DevelSystemViewAd.reso__Developer_Option_Dialog != null) {
                                        if (DevelSystemViewAd.reso__Developer_Option_Dialog.isShowing()) {
                                            return;
                                        }
                                    }
                                    DevelSystemViewAd.reso__Dev_Dialog(activity);
                                    DevelSystemViewAd.reso__Debug_Check(new DevelSystemViewAd.no_debug() {
                                        @Override
                                        public void no_debug() {
                                            DevelSystemViewAd.reso__Developer_Option_Dialog.dismiss();
                                        }
                                    });
                                } else if (DevelSystemViewAd.Is_VPN_Checker(activity)) {
                                    if (DevelSystemViewAd.reso__Vpn_Checker_Dialog != null) {
                                        if (DevelSystemViewAd.reso__Vpn_Checker_Dialog.isShowing()) {
                                            return;
                                        }
                                    }
                                    DevelSystemViewAd.reso__VpnConnectionDialog(activity);
                                    DevelSystemViewAd.reso__Vpn_Checker(new DevelSystemViewAd.vpn_Checker() {
                                        @Override
                                        public void vpn_Checker() {
                                            DevelSystemViewAd.reso__Vpn_Checker_Dialog.dismiss();
                                        }
                                    });


                                }
                            } else {
                                if (!InterstitialAdScreen.reso__Is_Ads_Show_Enabled) {

                                    if (InterstitialAdScreen.reso__Is_Ads_Show_Enabled) {
                                        InterstitialAdScreen.reso__Ads_Show_Check(true);

                                    } else {
                                        reso__is_Fast_Start = false;
                                    }
                                } else {

                                    InterstitialAdScreen.reso__Ads_Show_Check(true);
                                }
                            }
                        }
                    }
                }
                reso__Initialization_Count++;

            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {

                reso__Initialization_Count--;
                if (reso__Initialization_Count == 0) {

                } else {
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

}