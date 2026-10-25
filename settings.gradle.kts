pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Story-Ghost-Compose"

include(
    ":app",

    // Core Modules
    ":core:model",
    ":core:network",
    ":core:ui",
    ":core:common",
    ":core:analytics",
    ":core:ads",
    ":core:play",
    ":core:notification",

    // Data Modules
    ":data:local",
    ":data:story",
    ":data:user",
    ":data:billing",
    ":data:downloads",
    ":data:config",

    // Feature Modules
    ":feature:home",
    ":feature:story",
    ":feature:highlight",
    ":feature:login",
    ":feature:onboarding",
    ":feature:downloads",
    ":feature:subscription",
    ":feature:forceupdate"
)
