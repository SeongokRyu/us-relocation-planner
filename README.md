# US Relocation Planner

미국 이주를 준비하는 사용자를 위한 체크리스트/플래너 앱.

카테고리별로 할 일을 관리하고, 대시보드에서 전체 진행률을 한눈에 확인할 수 있습니다.

## Features

- **대시보드** — 전체/카테고리별 진행률, 미완료 고우선순위 항목 요약
- **카테고리별 체크리스트** — 비자 & 이민, 주거, 재정 & 은행, 커리어 & 구직, 이사 & 생활
- **CRUD** — 항목 추가/편집/삭제, 완료 토글
- **필터링** — 상태(전체/미완료/완료), 우선순위(높음/보통/낮음)
- **기본 데이터** — 첫 실행 시 28개 기본 체크리스트 항목 자동 생성

## Tech Stack

| 구성 요소 | 기술 |
|----------|------|
| Language | Python 3.13 |
| Package Manager | uv |
| UI | Streamlit |
| Database | SQLite |

## Quick Start

```bash
# 의존성 설치
uv sync

# 앱 실행
uv run streamlit run main.py
```

브라우저에서 `http://localhost:8501` 로 접속하면 앱을 사용할 수 있습니다.

## Project Structure

```
├── main.py                  # Streamlit 앱 진입점
├── src/
│   ├── models.py            # Task/Note 데이터 모델
│   ├── database.py          # SQLite 초기화 및 CRUD
│   └── pages/
│       ├── dashboard.py     # 대시보드 페이지
│       └── checklist.py     # 카테고리별 체크리스트 페이지
├── data/
│   └── default_tasks.json   # 기본 체크리스트 항목 (28개)
├── pyproject.toml           # 프로젝트 설정 및 의존성
└── CLAUDE.md                # AI 어시스턴트 컨텍스트
```

## License

MIT
