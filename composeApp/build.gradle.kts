
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinSerializer)
    alias(libs.plugins.kotlinAndroidParcelize)
    alias(libs.plugins.nativeCocoaPod)
    alias(libs.plugins.androidGoogleService)
    id("org.jetbrains.dokka")
}

val secretFolder = "$projectDir/build/generatedSecret"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.bumble.appyx.utils.multiplatform.Parcelize"
                )
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    cocoapods {
        version = "1.0"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
        podfile = project.file("../iosApp/Podfile")

        ios.deploymentTarget = "17.0"

        framework {
            baseName = "ComposeApp"

            isStatic = true
        }

        pod("netfox") {
            extraOpts += listOf("-compiler-option", "-fmodules")
            version = "1.21.0"
        }

        pod("GoogleMaps") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseCore") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseAuth") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("GoogleSignIn") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        // Maps custom Xcode configuration to NativeBuildType
//        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
//        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }

    sourceSets.commonMain.configure {
        kotlin.srcDirs(secretFolder)
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // ktor
            implementation(libs.ktor.client.okhttp)

            // google maps
            implementation(libs.googleMaps.android.core)
            implementation(libs.googleMaps.android.compose)

            // play service location
            implementation(libs.android.play.service.location)

            // android firebase auth
            implementation(libs.androidFirebase.auth)
            implementation(libs.android.play.service.auth)

            // android google identity
            implementation(libs.android.credential)
            implementation(libs.android.credential.play.service.auth)
            implementation(libs.android.google.id)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.json)
            implementation(libs.ktor.client.logging)

            // viewmodel
            implementation(libs.jetbrain.viewModel)

            // appyx
            implementation(libs.appyx.navigation)
            implementation(libs.appyx.interaction)
            api(libs.appyx.backstack)

            // image loader
            implementation(libs.imageLoader)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "org.utsman.cmpbasic"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources", secretFolder)

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }

    defaultConfig {
        applicationId = "org.utsman.cmpbasic"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

fun generateSecret(file: String) {
    val propContent = file("$rootDir/$file").readText()
    val propData = parseProp(propContent)

    var ktContent = "package org.utsman.cmpbasic\n\nobject SecretConfig {\n"
    propData.forEach { (key, value) ->
        ktContent += "    const val $key = $value\n"
    }

    ktContent += "}"

    val folder = file(secretFolder)
    if (!folder.exists()) {
        folder.mkdirs()
    }

    val fileSecret = file("$secretFolder/SecretConfig.kt")
    if (!fileSecret.exists()) {
        fileSecret.createNewFile()
    }

    fileSecret.writeText(ktContent)

}

fun parseProp(content: String): Map<String, Any> {
    val propData = mutableMapOf<String, Any>()
    content.lines().forEach { line ->
        val key = line.substringBefore("=")
        val rawValue = line.substringAfter("=")
        val value: Any = when {
            rawValue == "true" || rawValue == "false" -> {
                rawValue.toBoolean()
            }
            rawValue.toIntOrNull() != null -> {
                rawValue.toInt()
            }
            rawValue.toLongOrNull() != null -> {
                rawValue.toLong()
            }
            else -> "\"$rawValue\""
        }

        propData[key] = value
    }

    return propData
}

tasks.register("generateSecret") {
    doLast {
        generateSecret("secret.properties")
    }
}

afterEvaluate {
    tasks.getByName("generateComposeResClass").dependsOn("generateSecret")
}