# US Relocation Planner — Agent Harness

> 이 파일은 AI 에이전트의 진입점. 상세 내용은 docs/ 링크를 따라간다.

## Quick Reference

| 영역 | 문서 |
|------|------|
| Architecture Overview | [docs/architecture/OVERVIEW.md](docs/architecture/OVERVIEW.md) |
| Dependency Rules | [docs/architecture/DEPENDENCY_RULES.md](docs/architecture/DEPENDENCY_RULES.md) |
| Architecture Decisions | [docs/architecture/ADR/](docs/architecture/ADR/) |
| Coding Style | [docs/conventions/CODING_STYLE.md](docs/conventions/CODING_STYLE.md) |
| Testing Strategy | [docs/conventions/TESTING.md](docs/conventions/TESTING.md) |
| PR Checklist | [docs/conventions/PR_CHECKLIST.md](docs/conventions/PR_CHECKLIST.md) |
| Feature Roadmap | [docs/roadmap/FEATURE_ROADMAP.md](docs/roadmap/FEATURE_ROADMAP.md) |
| Harness Plan | [docs/HARNESS_PLAN.md](docs/HARNESS_PLAN.md) |

## Tech Stack

- Kotlin 2.0 + Jetpack Compose (Material 3)
- Room (SQLite ORM) + Hilt (DI) + Navigation Compose
- Coroutines + Flow (async)
- Clean Architecture + MVVM

## Environment Setup

```bash
export JAVA_HOME=/home/seongokryu/tools/jdk-17.0.13+11
export ANDROID_HOME=/home/seongokryu/tools/android-sdk
export PATH="$JAVA_HOME/bin:$PATH"
```

또는 wrapper 사용: `/home/seongokryu/tools/setup_env.sh <command>`

## Build Commands

```bash
./gradlew assembleDebug      # 빌드
./gradlew test               # 유닛 테스트
./gradlew ktlintCheck        # 코드 스타일 검사
./gradlew detekt             # 정적 분석
./gradlew ktlintFormat       # 자동 포매팅
```

## Pre-Commit Verification (커밋 전 반드시 실행)

코드 변경 후 커밋 전에 아래 검증을 순서대로 통과해야 한다:

```bash
./gradlew ktlintFormat       # 1. 자동 포매팅 적용
./gradlew ktlintCheck        # 2. 코드 스타일 검증
./gradlew detekt             # 3. 정적 분석
./gradlew assembleDebug      # 4. 빌드 성공 확인
./gradlew test               # 5. 유닛 테스트 통과
```

모든 단계 통과 후에만 커밋한다. 실패 시 수정 후 재실행.

## Architecture Invariants (반드시 준수)

```
domain/  →  순수 Kotlin. Android/Room/Hilt 의존성 금지.
data/    →  domain만 import. ui 참조 금지.
ui/      →  ViewModel 경유로만 데이터 접근. data/local 직접 참조 금지.
di/      →  모든 레이어 참조 가능 (wiring 전용).
```

## File Patterns

새 화면 추가 시 반드시 아래 구조를 따른다:
```
ui/screens/<feature>/
├── <Feature>Screen.kt       # @Composable, stateless
└── <Feature>ViewModel.kt    # @HiltViewModel, StateFlow
```

Entity-Domain 매핑:
```kotlin
// Entity 내에 정의
fun toDomain(): Task = ...
companion object { fun fromDomain(task: Task): TaskEntity = ... }
```

State 수집:
```kotlin
val tasks by viewModel.tasks.collectAsStateWithLifecycle()
```

## Coding Rules Summary

- 한국어 UI, 영어 코드
- 파일 200줄 이내 (300줄 초과 시 분리)
- Wildcard import 금지
- 새 기능에는 반드시 테스트 동반
- 테스트 네이밍: `should_<expected>_when_<condition>()`
- Composable 파라미터 순서: data → modifier → callbacks

## Current Phase

Phase 3 — Dashboard & UX 구현 중.
Spec: [docs/specs/phase3-dashboard-ux.md](docs/specs/phase3-dashboard-ux.md)
