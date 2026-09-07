plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.umkmsmart"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.umkmsmart"
        minSdk = 23
        targetSdk = 35
        versionCode = 3
        versionName = "2.1"
    }
}

kotlin {
    jvmToolchain(17)
}
