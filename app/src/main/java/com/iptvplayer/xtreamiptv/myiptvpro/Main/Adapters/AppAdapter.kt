package com.iptvplayer.xtreamiptv.myiptvpro.Main.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.getAdIndex
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.getAlternativeList
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference.setAdIndex
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppOpenAdView
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.NativeManegeView
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.QuizAdManege
import com.google.firebase.analytics.FirebaseAnalytics
import com.iptvplayer.xtreamiptv.myiptvpro.R
import java.util.Locale
import java.util.Random

data class AppInfo(
    val label: CharSequence,
    val packageName: CharSequence,
    val icon: Drawable
)

class AppAdapter(
    private val context: Context,
    apps: List<AppInfo>,    private val onUninstallClick: (AppInfo, Int) -> Unit

) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private val allApps: MutableList<AppInfo> = apps.toMutableList()
    private var filteredApps: MutableList<AppInfo> = allApps

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
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

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = filteredApps[position]
        holder.tvName.text = app.label
        holder.ivIcon.setImageDrawable(app.icon)

        holder.itemView.setOnClickListener {

            if (AppControlPreference.getQuizAdsButton()) {
                setLauncherEvent()
                if (AppControlPreference.getQuizDirectPlay()) {
                    if (AppControlPreference.getIsAlternativeAds()) {
                        val adType: String? = getNextAdType()
                        when (adType) {
                            "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                context as Activity?,
                                AppOpenAdView.OnCompleteAds { b: Boolean -> })

                            "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                context as Activity?,
                                InterstitialAdScreen.OnCompeteAds { b: Boolean -> })

                            "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                context as Activity?,
                                InterstitialAdScreen.OnCompeteAds {})

                            "Quiz" -> showQuiz(context)
                            "None" -> {
                                val launchIntent =
                                    context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                }
                            }
                        }
                    } else if (AppControlPreference.getIsRandomAds()) {
                        val adTypes = listOf("Appopen", "Interstitial", "Native", "Quiz")
                        val adType = adTypes.random()

                        when (adType) {

                            "Appopen" ->
                                AppOpenAdView.reso__App_Starting_Display(
                                context as Activity?,
                                    AppOpenAdView.OnCompleteAds { b: Boolean -> }
                            )

                            "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                context as Activity?,
                                InterstitialAdScreen.OnCompeteAds { b: Boolean -> }
                            )

                            "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                context as Activity?,
                                InterstitialAdScreen.OnCompeteAds {}
                            )

                            "Quiz" -> showQuiz(context)
                        }
                    } else if (AppControlPreference.getIsBothAdPriority()) {
                        if (AppControlPreference.getIsBothAdRandomPriority()) {
                            val random: Random = Random()
                            val number: Int = random.nextInt(3) + 1
                            if (1 == number) {
                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                context as Activity?,
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
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            AppOpenAdView.reso__App_Starting_Display(
                                                context as Activity?,
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
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                    context as Activity
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
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                context as Activity?,
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
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            AppOpenAdView.reso__App_Starting_Display(
                                                context as Activity?,
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
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                    context as Activity
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
                                context as Activity?,
                                object : AppOpenAdView.OnCompleteAds {
                                    override fun onCompleteAds(b: Boolean) {

                                    }
                                })
                        } else if (AppControlPreference.getAppClickAdsType()
                                .equals("Interstitial")
                        ) {

                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                context as Activity?,
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {

                                    }
                                })
                        } else if (AppControlPreference.getAppClickAdsType().equals("Native")) {
                            NativeManegeView.reso__Admob_Native_Full_ADS_Display(context as Activity) {

                            }

                        } else if (AppControlPreference.getAppClickAdsType().equals("Quiz")) {
                            showQuiz(context)

                        } else {
                            val launchIntent =
                                context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            }
                        }
                    }


                } else {

                    if (AppControlPreference.getIsAlternativeAds()) {
                        val adType: String? = getNextAdType()
                        when (adType) {
                            "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                context as Activity?,
                                object : AppOpenAdView.OnCompleteAds {
                                    override fun onCompleteAds(b: Boolean) {
                                        val launchIntent =
                                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    }
                                })

                            "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                context as Activity?,
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val launchIntent =
                                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    }
                                })

                            "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(context as Activity) {
                                val launchIntent =
                                    context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                }
                            }

                            "Quiz" -> InterstitialAdScreen.reso__FullScreenQuizAd(
                                context as Activity?,
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val launchIntent =
                                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    }
                                })

                            "None" -> {
                                val launchIntent =
                                    context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                }
                            }
                        }
                    } else if (AppControlPreference.getIsRandomAds()) {
                        val adTypes = listOf("Appopen", "Interstitial", "Native", "Quiz")
                        val adType = adTypes.random()

                        when (adType) {

                            "Appopen" -> AppOpenAdView.reso__App_Starting_Display(
                                context as Activity?,
                                object : AppOpenAdView.OnCompleteAds {
                                    override fun onCompleteAds(b: Boolean) {
                                        val launchIntent =
                                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    }
                                })

                            "Interstitial" -> InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                context as Activity?,
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val launchIntent =
                                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    }
                                })

                            "Native" -> NativeManegeView.reso__Admob_Native_Full_ADS_Display(context as Activity) {
                                val launchIntent =
                                    context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                }
                            }

                            "Quiz" -> InterstitialAdScreen.reso__FullScreenQuizAd(
                                context as Activity?,
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val launchIntent =
                                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
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
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                context as Activity?,
                                                object : InterstitialAdScreen.OnCompeteAds {
                                                    override fun onCompeteAds(b: Boolean) {
                                                        val launchIntent =
                                                            context.packageManager.getLaunchIntentForPackage(
                                                                app.packageName.toString()
                                                            )
                                                        if (launchIntent != null) {
                                                            context.startActivity(launchIntent)
                                                        }
                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else if (2 == number) {

                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            AppOpenAdView.reso__App_Starting_Display(
                                                context as Activity?,
                                                object : AppOpenAdView.OnCompleteAds {
                                                    override fun onCompleteAds(b: Boolean) {
                                                        val launchIntent =
                                                            context.packageManager.getLaunchIntentForPackage(
                                                                app.packageName.toString()
                                                            )
                                                        if (launchIntent != null) {
                                                            context.startActivity(launchIntent)
                                                        }
                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else {
                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                    context as Activity
                                                ) {
                                                    val launchIntent =
                                                        context.packageManager.getLaunchIntentForPackage(
                                                            app.packageName.toString()
                                                        )
                                                    if (launchIntent != null) {
                                                        context.startActivity(launchIntent)
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
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                                context as Activity?,
                                                object : InterstitialAdScreen.OnCompeteAds {
                                                    override fun onCompeteAds(b: Boolean) {
                                                        val launchIntent =
                                                            context.packageManager.getLaunchIntentForPackage(
                                                                app.packageName.toString()
                                                            )
                                                        if (launchIntent != null) {
                                                            context.startActivity(launchIntent)
                                                        }
                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else if (AppControlPreference.getreso__Alternet_Ads() == 2) {

                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            AppOpenAdView.reso__App_Starting_Display(
                                                context as Activity?,
                                                object : AppOpenAdView.OnCompleteAds {
                                                    override fun onCompleteAds(b: Boolean) {
                                                        val launchIntent =
                                                            context.packageManager.getLaunchIntentForPackage(
                                                                app.packageName.toString()
                                                            )
                                                        if (launchIntent != null) {
                                                            context.startActivity(launchIntent)
                                                        }
                                                    }
                                                })
                                        }
                                    },
                                    true
                                )
                            } else if (AppControlPreference.getreso__Alternet_Ads() == 3) {
                                QuizAdManege.reso__Quiz_Interstitial_ADS(
                                    context,
                                    object : InterstitialAdScreen.OnCompeteAds {
                                        override fun onCompeteAds(b: Boolean) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                NativeManegeView.reso__Admob_Native_Full_ADS_Display(
                                                    context as Activity
                                                ) {
                                                    val launchIntent =
                                                        context.packageManager.getLaunchIntentForPackage(
                                                            app.packageName.toString()
                                                        )
                                                    if (launchIntent != null) {
                                                        context.startActivity(launchIntent)
                                                    }
                                                }
                                            }, 1500)
                                        }
                                    },
                                    true
                                )
                            }
                        }

                    }   else if (AppControlPreference.getis_app_multi_types_ads_Priority()) {
                        InterstitialAdScreen.reso__App_Fullscreen_ADS_Display(
                            context as Activity?,
                            object : InterstitialAdScreen.OnCompeteAds {
                                override fun onCompeteAds(b: Boolean) {
                                    val launchIntent =
                                        context.packageManager.getLaunchIntentForPackage(
                                            app.packageName.toString()
                                        )
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
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
                                    val launchIntent =
                                        context.packageManager.getLaunchIntentForPackage(
                                            app.packageName.toString()
                                        )
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
                                    }
                                }
                            },
                            false
                        )

                    }
                    else {
                        if (AppControlPreference.getAppClickAdsType().equals("Appopen")) {
                            AppOpenAdView.reso__App_Starting_Display(
                                context as Activity?,
                                object : AppOpenAdView.OnCompleteAds {
                                    override fun onCompleteAds(b: Boolean) {
                                        val launchIntent =
                                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    }
                                })
                        } else if (AppControlPreference.getAppClickAdsType().equals("Native")) {
                            NativeManegeView.reso__Admob_Native_Full_ADS_Display(context as Activity) {
                                val launchIntent =
                                    context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                }
                            }
//
                        } else if (AppControlPreference.getAppClickAdsType()
                                .equals("Interstitial")
                        ) {

                            InterstitialAdScreen.reso__FullScreenInterstailOnly(
                                context as Activity?,
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val launchIntent =
                                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    }
                                })
                        } else if (AppControlPreference.getAppClickAdsType().equals("Quiz")) {
                            InterstitialAdScreen.reso__FullScreenQuizAd(
                                context as Activity?,
                                object : InterstitialAdScreen.OnCompeteAds {
                                    override fun onCompeteAds(b: Boolean) {
                                        val launchIntent =
                                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    }
                                })

                        } else {
                            val launchIntent =
                                context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            }
                        }
                    }


                }

            } else {
                val launchIntent =
                    context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                }
            }


        }

        holder.itemView.setOnLongClickListener {
            showAppOptionsMenu(holder.itemView, app, position,holder)
            true
        }
    }

    fun getNextAdType(): String {
        val list = getAlternativeList()

        if (list.isNullOrEmpty()) {
            return "Interstitial"
        }

        var index = getAdIndex()

        if (index >= list.size) {
            index = 0 // reset
        }

        val adType = list[index]

        setAdIndex(index + 1)

        return adType
    }

    override fun getItemCount(): Int {
        return filteredApps.size
    }

    fun filterApps(query: String) {
        val lowerCaseQuery = query.lowercase(Locale.getDefault())
        filteredApps = if (lowerCaseQuery.isEmpty()) {
            allApps
        } else {
            allApps.filter {
                it.label.toString().lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    private fun setLauncherEvent() {
        val countCallEnd = AppControlPreference.getLauncherEvent() + 1
        AppControlPreference.setLauncherEvent(countCallEnd)

        if (countCallEnd % 5 != 0 || countCallEnd > 100) return

        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

        val prefKey = "appButtonClick_" + countCallEnd

        if (!AppControlPreference.getDynamicFlag(context, prefKey)) {
            val bundle = Bundle()
            bundle.putInt("click_count", countCallEnd)

            val eventName = "appButtonClick" + countCallEnd
            firebaseAnalytics.logEvent(eventName, bundle)

            AppControlPreference.setDynamicFlag(context, prefKey, true)
        }
    }

    private fun showAppOptionsMenu(view: View, app: AppInfo, position: Int, holder: AppViewHolder) {
        val popup = PopupMenu(context, view)
        popup.menu.add(0, MENU_ADD_TO_HOME, 0, "Add to Home Screen")
        popup.menu.add(0, MENU_APP_INFO, 1, "App info")

        val packageInfo = try {
            context.packageManager.getPackageInfo(app.packageName.toString(), 0)
        } catch (e: Exception) {
            null
        }
        val isSystemApp = packageInfo != null && (packageInfo.applicationInfo?.flags?.and(
            ApplicationInfo.FLAG_SYSTEM
        ) ?: 0) != 0
        if (!isSystemApp) {
            popup.menu.add(0, MENU_UNINSTALL, 2, "Uninstall")
        }
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                MENU_APP_INFO -> {
                    openAppInfo(app.packageName.toString())
                    true
                }

                MENU_UNINSTALL -> {
                    onUninstallClick.invoke(
                        app,
                        holder.adapterPosition
                    )
                    true
                }

                else -> false
            }
        }
        popup.show()
    }


    private fun openAppInfo(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun removeApp(app: AppInfo, position: Int) {

        if (position >= 0 && position < allApps.size) {

            allApps.remove(app)

            notifyItemRemoved(position)

            notifyItemRangeChanged(
                position,
                allApps.size
            )
        }
    }


    private fun uninstallApp(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val MENU_ADD_TO_HOME = 0
        private const val MENU_APP_INFO = 1
        private const val MENU_WIDGETS = 2
        private const val MENU_UNINSTALL = 3
    }
}
