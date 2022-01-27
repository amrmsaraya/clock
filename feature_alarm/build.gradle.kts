plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "1.6.10-1.0.2"
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk

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
        sourceCompatibility = AndroidConfig.sourceCompatibility
        targetCompatibility = AndroidConfig.targetCompatibility
    }

    kotlinOptions {
        jvmTarget = AndroidConfig.jvmTarget
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {

    // Local Modules
    implementation(project(":common"))
    implementation(project(":database"))

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
    testImplementation(TestLibs.junit)
    testImplementation(TestLibs.coroutines)
    testImplementation(TestLibs.truth)
    testImplementation(TestLibs.mockk)
    testImplementation(TestLibs.mockk_jvm)

    // Android Test
    androidTestImplementation(AndroidTestLibs.junit)
    androidTestImplementation(AndroidTestLibs.truth)
    androidTestImplementation(AndroidTestLibs.junit_compose)
    debugImplementation(AndroidTestLibs.debug_compose_ui)
    androidTestImplementation(AndroidTestLibs.arch_core)
    androidTestImplementation(AndroidTestLibs.coroutines)
    androidTestImplementation(AndroidTestLibs.mockk)
    androidTestImplementation(AndroidTestLibs.mockk_jvm)

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

    // Pager
    implementation(Libs.pager)

    // Timer
    implementation(Libs.timer)
}