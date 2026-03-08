# Testing Strategy

## Overview

모든 새 기능은 반드시 테스트를 동반한다.
테스트 없는 PR은 머지하지 않는다.

## Test Pyramid

```
        ┌──────────┐
        │  UI Test  │  ← 최소한 (Compose Test Rule)
       ┌┴──────────┴┐
       │ Integration │  ← DAO + Repository
      ┌┴────────────┴┐
      │   Unit Test   │  ← ViewModel, Domain, Mapping
      └──────────────┘
```

## Layer별 테스트 전략

### Domain Model (Unit Test)
- 대상: `domain/model/` 내 enum, data class
- 도구: JUnit 5
- 의존성: 없음 (순수 Kotlin)

```kotlin
class CategoryTest {
    @Test
    fun `should have 5 categories`() {
        assertEquals(5, Category.entries.size)
    }

    @Test
    fun `should have Korean labels`() {
        assertNotNull(Category.VISA.label)
        assertTrue(Category.VISA.label.isNotBlank())
    }
}
```

### Entity Mapping (Unit Test)
- 대상: `TaskEntity.toDomain()`, `TaskEntity.fromDomain()`
- 도구: JUnit 5
- 라운드트립 검증: `Task → Entity → Task`가 동일한지

```kotlin
class TaskEntityTest {
    @Test
    fun `should round-trip domain to entity and back`() {
        val original = Task(id = 1, title = "Test", category = Category.VISA)
        val entity = TaskEntity.fromDomain(original)
        val restored = entity.toDomain()
        assertEquals(original.title, restored.title)
        assertEquals(original.category, restored.category)
    }
}
```

### DAO (Integration Test)
- 대상: `data/local/dao/TaskDao`
- 도구: Room in-memory database + JUnit 5 + Coroutines Test
- 실제 SQLite 동작 검증

```kotlin
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.taskDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun should_insert_and_retrieve_task() = runTest {
        val entity = TaskEntity(title = "Test", category = "VISA", createdAt = "now", updatedAt = "now")
        dao.insert(entity)
        val tasks = dao.getAllTasks().first()
        assertEquals(1, tasks.size)
        assertEquals("Test", tasks[0].title)
    }
}
```

### Repository (Unit Test)
- 대상: `data/repository/TaskRepository`
- 도구: JUnit 5 + Fake DAO + Coroutines Test
- DAO를 fake로 대체하여 순수 로직만 검증

```kotlin
class TaskRepositoryTest {
    private lateinit var fakeDao: FakeTaskDao
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        fakeDao = FakeTaskDao()
        repository = TaskRepository(fakeDao, fakeContext)
    }

    @Test
    fun should_return_tasks_as_domain_models() = runTest {
        fakeDao.insertTestData()
        val tasks = repository.getAllTasks().first()
        assertTrue(tasks.all { it is Task })
    }
}
```

### ViewModel (Unit Test)
- 대상: `ui/screens/*/ViewModel`
- 도구: JUnit 5 + Turbine (Flow assertion) + Fake Repository
- StateFlow emission 검증

```kotlin
@HiltAndroidTest
class DashboardViewModelTest {
    @Test
    fun should_emit_stats_from_repository() = runTest {
        val fakeRepo = FakeTaskRepository()
        val viewModel = DashboardViewModel(fakeRepo)

        viewModel.categoryStats.test {
            val stats = awaitItem()
            // assert stats
        }
    }
}
```

### UI (Compose Test)
- 대상: `ui/screens/*/Screen`
- 도구: Compose Test Rule
- 화면 렌더링 + 기본 인터랙션 검증

```kotlin
class DashboardScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_display_progress_text() {
        composeTestRule.setContent {
            DashboardScreen(viewModel = fakeDashboardViewModel())
        }
        composeTestRule.onNodeWithText("전체 진행률").assertIsDisplayed()
    }
}
```

## Test Naming Convention

```
should_<expected>_when_<condition>()
```

예시:
- `should_return_empty_list_when_no_tasks_exist()`
- `should_toggle_isDone_when_checkbox_clicked()`
- `should_insert_27_default_tasks_when_db_is_empty()`

## Test File Location

```
app/src/
├── test/          # Unit tests (JVM)
│   └── java/com/seongokryu/relocationplanner/
│       ├── domain/model/
│       ├── data/local/entity/
│       ├── data/repository/
│       └── ui/screens/
└── androidTest/   # Instrumentation tests (emulator/device)
    └── java/com/seongokryu/relocationplanner/
        ├── data/local/dao/
        └── ui/screens/
```

## Test Dependencies (추가 필요)

```toml
# gradle/libs.versions.toml에 추가
junit = { group = "junit", name = "junit", version = "4.13.2" }
kotlin-test = { group = "org.jetbrains.kotlin", name = "kotlin-test" }
coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version = "1.9.0" }
turbine = { group = "app.cash.turbine", name = "turbine", version = "1.2.0" }
compose-ui-test = { group = "androidx.compose.ui", name = "ui-test-junit4" }
compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }
```

## CI Integration

모든 테스트는 CI에서 자동 실행:
```
./gradlew test            # Unit tests
./gradlew connectedCheck  # Instrumentation tests (emulator 필요 시)
```

PR 머지 조건: `./gradlew test` 통과 필수.
