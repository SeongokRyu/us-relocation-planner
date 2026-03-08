# ADR-001: Clean Architecture + MVVM

## Status
Accepted

## Date
2026-03-08

## Context
미국 이주 플래너 앱의 전체 아키텍처 패턴을 결정해야 한다.
Harness Engineering 방식으로 에이전트가 코드를 생성하므로, 명확한 레이어 분리와 예측 가능한 패턴이 필요하다.

후보:
- MVC — Android에서 비권장, 테스트 어려움
- MVP — Presenter와 View 계약이 복잡
- **MVVM + Clean Architecture** — Google 공식 권장, Compose와 자연스럽게 결합
- MVI — 학습 곡선 높음, 소규모 앱에 과도

## Decision
**Clean Architecture + MVVM**을 채택한다.

- **Domain Layer**: 순수 Kotlin 모델 (Task, Category, Priority)
- **Data Layer**: Room Entity/DAO + Repository
- **UI Layer**: Jetpack Compose Screen + ViewModel (StateFlow)

## Consequences

**장점:**
- Google Android 공식 가이드와 일치
- 레이어별 독립 테스트 가능
- 에이전트가 따르기 쉬운 명확한 패턴 (Screen + ViewModel pair)
- Compose의 선언적 모델과 UDF(Unidirectional Data Flow) 자연스러운 결합

**단점:**
- 소규모 앱에서는 보일러플레이트가 다소 많음
- Entity ↔ Domain 매핑 코드 필요

**Trade-off 수용**: 보일러플레이트 증가는 에이전트가 자동 생성하므로 부담이 적고, 테스트 용이성과 레이어 분리의 이점이 더 크다.
