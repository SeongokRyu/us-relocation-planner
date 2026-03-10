# Phase 4 — Productivity

## Overview

생산성 향상 기능을 추가한다.
마감일 관리 강화, 메모/노트, 타임라인 뷰, 알림 기능을 통해
사용자가 이주 준비 일정을 효율적으로 관리할 수 있도록 한다.

---

## 4.1 마감일 임박 하이라이트

**User Story**: 마감일이 다가오는 항목을 한눈에 파악하고 싶다.

**구현 사항**:
- Dashboard에 "마감 임박" 섹션 추가 (D-3 이내 미완료 항목)
- 마감일 지난(overdue) 항목은 빨간색 강조
- D-day 표시: "D-3", "D-Day", "D+2" 형식
- 체크리스트 카드에도 마감 임박 시 색상 변경

**레이아웃**:
```
┌─────────────────────────────┐
│ ⚠️ 마감 임박 (3)             │
├─────────────────────────────┤
│ 🔴 D+2  여권 갱신            │  ← overdue (빨간)
│ 🟠 D-Day 건강검진 예약        │  ← today (주황)
│ 🟡 D-3  비자 서류 준비        │  ← approaching (노랑)
└─────────────────────────────┘
```

**Domain 유틸리티** (`domain/model/DueDateUtil.kt`, 순수 Kotlin):
```kotlin
object DueDateUtil {
    fun daysUntil(dueDate: String?, today: String): Int?
    fun formatDDay(days: Int): String   // "D-3", "D-Day", "D+2"
    fun urgencyLevel(days: Int): UrgencyLevel
}

enum class UrgencyLevel { OVERDUE, TODAY, APPROACHING, NORMAL }
```

**테스트**:
- `should_return_negative_when_overdue()`
- `should_return_zero_on_due_date()`
- `should_return_positive_when_future()`
- `should_return_null_when_no_due_date()`
- `should_format_d_day_correctly()`
- `should_classify_urgency_level()`

---

## 4.2 Task별 메모/노트

**User Story**: 각 체크리스트 항목에 메모를 추가하여 상세 정보를 기록하고 싶다.

**구현 사항**:
- Note entity 추가 (id, taskId, content, createdAt)
- Task 상세 화면에서 메모 목록 표시 + 추가/삭제
- 체크리스트 카드에 메모 개수 표시 (아이콘 + 숫자)

**레이아웃** (TaskDetailScreen):
```
┌─────────────────────────────┐
│ ← 여권 갱신                  │  TopAppBar
├─────────────────────────────┤
│ 카테고리: 📄 서류             │
│ 우선순위: 🔴 높음             │
│ 담당자:   홍길동              │
│ 마감일:   2026-04-15 (D-36)  │
│ 상태:     ☐ 미완료            │
├─────────────────────────────┤
│ 📝 메모 (2)                  │
│ ┌───────────────────────┐   │
│ │ 여권 사진 2장 필요       │   │
│ │ 2026-03-08             │   │
│ │                   [🗑] │   │
│ └───────────────────────┘   │
│ ┌───────────────────────┐   │
│ │ 구청 방문 예약 완료      │   │
│ │ 2026-03-10             │   │
│ │                   [🗑] │   │
│ └───────────────────────┘   │
│                             │
│ [메모 입력...]         [추가] │
└─────────────────────────────┘
```

**Data Model**:

```kotlin
// domain/model/Note.kt (순수 Kotlin)
data class Note(
    val id: Long = 0,
    val taskId: Long,
    val content: String,
    val createdAt: String = "",
)
```

```kotlin
// data/local/entity/NoteEntity.kt
@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["id"],
        childColumns = ["task_id"],
        onDelete = ForeignKey.CASCADE,
    )],
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "task_id") val taskId: Long,
    val content: String,
    @ColumnInfo(name = "created_at") val createdAt: String = "",
) {
    fun toDomain(): Note = ...
    companion object { fun fromDomain(note: Note): NoteEntity = ... }
}
```

```kotlin
// data/local/dao/NoteDao.kt
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE task_id = :taskId ORDER BY created_at DESC")
    fun getNotesByTaskId(taskId: Long): Flow<List<NoteEntity>>

    @Query("SELECT task_id, COUNT(*) as count FROM notes GROUP BY task_id")
    fun getNoteCountsByTask(): Flow<List<NoteCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Delete
    suspend fun delete(note: NoteEntity)
}

data class NoteCount(
    @ColumnInfo(name = "task_id") val taskId: Long,
    val count: Int,
)
```

**DB 변경**: version 2 → 3, NoteEntity 추가, destructive migration.

**State Management** (`TaskDetailViewModel.kt`):
```kotlin
val task: StateFlow<Task?>
val notes: StateFlow<List<Note>>
val noteCounts: StateFlow<Map<Long, Int>>  // taskId → count
```

**파일 변경**:

| 파일 | 변경 |
|------|------|
| `domain/model/Note.kt` | 새 파일 |
| `data/local/entity/NoteEntity.kt` | 새 파일 |
| `data/local/dao/NoteDao.kt` | 새 파일 |
| `data/repository/NoteRepository.kt` | 새 파일 |
| `data/local/AppDatabase.kt` | version 3, NoteEntity 추가 |
| `di/AppModule.kt` | NoteDao provide 추가 |
| `ui/screens/detail/TaskDetailScreen.kt` | 새 파일 |
| `ui/screens/detail/TaskDetailViewModel.kt` | 새 파일 |
| `NavGraph.kt` | TaskDetail route 추가 |
| `ChecklistScreen.kt` | 카드에 메모 개수 표시, 클릭 시 detail로 이동 |

**테스트**:
- `should_map_note_entity_to_domain()`
- `should_map_note_domain_to_entity()`
- `should_show_notes_for_task()`
- `should_add_note_to_task()`
- `should_delete_note()`
- `should_display_note_count_on_card()`

---

## 4.3 타임라인 뷰

**User Story**: 마감일 기준으로 할 일을 시간순으로 보고 싶다.

**구현 사항**:
- 대시보드에서 접근 가능한 타임라인 화면
- 마감일이 있는 항목만 시간순 정렬
- 월별 그룹핑 (2026년 3월, 4월, ...)
- 마감일 없는 항목은 하단 "미정" 섹션에 표시
- 완료/미완료 시각적 구분

**레이아웃**:
```
┌─────────────────────────────┐
│ ← 타임라인                   │
├─────────────────────────────┤
│ ── 2026년 3월 ──            │
│ 03.10  ☑ 건강검진 예약       │
│ 03.15  ☐ 여권 갱신      D-5  │
│ 03.20  ☐ 비자 면접     D-10  │
│                             │
│ ── 2026년 4월 ──            │
│ 04.01  ☐ 항공편 예약   D-22  │
│ 04.15  ☐ 이사 업체    D-36  │
│                             │
│ ── 미정 ──                  │
│ ☐ 은행 계좌 정리             │
│ ☐ 보험 해지                 │
└─────────────────────────────┘
```

**파일**:

| 파일 | 설명 |
|------|------|
| `ui/screens/timeline/TimelineScreen.kt` | 새 파일 |
| `ui/screens/timeline/TimelineViewModel.kt` | 새 파일 |
| `NavGraph.kt` | Timeline route 추가 |
| `DashboardScreen.kt` | 타임라인 진입 버튼 추가 |

**State Management** (`TimelineViewModel.kt`):
```kotlin
data class MonthGroup(
    val label: String,       // "2026년 3월"
    val tasks: List<Task>,
)

val timelineGroups: StateFlow<List<MonthGroup>>
val undatedTasks: StateFlow<List<Task>>
```

**테스트**:
- `should_group_tasks_by_month()`
- `should_sort_tasks_within_month_by_date()`
- `should_separate_undated_tasks()`
- `should_show_empty_when_no_tasks()`

---

## 4.4 알림/리마인더

**User Story**: 마감일 전에 알림을 받아 중요한 일정을 놓치지 않고 싶다.

**구현 사항**:
- WorkManager로 마감일 D-1 자동 알림
- 알림 클릭 시 해당 Task 상세 화면으로 이동
- 설정에서 알림 on/off 토글 (DataStore)
- Notification channel: "마감일 리마인더"

**파일**:

| 파일 | 설명 |
|------|------|
| `data/notification/ReminderWorker.kt` | 새 파일, WorkManager Worker |
| `data/notification/ReminderScheduler.kt` | 새 파일, 알림 예약/취소 로직 |
| `data/preferences/ThemePreferences.kt` | → `UserPreferences.kt`로 확장 (알림 설정 포함) |
| `di/AppModule.kt` | WorkManager 설정 |
| `AndroidManifest.xml` | POST_NOTIFICATIONS 퍼미션 |

**테스트**:
- `should_schedule_reminder_for_task_with_due_date()`
- `should_not_schedule_when_no_due_date()`
- `should_cancel_reminder_when_task_completed()`
- `should_respect_notification_toggle()`

---

## 구현 순서

1. **4.1 마감일 임박 하이라이트** — 기존 코드에 추가, 가장 간단
2. **4.2 Task별 메모/노트** — 새 entity + 상세 화면
3. **4.3 타임라인 뷰** — 새 화면, 데이터 가공
4. **4.4 알림/리마인더** — WorkManager + 퍼미션, 가장 복잡

---

## Acceptance Criteria

- [ ] 대시보드에 마감 임박 섹션이 표시된다 (D-3 이내)
- [ ] overdue 항목이 빨간색으로 강조된다
- [ ] Task 상세 화면에서 메모를 추가/삭제할 수 있다
- [ ] 체크리스트 카드에 메모 개수가 표시된다
- [ ] 타임라인 뷰에서 월별 그룹핑된 일정을 볼 수 있다
- [ ] 마감일 D-1에 알림이 전송된다
- [ ] 알림 on/off를 설정할 수 있다
- [ ] 모든 새 기능에 대한 테스트가 존재한다
