package com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity;

import static com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.QuizAdManege.fetchIpDetails;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.WindowCompat;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppOpenAdView;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.DevelSystemViewAd;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.ManegeParameter;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.ManegeUtilsView;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.ManegeVariView;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.NativeManegeView;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.NativeSmallView;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.QuizAdManege;
import com.iptvplayer.xtreamiptv.myiptvpro.Main.intro.IntroOneScreen;
import com.iptvplayer.xtreamiptv.myiptvpro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SplashActivity extends BaseActivity {

    public static Dialog reso__App_Dialog;
    public static String bysaTopicIntent = "";
    public static String intentCode = "";
    public static boolean reso__Quiz_Show = false;
    static List<String> listStrPos3 = new ArrayList<>();
    static List<String> listStrPos4 = new ArrayList<>();
    static List<String> listStrPos5 = new ArrayList<>();
    private final Executor iukeb__backgroundExecutor = Executors.newSingleThreadExecutor();
    private final String iukeb__prefKey = ManegeVariView.checkedInstallReferrer;
    FirebaseRemoteConfig firebaseRemoteConfig;
    String string1;
    String urlToOpen;
    String string = "";
    int anInt;

    private static int getVCode(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private static void updateLocationPreferences(String jsonString, String vadl, String gval, String tval) throws JSONException {
        JSONObject root = new JSONObject(jsonString);
        JSONObject appProxyStructure = root.getJSONObject(ManegeParameter.app_proxy_structure);
        Boolean app_pro_xy_Check_Ip = appProxyStructure.getBoolean(ManegeParameter.app_pro_xy_Check_Ip);

        String app_pro_xy_Ip_Checker_baseUrl = appProxyStructure.getString(ManegeParameter.app_pro_xy_Ip_Checker_baseUrl);

        AppControlPreference.setIp_Checker_baseUrl(app_pro_xy_Ip_Checker_baseUrl);
        AppControlPreference.setIPChecker(app_pro_xy_Check_Ip);


        try {
            JSONArray str3 = appProxyStructure.getJSONArray(vadl);
            for (int i = 0; i < str3.length(); i++) {
                String City_list = str3.getString(i);

                listStrPos3.add(City_list);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        AppControlPreference.setCityList(listStrPos3);

        try {
            JSONArray str4 = appProxyStructure.getJSONArray(gval);
            for (int i = 0; i < str4.length(); i++) {
                String State_list = str4.getString(i);

                listStrPos4.add(State_list);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        AppControlPreference.setStateList(listStrPos4);


        try {
            JSONArray str5 = appProxyStructure.getJSONArray(tval);
            for (int i = 0; i < str5.length(); i++) {
                String Country_list = str5.getString(i);
                listStrPos5.add(Country_list);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        AppControlPreference.setCountryList(listStrPos5);

    }

    @Override
    public void onResume() {
        AppOpenAdView.reso__Is_Ad_Showing = true;
        AppControllerManege.reso__is_Fast_Start = true;
        if (reso__Quiz_Show) {
            AppOpenAdView.reso__on_Complete_Ads.onCompleteAds(true);
        }
        overridePendingTransition(0, 0);
        super.onResume();
    }

    private void makeFullScreenImmersive() {

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        } else {
            @SuppressWarnings("deprecation")
            int flags =
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        makeFullScreenImmersive();

        AppInstallReceiver installReceiver = new AppInstallReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(installReceiver, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(installReceiver, intentFilter);
        }

        try {
            it();
        } catch (Exception e) {

        }

        FirebaseApp.initializeApp(this);
        AppControllerManege.reso__is_Fast_Start = true;
        AppOpenAdView.reso__Is_Ad_Showing = false;
        AppControllerManege.reso__Is_Splash_Initiated = false;

        if (getIntent().getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);

                if (key.equals("opentype")) {
                    string1 = value.toString();
                }
                if (key.equals("url")) {
                    urlToOpen = value.toString();
                }
            }

            if (string1 != null) {
                if (string1.equals("web")) {
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        Bundle bundle = new Bundle();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            bundle.putBinder("android.support.customtabs.extra.SESSION", null);
                        }
                        intent.putExtras(bundle);
                        intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", getResources().getColor(R.color.color_primary));
                        intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                        intent.setPackage("com.android.chrome");
                        intent.setData(Uri.parse(urlToOpen));
                        startActivity(intent, null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    finishAffinity();
                    return;
                }
            }
        }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        anInt = getVCode(SplashActivity.this);

        reso__Process_Intent_Data();


        if (DevelSystemViewAd.reso__Is_Debuggle(SplashActivity.this)) {
            DevelSystemViewAd.reso__Dev_Dialog(SplashActivity.this);
            DevelSystemViewAd.reso__Debug_Check(new DevelSystemViewAd.no_debug() {
                @Override
                public void no_debug() {
                    DevelSystemViewAd.reso__Developer_Option_Dialog.dismiss();
                    if (DevelSystemViewAd.reso__Check_Internet_Connection(SplashActivity.this)) {

                        ManegeUtilsView.isManegeStructure(SplashActivity.this);
                        syncFirebaseConfig();
                    } else {
                        reso__Display_No_Internet_Dialog();
                    }
                }
            });
        } else if (DevelSystemViewAd.Is_VPN_Checker(SplashActivity.this)) {
            DevelSystemViewAd.reso__VpnConnectionDialog(SplashActivity.this);
            DevelSystemViewAd.reso__Vpn_Checker(new DevelSystemViewAd.vpn_Checker() {
                @Override
                public void vpn_Checker() {
                    DevelSystemViewAd.reso__Vpn_Checker_Dialog.dismiss();
                    if (DevelSystemViewAd.reso__Check_Internet_Connection(SplashActivity.this)) {

                        ManegeUtilsView.isManegeStructure(SplashActivity.this);
                        syncFirebaseConfig();
                    } else {
                        reso__Display_No_Internet_Dialog();
                    }
                }
            });

        } else {
            if (DevelSystemViewAd.reso__Check_Internet_Connection(SplashActivity.this)) {

                ManegeUtilsView.isManegeStructure(SplashActivity.this);

                syncFirebaseConfig();
            } else {
                reso__Display_No_Internet_Dialog();
            }
        }


    }

    void it() {
        if (getPreferences(MODE_PRIVATE).getBoolean(iukeb__prefKey, false)) {
            return;
        }
        InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(this).build();
        iukeb__backgroundExecutor.execute(() -> getInstallReferrerFromClient(referrerClient));
    }

    void getInstallReferrerFromClient(InstallReferrerClient referrerClient) {
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                    try {
                        ReferrerDetails response = referrerClient.getInstallReferrer();
                        String referrerUrl = response.getInstallReferrer();

                        AppControlPreference.iukeb__setReferrerUrl(referrerUrl);


                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    referrerClient.endConnection();
                }
            }


            @Override
            public void onInstallReferrerServiceDisconnected() {

            }
        });
    }

    private void reso__Display_No_Internet_Dialog() {
        DevelSystemViewAd.reso__No_Internet_Dialog(SplashActivity.this);
        DevelSystemViewAd.reso__Check_Network_Availablity(new DevelSystemViewAd.no_internet() {
            @Override
            public void no_internet() {
                if (DevelSystemViewAd.reso__Check_Internet_Connection(SplashActivity.this)) {
                    DevelSystemViewAd.reso__No_Internet_Dialog.dismiss();
                    if (DevelSystemViewAd.reso__Is_Debuggle(SplashActivity.this)) {

                        if (DevelSystemViewAd.reso__Developer_Option_Dialog != null) {
                            if (DevelSystemViewAd.reso__Developer_Option_Dialog.isShowing()) {
                                return;
                            }
                        }
                        DevelSystemViewAd.reso__Dev_Dialog(SplashActivity.this);
                        DevelSystemViewAd.reso__Debug_Check(new DevelSystemViewAd.no_debug() {
                            @Override
                            public void no_debug() {
                                DevelSystemViewAd.reso__Developer_Option_Dialog.dismiss();
                                AppControllerManege.reso__is_Fast_Start = true;
                                AppOpenAdView.reso__Is_Ad_Showing = false;

                                if (DevelSystemViewAd.reso__Is_Debuggle(SplashActivity.this)) {
                                    DevelSystemViewAd.reso__Dev_Dialog(SplashActivity.this);
                                    DevelSystemViewAd.reso__Debug_Check(new DevelSystemViewAd.no_debug() {
                                        @Override
                                        public void no_debug() {
                                            DevelSystemViewAd.reso__Developer_Option_Dialog.dismiss();
                                            if (DevelSystemViewAd.reso__Check_Internet_Connection(SplashActivity.this)) {

                                                ManegeUtilsView.isManegeStructure(SplashActivity.this);
                                                syncFirebaseConfig();
                                            } else {
                                                reso__Display_No_Internet_Dialog();
                                            }
                                        }
                                    });
                                } else {
                                    if (DevelSystemViewAd.reso__Check_Internet_Connection(SplashActivity.this)) {

                                        ManegeUtilsView.isManegeStructure(SplashActivity.this);
                                        syncFirebaseConfig();
                                    } else {
                                        reso__Display_No_Internet_Dialog();
                                    }
                                }
                                ManegeUtilsView.isManegeStructure(SplashActivity.this);
                                syncFirebaseConfig();
                            }
                        });
                    } else {
                        ManegeUtilsView.isManegeStructure(SplashActivity.this);
                        syncFirebaseConfig();
                    }
                }
            }
        });
    }

    private void reso__Display_Warning_Dialog(String message, final String link, String type) {

        reso__App_Dialog = new Dialog(this);
        reso__App_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = reso__App_Dialog.getLayoutInflater();
        View customView = inflater.inflate(R.layout.reso_maintenance_dialog, null);
        reso__App_Dialog.setContentView(customView);
        reso__App_Dialog.getWindow().getAttributes().windowAnimations = R.style.vardo_DialogAnimation;
        reso__App_Dialog.setCancelable(true);
        reso__App_Dialog.setCanceledOnTouchOutside(false);
        TextView txt_done = reso__App_Dialog.findViewById(R.id.reso__yes);
        TextView txt_cancel = reso__App_Dialog.findViewById(R.id.reso__no);
        TextView txt_message = reso__App_Dialog.findViewById(R.id.reso__Maintenance);
        txt_message.setText(message);

        reso__App_Dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (type.equals("alert_msg")) {
                    finishAffinity();
                } else {
                    screenState();
                }
            }
        });
        if (type.equals("alert_msg")) {
            txt_done.setText("Ok");
            txt_cancel.setVisibility(View.GONE);
        } else {
            txt_done.setText("Update");
            txt_cancel.setVisibility(View.VISIBLE);
        }

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reso__App_Dialog.dismiss();
                screenState();
            }
        });

        txt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("alert_msg")) {
                    finishAffinity();
                } else {
                    reso__App_Dialog.dismiss();
                    Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(rateIntent);
                    finish();
                }
            }
        });

        reso__App_Dialog.show();
    }

    private void reso__Process_Intent_Data() {

        if (getIntent() != null) {
            try {
                Uri uri = getIntent().getData();
                if (uri != null) {
                    String[] urlparams = uri.getQueryParameter("id").split("/");
                    bysaTopicIntent = urlparams[urlparams.length - 1];
                    intentCode = urlparams[urlparams.length - 2];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            bysaTopicIntent = "";
            intentCode = "";
        }
    }

    private void syncFirebaseConfig() {


        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        firebaseRemoteConfig.setDefaultsAsync(R.xml.hrse_firebase_default_config);


        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if (task.isSuccessful()) {
                    string = ManegeParameter.ssfsfsfsf + anInt;
                    String jsonString = firebaseRemoteConfig.getString(string);
                    try {
                        updateLocationPreferences(jsonString, ManegeParameter.app_pro_xy_list_City, ManegeParameter.app_pro_xy_list_State, ManegeParameter.app_pro_xy_list_Country);
                        fetchIpDetails(SplashActivity.this, new QuizAdManege.IpCheckCallback() {
                            @Override
                            public void onResult(boolean isCheckIp) {
                                if (isCheckIp) {
                                    AppControlPreference.setCheckExclude(true);
                                } else {
                                    AppControlPreference.setCheckExclude(false);
                                }
                                try {
                                    ManegeVariView.manegeParameter(SplashActivity.this, firebaseRemoteConfig);
                                    JSONObject root = new JSONObject(jsonString);
                                    Long VERSION_CODE = root.getLong(ManegeParameter.Version_Code);
                                    Long VERSION_NAME = root.getLong(ManegeParameter.Version_Name);
                                    Boolean FORCLY_UPDATE_APP = root.getBoolean(ManegeParameter.Forcly_Update_App);
                                    Boolean IS_UPDATE_APP = root.getBoolean(ManegeParameter.Update_App);
                                    Boolean MAINTENANCE_DIALOG = root.getBoolean(ManegeParameter.Maintenance_Dialog);
                                    Boolean UPDATE_COMING = root.getBoolean(ManegeParameter.Update_Coming_Soon);
                                    String UPDATE_LINK = root.getString(ManegeParameter.App_Update_Link);
                                    String MAINTENANCE = root.getString(ManegeParameter.Maintenance_Text);


                                    float version = Float.parseFloat(String.valueOf(VERSION_NAME));
                                    float real_version = Float.parseFloat(String.valueOf(VERSION_NAME));
                                    if (FORCLY_UPDATE_APP && version > real_version) {
                                        reso__Display_Warning_Dialog(MAINTENANCE, UPDATE_LINK, "forcly_update");
                                    } else {
                                        if (MAINTENANCE_DIALOG) {
                                            reso__Display_Warning_Dialog(MAINTENANCE, UPDATE_LINK, "alert_msg");
                                        } else if (IS_UPDATE_APP && version > real_version) {
                                            reso__Display_Warning_Dialog(MAINTENANCE, UPDATE_LINK, "update");
                                        } else {
                                            int version1 = Integer.parseInt(String.valueOf(VERSION_CODE));
                                            int real_version1 = Integer.parseInt(String.valueOf(VERSION_CODE));
                                            if (version1 == real_version1) {
                                                if (UPDATE_COMING) {
                                                    AppControlPreference.setreso__Coming_Soon(true);
                                                    startActivity(new Intent(SplashActivity.this, ComingSoonActivity.class));
                                                    finish();
                                                    return;
                                                }
                                            }

                                            if (AppControlPreference.getreso__Coming_Soon()) {
                                                screenState();
                                            } else {
                                                if (AppControlPreference.getSplash_App_Open_Ads_Show()) {
                                                    reso__Show_App_Open_Ad();
                                                } else {
                                                    screenState();
                                                }

                                                if (!AppControlPreference.getreso__Coming_Soon()) {
                                                    reso__Show_Native_Ad();
                                                }
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }


                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                } else {
                    Toast.makeText(SplashActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void reso__Show_Native_Ad() {
        if (AppControlPreference.getreso__Ads_Type().equals("PreLoad")) {
            NativeManegeView.reso__Load_Admob_Big_NativeAd(SplashActivity.this, null, null, false);
            if (!AppControlPreference.getreso__Small_Ads_Type().equals("banner")) {
                NativeSmallView.reso__Admob_Bottom_ADS(SplashActivity.this, null, null);
            }
            InterstitialAdScreen.reso__Admob_Fullscreen_ADS_Load(SplashActivity.this);
        }
    }

    private void reso__Show_App_Open_Ad() {

        if (AppControlPreference.getreso__Ads_Free()) {
            screenState();
            return;
        }

        if (AppControlPreference.getreso__Splash_Ads_Type().equals("Appopen")) {
            AppOpenAdView.reso__App_Starting_Display(SplashActivity.this, new AppOpenAdView.OnCompleteAds() {
                @Override
                public void onCompleteAds(boolean b) {
                    screenState();
                    AppControllerManege.reso__is_Fast_Start = false;
                }
            });
        } else {
            AppOpenAdView.reso__Fullscreen_ADS_Display(SplashActivity.this, new AppOpenAdView.OnCompleteAds() {
                @Override
                public void onCompleteAds(boolean b) {
                    screenState();
                    AppControllerManege.reso__is_Fast_Start = false;
                }
            });
        }

    }

    private void screenState() {


        AppControllerManege.reso__Is_Splash_Initiated = true;
        if (AppControlPreference.getreso__Coming_Soon()) {
            Intent intent = new Intent(SplashActivity.this, ComingSoonActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (AppControlPreference.getDefaultPermission()) {
                if (isDefaultLauncher()) {
                    if (AppControlPreference.getreso__Intro_Screen_Display() == 0) {
                        startActivity(new Intent(SplashActivity.this, HomeLaunchScreen.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, IntroOneScreen.class));
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, PermissionActivity.class));
                }
            } else {
                if (AppControlPreference.getreso__Intro_Screen_Display() == 0) {
                    startActivity(new Intent(SplashActivity.this, HomeScreen2.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, IntroOneScreen.class));
                }
            }

            finish();

        }
    }

    private boolean isDefaultLauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        ResolveInfo resolveInfo = getPackageManager()
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo == null) {
            return false;
        }

        return resolveInfo.activityInfo != null &&
                resolveInfo.activityInfo.packageName.equals(getPackageName());
    }

}