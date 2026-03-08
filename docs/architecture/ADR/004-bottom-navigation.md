# ADR-004: Bottom Navigation

## Status
Accepted

## Date
2026-03-08

## Context
6개 주요 화면(대시보드 + 5개 카테고리) 간의 내비게이션 패턴을 결정해야 한다.

후보:
- Navigation Drawer — 화면 수가 많을 때 적합, 하지만 한 손 조작 어려움
- Tab Layout — 상단 탭, 스크롤 가능하나 Material 3 가이드와 다소 불일치
- **Bottom Navigation** — 모바일 한 손 조작에 최적, 5±1개 항목에 적합
- Top App Bar + Dropdown — 계층적이지 않은 플랫 구조에 부적합

## Decision
**Bottom Navigation Bar** (Material 3 `NavigationBar`)를 채택한다.

## Consequences

**장점:**
- 한 손으로 쉽게 탭 전환 가능 (모바일 UX 최적)
- Material 3 `NavigationBar` + `NavigationBarItem` 사용
- 6개 탭 (대시보드 + 5 카테고리)이 하단에 배치
- 각 탭 상태가 `saveState` + `restoreState`로 보존

**단점:**
- 6개 탭은 좁은 화면에서 라벨이 작아질 수 있음
- 향후 카테고리 추가 시 재구조화 필요

**구현:**
- Navigation Compose의 `NavHost` + `composable()` 사용
- Route: `dashboard`, `checklist/{category}`
- Category enum name을 navigation argument로 전달
- `SavedStateHandle`에서 category argument를 ViewModel이 수신

**향후 고려:**
카테고리가 7개 이상이 되면 Navigation Drawer 또는 scrollable tab으로 전환 검토.
