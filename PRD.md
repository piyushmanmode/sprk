# Spark – Product Requirements Document (PRD)

**Version:** 2.0 (Post-Review Revised & Aligned)  
**Status:** Approved / Active Baseline  
**Target Platform:** Android (Native Kotlin · Jetpack Compose · Room Database)  
**Last Updated:** September 2026  

---

## Executive Summary & Vision

**Spark** is a high-motivation, offline-first Android daily habit and streak tracking application built with a vibrant, flame-themed visual identity. Spark transforms abstract self-improvement goals into tangible visual momentum through real-time streak counting, dot-matrix consistency heatmaps, celebratory milestones, 4 interactive Android home-screen widgets, and an intelligent AI Habit Coach powered by Google DeepMind / Firebase AI (Gemini).

---

## 1. Problem Statement & Success Metrics

### 1.1 Problem Statement
Over 80% of individuals who start new habits abandon them within the first 7 days due to:
1. **Friction & Cognitive Load:** Cluttered multi-step habit trackers that feel like spreadsheets rather than rewarding personal spaces.
2. **Lack of Immediacy & Visual Reward:** Days of effort pass with no visceral feedback or celebratory feeling when completing an action.
3. **Out-of-Sight, Out-of-Mind:** Requiring users to remember to open the app each day instead of presenting glanceable momentum directly on the Android home screen.

Spark directly solves these problems through an instant single-tap check-off loop, micro-celebration animations, automated streak-retention mathematics, glanceable home-screen widgets, and celebratory shareable trophy cards.

### 1.2 Success Metrics (KPIs)
- **Day-7 Retention (D7):** ≥ 45% of users actively checking off at least one habit on day 7 (industry average: ~18%).
- **Day-30 Retention (D30):** ≥ 25% of active streak holders maintaining 30-day momentum.
- **Daily Completion Rate:** ≥ 70% of scheduled daily habits checked off per active user.
- **Home Screen Widget Adoption:** ≥ 50% of active users installing at least one of the 4 home-screen widgets.
- **Milestone Celebration Shares:** ≥ 15% of unlocked trophies shared via the native Android share sheet.

---

## 2. Technical Architecture & Stack Alignment

### 2.1 Core Architectural Principles
- **Offline-First Local Persistence:** All habit creation, completions, streaks, trophies, and user preferences are saved locally in a SQLite database via Android **Room**. The app is 100% functional without an active network connection.
- **Zero-Friction Onboarding:** Users can start creating habits immediately on initial launch without forced account creation or login paywalls.
- **Reliable Local Reminders:** Daily habit notifications run on-device using Android `AlarmManager` and `WorkManager` with a custom notification channel (`spark_reminders`). No external server-side Firebase Cloud Messaging (FCM) broker is required for local habit reminders.
- **AI Acceleration via Firebase AI (Gemini):** Optional cloud-assisted motivational sparks, personalized streak advice, and habit decomposition powered by the server-side Gemini API.
- **Google Account Synchronization (Optional):** Integrated Google Sign-In / Credential Manager enables seamless profile photo, name, and email population with cloud-backup readiness.

### 2.2 Technology Stack
| Layer | Technology | Specification / Details |
|---|---|---|
| **OS Support** | Android 8.0+ (API 26) through Android 15+ | Minimum SDK 26, Target/Compile SDK 35 |
| **Language & Tooling** | Kotlin 2.0+ · Gradle Kotlin DSL | Android Gradle Plugin 8.9+, R8 Full Mode |
| **UI Framework** | Jetpack Compose (100% Declarative) | Material Design 3 (M3), Dynamic Theming |
| **Local Database** | Room SQLite Persistence | Version 1, KSP Code Generation, Flow streams |
| **Widgets** | Android AppWidget / Glance Framework | 4 distinct home-screen widget receivers |
| **AI Integration** | Firebase AI / Google Gemini API | Server-side API key injection via `BuildConfig` |
| **Image Selection** | Android Photo Picker (`PickVisualMedia`) | Zero-permission privacy-safe avatar picker |
| **App Sizing & Security** | R8 Code/Resource Shrinking · Play Integrity | Release obfuscation enabled, debuggable disabled |

---

## 3. UI/UX Design Direction & Style Guide

### 3.1 Design Philosophy: The Spark Warm Flame Identity
Spark utilizes an energetic, warm flame aesthetic. The interface pairs clean, spacious off-white canvases with high-contrast charcoal typography and punchy amber-to-orange flame gradients that visually communicate energy and momentum.

### 3.2 Clear "Do" vs. "Avoid" Guidelines

#### ✅ DO (Design Standards to Follow)
1. **Warm Flame Accents:** Use vibrant orange and warm amber (`#FF6D00`, `#FF5722`, `#FF9100`) as primary action colors, streak indicators, and flame badges.
2. **Generous Breathing Room:** Maintain 16dp–24dp screen edge padding and consistent 8dp/12dp item spacing.
3. **High Contrast Typography:** Ensure primary text sits at `#1E130D` (deep warm charcoal) on off-white surfaces (`#FFFBF8` / `#FFFFFF`) for crisp legibility.
4. **Haptic & Visual Feedback:** Provide instant tactile and visual reinforcement upon habit completion (color fills, flame count increments, celebration dialogs).
5. **Card Elevation & Outlines:** Use soft border outlines (`#FFE5D8`) and 16dp–20dp rounded corners for clean, modern Material 3 cards.
6. **Dark Mode & Dynamic Appearance:** Respect system color appearance with automatic theme adaptation.

#### ❌ AVOID (Explicit Anti-Patterns)
1. **Harsh, Saturated Full-Screen Gradients:** Do NOT paint full screens with saturated rainbow or neon gradients.
2. **Cold, Clinical Color Palettes:** Do NOT use cold dark navy or hospital-blue palettes that contradict the warm flame identity.
3. **Low Contrast Text:** Do NOT use washed-out grey or pale pastel text against light backgrounds.
4. **Cluttered Data Tables:** Do NOT overwhelm users with spreadsheets or tiny unclickable rows; keep touch targets ≥ 48dp.
5. **Forced Modals & Popups:** Do NOT interrupt daily workflows with intrusive rating prompts, upsells, or sync banners.
6. **Generic Stock Placeholders:** Do NOT use generic placeholder icons where custom flame, trophy, or habit category icons belong.

### 3.3 Canonical Color Tokens (Production Aligned)
| Token Name | Hex Value | Semantic Usage in UI |
|---|---|---|
| `SparkFlame` | `#FF6D00` | Primary flame action color, streak counters, active tabs |
| `SparkOrange` | `#FF5722` | Primary button fill, floating action buttons, badge highlight |
| `SparkDeepOrange` | `#E64A19` | Flame gradient end, high-intensity streak emphasis |
| `SparkWarmAmber` | `#FF9100` | Flame gradient start, secondary accents |
| `SparkCanvas` | `#FFFBF8` | Primary screen background canvas (warm off-white) |
| `SparkSurface` | `#FFFFFF` | Card containers, bottom sheets, dialog backgrounds |
| `SparkCardBorder` | `#FFE5D8` | Subtle warm borders for habit and widget cards |
| `SparkTextPrimary` | `#1E130D` | Primary headings, titles, and high-emphasis labels |
| `SparkTextSecondary`| `#827067` | Subheadings, dates, completion statistics, category labels |
| `SparkTextMuted` | `#AAA09A` | Inactive days, placeholder text, disabled states |
| `SparkInactiveDot` | `#FFE2D4` | Uncompleted heatmap matrix dots |
| `SparkActiveDot` | `#FF5A1E` | Completed heatmap matrix dots |
| `SparkTrophyGold` | `#FFB300` | Milestone trophy wreath and star icons |
| `SparkSuccessGreen`| `#10B981` | Completed checkmarks, streak streak-safe indicators |

### 3.4 Typography Specification
- **Android Primary System Typeface:** **Roboto** (Native Android System Default).
- **Alternative Branded Font:** **Inter** / **Plus Jakarta Sans** for modern geometric headings.
- **Platform Error Resolution:** Any previous reference to Apple's *San Francisco (SF)* is formally rescinded as invalid for Android.

| Text Style | Weight | Size | Line Height | Tracking |
|---|---|---|---|---|
| **Display Large (Hero Flame)** | Bold (700) | 36sp | 44sp | -0.25sp |
| **Headline Medium (Screen Titles)** | SemiBold (600) | 22sp | 28sp | 0sp |
| **Title Medium (Habit Names)** | SemiBold (600) | 18sp | 24sp | 0.15sp |
| **Body Large (Descriptions)** | Normal (400) | 16sp | 24sp | 0.5sp |
| **Label Large (Buttons & Badges)** | Medium (500) | 14sp | 20sp | 0.1sp |
| **Label Small (Heatmap Days)** | Medium (500) | 11sp | 16sp | 0.5sp |

---

## 4. Comprehensive Data Model

### 4.1 Entity: `Habit`
Local Room SQLite table: `habits`

| Field | Type | Constraint | Description |
|---|---|---|---|
| `id` | `Long` | Primary Key, Auto-Gen | Unique identifier for each habit |
| `title` | `String` | Not Null | User-defined habit title (e.g., "Morning Run") |
| `description` | `String` | Default `""` | Optional motivation note or habit cue |
| `category` | `String` | Default `"General"` | Categorization (Health, Mind, Fitness, Work, Custom) |
| `targetDays` | `Int` | Default `60` | Target milestone goal in days |
| `startDateMillis` | `Long` | Default `now()` | Timestamp when habit was created |
| `endDateMillis` | `Long?` | Nullable | Optional target completion deadline |
| `reminderFrequency` | `String` | Default `"Daily"` | Notification frequency (Daily, Weekdays, Custom) |
| `reminderTime` | `String` | Default `"10:00 AM"`| Scheduled daily reminder time |
| `timeSpentMinutes` | `Int` | Default `0` | Cumulative tracked duration |
| `colorHex` | `String` | Default `"#FF5722"`| Accent color assigned to the habit |
| `createdAtMillis` | `Long` | Default `now()` | Epoch timestamp for sorting |

### 4.2 Entity: `HabitCompletion`
Local Room SQLite table: `habit_completions`  
*Unique Index on `(habitId, dateEpochDay)` prevents duplicate completions on the same calendar day.*

| Field | Type | Constraint | Description |
|---|---|---|---|
| `id` | `Long` | Primary Key, Auto-Gen | Unique record identifier |
| `habitId` | `Long` | Foreign Key (`habits.id` CASCADE) | Reference to parent habit |
| `dateEpochDay` | `Long` | Not Null | `LocalDate.toEpochDay()` for timezone-safe calendar math |
| `timestampMillis` | `Long` | Default `now()` | Precise millisecond when completion was logged |

### 4.3 Entity: `Trophy`
Local Room SQLite table: `trophies`

| Field | Type | Constraint | Description |
|---|---|---|---|
| `id` | `String` | Primary Key | Semantic identifier (e.g., `"spark_starter"`, `"fire_master"`) |
| `title` | `String` | Not Null | Achievement headline (e.g., "Flame Starter") |
| `subtitle` | `String` | Not Null | Short achievement tagline |
| `description` | `String` | Not Null | Criteria description (e.g., "Maintain a 7-day streak") |
| `requiredDays` | `Int` | Not Null | Threshold days required to unlock (3, 7, 14, 30, 60, 100) |
| `habitTitle` | `String?` | Nullable | Associated habit name (if habit-specific) |
| `isUnlocked` | `Boolean` | Default `false` | True when unlocked |
| `unlockedAtMillis`| `Long?` | Nullable | Timestamp of achievement unlock |
| `iconType` | `String` | Default `"wreath_star"` | Visual icon variant (`"wreath_star"`, `"medal"`, `"cup"`, `"flame"`) |

### 4.4 Aggregate Model: `HabitWithStats`
Calculated in-memory via repository reactive flows:
```kotlin
data class HabitWithStats(
    val habit: Habit,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCompletedDays: Int,
    val isCompletedToday: Boolean,
    val recentCompletions: Set<Long> // Set of dateEpochDay for heatmap rendering
)
```

---

## 5. Functional Requirements & Feature Flows

### 5.1 P0: Core Must-Have Features

#### FR-01: Home Screen Dashboard & Checklist
- Displays personalized greeting based on current time of day ("Good Morning / Afternoon / Evening").
- Shows interactive weekly date selector with real-time day navigation.
- Lists active habits rendered as interactive `HabitCard` components displaying:
  - Habit title, category badge, and target duration.
  - Current streak flame count (`SparkFlameIcon` + integer count).
  - 7-day dot-matrix completion progress row.
  - Interactive circular check-off button with animated fill state.
- Floating Action Button (FAB) or Header action to open Create Habit sheet.

#### FR-02: Instant Check-off & Celebration Engine
- Single-tap on habit check button toggles completion state for the selected day.
- Toggling automatically recalibrates:
  - `currentStreak`: Consecutive consecutive days counting backward from today (or yesterday if today is not yet completed).
  - `longestStreak`: All-time maximum consecutive streak.
  - `totalCompletedDays`: Total count of recorded completion records.
- When all habits for the day are checked off, triggers the `CompletionCelebrationDialog` displaying flame particle animations, streak summary, and encouraging copy.
- Triggers instant background update to all active home-screen widgets via `WidgetUpdater`.

#### FR-03: Create & Edit Habit Flow
- Sliding bottom sheet modal (`CreateHabitSheet`).
- Form inputs:
  - Title (required, validated).
  - Category selector (Health, Fitness, Mind, Work, Learning, Custom).
  - Target days goal picker (30, 60, 90, or custom days).
  - Daily reminder frequency and time picker.
- Immediate save to Room database with automatic UI recomposition.

#### FR-04: Habit Detail Screen
- Deep-dive into individual habit performance:
  - Current streak vs. Best streak comparison card.
  - Full-year interactive dot-matrix heatmap.
  - History log with ability to retroactively toggle missed days.
  - Edit habit parameters or delete habit with confirmation prompt.

---

### 5.2 P1: Engagement & Retention Features

#### FR-05: Trophies & Milestones Gallery
- Gallery displaying earned and in-progress achievement badges:
  - **Spark Starter:** Complete your first habit (Day 1).
  - **Flame Ignited:** 3-day consecutive streak.
  - **Week of Fire:** 7-day consecutive streak.
  - **Two-Week Blaze:** 14-day consecutive streak.
  - **Habit Solidified:** 30-day streak (habit automation milestone).
  - **Diamond Flame:** 60-day complete transformation.
  - **Century Spark:** 100-day legendary streak.
- Tapping an unlocked trophy opens the `ShareTrophyDialog` with a custom branded card preview and one-tap Android system share intent to Instagram, WhatsApp, X, or Messages.

#### FR-06: 4 Android Home-Screen Widgets
Spark includes 4 distinct native AppWidget providers registered in `AndroidManifest.xml`:
1. **Streaks Widget (`StreaksWidgetProvider`):** Compact widget displaying top 3 habits with active flame counts and quick-launch links.
2. **Tasks Widget (`TasksWidgetProvider`):** Actionable daily habit list displaying today's pending check-offs.
3. **Spark Flame Widget (`SparkFlameWidgetProvider`):** Signature 2x2 motivational widget showing active flame mascot, cumulative streak score, and daily motivational spark quote.
4. **Heatmap Widget (`HeatmapWidgetProvider`):** Visual 4x2 matrix widget displaying the user's monthly habit consistency dots right on the home screen.
- A dedicated in-app preview screen (`WidgetOptionsScreen`) allows users to preview each widget layout with instructions for pinning to the home screen.

#### FR-07: AI Habit Coach (Powered by Gemini)
- Integrated AI Coach provides:
  - Smart habit breakdown: Breaks broad goals ("get fit") into tiny, actionable sparks ("do 10 pushups after morning coffee").
  - Personalized streak recovery: Contextual encouragement when a streak is at risk or recently broken.
  - Weekly spark insights: Summarizes completion patterns and optimal execution times.

#### FR-08: User Profile & Preferences
- Profile management with zero-permission Android Photo Picker (`ActivityResultContracts.PickVisualMedia`).
- Notification preferences toggle and daily digest time selector.
- Google Account connection status with sign-in / sign-out support.

---

## 6. Resolution of Stale PRD Open Questions

| Item | Previous Open Question | Official PRD Decision & Status |
|---|---|---|
| **OQ-1: Offline vs. Connected** | Is Spark an offline app or server-reliant? | **Closed – Offline-First with Cloud Extensions:** Core habit loop, streaks, and database are 100% offline via Room SQLite. Network is only invoked for optional Gemini AI coaching and Google Account profile sync. |
| **OQ-2: Push Notifications** | Does Spark require Firebase Cloud Messaging (FCM)? | **Closed – Native Local Reminders:** Habit alerts run on-device using Android `AlarmManager` / `WorkManager`. FCM is NOT required for local notifications, eliminating backend server maintenance. |
| **OQ-3: Monetization & Paywalls** | Will Spark have a free vs. paid subscription model? | **Closed – 100% Free Core (v1.0):** No paywalls, ads, or Play Billing in v1.0. All 4 widgets, unlimited habits, trophies, and offline storage are free. In-App Billing (Play Billing) is reserved for post-launch v2.0 (optional cloud backup & premium AI tiers). |
| **OQ-4: Authentication** | Is Google Sign-In in scope? | **Closed – Implemented as Optional:** Google Identity Services / Credential Manager is integrated for optional single-tap profile sync. Users are never blocked by mandatory login walls. |
| **OQ-5: Home Screen Widgets** | Are home-screen widgets in scope? | **Closed – Formally Specified in Scope:** All 4 widgets (Streaks, Tasks, SparkFlame, Heatmap) are core deliverables with active providers and layout previews. |

---

## 7. Quality, Performance & Release Readiness

### 7.1 Security & Release Hardening
- **Debuggable Flag:** Strictly disabled (`isDebuggable = false`) in the release build configuration.
- **R8 Minification & Resource Shrinking:** `isMinifyEnabled = true` and `isShrinkResources = true` enabled with `proguard-android-optimize.txt` to strip unneeded bytecode and shrink APK size to ~22MB.
- **Compose Tooling Isolation:** `androidx.compose.ui:ui-tooling` and `ui-test-manifest` isolated exclusively under `debugImplementation`, ensuring zero preview activities leak into production manifests.
- **App Check / Play Integrity:** Production environment enforces Google Play Integrity validation while isolating debug providers to debug builds.
- **Single HTTP Stack:** Redundant Retrofit / Moshi / OkHttp libraries removed in favor of the optimized native Firebase AI SDK stack.

### 7.2 Verification & Acceptance Criteria
1. `gradle assembleDebug` and `compile_applet` must build green with zero compiler warnings or missing symbol errors.
2. Local Robolectric JVM tests (`gradle testDebugUnitTest`) must execute with 100% passing results.
3. Adding a habit, checking it off across days, and viewing the heatmap must update reactively within ≤ 16ms frame budgets (60fps UI).
4. All 4 home-screen widgets must update their remote views immediately upon any habit completion toggle.
