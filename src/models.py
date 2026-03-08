from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum


class Category(str, Enum):
    VISA = "visa"
    HOUSING = "housing"
    FINANCE = "finance"
    CAREER = "career"
    LOGISTICS = "logistics"


CATEGORY_LABELS: dict[Category, str] = {
    Category.VISA: "비자 & 이민",
    Category.HOUSING: "주거",
    Category.FINANCE: "재정 & 은행",
    Category.CAREER: "커리어 & 구직",
    Category.LOGISTICS: "이사 & 생활",
}

CATEGORY_ICONS: dict[Category, str] = {
    Category.VISA: "🛂",
    Category.HOUSING: "🏠",
    Category.FINANCE: "💰",
    Category.CAREER: "💼",
    Category.LOGISTICS: "📦",
}


class Priority(str, Enum):
    HIGH = "high"
    MEDIUM = "medium"
    LOW = "low"


PRIORITY_LABELS: dict[Priority, str] = {
    Priority.HIGH: "높음",
    Priority.MEDIUM: "보통",
    Priority.LOW: "낮음",
}


@dataclass
class Task:
    id: int | None = None
    title: str = ""
    description: str = ""
    category: Category = Category.VISA
    priority: Priority = Priority.MEDIUM
    is_done: bool = False
    assignee: str = ""  # person responsible
    due_date: str | None = None  # ISO format date string
    created_at: str = field(default_factory=lambda: datetime.now().isoformat())
    updated_at: str = field(default_factory=lambda: datetime.now().isoformat())


@dataclass
class Note:
    id: int | None = None
    task_id: int = 0
    content: str = ""
    created_at: str = field(default_factory=lambda: datetime.now().isoformat())
