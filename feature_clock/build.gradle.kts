plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "1.6.10-1.0.2"
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
}

dependencies {

    implementation(project(":common"))

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
    implementation(Libs.compose_activity)
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

    // ViewModel
    implementation(Libs.viewModel)
    implementation(Libs.viewModel_compose)

    // Coroutine
    implementation(Libs.coroutines_core)
    implementation(Libs.coroutines_android)

    // Room
    implementation(Libs.room_runtime)
    implementation(Libs.room_ktx)
    ksp(Libs.room_compiler_ksp)

    // Hilt
    implementation(Libs.hilt_android)
    kapt(Libs.hilt_compiler_dagger_kapt)
    implementation(Libs.hilt_compose_navigation)
    kapt(Libs.hilt_compiler_android_kapt)
}