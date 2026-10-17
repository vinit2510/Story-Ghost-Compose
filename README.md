# Story Ghost

An open-source Android app to view Instagram stories anonymously, uncover hidden mentions, and
download media — built 100% with **Jetpack Compose**, **Material 3**, and modern multi-module
architecture.

[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B-3DDC84?logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

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
  list.
- **Hidden Mention Inspector**: Automatically identifies hidden, transparent, or tiny mentions
  placed on stories and presents them in an easy-to-tap card.
- **Direct Media Downloads**: Save story photos and full-resolution MP4 videos directly to your
  gallery via Android's MediaStore.
- **ExoPlayer Video Playback**: Smooth, hardware-accelerated playback powered by AndroidX Media3
  with progress tracking and mute/unmute toggle.
- **User Search & Highlights**: Search any username to view their active 24-hour stories and pinned
  highlight collections.
- **Flexible Sign-in**: Log in through an in-app WebView session or paste your session cookies
  directly. Your credentials stay strictly on your device.
- **Offline Downloads Gallery**: View and manage all your saved media inside the app with quick
  options to share or delete.
- **8+ Languages**: Fully localized in English, Spanish, Hindi, French, German, Portuguese, Arabic,
  and Japanese.
- **Material 3 UI**: Clean dark theme with edge-to-edge support and smooth Compose animations.

---

## Architecture

Story Ghost is built using a modern **multi-module architecture** following Clean Architecture
principles and Unidirectional Data Flow (UDF).

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

    subgraph Core["Core Modules (:core:*)"]
        CoreNetwork --> CoreModel
        CoreNetwork --> CoreCommon
        CoreAds[":core:ads"]
        CoreAnalytics[":core:analytics"]
        CorePlay[":core:play"]
        CoreUI --> CoreModel
        CoreCommon
    end
```

### Module Structure

```text
├── app/                  # Application class, root DI graph, Navigation 3 backstack
├── feature/              # Independent UI features (Compose + ViewModels)
│   ├── home/             # Story feed, search, and highlight mode
│   ├── story/            # Fullscreen story viewer & ExoPlayer integration
│   ├── highlight/        # User highlight reels
│   ├── downloads/        # Saved media gallery
│   ├── login/            # WebView login & cookie parser
│   ├── onboarding/       # First-launch introduction
│   └── subscription/     # In-app purchase & billing options
├── data/                 # Repositories and data sources
│   ├── story/            # Story & highlight API integration
│   ├── user/             # User search & profile fetching
│   ├── downloads/        # MediaStore & DownloadManager operations
│   ├── billing/          # Google Play Billing Client integration
│   ├── config/           # Firebase Remote Config repository
│   └── local/            # Local data source abstraction
└── core/                 # Shared low-level modules
    ├── ui/               # Reusable Composables, theme, and VideoPlayer
    ├── model/            # Plain Kotlin domain data models
    ├── network/          # OkHttp, Retrofit, interceptors, and mock handlers
    ├── common/           # DataStore LocalStorage, file helpers, logger
    ├── ads/              # AdMob & Meta Audience Network integration
    ├── analytics/        # Firebase Analytics abstraction
    └── play/             # In-App Review & In-App Update helpers
```

### Key Libraries

- **UI:** Jetpack Compose, Material 3, Compose Foundation
- **Navigation:** AndroidX Navigation 3 (`androidx.navigation3`)
- **Dependency Injection:** [Koin](https://insert-koin.io/)
- **Media Playback:** AndroidX Media3 (ExoPlayer)
- **Image Loading:** Coil 3 (with video thumbnail decoding)
- **Networking:** Retrofit 2, OkHttp 3, Gson
- **Local Storage:** Jetpack DataStore Preferences
- **Testing:** JUnit 4, MockK, Cash App Turbine

---

## Getting Started

### Prerequisites

- **Android Studio:** Ladybug (2024.2+) or newer
- **JDK:** Java 21 (bundled with Android Studio)
- **Android SDK:** Compile SDK 35/37, Min SDK 26 (Android 8.0 Oreo)

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

##### Key Environment Variables:

| Variable            | Description                                                                                                                      | Default in `.env.example`      |
|:--------------------|:---------------------------------------------------------------------------------------------------------------------------------|:-------------------------------|
| `IS_MOCK`           | When `true`, uses bundled offline JSON fixtures. Set to `false` for live Instagram network calls.                                | `true`                         |
| `BASE_URL`          | Base endpoint URL for network requests.                                                                                          | `https://www.mock-server.com/` |
| `FIREBASE_BASE64`   | Base64-encoded `google-services.json`. The build script automatically decodes this into `app/google-services.json` during build. | Included mock configuration    |
| `QH_*`              | Instagram GraphQL query hashes for stories, highlights, and profile endpoints.                                                   | Pre-configured                 |
| `ADMOB_*` / `FAN_*` | Ad unit IDs. Populated with official Google test ad unit IDs.                                                                    | Test ad IDs                    |

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
  directly from local JSON assets. This is ideal for testing UI components, animations, and flows.
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

## Contributing

Contributions, bug reports, and suggestions are very welcome!

1. Fork the repo (`https://github.com/vinit2510/Story-Ghost-Compose`)
2. Create your feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m "Add my feature"`)
4. Make sure tests pass (`./gradlew testDebugUnitTest`)
5. Push to the branch (`git push origin feature/my-feature`)
6. Open a Pull Request

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
