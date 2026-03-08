import streamlit as st

from src.database import init_db
from src.models import CATEGORY_ICONS, CATEGORY_LABELS, Category
from src.pages import checklist, dashboard

st.set_page_config(
    page_title="US Relocation Planner",
    page_icon="🇺🇸",
    layout="wide",
)

# Initialize database on first run
init_db()

# Sidebar navigation
st.sidebar.title("🇺🇸 미국 이주 플래너")
st.sidebar.markdown("---")

page_options = ["📊 대시보드"] + [
    f"{CATEGORY_ICONS[cat]} {CATEGORY_LABELS[cat]}" for cat in Category
]

selected = st.sidebar.radio("메뉴", page_options, label_visibility="collapsed")

st.sidebar.markdown("---")
st.sidebar.caption("Chemistry PhD + Marketing PhD")
st.sidebar.caption("미국 이주 준비 체크리스트")

# Route to page
if selected == "📊 대시보드":
    dashboard.render()
else:
    # Find matching category
    for cat in Category:
        label = f"{CATEGORY_ICONS[cat]} {CATEGORY_LABELS[cat]}"
        if selected == label:
            checklist.render(cat)
            break
