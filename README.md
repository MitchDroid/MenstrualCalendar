# BloomCycle

**Privacy-first menstrual health tracking for Android.**

All data stays on your device. No accounts, no analytics, no internet required.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Build & Run](#build--run)
- [Testing](#testing)
- [Localization](#localization)
- [Privacy & Security](#privacy--security)
- [Roadmap](#roadmap)
- [License](#license)

---

## Overview

BloomCycle is a modern Android app designed to help users track their menstrual cycle, log daily symptoms, predict upcoming periods and fertile windows, and gain personalized health insights — all while keeping data 100% local on the device.

Built with Jetpack Compose, Material Design 3, and Clean Architecture principles, BloomCycle prioritizes user privacy above all else. There are no accounts to create, no servers to sync with, and no analytics tracking your behavior.

---

## Features

### Cycle Tracking & Predictions
- Automatic period prediction based on logged flow data
- Fertile window and ovulation day estimation
- Cycle phase detection (Menstrual, Follicular, Ovulation, Luteal)
- Fertility status indicator (Low, Medium, High, Peak)
- Cycle statistics: averages, shortest/longest, regularity assessment

### Daily Logging
- Flow intensity (None, Spotting, Light, Medium, Heavy)
- Mood tracking (Happy, Sad, Anxious, Irritable, Calm, Energetic)
- 10 symptom types (Cramps, Headache, Bloating, Acne, Mood Swings, Fatigue, Tender Breasts, Back Pain, Nausea, Food Cravings)
- Cervical mucus observations (Dry, Sticky, Creamy, Watery, Egg White)
- Sexual activity logging (Protected, Unprotected, Solo)
- Temperature and weight vitals
- Free-text notes

### Calendar View
- Month-at-a-glance with color-coded markers
- Period, predicted period, fertile window, and ovulation indicators
- Quick-access daily log from any selected date
- Month navigation with "Go to Today" shortcut

### Insights & Analytics
- Upcoming predictions (next period, fertile window, ovulation)
- Cycle statistics dashboard (averages, variation, consistency)
- Top symptom frequency analysis
- Mood trend distribution
- Flow pattern breakdown
- Goal-specific insights (Trying to Conceive, Avoid Pregnancy, Track Cycle, Monitor Health)

### Education Hub
- **Cycle Phase Guide** — detailed information for all 4 phases with symptoms, nutrition, exercise, and self-care tips
- **Symptom Guide** — 10 symptom profiles explaining causes, management strategies, and when to see a doctor
- **Health Tips** — 19 phase-aware wellness tips across 5 categories (Nutrition, Exercise, Self-Care, Sleep, Mental Health)

### Reports & Data Export
- Comprehensive health report with cycle history, statistics, and trends
- CSV export (spreadsheet-compatible daily log data)
- Formatted text report export (.txt)
- Quick share to any app

### Notifications
- Period reminders (configurable: 1, 2, or 3 days before)
- Daily log reminders (9:00 AM daily)
- Fertile window alerts
- Powered by WorkManager for reliable background scheduling

### Privacy & Security
- Biometric lock (fingerprint/face authentication)
- Screen security (blocks screenshots and screen recordings)
- No internet permissions — works entirely offline
- No analytics, tracking, or telemetry of any kind
- Complete data deletion from Settings
- Uninstall = complete deletion with zero residual data

### Multi-Language Support
- English (default)
- Spanish (complete translation of 800+ strings)

---

## Screenshots

#### Screenshots of the change <!-- (Optional) -->
<!-- *For changes that affect UI, add screenshots helping reviewers understand the change visually.* -->

| screen | screen | screen | screen |screen |
| ------------- | ------------- |------------- |------------- | ------------- |
| <!-- Drop screenshot here --> | <!-- Drop screenshot here --> | <!-- Drop screenshot here --> | <!-- Drop screenshot here --> | <!-- Drop screenshot here --> |
| <img src="https://github.com/user-attachments/assets/eed88384-744a-4049-9776-e8f3560fb38e" width="300"> | <img src="https://github.com/user-attachments/assets/b40a7133-60d1-44ea-947d-b1bedfc54b56" width="300"> | <img src="https://github.com/user-attachments/assets/ec0fb267-cc45-45e9-9945-d18010c5ef7c" width="300">  | <img src="https://github.com/user-attachments/assets/2ab946ec-e535-4733-8ecc-d3462a726d96" width="300"> | <img src="https://github.com/user-attachments/assets/39208f7a-8ff0-4222-bd88-47ba547fd88a" width="300"> | <img width="300" src="https://github.com/user-attachments/assets/fe92e3f4-fe6f-4460-b2f0-46b2bb533835" />


---

## Architecture

BloomCycle follows **Clean Architecture** with the **MVVM** (Model-View-ViewModel) pattern, organized into three distinct layers:

```
┌─────────────────────────────────────────┐
│                UI Layer                 │
│  Compose Screens + ViewModels + Theme   │
├─────────────────────────────────────────┤
│              Domain Layer               │
│  Use Cases + Models + Repository Ifaces │
├─────────────────────────────────────────┤
│               Data Layer                │
│  Room DB + DAOs + DataStore + Workers   │
└─────────────────────────────────────────┘
```

### UI Layer
- **Jetpack Compose** screens with Material Design 3 theming
- **ViewModels** expose `StateFlow<UiState>` consumed by composables
- State management via `stateIn(SharingStarted.WhileSubscribed(5_000))`
- Navigation handled by `NavHost` with 11 screen destinations

### Domain Layer
- **Models**: Pure Kotlin data classes and enums (no Android dependencies)
- **Repository interfaces**: Abstractions for data access
- **Use cases**: `CyclePredictionEngine` (prediction/analytics) and `ReportGenerator` (export/reports)

### Data Layer
- **Room database** with 2 entities (`daily_logs`, `cycles`), type converters, and reactive `Flow` queries
- **DataStore Preferences** for user settings (cycle length, period duration, notification toggles, etc.)
- **Repository implementations** mapping between entities and domain models
- **WorkManager** for reliable background notification scheduling
- **BiometricAuthManager** for secure app lock

### Dependency Injection
All dependencies are provided via **Hilt** with 3 modules:
- `AppModule` — DataStore, UserPreferencesManager
- `DatabaseModule` — Room database, DAOs
- `RepositoryModule` — Repository interface bindings

---

## Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Language** | Kotlin | 2.1.21 |
| **UI** | Jetpack Compose + Material 3 | BOM 2024.12.01 |
| **Navigation** | Navigation Compose | 2.8.5 |
| **DI** | Hilt (with KSP) | 2.56.2 |
| **Database** | Room | 2.7.1 |
| **Preferences** | DataStore | 1.1.1 |
| **Async** | Kotlin Coroutines + Flow | 1.9.0 |
| **Background** | WorkManager | 2.10.0 |
| **Security** | Biometric API | 1.1.0 |
| **Build** | Gradle (Kotlin DSL) | 8.13 |
| **AGP** | Android Gradle Plugin | 8.11.2 |
| **Symbol Processing** | KSP | 2.1.21-2.0.2 |
| **Java Compat** | Core Library Desugaring | 2.1.4 |
| **Testing** | JUnit 4 + MockK + Coroutines Test + Turbine | — |

### Build Targets

| Property | Value |
|----------|-------|
| `compileSdk` | 35 |
| `minSdk` | 24 (Android 7.0) |
| `targetSdk` | 36 |
| `Java target` | JVM 17 |

---

## Project Structure

```
app/src/main/java/com/bloomcycle/app/
├── BloomCycleApplication.kt          # @HiltAndroidApp, WorkManager setup
├── MainActivity.kt                    # Compose entry, biometric & screen security
│
├── data/
│   ├── export/
│   │   └── DataExporter.kt           # CSV/text file creation via FileProvider
│   ├── local/
│   │   ├── BloomCycleDatabase.kt     # Room database (v1)
│   │   ├── converter/Converters.kt   # LocalDate & enum type converters
│   │   ├── dao/
│   │   │   ├── CycleDao.kt           # Cycle CRUD with Flow queries
│   │   │   └── DailyLogDao.kt        # Daily log CRUD with upsert
│   │   └── entity/
│   │       ├── CycleEntity.kt        # Room entity: cycles table
│   │       └── DailyLogEntity.kt     # Room entity: daily_logs table
│   ├── notification/
│   │   ├── NotificationHelper.kt     # 3 notification channels
│   │   ├── NotificationScheduler.kt  # WorkManager scheduling
│   │   └── ReminderWorker.kt         # Daily background check worker
│   ├── preferences/
│   │   └── UserPreferencesManager.kt # DataStore-backed preferences
│   ├── repository/
│   │   ├── CycleRepositoryImpl.kt    # CycleRepository implementation
│   │   └── DailyLogRepositoryImpl.kt # DailyLogRepository implementation
│   └── security/
│       └── BiometricAuthManager.kt   # Fingerprint/face auth wrapper
│
├── di/
│   ├── AppModule.kt                  # DataStore & preferences DI
│   ├── DatabaseModule.kt             # Room database & DAO DI
│   └── RepositoryModule.kt           # Repository binding DI
│
├── domain/
│   ├── model/
│   │   ├── Cycle.kt                  # Domain cycle model
│   │   ├── DailyLog.kt              # Domain daily log model
│   │   └── Enums.kt                  # FlowIntensity, Mood, Symptom, etc.
│   ├── repository/
│   │   ├── CycleRepository.kt        # Interface
│   │   └── DailyLogRepository.kt     # Interface
│   └── usecase/
│       ├── CyclePredictionEngine.kt  # Prediction, stats, analytics
│       └── ReportGenerator.kt        # CSV, text reports, summaries
│
└── ui/
    ├── calendar/                      # Calendar screen + grid
    ├── components/                    # Shared: bottom navigation bar
    ├── education/                     # Phase guide, symptom guide, health tips
    ├── home/                          # Home dashboard
    ├── insights/                      # Analytics & predictions
    ├── navigation/                    # NavHost + Screen sealed class
    ├── onboarding/                    # 5-step onboarding flow
    ├── privacy/                       # App lock + privacy policy screens
    ├── reports/                       # Reports & data export
    ├── settings/                      # Settings + notifications config
    ├── theme/                         # Color, Shape, Theme, Typography
    └── tracking/                      # Daily symptom/mood/flow logging
```

### Resources

```
app/src/main/res/
├── drawable/                          # Launcher icons
├── mipmap-*/                          # Adaptive icons (all densities)
├── values/
│   ├── strings.xml                    # English strings (800+ entries)
│   ├── colors.xml                     # Color definitions
│   └── themes.xml                     # App theme
├── values-es/
│   └── strings.xml                    # Spanish translations (complete)
└── xml/
    ├── backup_rules.xml               # Android backup config
    ├── data_extraction_rules.xml      # Data extraction config
    └── file_paths.xml                 # FileProvider paths for sharing
```

---

## Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2.1) or later
- **JDK 17** or later
- **Android SDK** with API 35 installed
- A physical device or emulator running **Android 7.0 (API 24)** or higher

### Clone

```bash
git clone https://github.com/your-username/BloomCycle.git
cd BloomCycle
```

### Open in Android Studio

1. Open Android Studio
2. Select **File > Open** and navigate to the project directory
3. Wait for Gradle sync to complete
4. Select a device/emulator and click **Run**

---

## Build & Run

### Debug build

```bash
./gradlew assembleDebug
```

### Release build

```bash
./gradlew assembleRelease
```

### Install on connected device

```bash
./gradlew installDebug
```

---

## Testing

BloomCycle has **135 unit tests** across 14 test classes, covering all three architecture layers.

### Run all tests

```bash
./gradlew testDebugUnitTest
```

### Test coverage by layer

| Layer | Class | Tests | What's Covered |
|-------|-------|-------|----------------|
| **Domain** | `CyclePredictionEngineTest` | 31 | Phase detection, fertility status, cycle wrapping, date calculations, stats computation, symptom/mood/flow analysis |
| **Domain** | `ReportGeneratorTest` | 10 | Summary reports, CSV export (headers, data, nulls, escaping, sorting), text report localization |
| **UI** | `EducationContentProviderTest` | 19 | Phase guides, symptom guides, health tips, filtering, lookups |
| **UI** | `HomeViewModelTest` | 3 | Initial state, data loading |
| **UI** | `CalendarViewModelTest` | 5 | Date selection, month navigation, go-to-today |
| **UI** | `InsightsViewModelTest` | 4 | Initial state, data loading, goal insights |
| **UI** | `SettingsViewModelTest` | 7 | Toggle preferences, cycle settings, data deletion |
| **UI** | `DailyTrackingViewModelTest` | 10 | Flow/mood/symptom selection, vitals input, save/delete |
| **UI** | `OnboardingViewModelTest` | 10 | Step navigation, field updates, completion flow |
| **UI** | `ReportsViewModelTest` | 5 | Data loading, CSV/text export, event consumption |
| **Data** | `DailyLogRepositoryImplTest` | 9 | CRUD operations, entity-domain mapping |
| **Data** | `CycleRepositoryImplTest` | 5 | CRUD operations, query delegation |
| **Data** | `UserPreferencesManagerTest` | 16 | All preference read/write operations with real DataStore |

### Testing libraries

- **JUnit 4** — test framework
- **MockK** — Kotlin-native mocking (relaxed mocks, coroutine verification)
- **kotlinx-coroutines-test** — `StandardTestDispatcher`, `UnconfinedTestDispatcher`, `runTest`
- **Turbine** — Flow testing utilities
- **AndroidX Arch Core Testing** — `InstantTaskExecutorRule` for LiveData

---

## Localization

BloomCycle supports full localization with complete translations for:

| Language | Status | Strings |
|----------|--------|---------|
| English | Default | 800+ |
| Spanish | Complete | 800+ |

### String resource structure

All user-facing text is externalized into Android string resources:

- **UI labels and messages** — standard `<string>` resources
- **Education content** — Phase guides, symptom guides, health tips (including `<string-array>` resources for list items)
- **Report generator** — CSV headers, text report sections, and formatted entries with `%s`/`%d` placeholders
- **Notifications** — Channel names, titles, and body text

### Adding a new language

1. Create `app/src/main/res/values-{locale}/strings.xml`
2. Copy all `<string>` and `<string-array>` entries from `values/strings.xml`
3. Translate the values (keep the `name` attributes identical)
4. Build and test — Android automatically selects the correct locale at runtime

---

## Privacy & Security

BloomCycle is built with privacy as its core design principle.

### Zero Data Collection
- **No accounts** — no registration, email, or personal identity required
- **No analytics** — no usage tracking, crash reports, or telemetry
- **No internet** — the app requests zero network permissions
- **No third-party SDKs** — no tracking pixels or advertising identifiers

### Local-Only Storage
- All data stored in Room database within Android's sandboxed app directory
- User preferences stored in DataStore (app-private directory)
- No data is ever transmitted over any network

### Security Features
- **Biometric lock** — optional fingerprint/face authentication on every app open
- **Screen security** — optional `FLAG_SECURE` prevents screenshots and screen recordings
- **Data export** — users own their data and can export it anytime (CSV or text)
- **Full deletion** — one-tap permanent deletion of all data from Settings

### Permissions

| Permission | Purpose | Required |
|------------|---------|----------|
| `POST_NOTIFICATIONS` | Period reminders, daily log reminders, fertile window alerts | Optional (Android 13+ runtime) |
| `SCHEDULE_EXACT_ALARM` | Precise notification timing | Optional (graceful fallback) |

No other permissions are requested. No camera, storage, location, contacts, or internet access.

---

## Roadmap

- [ ] Instrumented UI tests (Compose testing)
- [ ] Dark theme support
- [ ] Widget for home screen cycle overview
- [ ] Data backup/restore via local file
- [ ] Additional language translations
- [ ] Cycle length trend graphs
- [ ] Period prediction accuracy improvements with ML
- [ ] Wear OS companion app

---

## License

This project is a personal, privacy-focused menstrual health tracking app. All rights reserved.

---

<p align="center">
  <b>BloomCycle</b> — Your cycle, your way.
</p>
