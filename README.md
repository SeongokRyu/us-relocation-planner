# US Relocation Planner

미국 이주를 준비하는 사용자를 위한 체크리스트/플래너 Android 앱.

카테고리별로 할 일을 관리하고, 대시보드에서 전체 진행률을 확인할 수 있습니다.

## Tech Stack

| 구성 요소 | 기술 |
|----------|------|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Database | Room (SQLite) |
| DI | Hilt |
| Architecture | Clean Architecture + MVVM |
| Navigation | Navigation Compose |
| Async | Coroutines + Flow |

## Quick Start

1. Android Studio에서 프로젝트 열기
2. Gradle sync 완료 대기
3. Run (에뮬레이터 또는 실제 기기)

```bash
# CLI 빌드
./gradlew assembleDebug
```

## Architecture

```
┌─────────────────────────────────────┐
│  UI Layer (Compose + ViewModel)     │
├─────────────────────────────────────┤
│  Domain Layer (Models)              │
├─────────────────────────────────────┤
│  Data Layer (Repository + Room DB)  │
└─────────────────────────────────────┘
```

## Project Structure

```
app/src/main/java/com/seongokryu/relocationplanner/
├── MainActivity.kt
├── RelocationApp.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/TaskDao.kt
│   │   └── entity/TaskEntity.kt
│   └── repository/TaskRepository.kt
├── di/AppModule.kt
├── domain/model/Task.kt
└── ui/
    ├── navigation/NavGraph.kt
    ├── theme/
    └── screens/
        ├── dashboard/
        └── checklist/
```

## Features

- **대시보드** — 전체/카테고리별 진행률
- **체크리스트** — 5개 카테고리 (비자, 주거, 재정, 커리어, 이사)
- **CRUD** — 항목 추가/수정/삭제/완료 토글
- **기본 데이터** — 첫 실행 시 27개 기본 항목 자동 생성

## License

MIT
