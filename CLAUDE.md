# US Relocation Planner

## Project Overview
미국 이주를 준비하는 사용자를 위한 체크리스트/플래너 앱.

## Tech Stack
- **Python 3.13** (uv로 패키지 관리)
- **Streamlit** — UI 프레임워크
- **SQLite** — 로컬 데이터 저장

## Project Structure
```
├── main.py                 # Streamlit 앱 진입점
├── src/
│   ├── database.py         # SQLite DB 초기화 및 CRUD
│   ├── models.py           # 데이터 모델 (dataclass)
│   └── pages/              # Streamlit 페이지 모듈
│       ├── dashboard.py    # 대시보드 (진행률 요약)
│       ├── visa.py         # 비자 & 이민
│       ├── housing.py      # 주거
│       ├── finance.py      # 재정 & 은행
│       ├── career.py       # 커리어 & 구직
│       └── logistics.py    # 이사 & 생활 셋업
├── data/
│   └── default_tasks.json  # 기본 체크리스트 항목
└── .gitignore
```

## Commands
- `uv run streamlit run main.py` — 앱 실행
- `uv sync` — 의존성 설치

## Conventions
- 한국어 UI, 코드 주석은 영어
- Streamlit multi-page는 sidebar navigation으로 구현
- DB 파일(planner.db)은 gitignore 처리
- dataclass로 모델 정의, type hint 필수
