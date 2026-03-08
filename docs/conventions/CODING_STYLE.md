# Coding Style Guide

> ktlint + detekt로 기계적으로 강제. 위반 시 CI 빌드 실패.

## Language

- **UI 텍스트**: 한국어
- **코드** (변수명, 함수명, 클래스명, 주석): 영어
- **문서**: 한국어 (docs/ 내 모든 문서)

## Naming Conventions

### Packages
```
com.seongokryu.relocationplanner.<layer>.<sublayer>
```
모두 소문자, 언더스코어 없음.

### Classes & Interfaces
- PascalCase
- Suffix 규칙:

| 타입 | Suffix | 예시 |
|------|--------|------|
| Activity | `Activity` | `MainActivity` |
| Application | `App` | `RelocationApp` |
| ViewModel | `ViewModel` | `DashboardViewModel` |
| Composable Screen | `Screen` | `DashboardScreen` |
| Room Entity | `Entity` | `TaskEntity` |
| Room DAO | `Dao` | `TaskDao` |
| Room Database | `Database` | `AppDatabase` |
| Repository | `Repository` | `TaskRepository` |
| Hilt Module | `Module` | `AppModule` |

### Functions
- camelCase
- 동사로 시작: `getTasksByCategory()`, `toggleTask()`, `addNote()`
- Composable 함수: PascalCase (Kotlin convention): `DashboardScreen()`, `TaskCard()`

### Variables & Properties
- camelCase
- Boolean: `is` / `has` prefix: `isDone`, `hasNotes`
- StateFlow: 명사형: `tasks`, `categoryStats`
- UI event 함수: `on` prefix parameter: `onToggle`, `onDelete`

### Constants
- SCREAMING_SNAKE_CASE (companion object 내)
- 또는 top-level `val` (변환 불필요 시)

## File Organization

### Kotlin File Structure (순서)
1. Package declaration
2. Import statements (알파벳 순, wildcard 금지)
3. Top-level declarations

### Single Class per File
- 하나의 파일에 하나의 public class/interface
- 예외: 밀접하게 관련된 소규모 data class (예: `CategoryStat` in `TaskDao.kt`)

### File Size
- **최대 200줄** 권장
- 300줄 초과 시 분리 검토

## Compose Conventions

### Composable 파라미터 순서
```kotlin
@Composable
fun TaskCard(
    task: Task,                    // 1. Required data
    modifier: Modifier = Modifier, // 2. Modifier (always optional with default)
    onToggle: () -> Unit,          // 3. Event callbacks
    onDelete: () -> Unit,
)
```

### Preview
- Screen 단위 Composable에 `@Preview` 어노테이션 추가
- Preview 함수명: `Preview<ScreenName>`

### State Hoisting
- Composable은 stateless로 유지
- 상태는 ViewModel에서 관리, Composable은 받아서 표시만

## Formatting

- ktlint가 자동 적용
- Indent: 4 spaces (탭 금지)
- Max line length: 120
- Trailing comma: 사용
- 빈 줄: 함수 사이 1줄

## Error Handling

- `try-catch`는 Repository layer에서만 사용
- ViewModel은 sealed class/interface로 UI 상태 표현
- Composable에서 직접 예외 처리 금지

## Comments

- 자명한 코드에는 주석 금지
- 복잡한 비즈니스 로직에만 `//` 주석
- KDoc은 public API에만 (라이브러리 코드가 아니므로 최소화)
- TODO: `// TODO: <description>` 형식
