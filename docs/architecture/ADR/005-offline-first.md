# ADR-005: Offline-First Architecture

## Status
Accepted

## Date
2026-03-08

## Context
앱의 데이터 접근 전략을 결정해야 한다. 사용자가 네트워크 없이도 체크리스트를 확인/수정할 수 있어야 한다.

후보:
- **Offline-first** — 로컬 DB가 primary, 서버는 sync용
- Online-first — 서버가 primary, 캐시용 로컬
- Hybrid — 일부 데이터는 로컬, 일부는 서버

## Decision
**Offline-first** 아키텍처를 채택한다.

Phase 1~5: 완전 로컬 (Room SQLite)
Phase 6: Firebase Firestore 동기화 추가 (선택적)

## Consequences

**장점:**
- 네트워크 없이 완전한 기능 동작
- 앱 응답 속도 빠름 (로컬 DB 쿼리)
- 서버 인프라 불필요 (Phase 5까지)
- 단순한 데이터 플로우 (Room Flow → StateFlow → UI)

**단점:**
- Phase 6에서 sync 추가 시 conflict resolution 필요
- 두 기기 간 실시간 동기화 없음 (Phase 6 전까지)

**Migration Path (Phase 6):**
```
현재:  UI ← ViewModel ← Repository ← Room DAO ← SQLite
Phase 6: UI ← ViewModel ← Repository ← Room DAO ← SQLite
                                          ↕ sync
                                     Firestore
```

Repository 패턴 덕분에, sync 추가 시 Repository 내부만 변경하면 된다.
ViewModel과 UI는 변경 불필요.
