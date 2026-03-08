import streamlit as st

from src.database import add_task, delete_task, get_all_tasks, toggle_task, update_task
from src.models import (
    CATEGORY_ICONS,
    CATEGORY_LABELS,
    PRIORITY_LABELS,
    Category,
    Priority,
    Task,
)


def render(category: Category) -> None:
    icon = CATEGORY_ICONS[category]
    label = CATEGORY_LABELS[category]
    st.header(f"{icon} {label}")

    # Filters
    col1, col2 = st.columns(2)
    with col1:
        filter_status = st.selectbox(
            "상태 필터",
            ["전체", "미완료", "완료"],
            key=f"filter_{category.value}",
        )
    with col2:
        filter_priority = st.selectbox(
            "우선순위 필터",
            ["전체"] + [PRIORITY_LABELS[p] for p in Priority],
            key=f"priority_{category.value}",
        )

    is_done_filter: bool | None = None
    if filter_status == "미완료":
        is_done_filter = False
    elif filter_status == "완료":
        is_done_filter = True

    tasks = get_all_tasks(category=category, is_done=is_done_filter)

    # Apply priority filter
    if filter_priority != "전체":
        priority_val = [p for p, l in PRIORITY_LABELS.items() if l == filter_priority][0]
        tasks = [t for t in tasks if t.priority == priority_val]

    # Task list
    if not tasks:
        st.info("해당 조건의 항목이 없습니다.")
    else:
        for task in tasks:
            _render_task(task, category)

    st.divider()

    # Add new task
    with st.expander("➕ 새 항목 추가"):
        _render_add_form(category)


def _render_task(task: Task, category: Category) -> None:
    priority_emoji = {"high": "🔴", "medium": "🟡", "low": "🟢"}
    emoji = priority_emoji.get(task.priority.value, "⚪")

    col1, col2, col3 = st.columns([0.6, 0.2, 0.2])
    with col1:
        checked = st.checkbox(
            f"{emoji} {task.title}",
            value=task.is_done,
            key=f"task_{task.id}",
        )
        if checked != task.is_done:
            toggle_task(task.id)  # type: ignore[arg-type]
            st.rerun()

    with col2:
        if task.assignee:
            st.caption(f"👤 {task.assignee}")
    with col3:
        if task.due_date:
            st.caption(f"📅 {task.due_date}")

    if task.description:
        st.caption(f"  ↳ {task.description}")

    # Edit/Delete in expander
    with st.expander("편집", expanded=False):
        _render_edit_form(task, category)


def _render_edit_form(task: Task, category: Category) -> None:
    key = f"edit_{task.id}"
    new_title = st.text_input("제목", value=task.title, key=f"{key}_title")
    new_desc = st.text_area("설명", value=task.description, key=f"{key}_desc")
    new_priority = st.selectbox(
        "우선순위",
        list(Priority),
        format_func=lambda p: PRIORITY_LABELS[p],
        index=list(Priority).index(task.priority),
        key=f"{key}_priority",
    )
    new_assignee = st.text_input("담당자", value=task.assignee, key=f"{key}_assignee")
    new_due = st.text_input(
        "마감일 (YYYY-MM-DD)", value=task.due_date or "", key=f"{key}_due"
    )

    col1, col2 = st.columns(2)
    with col1:
        if st.button("💾 저장", key=f"{key}_save"):
            task.title = new_title
            task.description = new_desc
            task.priority = new_priority
            task.assignee = new_assignee
            task.due_date = new_due if new_due else None
            update_task(task)
            st.success("저장되었습니다.")
            st.rerun()
    with col2:
        if st.button("🗑️ 삭제", key=f"{key}_del"):
            delete_task(task.id)  # type: ignore[arg-type]
            st.rerun()


def _render_add_form(category: Category) -> None:
    key = f"add_{category.value}"
    title = st.text_input("제목", key=f"{key}_title")
    desc = st.text_area("설명", key=f"{key}_desc")
    priority = st.selectbox(
        "우선순위",
        list(Priority),
        format_func=lambda p: PRIORITY_LABELS[p],
        key=f"{key}_priority",
    )
    assignee = st.text_input("담당자", key=f"{key}_assignee")
    due_date = st.text_input("마감일 (YYYY-MM-DD)", key=f"{key}_due")

    if st.button("추가", key=f"{key}_submit"):
        if not title:
            st.warning("제목을 입력하세요.")
            return
        task = Task(
            title=title,
            description=desc,
            category=category,
            priority=priority,
            assignee=assignee,
            due_date=due_date if due_date else None,
        )
        add_task(task)
        st.success(f"'{title}' 추가 완료!")
        st.rerun()
