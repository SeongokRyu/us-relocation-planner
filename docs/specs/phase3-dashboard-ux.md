# Phase 3: Dashboard & UX — Feature Spec

## Overview

대시보드 UX를 개선하고, 검색/정렬/담당자 필터 기능을 추가한다.

## Features

### 3.1 원형 진행률 차트

**User Story**: 사용자로서, 전체 진행률을 시각적으로 한눈에 파악하고 싶다.

**Screen Layout**:
```
┌─────────────────────────────────┐
│        ┌──────────┐             │
│        │  72%     │  ← 원형 차트 │
│        │ 48 / 66  │  (Canvas)   │
│        └──────────┘             │
│                                 │
│  카테고리별 현황                  │
│  ┌─ 🛂 비자 & 이민 ───── 8/12 ─┐│
│  │ ██████████░░░░░  67%        ││
│  └─────────────────────────────┘│
│  ...                            │
└─────────────────────────────────┘
```

**구현 사항**:
- `CircularProgressChart` Composable: Canvas + drawArc
- 중앙에 퍼센트 + 완료/전체 텍스트
- track color + progress color (Material3 primary)

**테스트**:
- `should_calculate_progress_percentage_correctly()`

---

### 3.2 검색 기능

**User Story**: 사용자로서, 전체 항목에서 키워드로 빠르게 검색하고 싶다.

**구현 사항**:
- DAO에 `searchTasks(query)` 추가 (LIKE '%query%')
- Repository에 `searchTasks(query)` 래핑
- DashboardViewModel에 `searchQuery` StateFlow + `searchResults`
- Dashboard TopAppBar에 검색 아이콘 → SearchBar 토글
- 검색 결과를 카테고리 그룹 없이 flat list로 표시
- 검색 결과 카드 클릭 시 해당 카테고리 체크리스트로 이동

**State**:
```kotlin
val searchQuery: MutableStateFlow<String>
val searchResults: StateFlow<List<Task>>
val isSearchActive: MutableStateFlow<Boolean>
```

**테스트**:
- `should_find_tasks_matching_title()`
- `should_find_tasks_matching_description()`
- `should_return_empty_when_no_match()`

---

### 3.3 정렬 옵션

**User Story**: 사용자로서, 체크리스트를 우선순위/마감일/생성일 순으로 정렬하고 싶다.

**구현 사항**:
- `SortOption` enum: PRIORITY, DUE_DATE, CREATED_AT
- ChecklistViewModel에 `sortOption` StateFlow
- FilterRow에 정렬 드롭다운 추가
- combine(tasks, filterState, sortOption) → filteredAndSortedTasks

**테스트**:
- `should_sort_by_priority()`
- `should_sort_by_due_date()`
- `should_sort_by_created_at()`

---

### 3.4 담당자 필터

**User Story**: 사용자로서, 담당자별로 항목을 필터링하고 싶다.

**구현 사항**:
- FilterState에 `assigneeFilter: String` 추가 (빈 문자열 = 전체)
- 고유 담당자 목록을 tasks에서 추출
- FilterRow에 담당자 드롭다운 추가

**테스트**:
- `should_filter_by_assignee()`
- `should_show_all_when_assignee_filter_empty()`

---

### 3.5 카드 디자인 개선

**User Story**: 사용자로서, 카테고리 카드가 더 시각적으로 구별되었으면 좋겠다.

**구현 사항**:
- 카테고리 카드에 elevation 추가
- 진행률 바를 카드 내부에서 더 눈에 띄게
- 카테고리 아이콘 크기 확대
- 완료/전체 수를 chip 스타일로 표시

---

## Data Model Changes

### 새로운 파일

| 파일 | 설명 |
|------|------|
| `ui/screens/dashboard/CircularProgressChart.kt` | 원형 진행률 차트 |
| `ui/screens/checklist/SortOption.kt` | 정렬 enum |

### 수정 파일

| 파일 | 변경 내용 |
|------|----------|
| `TaskDao.kt` | `searchTasks(query)` 쿼리 추가 |
| `TaskRepository.kt` | `searchTasks(query)` 추가 |
| `DashboardScreen.kt` | 원형 차트, 검색 결과, 카드 디자인 개선 |
| `DashboardViewModel.kt` | 검색 관련 StateFlow 추가 |
| `NavGraph.kt` | SearchBar 토글 + 검색 UI |
| `ChecklistViewModel.kt` | 정렬 + 담당자 필터 추가 |
| `ChecklistScreen.kt` | 정렬 드롭다운 통합 |
| `FilterState.kt` | 담당자 필터 필드 추가 |
| `FilterRow.kt` | 정렬 + 담당자 드롭다운 추가 |

### DB 변경
없음. 기존 스키마로 충분.

---

## Acceptance Criteria

- [ ] 대시보드에 원형 진행률 차트가 표시된다
- [ ] 검색 아이콘 클릭 시 검색바가 열린다
- [ ] 검색어 입력 시 실시간으로 결과가 표시된다
- [ ] 체크리스트에서 정렬 옵션을 선택할 수 있다
- [ ] 담당자별 필터링이 동작한다
- [ ] 카테고리 카드 디자인이 개선되었다
- [ ] 모든 새 기능에 대한 테스트가 존재한다
