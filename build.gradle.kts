plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.hilt.android.gradle.plugin) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp.plugin) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.7" apply false
    alias(libs.plugins.kotlin.dokka)
    id("org.jetbrains.kotlinx.kover") version "0.9.9"
}

subprojects {
    pluginManager.apply("org.jetbrains.kotlinx.kover")
    pluginManager.apply("org.jetbrains.dokka")
}
