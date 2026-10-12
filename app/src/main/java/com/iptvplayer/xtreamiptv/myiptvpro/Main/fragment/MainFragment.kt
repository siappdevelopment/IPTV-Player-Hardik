package com.iptvplayer.xtreamiptv.myiptvpro.Main.fragment

import android.app.Activity
import android.app.WallpaperColors
import android.app.WallpaperManager
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeLaunchScreen
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Other.DraggableTouchListener
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Helper.HomeGridHelper
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Other.InterceptFrameLayout
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Other.ResizeHandleView
import com.iptvplayer.xtreamiptv.myiptvpro.Main.model.ShortcutInfo
import com.iptvplayer.xtreamiptv.myiptvpro.Main.model.WidgetInfo
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.getAdIndex
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.getAlternativeList
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.setAdIndex
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppOpenAdView
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.NativeManegeView
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.QuizAdManege
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random

class MainFragment : Fragment() {

    private lateinit var widgetContainer: FrameLayout
    private lateinit var gridHelper: HomeGridHelper
    private var pageIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageIndex = arguments?.getInt(ARG_PAGE_INDEX) ?: 0
    }

    private var touchX = 0
    private var touchY = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        widgetContainer = view.findViewById(R.id.widget_container)
        // keep the grid clear of the status bar (the dock itself is lifted above the navigation bar by the activity)
        view.applySystemBarInsets(top = true, bottom = true, sides = false)
        gridHelper =
            HomeGridHelper(
                requireContext()
            )

        // Fit the rows to the real container height (it shrinks by the system bar insets), then re-place the items.
        widgetContainer.addOnLayoutChangeListener { v, _, top, _, bottom, _, oldTop, _, oldBottom ->
            if (bottom - top != oldBottom - oldTop && gridHelper.setContainerHeight(bottom - top)) {
                v.post { if (isAdded) loadHomeItems(force = true) }
            }
        }
        loadHomeItems()
        widgetContainer.isFocusable = true
        widgetContainer.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                touchX = event.rawX.toInt()
                touchY = event.rawY.toInt()
            }
            false
        }
        widgetContainer.setOnLongClickListener { view ->
            showAddWidgetMenuAtPosition(view, touchX, touchY)
            true
        }
        setupRoundedCorners()
    }

    private fun showAddWidgetMenuAtPosition(parentView: View, x: Int, y: Int) {

        val mainActivity = activity as? HomeLaunchScreen ?: return
        if (mainActivity.isDraggingItem) return

        val popupView = layoutInflater.inflate(R.layout.popup_widget, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 10f

        popupView.findViewById<TextView>(R.id.btnAddWidget).setOnClickListener {
            popupView.findViewById<TextView>(R.id.btnAddWidget).setOnClickListener {

                launchWallpapersIntent()

                popupWindow.dismiss()
            }
        }

        popupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, x, y)
    }

    private fun launchWallpapersIntent() {
        try {
            Intent(Intent.ACTION_SET_WALLPAPER).apply {
                startActivity(this)
            }
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Wallpaper settings not available", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Wallpaper Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRoundedCorners() {
        val radiusContextSafe = dpToPx(24).toFloat()
        widgetContainer.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radiusContextSafe)
            }
        }
        widgetContainer.clipToOutline = true
    }

    private fun dpToPx(dp: Int): Int {
        val density = context?.resources?.displayMetrics?.density ?: Resources.getSystem().displayMetrics.density
        return (dp * density).toInt()
    }

    fun loadHomeItems(force: Boolean = false) {
        val mainActivity = activity as? HomeLaunchScreen
            ?: return

        if (!force && widgetContainer.childCount > 0) return

        widgetContainer.removeAllViews()

        val shortcuts =
            mainActivity.fetchWidgetManager().loadShortcuts().filter { it.pageIndex == pageIndex }
        for (shortcut in shortcuts) {
            addShortcutToView(shortcut)
        }

        val savedWidgets =
            mainActivity.fetchWidgetManager().loadWidgets().filter { it.pageIndex == pageIndex }
        for (widgetInfo in savedWidgets) {
            if (widgetInfo.isCustom) {
                renderCustomWidget(widgetInfo)
            } else {
                renderSystemWidget(widgetInfo)
            }
        }
    }

    private fun renderSystemWidget(widgetInfo: WidgetInfo) {
        val mainActivity = activity as? HomeLaunchScreen
            ?: return
        val pixelWidth = gridHelper.getSpanWidth(widgetInfo.spanX)
        val pixelHeight = gridHelper.getSpanHeight(widgetInfo.spanY)
        val minW = gridHelper.pxToDp(pixelWidth)
        val minH = gridHelper.pxToDp(pixelHeight)

        val widgetView = mainActivity.fetchWidgetManager().createWidgetView(
            widgetInfo.widgetId, minW, minH, minW, minH
        )

        if (widgetView != null) {
            val wrapper =
                InterceptFrameLayout(
                    requireContext()
                )
            wrapper.layoutParams = FrameLayout.LayoutParams(pixelWidth, pixelHeight)
            wrapper.x = gridHelper.getPixelX(widgetInfo.cellX)
            wrapper.y = gridHelper.getPixelY(widgetInfo.cellY)
            wrapper.setPadding(0, 0, 0, 0)

            wrapper.addView(
                widgetView, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )

            setupDraggableWidget(wrapper, widgetInfo)
            widgetContainer.addView(wrapper)
            mainActivity.fetchWidgetManager().notifyWidgetDataChanged(widgetInfo.widgetId)
        }
    }

    private fun renderCustomWidget(widgetInfo: WidgetInfo) {
        val mainActivity = activity as? HomeLaunchScreen
            ?: return
        val pixelWidth = gridHelper.getSpanWidth(widgetInfo.spanX)
        val pixelHeight = gridHelper.getSpanHeight(widgetInfo.spanY)

        val wrapper =
            InterceptFrameLayout(
                requireContext()
            )
        wrapper.layoutParams = FrameLayout.LayoutParams(pixelWidth, pixelHeight)
        wrapper.x = gridHelper.getPixelX(widgetInfo.cellX)
        wrapper.y = gridHelper.getPixelY(widgetInfo.cellY)

        val customView =
            LayoutInflater.from(context).inflate(R.layout.item_custom_google_widget, wrapper, false)

        // Handle Clock Updates
        val tvDate = customView.findViewById<TextView>(R.id.tv_custom_date)
        val tvTime = customView.findViewById<TextView>(R.id.tv_custom_time)
        val llSearch = customView.findViewById<View>(R.id.ll_search_bar)

        val updateTime = object : Runnable {
            override fun run() {
                val now = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                val timeFormat = SimpleDateFormat("H:mm", Locale.getDefault())

                try {
                    tvDate.text = dateFormat.format(now.time)
                    tvTime.text = timeFormat.format(now.time)
                } catch (e: Exception) {
                }

                tvTime.postDelayed(this, 10000)
            }
        }
        tvTime.post(updateTime)

        // Initial color adjustment
        adjustWidgetColor(tvDate, tvTime)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val wallpaperManager = WallpaperManager.getInstance(requireContext())
            val listener = object : WallpaperManager.OnColorsChangedListener {
                override fun onColorsChanged(colors: WallpaperColors?, which: Int) {
                    if (which and WallpaperManager.FLAG_SYSTEM != 0) {
                        tvTime.post { adjustWidgetColor(tvDate, tvTime) }
                    }
                }
            }
            wallpaperManager.addOnColorsChangedListener(listener, Handler(Looper.getMainLooper()))
        }

        // Handle Search
        llSearch.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.setPackage("com.google.android.googlequicksearchbox")
                startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent(Intent.ACTION_WEB_SEARCH)
                    startActivity(intent)
                } catch (e2: Exception) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                    startActivity(intent)
                }
            }
        }

        wrapper.addView(customView)
        setupDraggableWidget(wrapper, widgetInfo)
        widgetContainer.addView(wrapper)
    }

    private fun adjustWidgetColor(tvDate: TextView, tvTime: TextView) {
        try {
            val context = context ?: return
            val wallpaperManager = WallpaperManager.getInstance(context)
            val wallpaperDrawable = wallpaperManager.drawable ?: return

            if (wallpaperDrawable is BitmapDrawable) {
                val fullBitmap = wallpaperDrawable.bitmap

                val width = fullBitmap.width
                val height = (fullBitmap.height / 4).coerceAtLeast(1)

                val analysisWidth = 100
                val analysisHeight = (analysisWidth * height / width).coerceAtLeast(1)
                val scaledBitmap =
                    Bitmap.createScaledBitmap(fullBitmap, analysisWidth, analysisHeight, false)

                var totalBrightness = 0L
                val pixelCount = analysisWidth * analysisHeight
                val pixels = IntArray(pixelCount)
                scaledBitmap.getPixels(
                    pixels,
                    0,
                    analysisWidth,
                    0,
                    0,
                    analysisWidth,
                    analysisHeight
                )

                for (pixel in pixels) {
                    val r = Color.red(pixel)
                    val g = Color.green(pixel)
                    val b = Color.blue(pixel)
                    val brightness = (r * 0.299 + g * 0.587 + b * 0.114).toInt()
                    totalBrightness += brightness
                }

                val avgBrightness = totalBrightness / pixelCount
                val textColor = if (avgBrightness > 160) Color.BLACK else Color.WHITE

                tvDate.setTextColor(textColor)
                tvTime.setTextColor(textColor)

                if (scaledBitmap != fullBitmap) {
                    scaledBitmap.recycle()
                }
            } else {
                // Fallback for non-bitmap drawables
                tvDate.setTextColor(Color.WHITE)
                tvTime.setTextColor(Color.WHITE)
            }
        } catch (e: SecurityException) {
            tvDate.setTextColor(Color.WHITE)
            tvTime.setTextColor(Color.WHITE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupDraggableWidget(
        wrapper: InterceptFrameLayout,
        widgetInfo: WidgetInfo
    ) {
        val mainActivity =
            activity as? HomeLaunchScreen
                ?: return
        val listener =
            DraggableTouchListener(
                mainActivity,
                onMove = { x, y -> },
                onFinish = {
                    val snappedX = gridHelper.snapX(wrapper.x)
                    val snappedY = gridHelper.snapY(wrapper.y)

                    val cellX = gridHelper.getCellX(snappedX)
                    val cellY = gridHelper.getCellY(snappedY)

                    val maxCellX =
                        HomeGridHelper.Companion.GRID_COLUMNS - widgetInfo.spanX
                    val maxCellY =
                        HomeGridHelper.Companion.GRID_ROWS - widgetInfo.spanY
                    val finalCellX = cellX.coerceIn(0, maxCellX)
                    val finalCellY = cellY.coerceIn(0, maxCellY)

                    wrapper.x = gridHelper.getPixelX(finalCellX)
                    wrapper.y = gridHelper.getPixelY(finalCellY)
                    mainActivity.fetchWidgetManager().updateWidgetPosition(
                        widgetInfo.widgetId,
                        finalCellX,
                        finalCellY,
                        pageIndex
                    )
                },
                onLongClick = {
                    showRemoveMenu(wrapper, isWidget = true, id = widgetInfo.widgetId.toString())
                }
            )
        wrapper.setOnTouchListener(listener)
        wrapper.onLongPressDetected = { ev -> listener.onTouch(wrapper, ev) }
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

        val firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        val prefKey = "appButtonClick_" + countCallEnd

        if (!AppControlPreference.getDynamicFlag(context, prefKey)) {
            val bundle = Bundle()
            bundle.putInt("click_count", countCallEnd)

            val eventName = "appButtonClick" + countCallEnd
            firebaseAnalytics.logEvent(eventName, bundle)

            AppControlPreference.setDynamicFlag(context, prefKey, true)
        }
    }
    private fun addShortcutToView(shortcut: ShortcutInfo) {
        val mainActivity =
            activity as? HomeLaunchScreen
                ?: return

        val cellSize = gridHelper.cellWidth
        val cellHeight = gridHelper.cellHeight

        val wrapper =
            InterceptFrameLayout(
                requireContext()
            )
        val layoutParams = FrameLayout.LayoutParams(cellSize, cellHeight)
        wrapper.layoutParams = layoutParams
        wrapper.x = gridHelper.getPixelX(shortcut.cellX)
        wrapper.y = gridHelper.getPixelY(shortcut.cellY)

        val imageView = ImageView(context)
        imageView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        imageView.setPadding(45, 45, 45, 45)

        try {
            val icon = mainActivity.packageManager.getApplicationIcon(shortcut.packageName)
            imageView.setImageDrawable(icon)
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // Tap to launch
        imageView.setOnClickListener {
            if (AppControlPreference.getQuizAdsButton()) {
                setLauncherEvent()
                if (AppControlPreference.getQuizDirectPlay()) {
                    if (AppControlPreference.getIsAlternativeAds()){
                        val adType: String? = getNextAdType()
                        when (adType) {
                            "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                requireActivity(),
                                AppOpenAdView.OnCompleteAds { b: Boolean -> })

                            "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                requireActivity(),
                                InterstitialAdScreen.OnCompeteAds { b: Boolean -> })

                            "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                requireActivity(),
                                InterstitialAdScreen.OnCompeteAds {})

                            "Quiz" -> showQuiz(requireContext())
                            "None" ->
                            {
                                val intent =
                                    mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                if (intent != null) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    else if (AppControlPreference.getIsRandomAds()){
                        val adTypes = listOf("Appopen", "Interstitial", "Native", "Quiz")
                        val adType = adTypes.random()

                        when (adType) {

                            "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                requireActivity(),
                                AppOpenAdView.OnCompleteAds { b: Boolean -> }
                            )

                            "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                requireActivity(),
                                InterstitialAdScreen.OnCompeteAds { b: Boolean -> }
                            )

                            "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                requireActivity(),
                                InterstitialAdScreen.OnCompeteAds {}
                            )

                            "Quiz" -> showQuiz(requireContext())
                        }
                    }
                    else if (AppControlPreference.getIsBothAdPriority()) {
                        if (AppControlPreference.getIsBothAdRandomPriority()) {
                            val random: Random = Random()
                            val number: Int = random.nextInt(3) + 1
                            if (1 == number) {
                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                requireActivity(),
                                                object : InterstitialAdScreen.OnCompeteAds {
                                                    override fun onCompeteAds(b: Boolean) {

                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else if (2 == number) {

                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            AppOpenAdView.reso__App_Starting_Display(
                                                requireActivity(),
                                                object : AppOpenAdView.OnCompleteAds {
                                                    override fun onCompleteAds(b: Boolean) {

                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else {
                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                    requireActivity()
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
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                requireActivity(),
                                                object : InterstitialAdScreen.OnCompeteAds {
                                                    override fun onCompeteAds(b: Boolean) {

                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else if (AppControlPreference.getreso__Alternet_Ads() == 2) {

                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            AppOpenAdView.reso__App_Starting_Display(
                                                requireActivity(),
                                                object : AppOpenAdView.OnCompleteAds {
                                                    override fun onCompleteAds(b: Boolean) {

                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else if (AppControlPreference.getreso__Alternet_Ads() == 3) {
                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                    requireActivity()
                                                ) {

                                                }
                                            }, 1500)
                                        }
                                    },
                                    true
                                )
                            }
                        }

                    }
                    else if (AppControlPreference.getis_app_multi_types_ads_Priority()) {
                        InterstitialAdScreen.reso__App_Fullscreen_ADS_Display(
                            context as Activity?,
                            object : InterstitialAdScreen.OnCompeteAds {
                                override fun onCompeteAds(b: Boolean) {

                                }
                            },
                            false
                        )
                    }
                    else if (AppControlPreference.getHomePriorityOneAds()){
                        InterstitialAdScreen.reso__App_Fullscreen_ADS_Display(
                            context as Activity?,
                            object : InterstitialAdScreen.OnCompeteAds {
                                override fun onCompeteAds(b: Boolean) {

                                }
                            },
                            false
                        )

                    }
                    else {

                        if (AppControlPreference.getAppClickAdsType().equals("Appopen")) {
                            AppOpenAdView.reso__App_Starting_Display(
                                requireActivity(),
                                object : AppOpenAdView.OnCompleteAds {
                                    override fun onCompleteAds(b: Boolean) {

                                    }
                                })
                        } else if (AppControlPreference.getAppClickAdsType().equals("Interstitial")) {

                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                requireActivity(),
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {

                                    }
                                })
                        } else if (AppControlPreference.getAppClickAdsType().equals("Native")) {
                            NativeManegeView.reso__Admob_Native_Full_ADS_Display(requireActivity()) {

                            }

                        } else if (AppControlPreference.getAppClickAdsType().equals("Quiz")) {
                            showQuiz(requireContext())

                        } else {
                            val intent =
                                mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                            if (intent != null) {
                                startActivity(intent)
                            } else {
                                Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }




                } else {

                    if (AppControlPreference.getIsAlternativeAds()){
                        val adType: String? = getNextAdType()
                        when (adType) {
                            "Appopen" ->           AppOpenAdView.reso__App_Starting_Display(
                                requireActivity(),
                                object : AppOpenAdView.OnCompleteAds {
                                    override fun onCompleteAds(b: Boolean) {
                                        val intent =
                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                        if (intent != null) {
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })

                            "Interstitial" ->             InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                requireActivity(),
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val intent =
                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                        if (intent != null) {
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })

                            "Native" ->        NativeManegeView.reso__Admob_Native_Full_ADS_Display(requireActivity()) {
                                val intent =
                                    mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                if (intent != null) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                }
                            }

                            "Quiz" ->         InterstitialAdScreen.reso__FullScreenQuizAd(
                                requireActivity(),
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val intent =
                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                        if (intent != null) {
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })
                            "None" ->
                            {
                                val intent =
                                    mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                if (intent != null) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    else if (AppControlPreference.getIsRandomAds()){
                        val adTypes = listOf("Appopen", "Interstitial", "Native", "Quiz")
                        val adType = adTypes.random()

                        when (adType) {

                            "Appopen" ->           AppOpenAdView.reso__App_Starting_Display(
                                requireActivity(),
                                object : AppOpenAdView.OnCompleteAds {
                                    override fun onCompleteAds(b: Boolean) {
                                        val intent =
                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                        if (intent != null) {
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })

                            "Interstitial" ->  InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                requireActivity(),
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val intent =
                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                        if (intent != null) {
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })

                            "Native" ->        NativeManegeView.reso__Admob_Native_Full_ADS_Display(requireActivity()) {
                                val intent =
                                    mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                if (intent != null) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                }
                            }

                            "Quiz" ->         InterstitialAdScreen.reso__FullScreenQuizAd(
                                requireActivity(),
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val intent =
                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                        if (intent != null) {
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })
                        }
                    }
                    else if (AppControlPreference.getIsBothAdPriority()) {
                        if (AppControlPreference.getIsBothAdRandomPriority()) {
                            val random: Random = Random()
                            val number: Int = random.nextInt(3) + 1
                            if (1 == number) {
                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                requireActivity(),
                                                object : InterstitialAdScreen.OnCompeteAds {
                                                    override fun onCompeteAds(b: Boolean) {
                                                        val intent =
                                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                                        if (intent != null) {
                                                            startActivity(intent)
                                                        } else {
                                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else if (2 == number) {

                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            AppOpenAdView.reso__App_Starting_Display(
                                                requireActivity(),
                                                object : AppOpenAdView.OnCompleteAds {
                                                    override fun onCompleteAds(b: Boolean) {
                                                        val intent =
                                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                                        if (intent != null) {
                                                            startActivity(intent)
                                                        } else {
                                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else {
                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                    requireActivity()
                                                ) {
                                                    val intent =
                                                        mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                                    if (intent != null) {
                                                        startActivity(intent)
                                                    } else {
                                                        Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
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
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                requireActivity(),
                                                object : InterstitialAdScreen.OnCompeteAds {
                                                    override fun onCompeteAds(b: Boolean) {
                                                        val intent =
                                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                                        if (intent != null) {
                                                            startActivity(intent)
                                                        } else {
                                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else if (AppControlPreference.getreso__Alternet_Ads() == 2) {

                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            AppOpenAdView.reso__App_Starting_Display(
                                                requireActivity(),
                                                object : AppOpenAdView.OnCompleteAds {
                                                    override fun onCompleteAds(b: Boolean) {
                                                        val intent =
                                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                                        if (intent != null) {
                                                            startActivity(intent)
                                                        } else {
                                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else if (AppControlPreference.getreso__Alternet_Ads() == 3) {
                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    requireActivity(),
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                    requireActivity()
                                                ) {
                                                    val intent =
                                                        mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                                    if (intent != null) {
                                                        startActivity(intent)
                                                    } else {
                                                        Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }, 1500)
                                        }
                                    },
                                    true
                                )
                            }
                        }

                    }
                    else if (AppControlPreference.getis_app_multi_types_ads_Priority()) {

                        InterstitialAdScreen.reso__App_Fullscreen_ADS_Display(
                            context as Activity?,
                            object : InterstitialAdScreen.OnCompeteAds {
                                override fun onCompeteAds(b: Boolean) {
                                    val intent =
                                        mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                    if (intent != null) {
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            false
                        )
                    }
                    else if (AppControlPreference.getHomePriorityOneAds()){
                        InterstitialAdScreen.reso__App_Fullscreen_ADS_Display(
                            context as Activity?,
                            object : InterstitialAdScreen.OnCompeteAds {
                                override fun onCompeteAds(b: Boolean) {
                                    val intent =
                                        mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                    if (intent != null) {
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            false
                        )

                    }
                    else{
                        if (AppControlPreference.getAppClickAdsType().equals("Appopen")) {
                            AppOpenAdView.reso__App_Starting_Display(
                                requireActivity(),
                                object : AppOpenAdView.OnCompleteAds {
                                    override fun onCompleteAds(b: Boolean) {
                                        val intent =
                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                        if (intent != null) {
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })
                        }else if (AppControlPreference.getAppClickAdsType().equals("Native")) {
                            NativeManegeView.reso__Admob_Native_Full_ADS_Display(requireActivity()) {
                                val intent =
                                    mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                if (intent != null) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                }
                            }
//
                        }
                        else if (AppControlPreference.getAppClickAdsType().equals("Interstitial")) {

                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                requireActivity(),
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val intent =
                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                        if (intent != null) {
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })
                        } else if (AppControlPreference.getAppClickAdsType().equals("Quiz")) {
                            InterstitialAdScreen.reso__FullScreenQuizAd(
                                requireActivity(),
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val intent =
                                            mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                                        if (intent != null) {
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })

                        } else {
                            val intent =
                                mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                            if (intent != null) {
                                startActivity(intent)
                            } else {
                                Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                }

            } else {
                val intent =
                    mainActivity.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                if (intent != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                }
            }



        }

        wrapper.addView(imageView)

        val listener =
            DraggableTouchListener(
                mainActivity,
                onMove = { x, y -> },
                onFinish = {
//                val cellX = gridHelper.getCellX(wrapper.x + (cellSize / 2))
//                val cellY = gridHelper.getCellY(wrapper.y + (cellHeight / 2))
//
//                wrapper.x = gridHelper.getPixelX(cellX)
//                wrapper.y = gridHelper.getPixelY(cellY)
//                mainActivity.widgetManager.updateShortcutPosition(shortcut.packageName, cellX, cellY, pageIndex)
//                loadHomeItems()

                    // Snap to nearest grid position using top-left corner
                    val snappedX = gridHelper.snapX(wrapper.x)
                    val snappedY = gridHelper.snapY(wrapper.y)

                    // Calculate cell position from snapped pixel position
                    val cellX = gridHelper.getCellX(snappedX)
                    val cellY = gridHelper.getCellY(snappedY)

                    // Ensure shortcut doesn't go out of bounds (1x1 size)
                    val finalCellX = cellX.coerceIn(
                        0,
                        HomeGridHelper.Companion.GRID_COLUMNS - 1
                    )
                    val finalCellY = cellY.coerceIn(
                        0,
                        HomeGridHelper.Companion.GRID_ROWS - 1
                    )

                    // Set final position
                    wrapper.x = gridHelper.getPixelX(finalCellX)
                    wrapper.y = gridHelper.getPixelY(finalCellY)
                    mainActivity.fetchWidgetManager().updateShortcutPosition(
                        shortcut.packageName,
                        finalCellX,
                        finalCellY,
                        pageIndex
                    )
                    loadHomeItems()
                },
                onLongClick = {
                    showRemoveMenu(wrapper, isWidget = false, id = shortcut.packageName)
                }
            )
        wrapper.setOnTouchListener(listener)
        wrapper.onLongPressDetected = { ev -> listener.onTouch(wrapper, ev) }

        widgetContainer.addView(wrapper)
    }


    private fun showRemoveMenu(view: View, isWidget: Boolean, id: String) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add("Remove")

        if (isWidget) {
            popup.menu.add("Resize")
        } else {
            popup.menu.add("Uninstall")
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Remove" -> {
                    val mainActivity =
                        activity as? HomeLaunchScreen
                            ?: return@setOnMenuItemClickListener true
                    if (isWidget) {
                        mainActivity.fetchWidgetManager().removeWidget(id.toInt())
                    } else {
                        mainActivity.fetchWidgetManager().removeShortcut(id)
                    }
                    loadHomeItems()
                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show()
                }

                "Resize" -> {
                    startResizing(
                        view as InterceptFrameLayout,
                        id.toInt()
                    )
                }

                "Uninstall" -> {
                    val intent = Intent(Intent.ACTION_DELETE)
                    intent.data = Uri.parse("package:$id")
                    startActivity(intent)
                }
            }
            true
        }

        view.post {
            popup.show()
        }
    }

    private fun startResizing(
        wrapper: InterceptFrameLayout,
        widgetId: Int
    ) {

        val mainActivity =
            activity as? HomeLaunchScreen
                ?: return

        var resizeFinished = false

        for (i in widgetContainer.childCount - 1 downTo 0) {

            val child = widgetContainer.getChildAt(i)

            if (child is ResizeHandleView) {

                val parent = child.parent

                if (parent === widgetContainer) {

                    try {
                        widgetContainer.removeView(child)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        lateinit var handleView: ResizeHandleView

        handleView = ResizeHandleView(
            requireContext(),
            wrapper,
            gridHelper,

            onResizeFinish = { newSpanX, newSpanY ->

                // Prevent multiple callbacks
                if (resizeFinished) {
                    return@ResizeHandleView
                }

                resizeFinished = true

                // Fragment safety
                if (!isAdded || activity == null) {
                    return@ResizeHandleView
                }

                try {

                    mainActivity.fetchWidgetManager()
                        .updateWidgetSpan(widgetId, newSpanX, newSpanY)

                    // Update AppWidget size
                    val widgetView =
                        wrapper.getChildAt(0) as? AppWidgetHostView

                    widgetView?.let {

                        val pixelWidth =
                            gridHelper.getSpanWidth(newSpanX)

                        val pixelHeight =
                            gridHelper.getSpanHeight(newSpanY)

                        val bundle = Bundle().apply {

                            putInt(
                                AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                gridHelper.pxToDp(pixelWidth)
                            )

                            putInt(
                                AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                gridHelper.pxToDp(pixelHeight)
                            )

                            putInt(
                                AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                gridHelper.pxToDp(pixelWidth)
                            )

                            putInt(
                                AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                gridHelper.pxToDp(pixelHeight)
                            )
                        }

                        it.updateAppWidgetSize(
                            bundle,
                            gridHelper.pxToDp(pixelWidth),
                            gridHelper.pxToDp(pixelHeight),
                            gridHelper.pxToDp(pixelWidth),
                            gridHelper.pxToDp(pixelHeight)
                        )
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Remove handle safely on UI thread
                handleView.post {

                    try {

                        val parent = handleView.parent

                        if (parent is ViewGroup) {

                            if (handleView.parent === parent) {

                                parent.removeView(handleView)
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )

        val lp = FrameLayout.LayoutParams(
            wrapper.width,
            wrapper.height
        )

        handleView.layoutParams = lp

        handleView.x = wrapper.x
        handleView.y = wrapper.y

        try {

            // Prevent duplicate addView crash
            if (handleView.parent == null) {

                widgetContainer.addView(handleView)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        Toast.makeText(
            context,
            "Drag dots to resize",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun addWidget(widgetId: Int, provider: AppWidgetProviderInfo): Boolean {
        val mainActivity =
            activity as? HomeLaunchScreen
                ?: return false
        val container = view?.findViewById<FrameLayout>(R.id.widget_container) ?: return false

        return try {
            val (spanX, spanY) = gridHelper.getSpanFromDpSize(provider.minWidth, provider.minHeight)
            val emptySpace = findFirstEmptySpace(spanX, spanY)
            if (emptySpace == null) {
                Toast.makeText(context, "No space on this page!", Toast.LENGTH_SHORT).show()
                return false
            }

            val (cellX, cellY) = emptySpace
            val widthDp = provider.minWidth.coerceAtLeast(40)
            val heightDp = provider.minHeight.coerceAtLeast(40)
            val (pixelWidth, pixelHeight) = gridHelper.clampWidgetSizeToScreen(
                gridHelper.dpToPx(widthDp),
                gridHelper.dpToPx(heightDp)
            )

            val actualWidthDp = gridHelper.pxToDp(pixelWidth).coerceAtLeast(40)
            val actualHeightDp = gridHelper.pxToDp(pixelHeight).coerceAtLeast(40)

            val widgetView = mainActivity.fetchWidgetManager().createWidgetView(
                widgetId,
                actualWidthDp,
                actualHeightDp,
                actualWidthDp,
                actualHeightDp
            )
            if (widgetView == null) {
                return false
            }

            val wrapper =
                InterceptFrameLayout(
                    requireContext()
                )
            wrapper.layoutParams = FrameLayout.LayoutParams(pixelWidth, pixelHeight)
            wrapper.x = gridHelper.getPixelX(cellX)
            wrapper.y = gridHelper.getPixelY(cellY)
            wrapper.addView(
                widgetView, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )

            val listener =
                DraggableTouchListener(
                    mainActivity,
                    onMove = { _, _ -> },
                    onFinish = {
                        val snappedX = gridHelper.snapX(wrapper.x)
                        val snappedY = gridHelper.snapY(wrapper.y)
                        val finalCellX = gridHelper.getCellX(snappedX).coerceIn(
                            0,
                            HomeGridHelper.Companion.GRID_COLUMNS - spanX
                        )
                        val finalCellY = gridHelper.getCellY(snappedY).coerceIn(
                            0,
                            HomeGridHelper.Companion.GRID_ROWS - spanY
                        )

                        wrapper.x = gridHelper.getPixelX(finalCellX)
                        wrapper.y = gridHelper.getPixelY(finalCellY)
                        mainActivity.fetchWidgetManager().updateWidgetPosition(
                            widgetId,
                            finalCellX,
                            finalCellY,
                            pageIndex
                        )
                    },
                    onLongClick = {
                        showRemoveMenu(wrapper, isWidget = true, id = widgetId.toString())
                    }
                )
            wrapper.setOnTouchListener(listener)
            wrapper.onLongPressDetected = { ev -> listener.onTouch(wrapper, ev) }

            container.addView(wrapper)
            mainActivity.fetchWidgetManager().notifyWidgetDataChanged(widgetId)

            val widgetInfo =
                WidgetInfo(
                    widgetId = widgetId,
                    providerPackage = provider.provider.packageName,
                    providerClass = provider.provider.className,
                    cellX = cellX,
                    cellY = cellY,
                    spanX = spanX,
                    spanY = spanY,
                    pageIndex = pageIndex
                )
            val widgets = mainActivity.fetchWidgetManager().loadWidgets().toMutableList()
            widgets.add(widgetInfo)
            mainActivity.fetchWidgetManager().saveWidgets(widgets)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun findFirstEmptySpace(spanX: Int, spanY: Int): Pair<Int, Int>? {
        val mainActivity =
            activity as? HomeLaunchScreen
                ?: return null
        val widgets = mainActivity.fetchWidgetManager().loadWidgets().filter { it.pageIndex == pageIndex }
        val shortcuts =
            mainActivity.fetchWidgetManager().loadShortcuts().filter { it.pageIndex == pageIndex }

        for (y in 0..HomeGridHelper.Companion.GRID_ROWS - spanY) {
            for (x in 0..HomeGridHelper.Companion.GRID_COLUMNS - spanX) {
                if (isAreaVacant(x, y, spanX, spanY, widgets, shortcuts)) {
                    return x to y
                }
            }
        }
        return null
    }

    private fun isAreaVacant(
        x: Int,
        y: Int,
        spanX: Int,
        spanY: Int,
        widgets: List<WidgetInfo>,
        shortcuts: List<ShortcutInfo>
    ): Boolean {
        for (w in widgets) {
            if (x < w.cellX + w.spanX && x + spanX > w.cellX &&
                y < w.cellY + w.spanY && y + spanY > w.cellY
            ) {
                return false
            }
        }
        for (s in shortcuts) {
            if (x < s.cellX + 1 && x + spanX > s.cellX &&
                y < s.cellY + 1 && y + spanY > s.cellY
            ) {
                return false
            }
        }
        return true
    }


    companion object {
        private const val ARG_PAGE_INDEX = "page_index"
        fun newInstance(pageIndex: Int): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            args.putInt(ARG_PAGE_INDEX, pageIndex)
            fragment.arguments = args
            return fragment
        }
    }
}
