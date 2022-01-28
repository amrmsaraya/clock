plugins {
    id("com.android.library")
    kotlin("android")
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
    androidTestImplementation(TestLibs.truth)
    androidTestImplementation(AndroidTestLibs.junit_compose)
    debugImplementation(AndroidTestLibs.debug_compose_ui)
    androidTestImplementation(AndroidTestLibs.arch_core)
    androidTestImplementation(TestLibs.coroutines)
    androidTestImplementation(TestLibs.mockk)
    androidTestImplementation(TestLibs.mockk_jvm)

    // Coroutine
    implementation(Libs.coroutines_core)
    implementation(Libs.coroutines_android)

    // Pager
    implementation(Libs.pager)

    // Timer
    implementation(Libs.timer)
}