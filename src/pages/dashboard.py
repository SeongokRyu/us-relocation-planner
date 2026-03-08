import streamlit as st

from src.database import get_all_tasks, get_stats
from src.models import CATEGORY_ICONS, CATEGORY_LABELS, Category


def render() -> None:
    st.header("📊 대시보드")
    st.markdown("미국 이주 준비 전체 진행 현황을 한눈에 확인하세요.")

    stats = get_stats()

    # Overall progress
    total_all = sum(s["total"] for s in stats.values())
    done_all = sum(s["done"] for s in stats.values())
    if total_all > 0:
        pct = done_all / total_all
        st.metric("전체 진행률", f"{done_all}/{total_all} ({pct:.0%})")
        st.progress(pct)
    else:
        st.info("아직 등록된 할 일이 없습니다. 사이드바에서 카테고리를 선택해 추가하세요.")
        return

    st.divider()

    # Per-category progress
    cols = st.columns(len(Category))
    for col, cat in zip(cols, Category):
        with col:
            cat_stats = stats.get(cat.value, {"total": 0, "done": 0})
            total = cat_stats["total"]
            done = cat_stats["done"]
            icon = CATEGORY_ICONS[cat]
            label = CATEGORY_LABELS[cat]
            st.markdown(f"### {icon} {label}")
            if total > 0:
                st.progress(done / total)
                st.caption(f"{done}/{total} 완료")
            else:
                st.caption("항목 없음")

    st.divider()

    # Pending high-priority tasks
    st.subheader("🔴 미완료 고우선순위 항목")
    tasks = get_all_tasks(is_done=False)
    high_tasks = [t for t in tasks if t.priority.value == "high"]
    if high_tasks:
        for task in high_tasks:
            icon = CATEGORY_ICONS[task.category]
            assignee = f" ({task.assignee})" if task.assignee else ""
            st.markdown(f"- {icon} **{task.title}**{assignee}")
    else:
        st.success("모든 고우선순위 항목을 완료했습니다! 🎉")
