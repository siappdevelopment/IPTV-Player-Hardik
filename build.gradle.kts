// Top-level build file where you can add configuration options common to all sub-projects/modules.

// AGP's built-in Kotlin is older than the Kotlin metadata used by current libraries
// (coroutines, Coil). Pinning a newer Kotlin Gradle plugin on the build classpath makes
// AGP use it for its built-in Kotlin support.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.21")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ksp) apply false
}
