plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "1.6.10-1.0.2"
    id("kotlinx-serialization")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = AndroidConfig.sourceCompatibility
        targetCompatibility = AndroidConfig.sourceCompatibility
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    kotlinOptions {
        jvmTarget = AndroidConfig.jvmTarget
    }
}

dependencies {

    implementation(Libs.core_ktx)
    implementation(Libs.app_compat)


    // Room
    implementation(Libs.room_runtime)
    implementation(Libs.room_ktx)
    ksp(Libs.room_compiler_ksp)

    // Kotlin Serialization
    implementation(Libs.kotlinx_serialization)

    // Hilt
    implementation(Libs.hilt_android)
    kapt(Libs.hilt_compiler_dagger_kapt)
    kapt(Libs.hilt_compiler_android_kapt)

    // Test
    testImplementation(TestLibs.junit)
    testImplementation(TestLibs.truth)
    testImplementation(AndroidTestLibs.arch_core)
    testImplementation(TestLibs.coroutines)
    testImplementation(TestLibs.robolectric)

}