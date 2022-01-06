plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "1.6.10-1.0.2"
    id("kotlinx-serialization")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "com.github.amrmsaraya.clock"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = Versions.sourceCompatibility
        targetCompatibility = Versions.targetCompatibility
    }

    kotlinOptions {
        jvmTarget = Versions.jvmTarget
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose_compiler
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // local modules
    implementation(project(":common"))
    implementation(project(":feature_alarm"))
    implementation(project(":feature_clock"))
    implementation(project(":feature_stopwatch"))
    implementation(project(":feature_timer"))

    // Core
    implementation(Libs.core_ktx)
    implementation(Libs.app_compat)
    implementation(Libs.material)
    implementation(Libs.compose_ui)
    implementation(Libs.compose_material)
    implementation(Libs.compose_material3)
    implementation(Libs.compose_ui_tooling)
    implementation(Libs.compose_icons)
    implementation(Libs.compose_icons_extended)
    implementation(Libs.activity_compose)
    implementation(Libs.lifecycle_runtime)

    // Test
    testImplementation(Libs.test_junit)
    testImplementation(Libs.test_coroutines)
    testImplementation(Libs.test_truth)

    // Android Test
    androidTestImplementation(Libs.androidTest_junit)
    androidTestImplementation(Libs.androidTest_junit_compose)
    debugImplementation(Libs.debug_compose_ui)
    androidTestImplementation(Libs.androidTest_truth)

    // Splash Screen
    implementation(Libs.splashscreen)

    // ViewModel
    implementation(Libs.viewModel)
    implementation(Libs.viewModel_compose)

    // SystemUiController
    implementation(Libs.systemuicontroller)

    // Coroutine
    implementation(Libs.coroutines_core)
    implementation(Libs.coroutines_android)

    // Kotlin Serialization
    implementation(Libs.kotlinx_serialization)

    // Room
    implementation(Libs.room_runtime)
    implementation(Libs.room_ktx)
    ksp(Libs.room_compiler_ksp)

    // Hilt
    implementation(Libs.hilt_android)
    kapt(Libs.hilt_compiler_dagger_kapt)
    implementation(Libs.hilt_compose_navigation)
    kapt(Libs.hilt_compiler_android_kapt)

    // Navigation
    implementation(Libs.navigation)

    // Pager
    implementation(Libs.pager)

    // Timer
    implementation(Libs.timer)
}