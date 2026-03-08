# Phase 2: Core Checklist — Feature Spec

## Overview

체크리스트 CRUD의 핵심 기능을 구현한다. 사용자가 카테고리별로 할 일을 조회, 추가, 수정, 삭제, 완료 토글할 수 있다.

## Features

### 2.1 체크리스트 필터링

**User Story**: 사용자로서, 상태와 우선순위로 필터링하고 싶다. 필요한 항목만 빠르게 찾기 위해.

**Screen Layout**:
```
┌─────────────────────────────────┐
│ 🛂 비자 & 이민                   │ ← TopBar (category icon + label)
├─────────────────────────────────┤
│ [전체 ▼] [전체 ▼]               │ ← FilterRow (상태, 우선순위)
├─────────────────────────────────┤
│ ☑ 🔴 비자 종류 결정              │
│   ↳ 각자의 상황에 맞는...        │
│   👤 둘 다                       │
│                                 │
│ ☐ 🔴 이민 변호사 선임            │
│   ↳ 미국 이민 전문 변호사...     │
│   👤 둘 다                       │
│                                 │
│ ...                             │
├─────────────────────────────────┤
│                          [+ FAB]│
└─────────────────────────────────┘
```

**State**:
```kotlin
// ChecklistViewModel에 추가
data class FilterState(
    val statusFilter: StatusFilter = StatusFilter.ALL,
    val priorityFilter: PriorityFilter = PriorityFilter.ALL,
)

enum class StatusFilter { ALL, PENDING, DONE }
enum class PriorityFilter { ALL, HIGH, MEDIUM, LOW }

val filterState: MutableStateFlow<FilterState>
val filteredTasks: StateFlow<List<Task>>  // tasks + filterState 결합
```

**구현 사항**:
- `FilterRow` Composable: 두 개의 `ExposedDropdownMenuBox`
- ViewModel에서 `combine(tasks, filterState)` → `filteredTasks`
- 필터 변경 시 Flow가 자동으로 재발행

**테스트**:
- `should_show_only_pending_tasks_when_status_filter_is_PENDING()`
- `should_show_only_high_priority_when_priority_filter_is_HIGH()`
- `should_show_all_tasks_when_both_filters_are_ALL()`

---

### 2.2 Task 수정 다이얼로그

**User Story**: 사용자로서, 기존 항목의 제목/설명/우선순위/담당자/마감일을 수정하고 싶다.

**Screen Layout**:
```
┌─────────────────────────────────┐
│ 항목 수정                        │ ← Dialog title
├─────────────────────────────────┤
│ 제목:    [__________________]    │
│ 설명:    [__________________]    │
│ 우선순위: [높음 ▼]               │
│ 담당자:  [__________________]    │
│ 마감일:  [📅 날짜 선택]          │
├─────────────────────────────────┤
│              [취소]   [저장]     │
└─────────────────────────────────┘
```

**구현 사항**:
- `EditTaskDialog` Composable (AlertDialog 기반)
- 기존 `AddTaskDialog`와 유사하나 initial values 채움
- 우선순위: `ExposedDropdownMenuBox` with `Priority.entries`
- 마감일: `DatePickerDialog` (Material 3)
- 저장 시 `viewModel.updateTask(updatedTask)`

**State**:
```kotlin
// ChecklistScreen에서
var editingTask: Task? by remember { mutableStateOf(null) }
// 카드 클릭 or 편집 아이콘 → editingTask = task
// 다이얼로그 닫기 → editingTask = null
```

**테스트**:
- `should_show_edit_dialog_when_task_card_clicked()`
- `should_update_task_title_when_saved()`
- `should_dismiss_dialog_when_cancel_clicked()`

---

### 2.3 Swipe-to-Delete

**User Story**: 사용자로서, 항목을 왼쪽으로 스와이프해서 삭제하고 싶다. 빠르게 정리하기 위해.

**구현 사항**:
- `SwipeToDismissBox` (Material 3) 사용
- 왼쪽 스와이프 시 빨간 배경 + 휴지통 아이콘
- 스와이프 완료 시 `viewModel.deleteTask(task)`
- `Snackbar`로 "삭제됨" 메시지 + "되돌리기" 액션

**State**:
```kotlin
// ChecklistScreen
val snackbarHostState = remember { SnackbarHostState() }
// 삭제 후 Snackbar 표시, "되돌리기" 클릭 시 viewModel.addTask(deletedTask)
```

**테스트**:
- `should_delete_task_when_swiped_left()`
- `should_restore_task_when_undo_clicked()`

---

### 2.4 Add Task 다이얼로그 개선

현재 `AddTaskDialog`에 우선순위, 담당자, 마감일 필드를 추가한다.

**추가 필드**:
- 우선순위 선택 (`ExposedDropdownMenuBox`)
- 담당자 입력 (`OutlinedTextField`)
- 마감일 선택 (`DatePickerDialog`)

**테스트**:
- `should_create_task_with_high_priority_when_selected()`
- `should_create_task_with_due_date_when_date_picked()`

---

### 2.5 빈 상태 UI

**User Story**: 사용자로서, 할 일이 없을 때 안내 메시지를 보고 싶다.

**Screen Layout**:
```
┌─────────────────────────────────┐
│                                 │
│        📋                       │
│   아직 할 일이 없습니다.          │
│   + 버튼을 눌러 추가하세요.       │
│                                 │
└─────────────────────────────────┘
```

**구현 사항**:
- `EmptyState` Composable: 아이콘 + 텍스트
- tasks가 비어있을 때 LazyColumn 대신 표시

**테스트**:
- `should_show_empty_state_when_no_tasks()`

---

## Data Model Changes

### 새로운 파일

| 파일 | 설명 |
|------|------|
| `ui/screens/checklist/FilterRow.kt` | 필터 UI 컴포넌트 |
| `ui/screens/checklist/EditTaskDialog.kt` | 수정 다이얼로그 |
| `ui/screens/checklist/EmptyState.kt` | 빈 상태 UI |
| `ui/screens/checklist/FilterState.kt` | 필터 상태 enum + data class |

### 수정 파일

| 파일 | 변경 내용 |
|------|----------|
| `ChecklistViewModel.kt` | FilterState 추가, combine으로 filteredTasks |
| `ChecklistScreen.kt` | FilterRow, SwipeToDismiss, EditDialog 통합 |
| `AddTaskDialog` (기존) | 우선순위, 담당자, 마감일 필드 추가 |

### DB 변경
없음. 기존 스키마로 충분.

---

## Acceptance Criteria

- [ ] 카테고리별 체크리스트가 표시된다
- [ ] 상태 필터 (전체/미완료/완료)가 동작한다
- [ ] 우선순위 필터 (전체/높음/보통/낮음)가 동작한다
- [ ] 체크박스로 완료 토글이 가능하다
- [ ] FAB으로 새 항목 추가 시 제목, 설명, 우선순위, 담당자, 마감일을 입력할 수 있다
- [ ] 카드 클릭으로 항목 수정 다이얼로그가 열린다
- [ ] 수정 다이얼로그에서 저장하면 즉시 반영된다
- [ ] 왼쪽 스와이프로 삭제 가능하다
- [ ] 삭제 후 Snackbar에서 되돌리기 가능하다
- [ ] 항목이 없을 때 빈 상태 UI가 표시된다
- [ ] 모든 기능에 대한 테스트가 존재한다
