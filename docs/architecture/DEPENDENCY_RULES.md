# Dependency Rules

> 이 문서의 규칙은 기계적으로 강제됩니다 (detekt custom rule + CI).
> 위반 시 빌드가 실패합니다.

## Layer Dependency Direction

```
domain/  ←──  data/  ←──  ui/
                ↑
               di/ (wiring only)
```

의존성은 항상 **안쪽(domain) 방향**으로만 흐른다.

## Rules by Package

### `domain/model/`
**순수 Kotlin. 외부 의존성 완전 금지.**

| 허용 | 금지 |
|------|------|
| Kotlin stdlib | `android.*` |
| | `androidx.*` |
| | `javax.inject.*` |
| | `dagger.*` |
| | `com.google.gson.*` |
| | 같은 프로젝트의 다른 패키지 |

```kotlin
// OK
data class Task(val id: Long, val title: String, ...)
enum class Category(val label: String) { ... }

// FORBIDDEN
import androidx.room.Entity  // Room은 data layer
import javax.inject.Inject   // Hilt는 di layer
```

### `data/local/entity/`
Room Entity. domain 모델로의 매핑 함수를 포함.

| 허용 | 금지 |
|------|------|
| `domain.model.*` | `ui.*` |
| `androidx.room.*` | `di.*` |

### `data/local/dao/`
Room DAO interface.

| 허용 | 금지 |
|------|------|
| `data.local.entity.*` | `domain.*` (직접 참조) |
| `androidx.room.*` | `ui.*` |
| `kotlinx.coroutines.flow.*` | `di.*` |

### `data/repository/`
Repository. DAO와 Domain 모델 사이의 브릿지.

| 허용 | 금지 |
|------|------|
| `domain.model.*` | `ui.*` |
| `data.local.dao.*` | `di.*` |
| `data.local.entity.*` | |
| `javax.inject.*` | |
| `kotlinx.coroutines.*` | |

### `di/`
Hilt Module. 모든 레이어 참조 가능 (wiring 전용).

| 허용 | 금지 |
|------|------|
| 모든 패키지 | 없음 (wiring이므로) |

### `ui/screens/`
Composable + ViewModel. ViewModel을 통해서만 데이터 접근.

| 허용 | 금지 |
|------|------|
| `domain.model.*` | `data.local.*` (직접 참조) |
| `data.repository.*` (ViewModel만) | `di.*` |
| `androidx.compose.*` | |
| `androidx.hilt.*` | |
| `androidx.lifecycle.*` | |

**중요**: Screen(Composable)에서 Repository를 직접 호출하면 안 된다. 반드시 ViewModel 경유.

### `ui/navigation/`

| 허용 | 금지 |
|------|------|
| `ui.screens.*` | `data.*` |
| `domain.model.Category` (route용) | `di.*` |
| `androidx.navigation.*` | |

### `ui/theme/`
독립적. 다른 프로젝트 패키지에 의존하지 않음.

| 허용 | 금지 |
|------|------|
| `androidx.compose.material3.*` | 프로젝트 내 다른 패키지 |
| `androidx.compose.ui.*` | |

## Enforcement

1. **CI 검증**: `./gradlew detekt` — 위반 시 빌드 실패
2. **PR 체크리스트**: import 방향 수동 확인
3. **CLAUDE.md 참조**: 에이전트가 코드 생성 시 이 규칙을 따르도록 지시

## 규칙 추가/변경 절차

1. 이 문서에 변경사항 기술
2. ADR 작성 (docs/architecture/ADR/)
3. detekt rule 업데이트
4. CI 검증 통과 확인
