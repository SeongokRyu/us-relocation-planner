# ADR-003: Hilt for Dependency Injection

## Status
Accepted

## Date
2026-03-08

## Context
의존성 주입(DI) 프레임워크를 결정해야 한다.

후보:
- Manual DI — 소규모에서는 가능하나 확장성 부족
- Koin — 런타임 DI, 컴파일 타임 검증 없음
- **Hilt** — Dagger 기반, Google 공식, 컴파일 타임 검증
- Dagger (순수) — 설정이 복잡, Hilt가 래핑

## Decision
**Hilt**를 채택한다.

## Consequences

**장점:**
- `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel` — 최소 설정
- 컴파일 타임에 의존성 그래프 검증 → 런타임 크래시 방지
- `hilt-navigation-compose`로 ViewModel 자동 주입
- Google Android 공식 권장 DI
- 에이전트가 따르기 쉬운 단순한 패턴

**단점:**
- KSP/KAPT 빌드 시간 증가
- Dagger 개념 학습 필요 (Module, Component, Scope)

**사용 패턴:**
```kotlin
// Application
@HiltAndroidApp
class RelocationApp : Application()

// Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity()

// Module
@Module @InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDatabase(...): AppDatabase = ...
}

// ViewModel
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TaskRepository,
) : ViewModel()

// Composable
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel())
```
