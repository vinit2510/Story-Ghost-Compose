plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "in.vinit.storyghost.data.billing"
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
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":data:local"))

    implementation(libs.play.billing)
    implementation(libs.koin.android)
}
