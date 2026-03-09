# Feature Roadmap

## Phase Overview

| Phase | 이름 | 상태 | Spec |
|-------|------|------|------|
| 0 | Harness 구축 | ✅ Complete | [HARNESS_PLAN.md](../HARNESS_PLAN.md) |
| 1 | Foundation | ✅ Complete | — (초기 셋업) |
| 1.5 | Content 정비 | ✅ Complete | [content-categories.md](../specs/content-categories.md) |
| 2 | Core Checklist | ✅ Complete | [phase2-core-checklist.md](../specs/phase2-core-checklist.md) |
| 3 | Dashboard & UX | 🔄 Next | [phase3-dashboard-ux.md](../specs/phase3-dashboard-ux.md) |
| 4 | Productivity | ⬜ Planned | phase4-productivity.md |
| 5 | Advanced | ⬜ Planned | phase5-advanced.md |
| 6 | Sync & Share | ⬜ Planned | phase6-sync.md |

---

## Phase 0 — Harness 구축 ✅

- [x] CLAUDE.md harness 목차로 리팩토링
- [x] docs/architecture/ (OVERVIEW, DEPENDENCY_RULES, ADR 5개)
- [x] docs/conventions/ (CODING_STYLE, TESTING, PR_CHECKLIST)
- [x] CI/CD — GitHub Actions (build, lint, test)
- [x] Linting — ktlint + detekt 설정
- [x] Phase 2 feature spec 작성

## Phase 1 — Foundation ✅

- [x] Android 프로젝트 구조 (Gradle, KSP, version catalog)
- [x] Room DB + TaskEntity + TaskDao
- [x] Hilt DI (AppModule)
- [x] Domain model (Task, Category, Priority)
- [x] TaskRepository
- [x] Navigation Compose (Bottom Navigation, 6 tabs)
- [x] Material 3 Theme (Dynamic Color)
- [x] Dashboard Screen skeleton
- [x] Checklist Screen skeleton
- [x] default_tasks.json seed data (27 items)

## Phase 1.5 — Content 정비 ✅

- [x] 카테고리 재구성: 5개 → 8개 (DOCUMENTS, MEDICAL, TRANSPORT, KOREA_DEPARTURE 추가)
- [x] LOGISTICS 카테고리 해체 → 항목들을 적절한 카테고리로 분산
- [x] 체크리스트 항목: 27개 → 66개 (39개 신규 항목 추가)
- [x] 네비게이션: Bottom Nav → Dashboard 허브 방식 (TopAppBar + 카드 클릭)
- [x] DB version 2 + destructive migration
- [x] 콘텐츠 스펙 문서 작성 (docs/specs/content-categories.md)

## Phase 2 — Core Checklist ✅

- [x] 체크리스트 필터링 (상태 + 우선순위)
- [x] Task 수정 다이얼로그 (EditTaskDialog)
- [x] Swipe-to-delete + Snackbar undo
- [x] Add Task 다이얼로그 개선 (우선순위, 담당자, 마감일)
- [x] 빈 상태 UI (EmptyState)
- [x] 유닛 테스트 (ViewModel, Entity mapping — 16 tests)
- [x] Integration 테스트 (DAO — 8 tests, instrumented)

## Phase 3 — Dashboard & UX 🔄

- [ ] 대시보드 원형 진행률 차트 (Canvas)
- [ ] 검색 기능 (TopAppBar + SearchBar)
- [ ] Dark mode 토글
- [ ] 담당자 필터
- [ ] 카드 디자인 개선 (elevation, animation)
- [ ] 정렬 옵션 (우선순위, 마감일, 생성일)

## Phase 4 — Productivity ⬜

- [ ] Due date DatePicker integration
- [ ] 알림/리마인더 (WorkManager)
- [ ] Task별 메모/노트 (Note entity + NoteDao)
- [ ] 캘린더/타임라인 뷰
- [ ] 마감일 임박 항목 하이라이트

## Phase 5 — Advanced ⬜

- [ ] 문서 트래커 (사진 촬영/저장, CameraX)
- [ ] 비용 트래커 (Expense entity)
- [ ] 환율 계산기 (KRW ↔ USD, API 연동)
- [ ] 연락처 관리 (변호사, 부동산 등)
- [ ] 홈 화면 위젯 (Glance)
- [ ] PDF/Excel export

## Phase 6 — Sync & Share ⬜

- [ ] Firebase Firestore 연동
- [ ] 두 사용자 간 실시간 동기화
- [ ] 백업/복원
- [ ] Conflict resolution 전략
- [ ] 사용자 인증 (Firebase Auth)

---

## 개발 사이클

각 Phase는 아래 사이클을 따른다:

```
1. Feature spec 작성 (docs/specs/)
2. 에이전트가 spec 기반 구현 (code + test)
3. CI 자동 검증 (build, lint, test)
4. PR 리뷰 (PR_CHECKLIST.md 기준)
5. 머지 → 다음 feature
```

## Spec 작성 기준

각 feature spec은 다음을 포함해야 한다:
- User Story
- Screen Layout (텍스트 와이어프레임)
- Data Model Changes
- State Management (ViewModel StateFlow 정의)
- Edge Cases
- Acceptance Criteria (체크리스트)
- 테스트 케이스 목록
