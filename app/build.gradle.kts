plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
}

android {
    lint {
        // text sizes deliberately use the SDP scale (@dimen/_Nsdp) instead of sp
        disable += "SpUsage"
        // the SDP resource set is intentionally complete; unused sizes are shrunk out of release builds
        disable += "UnusedResources"
    }
    namespace = "com.iptvplayer.xtreamiptv.myiptvpro"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.iptvplayer.xtreamiptv.myiptvpro"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            optimization {
                enable = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                )
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.material)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)

    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation("com.intuit.sdp:sdp-android:1.0.3")

    //    ads
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation("com.google.android.exoplayer:exoplayer:2.18.1")
    implementation("com.github.bullheadandplato:AndroidEqualizer:2.2")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.google.firebase:firebase-crashlytics:20.0.4")
    implementation("com.airbnb.android:lottie:6.7.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation(libs.play.services.ads)
    implementation(libs.facebook.android.sdk)
    implementation(libs.firebase.config)
    implementation(platform(libs.firebase.bom))
    implementation(libs.gson)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.shimmerlayout)
    implementation("com.facebook.android:audience-network-sdk:6.+")
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")
    implementation(libs.installreferrer)
    implementation(libs.volley)
    implementation(libs.jersey.core)
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("org.jsoup:jsoup:1.20.1")
    implementation("com.otaliastudios:cameraview:2.7.2")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("io.github.ShawnLin013:number-picker:2.4.13")
    implementation("com.github.hadibtf:SemiCircleArcProgressBar:1.1.1")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    implementation(libs.dexter)

}
