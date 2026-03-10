# 렛츠고 USA 🇺🇸

미국 이주를 준비하는 사용자를 위한 체크리스트/플래너 Android 앱.

카테고리별로 할 일을 관리하고, 대시보드에서 전체 진행률을 확인하며,
비용 추적, 연락처 관리, 환율 계산 등 이주에 필요한 모든 것을 하나의 앱에서 관리할 수 있습니다.

> **[APK 다운로드](https://github.com/SeongokRyu/us-relocation-planner/releases/latest)** — GitHub Releases에서 최신 APK를 받을 수 있습니다.

> **Harness Engineering** 방식으로 개발합니다.
> Human의 개입을 최소화하고, AI 에이전트가 spec 기반으로 구현 → CI 검증 → 머지하는 사이클을 따릅니다.
> 자세한 내용은 [docs/HARNESS_PLAN.md](docs/HARNESS_PLAN.md)를 참고하세요.

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
| Settings | DataStore Preferences |
| Background | WorkManager |

## Quick Start

1. Android Studio에서 프로젝트 열기
2. Gradle sync 완료 대기
3. Run (에뮬레이터 또는 실제 기기)

```bash
# CLI 빌드
./gradlew assembleDebug

# 테스트
./gradlew test

# 코드 스타일 검사
./gradlew ktlintCheck

# 정적 분석
./gradlew detekt
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

**Dependency Rules:**
- `domain/` → 순수 Kotlin, 외부 의존성 없음
- `data/` → domain만 import
- `ui/` → ViewModel 경유, data/local 직접 참조 금지
- `di/` → 모든 레이어 참조 가능 (wiring 전용)

## Project Structure

```
app/src/main/java/com/seongokryu/relocationplanner/
├── MainActivity.kt
├── RelocationApp.kt
├── data/
│   ├── export/              # PDF 내보내기
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/             # TaskDao, NoteDao, ExpenseDao, ContactDao
│   │   └── entity/          # TaskEntity, NoteEntity, ExpenseEntity, ContactEntity
│   ├── notification/        # WorkManager 알림
│   ├── preferences/         # DataStore 설정
│   └── repository/          # TaskRepository, NoteRepository, ExpenseRepository, ContactRepository
├── di/AppModule.kt
├── domain/model/            # Task, Category, Priority, Note, Expense, Contact, DueDateUtil
└── ui/
    ├── navigation/NavGraph.kt
    ├── theme/
    └── screens/
        ├── dashboard/       # 대시보드 (진행률, 바로가기, PDF 내보내기)
        ├── checklist/       # 체크리스트 (필터, 정렬, 검색)
        ├── detail/          # 할 일 상세 + 메모
        ├── timeline/        # 타임라인 (월별 뷰)
        ├── expense/         # 비용 트래커
        ├── contact/         # 연락처 관리
        └── exchange/        # 환율 계산기
```

## Features

### 체크리스트 관리
- **8개 카테고리** — 비자, 서류, 주거, 재정, 커리어, 의료, 교통, 한국 정리
- **66개 기본 항목** — 첫 실행 시 자동 생성
- **항목별 수행 가이드** — 각 항목에 단계별 수행 방법 + 참고 링크 제공
- **CRUD** — 항목 추가/수정/삭제/완료 토글
- **필터 & 정렬** — 상태, 우선순위, 담당자별 필터링 + 다중 정렬
- **검색** — 제목/설명 전체 검색

### 대시보드
- **원형 진행률 차트** — 전체 완료율 시각화
- **카테고리별 현황** — 진행률 바 + 카드 UI
- **마감 임박 알림** — D-day 표시, 긴급도별 색상 (빨강/주황/노랑)
- **미완료 고우선순위** — 빠른 확인

### 생산성
- **타임라인 뷰** — 월별 그룹핑, 미정 항목 분리
- **할 일 메모** — 각 항목에 메모 추가/삭제
- **알림/리마인더** — D-0, D-1 자동 알림 (WorkManager)
- **다크 모드** — 시스템/다크/라이트 3단 토글

### 고급 기능
- **비용 트래커** — KRW/USD 지출 기록, 카테고리별 합계
- **연락처 관리** — 이민 변호사, 부동산 등 연락처 저장, 전화/이메일 바로 연결
- **환율 계산기** — KRW ↔ USD 실시간 변환, 기준 환율 저장
- **PDF 내보내기** — 체크리스트 현황을 PDF로 생성하여 공유

## Development Progress

| Phase | 이름 | 상태 |
|-------|------|------|
| 0 | Harness 구축 | ✅ Complete |
| 1 | Foundation | ✅ Complete |
| 1.5 | Content 정비 | ✅ Complete |
| 2 | Core Checklist | ✅ Complete |
| 3 | Dashboard & UX | ✅ Complete |
| 4 | Productivity | ✅ Complete |
| 5 | Advanced | ✅ Complete |

**총 69개 유닛 테스트**, CI/CD 전체 통과 (ktlint, detekt, build, test)

## Development Methodology

```
Feature Spec (human) → Agent 구현 (code + test) → CI 검증 → PR 리뷰 → 머지
```

자세한 로드맵과 harness 구축 계획은 아래 문서를 참고:
- [Feature Roadmap](docs/roadmap/FEATURE_ROADMAP.md)
- [Harness Plan](docs/HARNESS_PLAN.md)

## License

MIT
