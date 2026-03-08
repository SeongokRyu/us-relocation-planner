# ADR-002: Room over Raw SQLite

## Status
Accepted

## Date
2026-03-08

## Context
로컬 데이터 저장소로 SQLite를 사용하기로 했다. 접근 방식을 결정해야 한다.

후보:
- Raw SQLite (android.database.sqlite) — 보일러플레이트 많음, 타입 안전성 없음
- **Room** — Google 공식 ORM, 컴파일 타임 검증
- Realm — 3rd party, 추가 의존성
- DataStore — key-value만 지원, 관계형 데이터 부적합

## Decision
**Room**을 채택한다.

## Consequences

**장점:**
- `@Entity`, `@Dao`, `@Database` 어노테이션으로 선언적 정의
- SQL 쿼리 컴파일 타임 검증
- Flow 반환 타입 지원 → 실시간 UI 반영
- Migration 지원 (`@AutoMigration`)
- KSP로 코드 생성 (빌드 성능)

**단점:**
- 추가 의존성 (room-runtime, room-ktx, room-compiler)
- 스키마 변경 시 migration 코드 필요

**Entity-Domain 분리 원칙:**
Room Entity는 DB 스키마에 종속되므로 domain 모델과 분리한다.
매핑은 Entity 내 `toDomain()` / `fromDomain()` 함수로 처리.
