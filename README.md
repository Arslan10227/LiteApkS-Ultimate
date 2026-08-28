# LiteApkS - Ultimate

A personal, ad-free Material 3 clone of the Liteapks app store. Built with Jetpack Compose, it fetches public Liteapks data, lets you browse, search and download APKs, and keeps you up to date through GitHub releases.

## Features

- **Material 3 & Jetpack Compose** — modern UI with dynamic theming, animations and a clean bottom navigation.
- **Home, Search, Detail, Downloads, Settings and About screens** — full app-store browsing experience.
- **Offline-first cache** — posts, categories and search history are persisted in Room.
- **In-app downloader** — background downloads with WorkManager, pause/resume/retry and install support.
- **GitHub release update checker** — shows the latest release, changelog and one-tap download.
- **Lottie animations** — animated empty states and about screen.
- **TastyToast feedback** — styled toast messages for download started, completed, paused, failed, etc.
- **Centralized error & crash handling** — handled errors and fatal crashes are logged to local files.
- **No ads, no analytics, no telemetry** — only Firebase Cloud Messaging is used for update notices.
- **GitHub Actions CI** — manual release builds with signed APKs attached to releases.

## Build

Open the project in Android Studio Arctic Fox or later and run `gradle assembleRelease`. The release build expects a `keystore.properties` file and a `.jks` file in the project root; for CI these are created from GitHub secrets.
