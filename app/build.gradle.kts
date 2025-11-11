plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Включаем минификацию для оптимизации
            isShrinkResources = true // Включаем сжатие ресурсов
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Ультра-оптимизации для release
            buildConfigField("boolean", "ENABLE_ANIMATIONS", "true")
            buildConfigField("boolean", "ENABLE_PERFORMANCE_MONITORING", "false")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            // Ультра-оптимизации для debug режима
            buildConfigField("boolean", "ENABLE_ANIMATIONS", "true")
            buildConfigField("boolean", "ENABLE_PERFORMANCE_MONITORING", "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        // Стабильные оптимизации для Compose
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-Xjvm-default=all" // Включаем все JVM оптимизации
        )
    }
    
    // Настройки KAPT для совместимости с Room и Hilt
    kapt {
        correctErrorTypes = true
        useBuildCache = true
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true // Включаем BuildConfig для оптимизаций
    }
    
    // Оптимизации для Compose
    composeOptions {
        // Полагаться на версию компилятора от плагина Kotlin Compose
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Date/Time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // УЛЬТРА-ОПТИМИЗАЦИЯ для Redmi Note 13
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
    implementation("androidx.tracing:tracing:1.2.0")
}