# Bee Swarm Macro - Android

A mobile macro automation tool for Bee Swarm Simulator on Roblox. Uses Android Accessibility Services to automate farming, questing, mob defeating, and honey conversion.

## Features

- **Auto Farm** - Collects pollen using 10 gesture patterns (Circle, Spiral, Figure-8, Zigzag, Grid, Star, Diamond, Random Walk, Clover, Lawnmower)
- **Auto Quest** - Visits NPCs and accepts/completes quests automatically
- **Mob Defeat** - Detects and defeats mobs with combat patterns
- **Auto Convert** - Converts pollen to honey at the hive periodically
- **Floating Overlay** - Draggable control panel while playing
- **Real-time Stats** - Track pollen, quests, mobs, and gestures
- **Anti-Detection** - Randomized delays and gesture jitter
- **Material Design UI** - Bee-themed dark/light mode

## Requirements

- Android 7.0+ (API 24)
- Roblox app installed
- Accessibility Service permission
- Display over other apps permission

## Build

Open in Android Studio and build, or use Gradle:

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Architecture

```
com.beeswarm.macro/
├── config/       - MacroConfig (SharedPreferences)
├── macro/        - AutoFarm, AutoQuest, MobDefeat, AutoConvert, Orchestrator
├── service/      - AccessibilityService, OverlayService
├── ui/           - MainActivity, SettingsActivity
└── utils/        - GestureEngine, StatsTracker, Logger
```

## Tech Stack

- Kotlin + Coroutines
- Android Accessibility Service API
- Material Components
- SharedPreferences for config persistence
