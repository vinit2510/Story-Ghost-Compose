plugins { alias(libs.plugins.android.library) }
android {
    namespace = "in.vinit.storyghost.data.downloads"; compileSdk = 37; defaultConfig {
    minSdk = 26
}; compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21; targetCompatibility = JavaVersion.VERSION_21
}
}
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.koin.android)
}
