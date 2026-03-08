import json
import sqlite3
from pathlib import Path

from src.models import Category, Note, Priority, Task

DB_PATH = Path(__file__).parent.parent / "planner.db"
DEFAULT_TASKS_PATH = Path(__file__).parent.parent / "data" / "default_tasks.json"


def get_connection() -> sqlite3.Connection:
    conn = sqlite3.connect(str(DB_PATH))
    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA journal_mode=WAL")
    return conn


def init_db() -> None:
    conn = get_connection()
    conn.executescript("""
        CREATE TABLE IF NOT EXISTS tasks (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            description TEXT DEFAULT '',
            category TEXT NOT NULL,
            priority TEXT DEFAULT 'medium',
            is_done INTEGER DEFAULT 0,
            assignee TEXT DEFAULT '',
            due_date TEXT,
            created_at TEXT NOT NULL,
            updated_at TEXT NOT NULL
        );

        CREATE TABLE IF NOT EXISTS notes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            task_id INTEGER NOT NULL,
            content TEXT NOT NULL,
            created_at TEXT NOT NULL,
            FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
        );
    """)
    conn.commit()

    # Seed default tasks if table is empty
    row = conn.execute("SELECT COUNT(*) as cnt FROM tasks").fetchone()
    if row["cnt"] == 0:
        _seed_defaults(conn)

    conn.close()


def _seed_defaults(conn: sqlite3.Connection) -> None:
    if not DEFAULT_TASKS_PATH.exists():
        return
    with open(DEFAULT_TASKS_PATH, encoding="utf-8") as f:
        tasks_data = json.load(f)

    from datetime import datetime

    now = datetime.now().isoformat()
    for item in tasks_data:
        conn.execute(
            """INSERT INTO tasks (title, description, category, priority, assignee, due_date, is_done, created_at, updated_at)
               VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?)""",
            (
                item["title"],
                item.get("description", ""),
                item["category"],
                item.get("priority", "medium"),
                item.get("assignee", ""),
                item.get("due_date"),
                now,
                now,
            ),
        )
    conn.commit()


# --- CRUD operations ---


def get_all_tasks(
    category: Category | None = None,
    is_done: bool | None = None,
) -> list[Task]:
    conn = get_connection()
    query = "SELECT * FROM tasks WHERE 1=1"
    params: list = []

    if category is not None:
        query += " AND category = ?"
        params.append(category.value)
    if is_done is not None:
        query += " AND is_done = ?"
        params.append(int(is_done))

    query += " ORDER BY priority ASC, created_at ASC"
    rows = conn.execute(query, params).fetchall()
    conn.close()

    return [
        Task(
            id=r["id"],
            title=r["title"],
            description=r["description"],
            category=Category(r["category"]),
            priority=Priority(r["priority"]),
            is_done=bool(r["is_done"]),
            assignee=r["assignee"],
            due_date=r["due_date"],
            created_at=r["created_at"],
            updated_at=r["updated_at"],
        )
        for r in rows
    ]


def add_task(task: Task) -> int:
    conn = get_connection()
    cursor = conn.execute(
        """INSERT INTO tasks (title, description, category, priority, assignee, due_date, is_done, created_at, updated_at)
           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
        (
            task.title,
            task.description,
            task.category.value,
            task.priority.value,
            task.assignee,
            task.due_date,
            int(task.is_done),
            task.created_at,
            task.updated_at,
        ),
    )
    conn.commit()
    task_id = cursor.lastrowid
    conn.close()
    return task_id  # type: ignore[return-value]


def update_task(task: Task) -> None:
    from datetime import datetime

    conn = get_connection()
    conn.execute(
        """UPDATE tasks SET title=?, description=?, category=?, priority=?,
           assignee=?, due_date=?, is_done=?, updated_at=? WHERE id=?""",
        (
            task.title,
            task.description,
            task.category.value,
            task.priority.value,
            task.assignee,
            task.due_date,
            int(task.is_done),
            datetime.now().isoformat(),
            task.id,
        ),
    )
    conn.commit()
    conn.close()


def toggle_task(task_id: int) -> None:
    conn = get_connection()
    conn.execute(
        "UPDATE tasks SET is_done = 1 - is_done, updated_at = datetime('now') WHERE id = ?",
        (task_id,),
    )
    conn.commit()
    conn.close()


def delete_task(task_id: int) -> None:
    conn = get_connection()
    conn.execute("DELETE FROM tasks WHERE id = ?", (task_id,))
    conn.commit()
    conn.close()


def get_notes(task_id: int) -> list[Note]:
    conn = get_connection()
    rows = conn.execute(
        "SELECT * FROM notes WHERE task_id = ? ORDER BY created_at DESC",
        (task_id,),
    ).fetchall()
    conn.close()
    return [
        Note(
            id=r["id"],
            task_id=r["task_id"],
            content=r["content"],
            created_at=r["created_at"],
        )
        for r in rows
    ]


def add_note(note: Note) -> int:
    conn = get_connection()
    cursor = conn.execute(
        "INSERT INTO notes (task_id, content, created_at) VALUES (?, ?, ?)",
        (note.task_id, note.content, note.created_at),
    )
    conn.commit()
    note_id = cursor.lastrowid
    conn.close()
    return note_id  # type: ignore[return-value]


def get_stats() -> dict:
    conn = get_connection()
    rows = conn.execute(
        """SELECT category,
                  COUNT(*) as total,
                  SUM(is_done) as done
           FROM tasks GROUP BY category"""
    ).fetchall()
    conn.close()
    return {r["category"]: {"total": r["total"], "done": r["done"]} for r in rows}
