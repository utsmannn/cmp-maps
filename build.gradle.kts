plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinAndroidParcelize) apply false
    alias(libs.plugins.nativeCocoaPod) apply false
    alias(libs.plugins.androidGoogleService) apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
}