plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
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
        dataBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

    // Startup flow kit (Splash -> Language -> Onboarding -> Permission -> Policy -> Home).
    // SDP dimens come from the project's own res/values/sdp.xml.
    implementation("com.airbnb.android:lottie:6.7.1")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.google.android.gms:play-services-ads:24.7.0")
    implementation("com.facebook.android:audience-network-sdk:6.21.0")
    implementation("com.google.ads.mediation:facebook:6.21.0.0") {
        exclude(group = "org.jacoco", module = "org.jacoco.core")
    }
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.android.installreferrer:installreferrer:2.2")
    implementation("androidx.work:work-runtime:2.8.1")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.sun.jersey:jersey-core:1.19.4")

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
