plugins {
    id("com.android.library")
    kotlin("android")
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

    // Coroutine
    implementation(Libs.coroutines_core)
    implementation(Libs.coroutines_android)

    // Pager
    implementation(Libs.pager)

    // Timer
    implementation(Libs.timer)
}