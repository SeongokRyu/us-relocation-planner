# PR Checklist

> AI 에이전트가 생성한 코드를 리뷰할 때 확인할 항목.
> 이 체크리스트를 통과해야 머지 가능.

## Automated Checks (CI가 자동 검증)

- [ ] `./gradlew assembleDebug` — 빌드 성공
- [ ] `./gradlew ktlintCheck` — 코드 스타일 통과
- [ ] `./gradlew detekt` — 정적 분석 통과
- [ ] `./gradlew test` — 유닛 테스트 통과

## Architecture (수동 확인)

- [ ] 의존성 방향 준수 (domain ← data ← ui)
  - domain/ 에 Android/Room/Hilt import 없음
  - ui/screens/ 에서 data/local/ 직접 참조 없음
  - Screen에서 Repository 직접 호출 없음 (ViewModel 경유)
- [ ] 새 화면 추가 시 Screen + ViewModel pair로 구성
- [ ] Entity ↔ Domain 매핑이 Entity 내 toDomain()/fromDomain()으로 구현

## Code Quality (수동 확인)

- [ ] 파일당 200줄 이내 (300줄 초과 시 분리 필요)
- [ ] Wildcard import (`*`) 없음
- [ ] 불필요한 주석이나 TODO 없음
- [ ] 하드코딩된 문자열 없음 (strings.xml 또는 enum label 사용)
- [ ] 사용하지 않는 import/변수/함수 없음

## Testing (수동 확인)

- [ ] 새 기능에 대응하는 테스트 존재
- [ ] 테스트 네이밍: `should_<expected>_when_<condition>()`
- [ ] DAO 변경 시 in-memory Room 테스트 추가
- [ ] ViewModel 변경 시 StateFlow 테스트 추가

## Compose (수동 확인)

- [ ] Composable은 stateless (상태는 ViewModel에서 관리)
- [ ] Modifier 파라미터에 default 값 (`Modifier`) 제공
- [ ] 파라미터 순서: required data → modifier → callbacks
- [ ] LazyColumn에 `key` 파라미터 사용 (리스트 아이템)

## Data & DB (해당 시)

- [ ] Room 스키마 변경 시 version 번호 증가
- [ ] Migration 코드 작성 (또는 @AutoMigration)
- [ ] 쿼리에 인덱스 필요 여부 확인

## Security & Privacy (해당 시)

- [ ] 개인정보 (이름, 직업 등) 하드코딩 없음
- [ ] API 키, 시크릿 커밋 없음
- [ ] .gitignore에 민감 파일 포함 확인

## AI-Specific Failure Modes

에이전트가 자주 하는 실수를 주의 깊게 확인:

- [ ] **과도한 추상화** — 한 번만 쓰이는 헬퍼/유틸리티 함수 생성 안 했는지
- [ ] **불필요한 에러 핸들링** — 발생 불가능한 시나리오에 대한 try-catch 없는지
- [ ] **문서 드리프트** — 코드 변경에 맞춰 docs/ 업데이트했는지
- [ ] **중복 코드** — 기존 유틸리티/함수 재사용 대신 새로 만들지 않았는지
- [ ] **주석 과잉** — 자명한 코드에 불필요한 주석 없는지
