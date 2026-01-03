plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.trevia"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.trevia"
        minSdk = 26
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.material3)
    implementation(libs.androidx.hilt.common)
    //AMap SDK
    implementation(files("libs/AMap3d_Lite.aar"))
    //LeanCloud
    implementation("cn.leancloud:storage-android:8.2.28")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation(libs.androidx.ui)
    //Room
    val room_version = "2.8.3"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    //Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    implementation("com.google.dagger:hilt-android:2.57.1")
    implementation("androidx.hilt:hilt-work:1.3.0")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    ksp("androidx.hilt:hilt-compiler:1.3.0")
    //Coil
    implementation("io.coil-kt.coil3:coil-compose:3.0.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0")
    // WorkManager dependency
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    //EXIF
    implementation("androidx.exifinterface:exifinterface:1.4.1")
    //Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // Retrofit with Kotlin serialization Converter
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.9.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.9.0")
    implementation("androidx.media3:media3-ui:1.9.0")
    implementation("androidx.media3:media3-ui-compose:1.9.0")

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}