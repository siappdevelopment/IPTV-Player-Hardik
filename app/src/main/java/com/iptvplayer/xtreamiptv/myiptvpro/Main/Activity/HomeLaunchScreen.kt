package com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Adapters.AppAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Adapters.AppInfo
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Adapters.HomePagerAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Adapters.LanguageAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Other.OnSwipeTouchListener
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Other.PackageReceiver
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Other.WidgetManager
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.getAdIndex
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.getAlternativeList
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.setAdIndex
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppOpenAdView
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppOpenAdView.OnCompleteAds
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen.OnCompeteAds
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.NativeManegeView
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.QuizAdManege
import com.iptvplayer.xtreamiptv.myiptvpro.Main.intro.IntroOneScreen
import com.iptvplayer.xtreamiptv.myiptvpro.Main.intro.IntroTwoActivity
import com.iptvplayer.xtreamiptv.myiptvpro.Main.model.LanguageModel
import com.iptvplayer.xtreamiptv.myiptvpro.Main.viewmodel.PreferenceDayCycle
import com.iptvplayer.xtreamiptv.myiptvpro.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Random
import kotlin.math.abs

class HomeLaunchScreen : BaseActivity() {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var widgetPickerBehavior: BottomSheetBehavior<LinearLayout>
    private val widgetManager by lazy {
        WidgetManager(this)
    }

    fun fetchWidgetManager(): WidgetManager = widgetManager
    private var isInEditMode: Boolean = false
    lateinit var swipe_right: LinearLayout
    lateinit var swipe_bottom: LinearLayout
    lateinit var linearIntro: LinearLayout
    lateinit var btnNext: ImageView
    lateinit var coordinatorlayoutIntro: CoordinatorLayout
    var clickEventInt = 0

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: LanguageAdapter
    private lateinit var appAdapter: AppAdapter

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (Intent.ACTION_MAIN == intent.getAction()) {
            if (intent.hasCategory(Intent.CATEGORY_HOME)) {
                handleHomePressed()
                updateSystemBars(false)

            }
        }
    }

    private fun setAppRetentionLogEvent(dayCount: Int) {
        if (dayCount < 1 || dayCount > 30) return

        FirebaseApp.initializeApp(this)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val prefKey = "retention_day_" + dayCount

        if (!AppControlPreference.getRetentionDay(prefKey)) {
            val bundle = Bundle()
            bundle.putInt("day", dayCount)

            val eventName = "Retention_Day_" + dayCount

            firebaseAnalytics.logEvent(eventName, bundle);

            AppControlPreference.setRetentionDay(prefKey, true)
        }
    }


    override fun onResume() {
        super.onResume()
        setAppRetention()
        overridePendingTransition(0, 0)
    }

    private fun setAppRetention() {
        PreferenceDayCycle.checkAndUpdateDayCount(this)

        val dayCount = PreferenceDayCycle.getDayCount(this)

        setAppRetentionLogEvent(dayCount)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("launcher_pref", MODE_PRIVATE)
        val openIntroAfterLauncher = prefs.getBoolean("open_intro_after_launcher", false)

        prefs.edit().putBoolean("open_intro_after_launcher", false).apply()

        AppControlPreference.setDefaultPermissionCancel(false)

        if (AppControlPreference.getIntroLauncher()) {
            if (AppControlPreference.getreso__Intro_Screen_Display() == 0) {

            } else {
                if (!AppControlPreference.getIntroQuiz()) {
                    prefs.edit().putBoolean("open_intro_after_launcher", false).apply()
                    InterstitialAdScreen.reso__Fullscreen_ADS_Display(
                        this@HomeLaunchScreen,
                        object : OnCompeteAds {
                            override fun onCompeteAds(b: Boolean) {
                                startActivity(
                                    Intent(
                                        this@HomeLaunchScreen,
                                        IntroOneScreen::class.java
                                    )
                                )
                                finish()
                            }
                        },
                        false
                    )
                    AppControlPreference.setIntroQuiz(true)

                } else {

                }
            }
        } else {
            if (AppControlPreference.getIntroCounts()) {

            } else {
                if (AppControlPreference.getQuizAdsButton()) {

                    if (AppControlPreference.getreso__Intro_Screen_Display() == 0) {

                    } else {

                        if (openIntroAfterLauncher) {
                            InterstitialAdScreen.reso__Fullscreen_ADS_Display(
                                this@HomeLaunchScreen,
                                object : OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        startActivity(
                                            Intent(
                                                this@HomeLaunchScreen,
                                                IntroOneScreen::class.java
                                            )
                                        )
                                        finish()
                                    }
                                },
                                false
                            )

                            return
                        }
                    }
                } else {
                    if (AppControlPreference.getreso__Intro_Screen_Display() == 0) {

                    } else {
                        if (openIntroAfterLauncher) {
                            InterstitialAdScreen.reso__Fullscreen_ADS_Display(
                                this@HomeLaunchScreen,
                                object : OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        startActivity(
                                            Intent(
                                                this@HomeLaunchScreen,
                                                IntroOneScreen::class.java
                                            )
                                        )
                                        finish()
                                    }
                                },
                                false
                            )
                            return
                        }

                    }
                }
            }


        }

        setContentView(R.layout.activity_main2)
        linearIntro = findViewById(R.id.linearIntro)
        btnNext = findViewById(R.id.btnNext)
        recyclerView = findViewById(R.id.rvLanguage)
        coordinatorlayoutIntro = findViewById(R.id.coordinatorlayoutIntro)

        // Android 16+ forces edge-to-edge: keep the content clear of the status / navigation bars
        // (the wallpaper still shows behind them on the launcher pages).
        linearIntro.applySystemBarInsets(top = true, bottom = true)
        // The launcher pages (LeftFragment, MainFragment) apply the top / bottom insets to themselves, so each
        // page paints its own background behind the status / navigation bars instead of showing the wallpaper there.
        findViewById<View>(R.id.bottom_controls).applySystemBarInsets(top = false, bottom = true, sides = false)

        widgetManager.startListening()
        widgetManager.seedDefaultShortcuts()

        if (AppControlPreference.getIntroLauncher()) {
            linearIntro.visibility = View.VISIBLE
            coordinatorlayoutIntro.visibility = View.GONE

            val languageList = listOf(
                LanguageModel(R.drawable.ic_lan_english, "English", "en", "(English)"),
                LanguageModel(R.drawable.ic_lan_spanish, "Spanish", "es", "(Español)"),
                LanguageModel(R.drawable.ic_lan_arabic, "Arabic", "ar", "(عربي)"),
                LanguageModel(R.drawable.ic_lan_french, "French", "fr", "(Français)"),
                LanguageModel(R.drawable.ic_lan_portuguage, "Portuguese", "pt", "(Português)"),
                LanguageModel(R.drawable.ic_lan_german, "German", "de", "(Deutsch)"),
                LanguageModel(R.drawable.ic_lan_japanise, "Japanese", "ja", "(日本語)"),
                LanguageModel(R.drawable.ic_persian, "Persian", "fa", "(فارسی)"),
                LanguageModel(R.drawable.ic_italian, "Italian", "it", "(Italiano)"),
                LanguageModel(R.drawable.ic_lan_russian, "Russian", "ru", "(Русский)")
            )

            adapter = LanguageAdapter(languageList)

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

            btnNext.setOnClickListener {
                InterstitialAdScreen.reso__Fullscreen_ADS_Display(
                    this@HomeLaunchScreen,
                    object : OnCompeteAds {
                        override fun onCompeteAds(b: Boolean) {
                            if (AppControlPreference.getDefaultPermission()) {

                                if (AppControlPreference.getInsideHomeCount()) {
                                    if (AppControlPreference.getreso__Intro_Screen_Display() == 1) {

                                        AppControlPreference.setIntroLauncher(false)
                                        AppControlPreference.setIntroCounts(true)
                                        startActivity(
                                            Intent(
                                                this@HomeLaunchScreen,
                                                HomeLaunchScreen::class.java
                                            )
                                        )


                                    } else {
                                        startActivity(
                                            Intent(
                                                this@HomeLaunchScreen,
                                                IntroTwoActivity::class.java
                                            )
                                        )

                                    }
                                } else {
                                    if (AppControlPreference.getreso__Intro_Screen_Display() == 1) {
                                        AppControlPreference.setIntroLauncher(false)
                                        startActivity(
                                            Intent(
                                                this@HomeLaunchScreen,
                                                HomeLaunchScreen::class.java
                                            )
                                        )


                                    } else {
                                        startActivity(
                                            Intent(
                                                this@HomeLaunchScreen,
                                                IntroTwoActivity::class.java
                                            )
                                        )

                                    }
                                }

                            } else {
                                if (AppControlPreference.getreso__Intro_Screen_Display() == 1) {
                                    if (AppControlPreference.getQuizAdsButton()) {
                                        AppControlPreference.setIntroLauncher(false)
                                        startActivity(
                                            Intent(
                                                this@HomeLaunchScreen,
                                                HomeLaunchScreen::class.java
                                            )
                                        )

                                    } else {
                                        startActivity(
                                            Intent(
                                                this@HomeLaunchScreen,
                                                HomeScreen2::class.java
                                            )
                                        )
                                    }
                                } else {
                                    startActivity(
                                        Intent(
                                            this@HomeLaunchScreen,
                                            IntroTwoActivity::class.java
                                        )
                                    )
                                }
                            }
                        }
                    },
                    false
                )
            }
        } else {
            linearIntro.visibility = View.GONE
            coordinatorlayoutIntro.visibility = View.VISIBLE
            setAppRetention();


            swipe_right = findViewById(R.id.swipe_right)
            swipe_bottom = findViewById(R.id.swipe_bottom)

            if (!AppControlPreference.getPositionEvent()) {
                swipe_right.visibility = View.VISIBLE
            } else {
                swipe_right.visibility = View.GONE
            }
            swipe_right.setOnTouchListener(
                object : OnSwipeTouchListener(this@HomeLaunchScreen) {
                    override fun onSwipeRight() {
                        super.onSwipeRight()

                        swipe_right.visibility = View.GONE
                        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager)
                        viewPager2.setCurrentItem(0, true)
                    }
                })

            swipe_bottom.setOnTouchListener(
                object : OnSwipeTouchListener(this@HomeLaunchScreen) {
                    override fun onSwipeBottom() {
                        super.onSwipeBottom()
                        swipe_bottom.visibility = View.GONE
                    }
                })



            setupBottomSheet()
            setupWidgetPicker()
            setupViewPager()
            setupAppList()
            setupDock()
            setupTabLayout()

        }


        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (AppControlPreference.getIntroLauncher()) {
                        showQuiz(this@HomeLaunchScreen)

                    } else {
                        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

                        if (isInEditMode) {
                            exitEditMode()
                        } else if (widgetPickerBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                            widgetPickerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        } else if (viewPager.currentItem != 1) {
                            viewPager.currentItem = 1
                        }
                    }


                }
            })
    }


    private fun setupBottomSheet() {
        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.skipCollapsed = true

        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.isFitToContents = false

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    swipe_bottom.visibility = View.GONE
                    AppControlPreference.setBottomEvent(true)
                    bottomSheetBehavior.isDraggable = true
                    updateSystemBars(true)
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    updateSystemBars(false)
                }

                if (newState == BottomSheetBehavior.STATE_COLLAPSED && bottomSheetBehavior.peekHeight == 0) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        val etSearch = findViewById<EditText>(R.id.et_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val rvApps = findViewById<RecyclerView>(R.id.rv_apps)
                val adapter = rvApps.adapter as? AppAdapter
                adapter?.filterApps(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handleHomePressed() {
        if (AppControlPreference.getIntroLauncher()) {
            showQuiz(this@HomeLaunchScreen)

        } else {

            val viewPager = findViewById<ViewPager2>(R.id.view_pager)

            viewPager?.setCurrentItem(1, false)

            if (::bottomSheetBehavior.isInitialized &&
                bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
            ) {

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }


    }


    private var uninstallApp: AppInfo? = null
    private var uninstallPosition = -1
    private val uninstallLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            uninstallApp?.let { app ->

                val isInstalled = try {

                    packageManager.getPackageInfo(
                        app.packageName.toString(),
                        0
                    )

                    true

                } catch (e: Exception) {

                    false
                }

                // uninstall success
                if (!isInstalled) {

                    appAdapter.removeApp(
                        app,
                        uninstallPosition
                    )
                }
            }
        }

    private fun setupAppList() {

        val rvApps = findViewById<RecyclerView>(R.id.rv_apps)

        rvApps.layoutManager = GridLayoutManager(this, 4)

        rvApps.isNestedScrollingEnabled = true

        rvApps.setHasFixedSize(true)

        rvApps.setItemViewCacheSize(20)

        rvApps.isDrawingCacheEnabled = true

        rvApps.drawingCacheQuality =
            View.DRAWING_CACHE_QUALITY_HIGH

        lifecycleScope.launch(Dispatchers.IO) {

            val apps = getInstalledApps()

            withContext(Dispatchers.Main) {

                appAdapter = AppAdapter(
                    this@HomeLaunchScreen,
                    apps
                ) { app, position ->

                    uninstallApp = app

                    uninstallPosition = position

                    val intent = Intent(Intent.ACTION_DELETE)

                    intent.data =
                        Uri.parse("package:${app.packageName}")

                    uninstallLauncher.launch(intent)
                }

                rvApps.adapter = appAdapter
            }
        }
    }

    private fun getInstalledApps(): List<AppInfo> {

        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val apps = pm.queryIntentActivities(intent, 0)
        val appList = mutableListOf<AppInfo>()

        val myPackageName = packageName

        for (resolveInfo in apps) {

            val appPackageName = resolveInfo.activityInfo.packageName

            if (appPackageName == myPackageName) {
                continue
            }

            val appInfo = AppInfo(
                label = resolveInfo.loadLabel(pm),
                packageName = appPackageName,
                icon = resolveInfo.loadIcon(pm)
            )

            appList.add(appInfo)
        }

        return appList.sortedBy { it.label.toString().lowercase() }
    }

    var isDraggingItem: Boolean = false

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return super.dispatchTouchEvent(ev)

        if (ev.action == MotionEvent.ACTION_DOWN) {
            isDraggingItem = false
        }

        if (isDraggingItem) {
            return super.dispatchTouchEvent(ev)
        }

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        val isMainPage = viewPager.currentItem != 0
        if (isMainPage && canOpenAppDrawer()) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = ev.x
                    startY = ev.y
                }

                MotionEvent.ACTION_MOVE -> {
                    if (startY > 0) {
                        val dx = ev.x - startX
                        val dy = ev.y - startY

                        if (dy < -150 && abs(dy) > abs(dx) * 2.0) {
                            openAppDrawer()

                            startX = 0f
                            startY = 0f

                            val cancelEvent = MotionEvent.obtain(ev)
                            cancelEvent.action = MotionEvent.ACTION_CANCEL
                            super.dispatchTouchEvent(cancelEvent)
                            cancelEvent.recycle()

                            return true
                        }
                    }
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    fun canOpenAppDrawer(): Boolean {
        return bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN &&
                widgetPickerBehavior.state == BottomSheetBehavior.STATE_HIDDEN
    }

    fun openAppDrawer() {
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.peekHeight = 0

        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheet.translationY = 0f

        bottomSheet.post {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setupViewPager() {
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter = HomePagerAdapter(this)

        val widgets = widgetManager.loadWidgets()
        val shortcuts = widgetManager.loadShortcuts()
        val maxWidgetPage = widgets.maxOfOrNull { it.pageIndex } ?: 0
        val maxShortcutPage = shortcuts.maxOfOrNull { it.pageIndex } ?: 0
        val neededPages = maxOf(maxWidgetPage, maxShortcutPage) + 1

        (viewPager.adapter as? HomePagerAdapter)?.setNumMainPages(neededPages)

        viewPager.setCurrentItem(1, false)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
                val maxTranslation = dpToPx(80).toFloat()

                if (position == 0) {
                    bottomSheet.translationY = (1 - positionOffset) * maxTranslation
                } else if (position == 1) {
                    bottomSheet.translationY = positionOffset * maxTranslation
                } else {
                    bottomSheet.translationY = maxTranslation
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val bottomControls = findViewById<LinearLayout>(R.id.bottom_controls)
                val indicator = findViewById<TabLayout>(R.id.page_indicator)
                val dock = findViewById<LinearLayout>(R.id.dock)
                val isMainPage = position != 0
                val alreadyShown = AppControlPreference.getPositionEvent()

                clickEventInt++

                if (!alreadyShown) {
                    if (clickEventInt == 2) {

                        swipe_bottom.visibility = View.VISIBLE
                        AppControlPreference.setPositionEvent(true)

                    }

                }

                if (AppControlPreference.getQuizAdsButton()) {
                    if (AppControlPreference.getQuizDirectPlay()) {
                        if (AppControlPreference.getIsAlternativeAds()) {
                            val adType: String? = getNextAdType()
                            when (adType) {
                                "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                    this@HomeLaunchScreen,
                                    OnCompleteAds { b: Boolean -> })

                                "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                    this@HomeLaunchScreen,
                                    OnCompeteAds { b: Boolean -> })

                                "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                    this@HomeLaunchScreen,
                                    OnCompeteAds {})

                                "Quiz" -> showQuiz(this@HomeLaunchScreen)
                                "None" -> {
                                    val launchIntent =
                                        packageManager.getLaunchIntentForPackage(packageName)
                                    if (launchIntent != null) {
                                        startActivity(launchIntent)
                                    } else if (packageName == "com.android.settings") {
                                        startActivity(Intent(Settings.ACTION_SETTINGS))
                                    }
                                }
                            }
                        } else if (AppControlPreference.getIsRandomAds()) {
                            val adTypes = listOf("Appopen", "Interstitial", "Native", "Quiz")
                            val adType = adTypes.random()

                            when (adType) {

                                "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                    this@HomeLaunchScreen,
                                    OnCompleteAds { b: Boolean -> }
                                )

                                "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                    this@HomeLaunchScreen,
                                    OnCompeteAds { b: Boolean -> }
                                )

                                "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                    this@HomeLaunchScreen,
                                    OnCompeteAds {}
                                )

                                "Quiz" -> showQuiz(this@HomeLaunchScreen)
                            }
                        } else if (AppControlPreference.getIsBothAdPriority()) {
                            if (AppControlPreference.getIsBothAdRandomPriority()) {
                                val random: Random = Random()
                                val number: Int = random.nextInt(3) + 1
                                if (1 == number) {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompeteAds {
                                                        override fun onCompeteAds(b: Boolean) {

                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else if (2 == number) {

                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                AppOpenAdView.reso__App_Starting_Display(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompleteAds {
                                                        override fun onCompleteAds(b: Boolean) {

                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                        this@HomeLaunchScreen
                                                    ) {

                                                    }
                                                }, 1500)

                                            }
                                        },
                                        true
                                    )
                                }
                            } else {
                                if (AppControlPreference.getreso__Alternet_Ads() == 1) {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompeteAds {
                                                        override fun onCompeteAds(b: Boolean) {

                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else if (AppControlPreference.getreso__Alternet_Ads() == 2) {

                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                AppOpenAdView.reso__App_Starting_Display(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompleteAds {
                                                        override fun onCompleteAds(b: Boolean) {

                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else if (AppControlPreference.getreso__Alternet_Ads() == 3) {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                        this@HomeLaunchScreen
                                                    ) {

                                                    }
                                                }, 1500)
                                            }
                                        },
                                        true
                                    )
                                }
                            }

                        } else {

                            if (AppControlPreference.getAppClickAdsType().equals("Appopen")) {
                                AppOpenAdView.reso__App_Starting_Display(
                                    this@HomeLaunchScreen,
                                    object : OnCompleteAds {
                                        override fun onCompleteAds(b: Boolean) {

                                        }
                                    })
                            } else if (AppControlPreference.getAppClickAdsType()
                                    .equals("Interstitial")
                            ) {

                                InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                    this@HomeLaunchScreen,
                                    object : OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {

                                        }
                                    })
                            } else if (AppControlPreference.getAppClickAdsType().equals("Native")) {
                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(this@HomeLaunchScreen) {

                                }

                            } else if (AppControlPreference.getAppClickAdsType().equals("Quiz")) {
                                showQuiz(this@HomeLaunchScreen)

                            } else {
                                val launchIntent =
                                    packageManager.getLaunchIntentForPackage(packageName)
                                if (launchIntent != null) {
                                    startActivity(launchIntent)
                                } else if (packageName == "com.android.settings") {
                                    startActivity(Intent(Settings.ACTION_SETTINGS))
                                }
                            }
                        }


                    }

                }



                if (isMainPage && !isInEditMode) {
                    bottomControls.visibility = View.VISIBLE
                    indicator.visibility = View.VISIBLE
                    dock.visibility = View.VISIBLE
                } else if (!isMainPage) {
                    bottomControls.visibility = View.INVISIBLE
                }

                if (position == 0) {
                    updateSystemBars(true)
                } else {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN &&
                        widgetPickerBehavior.state == BottomSheetBehavior.STATE_HIDDEN
                    ) {
                        updateSystemBars(false)
                    }
                }

                if (position == 1) {
                    // Main Screen: Allow Drag, but keep it hidden on arrival
                    bottomSheetBehavior.isDraggable = true
                    bottomSheetBehavior.peekHeight = 0
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    findViewById<LinearLayout>(R.id.bottom_sheet).translationY = 0f
                } else {
                    // Other Screens: Disable Drag, keep hidden
                    bottomSheetBehavior.isDraggable = false
                    bottomSheetBehavior.peekHeight = 0
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }


            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    val viewPager = findViewById<ViewPager2>(R.id.view_pager)
                    if (viewPager.currentItem == 1) {
                        bottomSheetBehavior.peekHeight = 0
                        // Only force hide if it's currently in the collapsed state
                        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                    }
                }
            }
        })
    }


    private fun isMyAppLauncherDefault(): Boolean {
        val localPackageManager = packageManager
        val intent = Intent("android.intent.action.MAIN")
        intent.addCategory("android.intent.category.HOME")
        val resolveInfo =
            localPackageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == packageName
    }

    private fun promptToSetDefaultLauncher() {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        startActivity(intent)
    }

    private fun setupWidgetPicker() {
        val widgetPickerSheet = findViewById<LinearLayout>(R.id.widget_picker_bottom_sheet)
        widgetPickerBehavior = BottomSheetBehavior.from(widgetPickerSheet)
        widgetPickerBehavior.isHideable = true
        widgetPickerBehavior.peekHeight = 0
        widgetPickerBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        widgetPickerBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    updateSystemBars(true)
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        updateSystemBars(false)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }


    private fun startWidgetFlow(widgetId: Int, provider: AppWidgetProviderInfo) {
        if (provider.configure != null) {
            try {
                widgetManager.getAppWidgetHost().startAppWidgetConfigureActivityForResult(
                    this,
                    widgetId,
                    0,
                    REQUEST_CONFIGURE_WIDGET,
                    null
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Couldn't open widget setup", Toast.LENGTH_SHORT).show()
                widgetManager.deleteWidget(widgetId)
            }
        } else {
        }
    }


    private lateinit var packageReceiver: PackageReceiver

    override fun onStart() {
        super.onStart()
        widgetManager.startListening()

        // Register package receiver
        packageReceiver = PackageReceiver {
            refreshAllData()
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(packageReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(packageReceiver, filter)
        }
        refreshAllData()
    }

    private fun refreshAllData() {

        val rvApps = findViewById<RecyclerView>(R.id.rv_apps)

        lifecycleScope.launch(Dispatchers.IO) {

            try {

                widgetManager.cleanupUninstalledApps()

                val apps = getInstalledApps()

                val widgets = widgetManager.loadWidgets()

                val shortcuts = widgetManager.loadShortcuts()

                val maxWidgetPage =
                    widgets.maxOfOrNull { it.pageIndex } ?: 0

                val maxShortcutPage =
                    shortcuts.maxOfOrNull { it.pageIndex } ?: 0

                val neededPages =
                    maxOf(maxWidgetPage, maxShortcutPage) + 1

                withContext(Dispatchers.Main) {

                    if (isFinishing || isDestroyed) {
                        return@withContext
                    }

                    // recycler setup once
                    if (rvApps.layoutManager == null) {

                        rvApps.layoutManager =
                            GridLayoutManager(
                                this@HomeLaunchScreen,
                                4
                            )

                        rvApps.setHasFixedSize(true)

                        rvApps.isNestedScrollingEnabled = true
                    }

                    // adapter create
                    appAdapter = AppAdapter(
                        this@HomeLaunchScreen,
                        apps
                    ) { app, position ->

                        uninstallApp = app

                        uninstallPosition = position

                        try {

                            val intent =
                                Intent(Intent.ACTION_DELETE)

                            intent.data =
                                Uri.parse(
                                    "package:${app.packageName}"
                                )

                            uninstallLauncher.launch(intent)

                        } catch (e: Exception) {

                            e.printStackTrace()
                        }
                    }

                    // set adapter
                    rvApps.adapter = appAdapter
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BIND_APPWIDGET) {
            val widgetId = data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
            if (resultCode == RESULT_OK) {
                if (widgetId != -1) {
                    val appWidgetManager = AppWidgetManager.getInstance(this)
                    val providerInfo = appWidgetManager.getAppWidgetInfo(widgetId)
                    if (providerInfo != null) {
                        startWidgetFlow(widgetId, providerInfo)
                    }
                }
            } else if (widgetId != -1) {
                widgetManager.deleteWidget(widgetId)
            }
        }
    }


    override fun onStop() {
        super.onStop()
        widgetManager.stopListening()
        unregisterReceiver(packageReceiver)
    }

    private fun updateSystemBars(isWhite: Boolean) {
        val window = window
        val decorView = window.decorView
        val wic = WindowCompat.getInsetsController(window, decorView)

        if (isWhite) {
            window.statusBarColor = Color.WHITE
            window.navigationBarColor = Color.WHITE
            wic.isAppearanceLightStatusBars = true
            wic.isAppearanceLightNavigationBars = true
        } else {
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            wic.isAppearanceLightStatusBars = false
            wic.isAppearanceLightNavigationBars = false
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun setupDock() {

        val dockSlots = listOf(
            findViewById<ImageView>(R.id.dock_slot_1),
            findViewById<ImageView>(R.id.dock_slot_2),
            findViewById<ImageView>(R.id.dock_slot_3),
            findViewById<ImageView>(R.id.dock_slot_4),
            findViewById<ImageView>(R.id.dock_slot_5)
        )

        val targetIntents = listOf(
            Intent(Intent.ACTION_DIAL),
            Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_CONTACTS) },
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")),
            Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_BROWSER) },
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        )

        for (i in dockSlots.indices) {
            val slot = dockSlots[i]
            val intent = targetIntents[i]

            var resolvedPackage = resolveSystemApp(intent)

            if (i == 3) {
                try {
                    packageManager.getPackageInfo("com.android.chrome", 0)
                    resolvedPackage = "com.android.chrome"
                } catch (e: Exception) {
                }
            }

            if (resolvedPackage != null) {
                bindDockIcon(slot, resolvedPackage)
            } else {
                val fallbackPkg = if (i % 2 == 0) "com.android.settings" else "com.android.camera"
                bindDockIcon(slot, fallbackPkg)
            }
        }
    }

    private fun resolveSystemApp(intent: Intent): String? {
        val activities =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (activities.isNotEmpty()) {
            return activities[0].activityInfo.packageName
        }

        val commonPackages = when (intent.action) {
            Intent.ACTION_DIAL -> listOf("com.google.android.dialer", "com.android.dialer")
            Intent.ACTION_SENDTO -> listOf("com.google.android.apps.messaging", "com.android.mms")
            MediaStore.ACTION_IMAGE_CAPTURE -> listOf(
                "com.google.android.GoogleCamera",
                "com.android.camera",
                "com.android.camera2"
            )

            else -> emptyList()
        }

        for (pkg in commonPackages) {
            try {
                packageManager.getPackageInfo(pkg, 0)
                return pkg
            } catch (e: Exception) {
            }
        }
        return null
    }

    fun getNextAdType(): String {
        val list = getAlternativeList()

        if (list.isNullOrEmpty()) {
            return "Interstitial" // fallback
        }

        var index = getAdIndex()

        if (index >= list.size) {
            index = 0 // reset
        }

        val adType = list[index]

        setAdIndex(index + 1)

        return adType
    }

    private fun showQuiz(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val newBundle = Bundle()
        newBundle.putBoolean("browser_show", true)
        firebaseAnalytics.logEvent(
            context.getString(R.string.browser_user_show),
            newBundle
        )
        if (AppControlPreference.getOpen_Link_in_App()) {
            try {
                val intent = Intent("android.intent.action.VIEW")
                val bundle = Bundle()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    bundle.putBinder(
                        "android.support.customtabs.extra.SESSION",
                        null as IBinder?
                    )
                }
                intent.putExtras(bundle)
                intent.putExtra(
                    "android.support.customtabs.extra.TOOLBAR_COLOR",
                    context.getResources().getColor(R.color.color_primary)
                )
                intent.putExtra(
                    "android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS",
                    true
                )
                intent.setData(Uri.parse(AppControlPreference.getreso__Quiz_Link()))
                context.startActivity(intent, null as Bundle?)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            try {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(
                    context.getResources().getColor(R.color.color_primary)
                )
                val build = builder.build()
                build.intent.setFlags(268435456)
                build.launchUrl(
                    context,
                    Uri.parse(AppControlPreference.getreso__Quiz_Link())
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setLauncherEvent() {
        val countCallEnd = AppControlPreference.getLauncherEvent() + 1
        AppControlPreference.setLauncherEvent(countCallEnd)

        if (countCallEnd % 5 != 0 || countCallEnd > 100) return

        val firebaseAnalytics = FirebaseAnalytics.getInstance(this@HomeLaunchScreen)

        val prefKey = "appButtonClick_" + countCallEnd

        if (!AppControlPreference.getDynamicFlag(this@HomeLaunchScreen, prefKey)) {
            val bundle = Bundle()
            bundle.putInt("click_count", countCallEnd)

            val eventName = "appButtonClick" + countCallEnd
            firebaseAnalytics.logEvent(eventName, bundle)

            AppControlPreference.setDynamicFlag(this@HomeLaunchScreen, prefKey, true)
        }
    }

    private fun bindDockIcon(imageView: ImageView, packageName: String) {

        try {
            val icon = packageManager.getApplicationIcon(packageName)
            imageView.setImageDrawable(icon)
            imageView.setOnClickListener {
                if (AppControlPreference.getQuizAdsButton()) {
                    setLauncherEvent()
                    if (AppControlPreference.getQuizDirectPlay()) {
                        if (AppControlPreference.getIsAlternativeAds()) {
                            val adType: String? = getNextAdType()
                            when (adType) {
                                "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                    this@HomeLaunchScreen,
                                    OnCompleteAds { b: Boolean ->

                                    })

                                "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                    this@HomeLaunchScreen,
                                    OnCompeteAds { b: Boolean -> })

                                "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                    this@HomeLaunchScreen,
                                    OnCompeteAds {})

                                "Quiz" -> showQuiz(this)
                                "None" -> {
                                    val launchIntent =
                                        packageManager.getLaunchIntentForPackage(packageName)
                                    if (launchIntent != null) {
                                        startActivity(launchIntent)
                                    } else if (packageName == "com.android.settings") {
                                        startActivity(Intent(Settings.ACTION_SETTINGS))
                                    }
                                }
                            }
                        } else if (AppControlPreference.getIsRandomAds()) {
                            val adTypes = listOf("Appopen", "Interstitial", "Native", "Quiz")
                            val adType = adTypes.random()

                            when (adType) {

                                "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                    this@HomeLaunchScreen,
                                    OnCompleteAds { b: Boolean -> }
                                )

                                "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                    this@HomeLaunchScreen,
                                    OnCompeteAds { b: Boolean -> }
                                )

                                "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                    this@HomeLaunchScreen,
                                    OnCompeteAds {}
                                )

                                "Quiz" -> showQuiz(this)
                            }
                        } else if (AppControlPreference.getIsBothAdPriority()) {
                            if (AppControlPreference.getIsBothAdRandomPriority()) {
                                val random: Random = Random()
                                val number: Int = random.nextInt(3) + 1
                                if (1 == number) {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompeteAds {
                                                        override fun onCompeteAds(b: Boolean) {

                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else if (2 == number) {

                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                AppOpenAdView.reso__App_Starting_Display(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompleteAds {
                                                        override fun onCompleteAds(b: Boolean) {

                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                        this@HomeLaunchScreen
                                                    ) {

                                                    }
                                                }, 1500)

                                            }
                                        },
                                        true
                                    )
                                }
                            } else {
                                if (AppControlPreference.getreso__Alternet_Ads() == 1) {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompeteAds {
                                                        override fun onCompeteAds(b: Boolean) {

                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else if (AppControlPreference.getreso__Alternet_Ads() == 2) {

                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                AppOpenAdView.reso__App_Starting_Display(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompleteAds {
                                                        override fun onCompleteAds(b: Boolean) {

                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else if (AppControlPreference.getreso__Alternet_Ads() == 3) {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                        this@HomeLaunchScreen
                                                    ) {

                                                    }
                                                }, 1500)
                                            }
                                        },
                                        true
                                    )
                                }
                            }

                        } else if (AppControlPreference.getis_app_multi_types_ads_Priority()) {
                            InterstitialAdScreen.reso__App_Fullscreen_ADS_Display(
                                this@HomeLaunchScreen,
                                object : OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {

                                    }
                                },
                                false
                            )

                        } else {

                            if (AppControlPreference.getAppClickAdsType().equals("Appopen")) {
                                AppOpenAdView.reso__App_Starting_Display(
                                    this@HomeLaunchScreen,
                                    object : OnCompleteAds {
                                        override fun onCompleteAds(b: Boolean) {

                                        }
                                    })
                            } else if (AppControlPreference.getAppClickAdsType()
                                    .equals("Interstitial")
                            ) {

                                InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                    this@HomeLaunchScreen,
                                    object : OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {

                                        }
                                    })
                            } else if (AppControlPreference.getAppClickAdsType().equals("Native")) {
                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(this) {

                                }

                            } else if (AppControlPreference.getAppClickAdsType().equals("Quiz")) {
                                showQuiz(this)

                            } else {
                                val launchIntent =
                                    packageManager.getLaunchIntentForPackage(packageName)
                                if (launchIntent != null) {
                                    startActivity(launchIntent)
                                } else if (packageName == "com.android.settings") {
                                    startActivity(Intent(Settings.ACTION_SETTINGS))
                                }
                            }
                        }


                    } else {

                        if (AppControlPreference.getIsAlternativeAds()) {
                            val adType: String? = getNextAdType()
                            when (adType) {
                                "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                    this@HomeLaunchScreen,
                                    object : OnCompleteAds {
                                        override fun onCompleteAds(b: Boolean) {
                                            val launchIntent =
                                                packageManager.getLaunchIntentForPackage(packageName)
                                            if (launchIntent != null) {
                                                startActivity(launchIntent)
                                            } else if (packageName == "com.android.settings") {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            }
                                        }
                                    })

                                "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                    this@HomeLaunchScreen,
                                    object : OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            val launchIntent =
                                                packageManager.getLaunchIntentForPackage(packageName)
                                            if (launchIntent != null) {
                                                startActivity(launchIntent)
                                            } else if (packageName == "com.android.settings") {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            }
                                        }
                                    })

                                "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                    this
                                ) {
                                    val launchIntent =
                                        packageManager.getLaunchIntentForPackage(packageName)
                                    if (launchIntent != null) {
                                        startActivity(launchIntent)
                                    } else if (packageName == "com.android.settings") {
                                        startActivity(Intent(Settings.ACTION_SETTINGS))
                                    }
                                }

                                "Quiz" -> InterstitialAdScreen.reso__FullScreenQuizAd(
                                    this@HomeLaunchScreen,
                                    object : OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            val launchIntent =
                                                packageManager.getLaunchIntentForPackage(packageName)
                                            if (launchIntent != null) {
                                                startActivity(launchIntent)
                                            } else if (packageName == "com.android.settings") {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            }
                                        }
                                    })

                                "None" -> {
                                    val launchIntent =
                                        packageManager.getLaunchIntentForPackage(packageName)
                                    if (launchIntent != null) {
                                        startActivity(launchIntent)
                                    } else if (packageName == "com.android.settings") {
                                        startActivity(Intent(Settings.ACTION_SETTINGS))
                                    }
                                }
                            }
                        } else if (AppControlPreference.getis_app_multi_types_ads_Priority()) {
                            InterstitialAdScreen.reso__App_Fullscreen_ADS_Display(
                                this@HomeLaunchScreen,
                                object : OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val launchIntent =
                                            packageManager.getLaunchIntentForPackage(packageName)
                                        if (launchIntent != null) {
                                            startActivity(launchIntent)
                                        } else if (packageName == "com.android.settings") {
                                            startActivity(Intent(Settings.ACTION_SETTINGS))
                                        }
                                    }
                                },
                                false
                            )

                        } else if (AppControlPreference.getIsRandomAds()) {
                            val adTypes = listOf("Appopen", "Interstitial", "Native", "Quiz")
                            val adType = adTypes.random()

                            when (adType) {

                                "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                    this@HomeLaunchScreen,
                                    object : OnCompleteAds {
                                        override fun onCompleteAds(b: Boolean) {
                                            val launchIntent =
                                                packageManager.getLaunchIntentForPackage(packageName)
                                            if (launchIntent != null) {
                                                startActivity(launchIntent)
                                            } else if (packageName == "com.android.settings") {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            }
                                        }
                                    })

                                "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                    this@HomeLaunchScreen,
                                    object : OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            val launchIntent =
                                                packageManager.getLaunchIntentForPackage(packageName)
                                            if (launchIntent != null) {
                                                startActivity(launchIntent)
                                            } else if (packageName == "com.android.settings") {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            }
                                        }
                                    })

                                "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                    this
                                ) {
                                    val launchIntent =
                                        packageManager.getLaunchIntentForPackage(packageName)
                                    if (launchIntent != null) {
                                        startActivity(launchIntent)
                                    } else if (packageName == "com.android.settings") {
                                        startActivity(Intent(Settings.ACTION_SETTINGS))
                                    }
                                }

                                "Quiz" -> InterstitialAdScreen.reso__FullScreenQuizAd(
                                    this@HomeLaunchScreen,
                                    object : OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            val launchIntent =
                                                packageManager.getLaunchIntentForPackage(packageName)
                                            if (launchIntent != null) {
                                                startActivity(launchIntent)
                                            } else if (packageName == "com.android.settings") {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            }
                                        }
                                    })
                            }
                        } else if (AppControlPreference.getIsBothAdPriority()) {
                            if (AppControlPreference.getIsBothAdRandomPriority()) {
                                val random: Random = Random()
                                val number: Int = random.nextInt(3) + 1
                                if (1 == number) {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompeteAds {
                                                        override fun onCompeteAds(b: Boolean) {
                                                            val launchIntent =
                                                                packageManager.getLaunchIntentForPackage(
                                                                    packageName
                                                                )
                                                            if (launchIntent != null) {
                                                                startActivity(launchIntent)
                                                            } else if (packageName == "com.android.settings") {
                                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                                            }
                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else if (2 == number) {

                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                AppOpenAdView.reso__App_Starting_Display(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompleteAds {
                                                        override fun onCompleteAds(b: Boolean) {
                                                            val launchIntent =
                                                                packageManager.getLaunchIntentForPackage(
                                                                    packageName
                                                                )
                                                            if (launchIntent != null) {
                                                                startActivity(launchIntent)
                                                            } else if (packageName == "com.android.settings") {
                                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                                            }
                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                        this@HomeLaunchScreen
                                                    ) {
                                                        val launchIntent =
                                                            packageManager.getLaunchIntentForPackage(
                                                                packageName
                                                            )
                                                        if (launchIntent != null) {
                                                            startActivity(launchIntent)
                                                        } else if (packageName == "com.android.settings") {
                                                            startActivity(Intent(Settings.ACTION_SETTINGS))
                                                        }
                                                    }
                                                }, 1500)

                                            }
                                        },
                                        true
                                    )
                                }
                            } else {
                                if (AppControlPreference.getreso__Alternet_Ads() == 1) {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompeteAds {
                                                        override fun onCompeteAds(b: Boolean) {
                                                            val launchIntent =
                                                                packageManager.getLaunchIntentForPackage(
                                                                    packageName
                                                                )
                                                            if (launchIntent != null) {
                                                                startActivity(launchIntent)
                                                            } else if (packageName == "com.android.settings") {
                                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                                            }
                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else if (AppControlPreference.getreso__Alternet_Ads() == 2) {

                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                AppOpenAdView.reso__App_Starting_Display(
                                                    this@HomeLaunchScreen,
                                                    object : OnCompleteAds {
                                                        override fun onCompleteAds(b: Boolean) {
                                                            val launchIntent =
                                                                packageManager.getLaunchIntentForPackage(
                                                                    packageName
                                                                )
                                                            if (launchIntent != null) {
                                                                startActivity(launchIntent)
                                                            } else if (packageName == "com.android.settings") {
                                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                                            }
                                                        }
                                                    })
                                            }
                                        },
                                        true
                                    )
                                } else if (AppControlPreference.getreso__Alternet_Ads() == 3) {
                                    QuizAdManege.reso__Quiz_Interstitial_ADS(
                                        this@HomeLaunchScreen,
                                        object : OnCompeteAds {
                                            override fun onCompeteAds(b: Boolean) {
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                        this@HomeLaunchScreen
                                                    ) {
                                                        val launchIntent =
                                                            packageManager.getLaunchIntentForPackage(
                                                                packageName
                                                            )
                                                        if (launchIntent != null) {
                                                            startActivity(launchIntent)
                                                        } else if (packageName == "com.android.settings") {
                                                            startActivity(Intent(Settings.ACTION_SETTINGS))
                                                        }
                                                    }
                                                }, 1500)
                                            }
                                        },
                                        true
                                    )
                                }
                            }

                        } else {
                            if (AppControlPreference.getAppClickAdsType().equals("Appopen")) {
                                AppOpenAdView.reso__App_Starting_Display(
                                    this@HomeLaunchScreen,
                                    object : OnCompleteAds {
                                        override fun onCompleteAds(b: Boolean) {
                                            val launchIntent =
                                                packageManager.getLaunchIntentForPackage(packageName)
                                            if (launchIntent != null) {
                                                startActivity(launchIntent)
                                            } else if (packageName == "com.android.settings") {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            }
                                        }
                                    })
                            } else if (AppControlPreference.getAppClickAdsType().equals("Native")) {
                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(this) {
                                    val launchIntent =
                                        packageManager.getLaunchIntentForPackage(packageName)
                                    if (launchIntent != null) {
                                        startActivity(launchIntent)
                                    } else if (packageName == "com.android.settings") {
                                        startActivity(Intent(Settings.ACTION_SETTINGS))
                                    }
                                }
//
                            } else if (AppControlPreference.getAppClickAdsType()
                                    .equals("Interstitial")
                            ) {

                                InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                    this@HomeLaunchScreen,
                                    object : OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            val launchIntent =
                                                packageManager.getLaunchIntentForPackage(packageName)
                                            if (launchIntent != null) {
                                                startActivity(launchIntent)
                                            } else if (packageName == "com.android.settings") {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            }
                                        }
                                    })
                            } else if (AppControlPreference.getAppClickAdsType().equals("Quiz")) {
                                InterstitialAdScreen.reso__FullScreenQuizAd(
                                    this@HomeLaunchScreen,
                                    object : OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            val launchIntent =
                                                packageManager.getLaunchIntentForPackage(packageName)
                                            if (launchIntent != null) {
                                                startActivity(launchIntent)
                                            } else if (packageName == "com.android.settings") {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            }
                                        }
                                    })

                            } else {
                                val launchIntent =
                                    packageManager.getLaunchIntentForPackage(packageName)
                                if (launchIntent != null) {
                                    startActivity(launchIntent)
                                } else if (packageName == "com.android.settings") {
                                    startActivity(Intent(Settings.ACTION_SETTINGS))
                                }
                            }
                        }


                    }

                } else {
                    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                    if (launchIntent != null) {
                        startActivity(launchIntent)
                    } else if (packageName == "com.android.settings") {
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                }


            }


        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
            e.printStackTrace()
        }
    }

//    private var isInEditMode = false

    fun enterEditMode() {
        if (isInEditMode) return
        isInEditMode = true

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val dock = findViewById<LinearLayout>(R.id.dock)
//        val indicator = findViewById<TabLayout>(R.id.page_indicator)
//        val editModeBar = findViewById<View>(R.id.edit_mode_bar)

        // Scale down ViewPager
        viewPager.animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
            .setDuration(300)
            .start()

        // Hide dock but keep indicator visible
        dock.visibility = View.GONE
        // indicator.visibility = View.VISIBLE // Keep indicator visible

        // Show edit mode bar with animation
//        editModeBar.visibility = View.VISIBLE
//        editModeBar.translationY = dpToPx(200).toFloat()
//        editModeBar.animate()
//            .translationY(0f)
//            .setDuration(300)
//            .start()

        // Show overlay so tap anywhere on screen exits edit mode (dock will show again)
        val tapOverlay = findViewById<View>(R.id.edit_mode_tap_overlay)
        tapOverlay.visibility = View.VISIBLE

//        setupEditModeListeners()
        setupEditModeClickOutside()
    }

    fun exitEditMode() {
        if (!isInEditMode) return
        isInEditMode = false

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val dock = findViewById<LinearLayout>(R.id.dock)
        val indicator = findViewById<TabLayout>(R.id.page_indicator)
//        val editModeBar = findViewById<View>(R.id.edit_mode_bar)


        // Scale back ViewPager
        viewPager.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(300)
            .start()

        // Show normal controls based on current page
        val isMainPage = viewPager.currentItem != 0
        if (isMainPage) {
            dock.visibility = View.VISIBLE
            indicator.visibility = View.VISIBLE
        }

        // Hide edit mode bar
//        editModeBar.animate()
//            .translationY(dpToPx(200).toFloat())
//            .setDuration(300)
//            .withEndAction { editModeBar.visibility = View.GONE }
//            .start()

        findViewById<View>(R.id.edit_mode_tap_overlay).visibility = View.GONE
        viewPager.setOnTouchListener(null)
    }

//    private fun setupEditModeListeners() {
//        findViewById<View>(R.id.btn_wallpaper).setOnClickListener {
//            val intent = Intent(Intent.ACTION_SET_WALLPAPER)
//            startActivity(Intent.createChooser(intent, "Select Wallpaper"))
//        }
//
//        findViewById<View>(R.id.btn_widgets).setOnClickListener {
//            showAllWidgetsPicker()
//            exitEditMode()
//        }
//
//        findViewById<View>(R.id.btn_themes).setOnClickListener {
//            Toast.makeText(this, "Themes coming soon", Toast.LENGTH_SHORT).show()
//        }
//
//        findViewById<View>(R.id.btn_settings).setOnClickListener {
//            startActivity(Intent(Settings.ACTION_SETTINGS))
//        }
//    }

    private fun setupEditModeClickOutside() {
        val tapOverlay = findViewById<View>(R.id.edit_mode_tap_overlay)
        tapOverlay.setOnClickListener {
            if (widgetPickerBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                exitEditMode()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppControllerManege.removeListener()
    }

    private fun setupTabLayout() {
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.page_indicator)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 1) {
                tab.setIcon(R.drawable.ic_home_indicator)
            } else {
                tab.setIcon(R.drawable.ic_dot_indicator)
            }
        }.attach()
    }

//    override fun onBackPressed() {
//        if (isInEditMode) {
//            exitEditMode()
//        } else {
//            super.onBackPressed()
//        }
//    }

    companion object {
        private const val REQUEST_BIND_APPWIDGET = 1001
        private const val REQUEST_CONFIGURE_WIDGET = 1002
    }
}