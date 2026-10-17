plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "in.vinit.storyghost.core.model"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.converter.gson)
}
