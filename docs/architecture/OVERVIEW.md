# Architecture Overview

## High-Level Diagram

```
┌──────────────────────────────────────────────────────┐
│                    UI Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐ │
│  │  Composable  │  │  Composable  │  │  Navigation  │ │
│  │   Screen     │  │   Screen     │  │   NavHost    │ │
│  └──────┬───────┘  └──────┬───────┘  └──────────────┘ │
│         │                 │                            │
│  ┌──────▼───────┐  ┌──────▼───────┐                   │
│  │  ViewModel   │  │  ViewModel   │  ← StateFlow      │
│  └──────┬───────┘  └──────┬───────┘                   │
├─────────┼─────────────────┼──────────────────────────┤
│         │   Domain Layer  │                           │
│         │  ┌──────────────┤                           │
│         │  │ Task, Category, Priority (data class)    │
│         │  └──────────────┘                           │
├─────────┼────────────────────────────────────────────┤
│         │     Data Layer                              │
│  ┌──────▼───────┐                                    │
│  │  Repository   │  ← Single source of truth         │
│  └──────┬───────┘                                    │
│         │                                            │
│  ┌──────▼───────┐  ┌──────────────┐                  │
│  │   Room DAO   │  │  TaskEntity   │  ← DB mapping   │
│  └──────┬───────┘  └──────────────┘                  │
│         │                                            │
│  ┌──────▼───────┐                                    │
│  │  AppDatabase  │  ← SQLite (Room)                  │
│  └──────────────┘                                    │
├──────────────────────────────────────────────────────┤
│                    DI Layer                           │
│  ┌──────────────┐                                    │
│  │  AppModule    │  ← Hilt: DB, DAO 제공             │
│  └──────────────┘                                    │
└──────────────────────────────────────────────────────┘
```

## Design Principles

### Single Activity
- `MainActivity`가 유일한 Activity
- 모든 화면 전환은 Navigation Compose로 처리
- `RelocationApp`은 Hilt `@HiltAndroidApp` Application class

### Unidirectional Data Flow (UDF)
```
User Action → ViewModel → Repository → DAO → DB
                  ↓
            StateFlow emission
                  ↓
            Composable recomposition
```

1. 사용자 액션이 ViewModel의 함수를 호출
2. ViewModel이 Repository를 통해 데이터 변경
3. Room이 Flow로 변경 사항을 emit
4. StateFlow가 UI를 자동 업데이트

### Separation of Concerns

| Layer | 역할 | 허용된 의존성 |
|-------|------|-------------|
| `domain/` | 순수 비즈니스 모델 | 없음 (순수 Kotlin) |
| `data/local/` | Room Entity, DAO, Database | domain |
| `data/repository/` | 데이터 접근 추상화 | domain, data/local |
| `di/` | Hilt 모듈 (wiring) | 모든 레이어 |
| `ui/screens/` | Composable + ViewModel | domain (ViewModel 경유) |
| `ui/navigation/` | 화면 라우팅 | ui/screens |
| `ui/theme/` | Material 3 테마 | 없음 |

자세한 의존성 규칙은 [DEPENDENCY_RULES.md](DEPENDENCY_RULES.md)를 참고.

## Key Patterns

### Screen + ViewModel Pair
각 화면은 반드시 아래 구조를 따른다:
```
screens/<feature>/
├── <Feature>Screen.kt      # @Composable, UI만 담당
└── <Feature>ViewModel.kt   # @HiltViewModel, 상태 + 로직
```

### Entity ↔ Domain Mapping
Room Entity와 Domain Model은 분리하고, Entity 내에서 변환:
```kotlin
// TaskEntity.kt
fun toDomain(): Task = Task(...)

companion object {
    fun fromDomain(task: Task): TaskEntity = TaskEntity(...)
}
```

### State Management
```kotlin
// ViewModel
val tasks: StateFlow<List<Task>> = repository.getAllTasks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

// Composable
val tasks by viewModel.tasks.collectAsStateWithLifecycle()
```

## Navigation Structure

Bottom Navigation Bar with 6 tabs:
```
[대시보드] [비자] [주거] [재정] [커리어] [이사]
```

- Dashboard: `dashboard` route
- Category screens: `checklist/{category}` route (Category enum name as argument)
