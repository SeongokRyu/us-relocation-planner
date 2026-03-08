# Harness Engineering Plan

> "에이전트가 헤맬 때, 그것은 에이전트의 문제가 아니라 harness의 문제다."
> — OpenAI Codex Team

## 1. Harness Engineering이란?

코드를 직접 쓰는 것에서 **에이전트가 잘 동작하는 환경을 설계하는 것**으로의 패러다임 전환.

엔지니어의 역할이 바뀐다:

| 기존 | Harness Engineering |
|------|---------------------|
| 코드 작성 | 환경 설계 |
| 코드 리뷰 | 에이전트 출력 + harness 효과 검증 |
| 테스트 작성 | 테스트 전략 설계 → 에이전트가 테스트 작성 |
| 문서는 후순위 | 문서는 핵심 인프라 |
| 아키텍처는 부수적 | 아키텍처 설계가 주업무 |

핵심 비유:
- **Horse** = AI 모델 (강력하지만 방향이 없음)
- **Harness** = 인프라 (제약, 가드레일, 피드백 루프)
- **Rider** = 엔지니어 (방향 제시, 직접 실행하지 않음)

---

## 2. Three Pillars

### 2.1 Context Engineering

> "repo에 없으면 에이전트 관점에서 존재하지 않는다."

**Static Context:**
- CLAUDE.md → "목차" 역할, 상세 내용은 docs/에 분리
- 아키텍처 명세, API 계약, 스타일 가이드를 repo 내 문서로
- 린터가 문서 유효성을 검증

**Dynamic Context:**
- CI/CD 파이프라인 상태, 테스트 결과
- 에이전트 시작 시 디렉토리 구조 매핑
- 로그, 메트릭, 트레이스 (관찰가능성 데이터)

**원칙**: Slack, Google Docs, 머릿속에 있는 지식은 에이전트에게 보이지 않는다. 모든 결정과 규칙은 **repo가 single source of truth**.

### 2.2 Architectural Constraints

> "자유도를 줄이면 에이전트는 오히려 더 빨라진다."

에이전트에게 "좋은 코드를 써라"가 아니라, **기계적으로 품질을 강제**한다:

- Deterministic linter (자동 위반 탐지)
- Structural test (의존성 방향, 레이어 규칙 검증)
- Pre-commit hook (커밋 전 자동 체크)
- CI validation (PR 시 자동 빌드/테스트)

### 2.3 Entropy Management ("Garbage Collection")

AI 생성 코드베이스는 시간이 지나면 엔트로피가 쌓인다 — 문서 불일치, 네이밍 편차, 데드 코드 축적.

주기적 정리 에이전트 운영:
- 문서 일관성 검증
- 아키텍처 규칙 위반 스캔
- 패턴 강제 및 편차 수정
- 의존성 감사

---

## 3. 프로젝트 현황 Gap Analysis

| 영역 | 현재 상태 | 목표 상태 | 우선순위 |
|------|----------|----------|---------|
| CLAUDE.md | 기본 구조만 기술 | docs/로의 목차 + harness 규칙 | **P0** |
| Architecture docs | 없음 | ADR + 레이어 규칙 + 의존성 방향 명세 | **P0** |
| Feature specs | 브레인스토밍 수준 | 화면별 상세 스펙 (IO, 상태, 엣지케이스) | **P0** |
| Coding conventions | 암묵적 | 기계 판독 가능한 명시적 규칙 문서 | **P1** |
| Linting | 없음 | ktlint + detekt 설정 | **P1** |
| CI/CD | 없음 | GitHub Actions (빌드, 린트, 테스트) | **P1** |
| Test strategy | 없음 | 레이어별 테스트 전략 + 예시 | **P1** |
| PR checklist | 없음 | AI 생성 코드 리뷰 체크리스트 | **P2** |
| Entropy management | 없음 | 주기적 정리 작업 정의 | **P2** |

---

## 4. Harness 구축 계획

### Phase 0-A: Context Engineering 기반 구축

#### 4.1 docs/ 디렉토리 구조

```
docs/
├── HARNESS_PLAN.md          # 이 문서 (harness 전체 계획)
├── architecture/
│   ├── OVERVIEW.md           # 아키텍처 개요 + 레이어 다이어그램
│   ├── DEPENDENCY_RULES.md   # 의존성 방향 규칙 (기계 강제 대상)
│   └── ADR/                  # Architecture Decision Records
│       ├── 001-clean-architecture-mvvm.md
│       ├── 002-room-over-raw-sqlite.md
│       ├── 003-hilt-for-di.md
│       ├── 004-bottom-navigation.md
│       └── 005-offline-first.md
├── specs/
│   ├── phase2-core-checklist.md
│   ├── phase3-dashboard-ux.md
│   ├── phase4-productivity.md
│   ├── phase5-advanced.md
│   └── phase6-sync.md
├── conventions/
│   ├── CODING_STYLE.md       # 네이밍, 포매팅, 패턴 규칙
│   ├── TESTING.md            # 테스트 전략 및 예시
│   └── PR_CHECKLIST.md       # AI 생성 PR 리뷰 체크리스트
└── roadmap/
    └── FEATURE_ROADMAP.md    # Phase별 feature 목록 + 상태
```

#### 4.2 CLAUDE.md 리팩토링

CLAUDE.md는 **harness의 진입점**으로 역할을 재정의:

```markdown
# US Relocation Planner — Agent Harness

## Quick Reference
- Architecture: docs/architecture/OVERVIEW.md
- Dependency rules: docs/architecture/DEPENDENCY_RULES.md
- Coding style: docs/conventions/CODING_STYLE.md
- Test strategy: docs/conventions/TESTING.md
- Current phase spec: docs/specs/phase2-core-checklist.md

## Enforced Rules (기계적 강제)
- [ ] ktlint check pass
- [ ] detekt analysis pass
- [ ] ./gradlew test pass
- [ ] dependency layer 위반 없음

## Architecture Invariants
- domain/ → 외부 의존성 없음 (순수 Kotlin)
- data/ → domain만 import
- ui/ → domain 직접 참조 금지, ViewModel 경유
- di/ → 모든 레이어 참조 가능 (wiring 전용)
```

### Phase 0-B: Architectural Constraints 설정

#### 4.3 Dependency Layer 규칙

```
domain/model/       → 순수 Kotlin, Android/Room/Hilt 의존성 금지
data/local/         → domain만 import, ui 참조 금지
data/repository/    → domain + data/local만 import
di/                 → 모든 레이어 참조 가능 (wiring 전용)
ui/                 → domain + di 참조, data/local 직접 참조 금지
```

이 규칙을 강제하는 방법:
1. **Structural test** — 빌드 시 import 방향 검증
2. **CI에서 detekt custom rule** — 위반 시 빌드 실패
3. **CLAUDE.md에 명시** — 에이전트가 참조

#### 4.4 Linting & Static Analysis

| 도구 | 역할 | 설정 |
|------|------|------|
| ktlint | 코드 포매팅 | Kotlin official style |
| detekt | 정적 분석 | complexity, naming, style rules |
| Android Lint | Android-specific 규칙 | default + custom |

#### 4.5 CI/CD Pipeline (GitHub Actions)

```yaml
# 트리거: PR, push to main
jobs:
  build:       ./gradlew assembleDebug
  lint:        ./gradlew ktlintCheck detekt
  test:        ./gradlew test
  # 모든 job 통과해야 머지 가능
```

### Phase 0-C: Test Strategy

#### 4.6 레이어별 테스트 전략

| 레이어 | 테스트 유형 | 도구 | 예시 |
|--------|-----------|------|------|
| domain/model | Unit test | JUnit 5 | Category enum, Task data class |
| data/local | Integration test | Room in-memory DB | DAO CRUD 검증 |
| data/repository | Unit test | Fake DAO | Repository 로직 검증 |
| ui/viewmodel | Unit test | Turbine (Flow test) | 상태 변화 검증 |
| ui/screen | UI test | Compose test rule | 화면 렌더링, 클릭 동작 |

#### 4.7 테스트 작성 규칙

- 새 기능 구현 시 반드시 테스트 동반
- DAO 테스트: in-memory Room DB 사용
- ViewModel 테스트: fake Repository 주입
- 네이밍: `should_기대결과_when_조건()`

---

## 5. Feature Spec 작성 기준

에이전트가 **spec만 보고 바로 구현 가능한 수준**으로 작성:

```markdown
## Feature: [기능명]

### Overview
한 줄 설명

### User Story
"사용자로서 ___를 하고 싶다. ___를 위해."

### Screen Layout (텍스트 와이어프레임)
┌─────────────────────┐
│ [TopBar: 제목]       │
│ [Filter: 상태/우선순위]│
│ [TaskCard]           │
│ [TaskCard]           │
│ [FAB: + 추가]        │
└─────────────────────┘

### Data Model Changes
- 새 Entity/필드 추가 여부
- Migration 필요 여부

### State Management
- ViewModel의 StateFlow 정의
- UI Event → ViewModel Action 매핑

### Edge Cases
- 빈 목록일 때
- 네트워크 오류 (해당 시)
- 입력 validation

### Acceptance Criteria
- [ ] 체크리스트로 검증 가능한 조건들
```

---

## 6. 구현 사이클 (Harness 기반)

```
┌─ 1. Feature spec 작성 (human) ─────────────────┐
│                                                  │
│  2. 에이전트가 spec 기반 구현 (code + test)       │
│                                                  │
│  3. CI 자동 검증 (빌드, 린트, 테스트)             │
│         │                                        │
│         ├─ 통과 → PR 리뷰 (human minimal review) │
│         │                                        │
│         └─ 실패 → 에이전트가 피드백 보고 수정     │
│                                                  │
│  4. 머지                                         │
│                                                  │
│  5. 문제 발견 시 → harness 개선 (문서/규칙/도구)  │
└──────────────────────────────────────────────────┘
```

---

## 7. 실행 우선순위

### 즉시 실행 (Phase 0 — Harness 구축)

1. `docs/architecture/OVERVIEW.md` — 아키텍처 개요 + 레이어 다이어그램
2. `docs/architecture/DEPENDENCY_RULES.md` — 의존성 규칙 명문화
3. `docs/conventions/CODING_STYLE.md` — 코딩 컨벤션
4. `docs/conventions/TESTING.md` — 테스트 전략
5. CLAUDE.md 리팩토링 — harness 목차로 전환
6. CI/CD 파이프라인 — GitHub Actions 워크플로우
7. Linting 설정 — ktlint + detekt
8. `docs/specs/phase2-core-checklist.md` — 첫 번째 feature spec

### 그 이후 (Phase 2+ — 에이전트 주도 구현)

Phase 0 harness가 갖춰지면, 이후 Phase들은:
- Human: feature spec 작성 + 최종 리뷰
- Agent: 구현 + 테스트 + PR 생성
- CI: 자동 검증

---

## References

- [Harness Engineering — OpenAI](https://openai.com/index/harness-engineering/)
- [Harness Engineering — Birgitta Böckeler (Martin Fowler)](https://martinfowler.com/articles/exploring-gen-ai/harness-engineering.html)
- [Harness Engineering Complete Guide — NxCode](https://www.nxcode.io/resources/news/harness-engineering-complete-guide-ai-agent-codex-2026)
- [OpenAI Harness Engineering — InfoQ](https://www.infoq.com/news/2026/02/openai-harness-engineering-codex/)
