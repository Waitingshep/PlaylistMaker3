plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
    // Для Kotlin 2.0+ (если у тебя Kotlin 1.9, используй composeOptions ниже)
    // id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.practicum.playlistmaker3"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.practicum.playlistmaker3"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // ВКЛЮЧАЕМ COMPOSE
    buildFeatures {
        compose = true
    }

    // Если Kotlin < 2.0, раскомментируй и укажи версию компилятора:
    // composeOptions {
    //     kotlinCompilerExtensionVersion = "1.5.8" // под свою версию Kotlin
    // }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material.v1110)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.glide)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Coroutines
    implementation(libs.coroutines.android)

    // ViewModel и LiveData
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Koin
    implementation(libs.koin.android)

    implementation(libs.androidx.viewpager2)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // ========== COMPOSE ==========
    // Activity Compose (для setContent)
    implementation("androidx.activity:activity-compose:1.9.3")

    // Compose BOM (управляет версиями всех Compose-библиотек)
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))

    // Material 3
    implementation("androidx.compose.material3:material3")

    // UI, Foundation, Runtime
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime")

    // Иконки (если используешь Icons.Default.*)
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // LiveData в Compose
    implementation("androidx.compose.runtime:runtime-livedata:1.7.6")

    // Coil для картинок
    implementation("io.coil-kt:coil-compose:2.7.0")
    // Koin для Compose
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
}