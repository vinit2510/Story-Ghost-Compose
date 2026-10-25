# Story Ghost

An open-source Android app to view Instagram stories anonymously, uncover hidden mentions, and
download media — built 100% with **Jetpack Compose**, **Material 3**, and modern multi-module
architecture.

[![GitHub Release](https://img.shields.io/github/v/release/vinit2510/Story-Ghost-Compose?color=0969DA&logo=github&logoColor=white)](https://github.com/vinit2510/Story-Ghost-Compose/releases/latest)
[![Google Play Downloads](https://img.shields.io/endpoint?url=https://play.rajkumaar.co.in/downloads?id=in.vinit.storyghost&color=34A853&logo=googleplay&logoColor=white)](https://play.google.com/store/apps/details?id=in.vinit.storyghost)
[![Google Play Rating](https://img.shields.io/endpoint?url=https://play.rajkumaar.co.in/rating?id=in.vinit.storyghost&color=FBBC04&logo=googleplay&logoColor=white)](https://play.google.com/store/apps/details?id=in.vinit.storyghost)
[![CI](https://github.com/vinit2510/Story-Ghost-Compose/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/vinit2510/Story-Ghost-Compose/actions/workflows/ci.yml)
[![CD](https://github.com/vinit2510/Story-Ghost-Compose/actions/workflows/release.yml/badge.svg)](https://github.com/vinit2510/Story-Ghost-Compose/actions/workflows/release.yml)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B-3DDC84?logo=android&logoColor=white)](https://android.com)
[![Jetpack Compose](https://img.shields.io/badge/Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

![Story Ghost Features Showcase](.github/assets/banner.png)

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=in.vinit.storyghost">
    <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="75">
  </a>
</p>

---

## Why Story Ghost?

When you view stories on Instagram, your profile is added to the viewer list. Also, creators often
hide small mentions in stories that are impossible to tap, and downloading a story video usually
requires screen recording or sketchy third-party websites full of ads.

Story Ghost gives you a clean, private, and ad-minimal way to:

- Browse stories and highlights without leaving seen receipts.
- Detect hidden mentions and view tagged profiles instantly.
- Save photos and high-resolution videos directly to your device with one tap.

---

## Features

- **Anonymous Story Viewer**: Watch stories and highlights without appearing on the account's viewer
  list or sending seen receipts.
- **Hidden Mention Inspector**: Automatically scans story sticker data to identify hidden,
  transparent, or micro-sized mentions and reveals tagged accounts in an interactive popup.
- **Direct Media Downloads**: Save story photos and full-resolution MP4 videos directly to your
  device's public `Download/Story Ghost/` directory via Android's `DownloadManager` with real-time
  download notification progress.
- **ExoPlayer Video Playback**: Smooth, hardware-accelerated looping video playback powered by
  AndroidX Media3 (`ExoPlayer`), featuring lifecycle-aware auto-pausing and shimmer buffering
  loaders.
- **User Search & Highlights**: Search any public profile to inspect their active 24-hour stories
  and browse chronologically ordered highlight reels.
- **Flexible Sign-in**: Log in securely through an in-app WebView session or paste your session
  cookies (`sessionid` & `ds_user_id`) directly. Credentials stay strictly stored in local
  DataStore.
- **Offline Downloads Gallery**: View and manage all your saved media inside the app with quick
  options to preview, share, or delete downloaded files.
- **In-App Purchases (Premium)**: In-app billing powered by Google Play Billing Client (
  `:data:billing`, `:feature:subscription`) to unlock hidden mention inspection and remove all
  interstitial and open ads.
- **Native Push Notifications (`:core:notification`)**: Standalone, 100% data-only push notification
  system built on Firebase Cloud Messaging (FCM HTTP v1). Supports on-device constraint filtering (
  app version code ranges, user subscription tiers, and auth states), asynchronous rich media
  loading (BigPicture banners and LargeIcon avatars), interactive action buttons, and direct deep
  link routing — completely decoupled from third-party push SDKs.
- **Seamless DataStore Migration**: Automated, backward-compatible migration from legacy
  `SharedPreferences` to Jetpack DataStore Preferences ensures zero session loss for upgrading
  users.
- **Multilingual Support**: Fully localized in 8 languages (English, Arabic, French, Indonesian,
  Italian, Portuguese, Spanish, and Turkish) — with community contributions for additional languages
  welcome!
- **Modern Material 3 UI**: Clean dark theme with edge-to-edge support, custom shimmer placeholders,
  and fluid Compose transitions.

---

## Architecture

Story Ghost is built using a modern **multi-module architecture** following Clean
Architecture principles and Unidirectional Data Flow (UDF).

```mermaid
graph TD
    App[":app"] --> Features
    App --> CoreUI[":core:ui"]
    App --> CoreCommon[":core:common"]

    subgraph Features["Feature Modules (:feature:*)"]
        Home[":feature:home"]
        Story[":feature:story"]
        Highlight[":feature:highlight"]
        Downloads[":feature:downloads"]
        Subscription[":feature:subscription"]
        Login[":feature:login"]
        Onboarding[":feature:onboarding"]
        ForceUpdate[":feature:forceupdate"]
    end

    Features --> CoreUI
    Features --> CoreModel[":core:model"]
    Features --> DataModules

    subgraph DataModules["Data Modules (:data:*)"]
        DataStory[":data:story"]
        DataUser[":data:user"]
        DataBilling[":data:billing"]
        DataDownloads[":data:downloads"]
        DataConfig[":data:config"]
        DataLocal[":data:local"]
    end

    DataModules --> CoreNetwork[":core:network"]
    DataModules --> CoreModel
    DataModules --> CoreCommon

    App --> CoreNotification[":core:notification"]

    subgraph Core["Core Modules (:core:*)"]
        CoreNetwork --> CoreModel
        CoreNetwork --> CoreCommon
        CoreAds[":core:ads"]
        CoreAnalytics[":core:analytics"]
        CorePlay[":core:play"]
        CoreNotification
        CoreUI --> CoreModel
        CoreCommon
    end
```

### Module Structure

```text
├── app/                  # Application entry point, root Koin DI graph, Navigation 3 backstack
├── feature/              # Independent UI features (Compose + ViewModels)
│   ├── home/             # Story feed, following list, and profile search
│   ├── story/            # Fullscreen story viewer & ExoPlayer integration
│   ├── highlight/        # User highlight reels
│   ├── downloads/        # Saved media gallery
│   ├── login/            # WebView login & cookie parser dialogs
│   ├── onboarding/       # First-launch onboarding flow
│   ├── subscription/     # In-app purchase & Google Play Billing UI
│   └── forceupdate/      # Mandatory app update prompt screen
├── data/                 # Repositories and data sources
│   ├── story/            # Story & highlight API repository (modern Relay query)
│   ├── user/             # User profile & following list repository
│   ├── downloads/        # Decoupled DownloadManager repository
│   ├── billing/          # Google Play Billing Client integration
│   ├── config/           # Firebase Remote Config repository
│   └── local/            # Local data source abstraction
└── core/                 # Reusable low-level foundations
    ├── ui/               # Design system, theme, reusable components & VideoPlayer
    ├── model/            # Domain models (DTOs, entity models)
    ├── network/          # OkHttp, Retrofit, SafeApiCall & MockInterceptor
    ├── common/           # LocalStorage DataStore, Logger, TimeUtils
    ├── ads/              # AdMob & Meta Audience Network integration
    ├── analytics/        # Firebase Analytics abstraction
    ├── play/             # In-App Review & In-App Update helpers
    └── notification/     # Standalone FCM push notifications, constraints & rich media
```

### Key Libraries & Tooling

- **UI:** Jetpack Compose, Material 3, Compose Foundation
- **Navigation:** AndroidX Navigation 3 (`androidx.navigation3`)
- **Dependency Injection:** [Koin](https://insert-koin.io/)
- **Media Playback:** AndroidX Media3 (ExoPlayer)
- **Image Loading:** Coil 3 (with OkHttp integration)
- **Networking:** Retrofit, OkHttp, Gson
- **In-App Billing:** Google Play Billing Client
- **Local Storage:** Jetpack DataStore Preferences
- **Push Notifications:** Firebase Cloud Messaging (FCM HTTP v1 data-only)
- **Analytics, Crashlytics & Performance:** Firebase Analytics, Crashlytics, Performance
- **Monetization:** Google AdMob & Meta Audience Network (FAN)
- **Testing:** JUnit, MockK, Cash App Turbine, Coroutines Test

---

## Getting Started

### Prerequisites

- **Android Studio:** Ladybug (2024.2+) or newer
- **JDK:** Java 21 (bundled with Android Studio / JBR)
- **Android SDK:** Compile SDK 37, Target SDK 37, Min SDK 26 (Android 8.0 Oreo)
- **NDK:** Configured in `app/build.gradle.kts` with native debug symbols for release builds

---

### End-to-End Setup Guide

#### 1. Clone the Repository

```bash
git clone https://github.com/vinit2510/Story-Ghost-Compose.git
cd Story-Ghost-Compose
```

#### 2. Environment Configuration (`.env`)

The build system uses a root `.env` file to manage API parameters, GraphQL query hashes, and ad unit
IDs.

A template file [`.env.example`](.env.example) is already included at the root of the project with
working defaults:

```bash
# Copy the example file to create your active .env
cp .env.example .env
```

> **Note:** If you skip this step, Gradle's build script (`readEnv.gradle`) will automatically copy
`.env.example` to `.env` on your first build so the project compiles immediately.

#### 3. Build & Run

- **In Android Studio:**  
  Open the project directory, wait for Gradle sync to complete, and click **Run** (`Shift + F10`).
- **Via Command Line:**
  ```bash
  ./gradlew assembleDebug
  ```

---

### Mock Mode vs. Live Mode

- **Mock Mode (`IS_MOCK=true`):**  
  No Instagram account or live internet connection is needed. The app's `MockInterceptor` intercepts
  requests and serves realistic mock data (story feeds, user profiles, video reels, and highlights)
  directly from local JSON assets. All bundled mock media URLs are verified public links (Unsplash
  images and VideoJS MP4 videos) that render immediately. This is ideal for testing UI components,
  animations, and flows.
- **Live Mode (`IS_MOCK=false`):**  
  The app makes real network calls to Instagram. Sign in using the in-app WebView or paste your
  session cookies to browse live stories.

---

## Running Tests

To run the full unit test suite across all modules:

```bash
./gradlew testDebugUnitTest
```

---

## Push Notifications

Story Ghost uses a native, 100% data-only push notification module (`:core:notification`) built on **Firebase Cloud Messaging (FCM HTTP v1)** — replacing third-party SDKs like OneSignal with a lightweight, zero-tracking solution that gives complete control over custom layouts, deep links, and on-device targeting.

### Triggering Notifications via GitHub Actions

Broadcast notifications are sent directly through the [push_notification.yml](.github/workflows/push_notification.yml) workflow:

1. Navigate to **Actions &rarr; "Push Notification" &rarr; "Run workflow"**.
2. Fill in the parameters (Title, Body, Channel, Deep Link, Banner/Icon URLs, or client filters like `constraint_tier`).
3. Click **Run workflow** to dispatch.

> **Required Secret:** Add your Firebase service account private key JSON as a repository secret named `FIREBASE_SERVICE_ACCOUNT` (*Firebase Console &rarr; Project Settings &rarr; Service accounts*).

*(For local testing, the bundled CLI script `.github/scripts/send_fcm.py` is also available.)*

---

## Contributing

Contributions, bug reports, and translations are very welcome!

1. **Fork & Branch**: Create a feature branch off **`dev`**:
   ```bash
   git checkout -b feat/your-feature-name
   ```
2. **Local Development**: No API keys or credentials needed. Keep `IS_MOCK=true` in `.env` (default)
   to develop and test offline with mock data.
3. **Run Tests**: Verify that unit tests pass:
   ```bash
   ./gradlew testDebugUnitTest
   ```
4. **Submit PR**: Open a Pull Request targeting the **`dev`** branch
   using [Conventional Commits](https://www.conventionalcommits.org/).

### Localization

To add or update translations, edit the string resources in
`core/ui/src/main/res/values-<locale>/strings.xml` and submit a PR titled
`feat(i18n): add <Language> translations`.

---

### Releases & Direct APK Downloads

In addition
to [Google Play Store](https://play.google.com/store/apps/details?id=in.vinit.storyghost) releases,
production builds are automatically published on GitHub:

- When a new release tag (e.g., `vX.Y.Z`) is pushed to `master` or triggered via workflow dispatch,
  GitHub Actions automatically builds, signs, and publishes a new **GitHub Release** with both the
  signed production AAB and universal APK.
- Users and testers can download and install updates directly from
  the [GitHub Releases page](https://github.com/vinit2510/Story-Ghost-Compose/releases) to get the
  latest features without waiting for app store review delays.

---

## License

```text
Copyright 2026 Vinit Hingu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## Disclaimer

This project is open-source and intended for educational and personal use only. It is not affiliated
with, authorized, maintained, or endorsed by Instagram or Meta Platforms, Inc. Use responsibly and
in accordance with applicable laws.
