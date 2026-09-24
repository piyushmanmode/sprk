# Spark Habit Tracker & AI Coach 🔥

A modern, high-performance Android habit and streak tracking app built with Jetpack Compose, Material Design 3, Room, and Google Gemini AI.

---

## Features

- **Daily Streak Tracking**: Interactive 7-day carousel, habit check-ins, flame status, and milestone progression.
- **AI Habit Coach (Powered by Gemini)**:
  - **Atomic Habit Breakdown**: Deconstructs ambitious aspirations (e.g. "Get fit") into BJ Fogg / James Clear micro-habits anchored to daily cues.
  - **Streak Recovery**: Empathetic, behavioral-science momentum recovery advice and emergency 2-minute routines to restart broken streaks.
  - **Weekly Insight Summaries**: Personalized progress evaluations, momentum metrics, and focus areas for the upcoming week.
- **Zero-Permission Photo Picker**: Change profile photos using modern `ActivityResultContracts.PickVisualMedia` with zero storage permissions required.
- **Android App Widgets**:
  - Streaks Widget (`RemoteViews`)
  - Today's Tasks Widget
  - Spark Flame Widget
  - Heatmap Activity Widget
- **Milestone Trophies**: Automatically unlocks awards at streak thresholds (3, 7, 14, 15, 30, and 60 days) with sharing capability.
- **Local Persistence**: Powered by SQLite via Android Room with reactive Kotlin Flows.

---

## Tech Stack & Architecture

- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: Clean MVVM (Model-View-ViewModel) + Repository Pattern
- **Database**: Room Persistence Library (`room-runtime`, `room-ktx`, KSP)
- **AI & LLM**: Firebase AI (`firebase-ai`) with Gemini 2.5 Flash + Direct REST fallback
- **Image Loading**: Coil Compose (`coil-compose`)
- **Unit & Robolectric Testing**: JUnit 4, Robolectric, Roborazzi

---

## Environment Variables & Secrets Management

The app uses the `secrets-gradle-plugin` to securely inject API keys at build time via `.env`.

### Setting up `GEMINI_API_KEY`:

1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and configure your Gemini API Key:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
3. In Google AI Studio, keys are automatically injected at runtime via user secrets.
4. If running outside AI Studio or without an API key, the app gracefully falls back to intelligent, built-in behavioral science coaching recommendations with zero crashes.

---

## Build & Test Commands

### 1. Build Debug APK
```bash
./gradlew assembleDebug
```
*Note: Debug APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.*

### 2. Run Local JVM & Robolectric Unit Tests
```bash
./gradlew testDebugUnitTest
```

### 3. Check Applet Compilation
```bash
./gradlew compileDebugKotlin
```

---

## Continuous Integration (GitHub Actions)

A GitHub Actions workflow is provided in `.github/workflows/android.yml`. On every push and pull request to `main` or `master`, it validates:
1. End-to-end debug build compilation (`./gradlew assembleDebug`)
2. Complete local test suite execution (`./gradlew testDebugUnitTest`)

---

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
