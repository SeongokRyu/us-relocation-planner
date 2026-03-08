# US Relocation Planner

## Project Overview
미국 이주를 준비하는 사용자를 위한 체크리스트/플래너 Android 앱.

## Tech Stack
- **Kotlin 2.0** + **Jetpack Compose** (선언적 UI)
- **Room** — SQLite ORM
- **Hilt** — Dependency Injection
- **Navigation Compose** — 화면 전환
- **Coroutines + Flow** — 비동기 처리

## Architecture
Clean Architecture + MVVM
```
domain/model/       → 순수 데이터 모델 (Task, Category, Priority)
data/local/         → Room Entity, DAO, Database
data/repository/    → Repository (data layer 추상화)
di/                 → Hilt DI module
ui/screens/         → Composable + ViewModel (per screen)
ui/navigation/      → NavHost + bottom navigation
ui/theme/           → Material 3 테마
```

## Project Structure
```
app/src/main/
├── assets/default_tasks.json       # 기본 체크리스트 (27개)
├── java/com/seongokryu/relocationplanner/
│   ├── MainActivity.kt             # Single Activity
│   ├── RelocationApp.kt            # Hilt Application
│   ├── data/                        # Data layer
│   ├── di/                          # DI modules
│   ├── domain/                      # Domain models
│   └── ui/                          # UI layer
└── res/                             # Android resources
```

## Build
- Android Studio에서 열기 → Gradle sync → Run
- `./gradlew assembleDebug` (CLI 빌드)

## Conventions
- 한국어 UI, 영어 코드
- 화면당 Screen + ViewModel 쌍
- Room Entity ↔ Domain Model 변환은 Entity 내 toDomain()/fromDomain()
- StateFlow + collectAsStateWithLifecycle 패턴
