# Phase 5 — Advanced

## Overview

이주 준비를 실질적으로 돕는 고급 기능을 추가한다.
비용 관리, 연락처, 환율 계산, 체크리스트 내보내기를 통해
사용자가 이주 전 과정을 하나의 앱에서 관리할 수 있도록 한다.

---

## 5.1 비용 트래커

**User Story**: 이주 관련 지출을 기록하고 총 비용을 파악하고 싶다.

**구현 사항**:
- Expense entity (id, title, amount, currency, category, date, note)
- 대시보드에서 접근 가능한 비용 관리 화면
- 카테고리별 지출 합계 표시
- KRW/USD 통화 선택
- 지출 추가/삭제

**레이아웃** (ExpenseScreen):
```
┌─────────────────────────────┐
│ ← 비용 관리                  │
├─────────────────────────────┤
│ 총 지출                      │
│ ₩ 5,230,000 / $ 4,150       │
├─────────────────────────────┤
│ 📋 서류 & 행정        ₩350,000│
│ 🏠 주거             $2,400   │
│ 🚗 교통             $1,200   │
│ 💰 재정              ₩80,000 │
├─────────────────────────────┤
│ 최근 지출                    │
│ ┌───────────────────────┐   │
│ │ 비자 신청비    ₩350,000│   │
│ │ 📋 서류  2026-03-05   │   │
│ │                  [🗑] │   │
│ └───────────────────────┘   │
│                       [+]   │
└─────────────────────────────┘
```

**Data Model**:
```kotlin
// domain/model/Expense.kt
data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val currency: String = "KRW",  // "KRW" or "USD"
    val category: Category,
    val date: String = "",
    val note: String = "",
    val createdAt: String = "",
)
```

**DB 변경**: version 3 → 4, ExpenseEntity 추가.

**테스트**:
- `should_map_expense_entity_to_domain()`
- `should_map_expense_domain_to_entity()`
- `should_calculate_total_by_currency()`
- `should_group_expenses_by_category()`

---

## 5.2 연락처 관리

**User Story**: 이주 관련 주요 연락처(변호사, 부동산, 은행 등)를 정리하고 싶다.

**구현 사항**:
- Contact entity (id, name, role, phone, email, note)
- 연락처 목록 + 추가/삭제
- 역할별 아이콘 표시
- 전화/이메일 인텐트 연동

**레이아웃** (ContactScreen):
```
┌─────────────────────────────┐
│ ← 연락처                     │
├─────────────────────────────┤
│ ⚖️ 이민 변호사                │
│    김변호사  010-1234-5678    │
│    kim@law.com         [🗑]  │
├─────────────────────────────┤
│ 🏠 부동산 에이전트             │
│    John Smith  555-1234      │
│    john@realty.com     [🗑]  │
├─────────────────────────────┤
│                        [+]  │
└─────────────────────────────┘
```

**Data Model**:
```kotlin
// domain/model/Contact.kt
data class Contact(
    val id: Long = 0,
    val name: String,
    val role: String = "",
    val phone: String = "",
    val email: String = "",
    val note: String = "",
    val createdAt: String = "",
)
```

**테스트**:
- `should_map_contact_entity_to_domain()`
- `should_map_contact_domain_to_entity()`
- `should_roundtrip_contact()`

---

## 5.3 환율 계산기

**User Story**: KRW ↔ USD 환율을 빠르게 계산하고 싶다.

**구현 사항**:
- 간단한 환율 계산 화면
- 금액 입력 → 변환 결과 표시
- 기본 환율 수동 설정 (사용자가 직접 입력)
- DataStore에 마지막 환율 저장

**레이아웃**:
```
┌─────────────────────────────┐
│ ← 환율 계산기                 │
├─────────────────────────────┤
│ 기준 환율: 1 USD = [1,350] KRW│
├─────────────────────────────┤
│ USD → KRW                    │
│ [1,000    ]  →  ₩ 1,350,000 │
├─────────────────────────────┤
│ KRW → USD                    │
│ [1,000,000]  →  $ 740.74    │
└─────────────────────────────┘
```

**테스트**:
- `should_convert_usd_to_krw()`
- `should_convert_krw_to_usd()`
- `should_handle_zero_amount()`
- `should_handle_custom_rate()`

---

## 5.4 PDF Export

**User Story**: 체크리스트 현황을 PDF로 내보내 공유하고 싶다.

**구현 사항**:
- Android PdfDocument API 사용 (외부 라이브러리 불필요)
- 카테고리별 진행률 + 전체 항목 목록 포함
- 공유 인텐트로 다른 앱에 전달
- 대시보드에 "내보내기" 버튼

**테스트**:
- `should_format_task_for_export()`
- `should_calculate_export_stats()`

---

## 구현 순서

1. **5.1 비용 트래커** — 새 entity + 화면, Note 패턴 활용
2. **5.2 연락처 관리** — 새 entity + 화면, 유사 패턴
3. **5.3 환율 계산기** — 단일 화면, DataStore 연동
4. **5.4 PDF Export** — Android API, 공유 인텐트

---

## Acceptance Criteria

- [ ] 비용을 추가/삭제하고 카테고리별 합계를 볼 수 있다
- [ ] 연락처를 추가/삭제하고 전화/이메일을 실행할 수 있다
- [ ] 환율 계산기에서 KRW ↔ USD 변환이 동작한다
- [ ] 체크리스트를 PDF로 내보내 공유할 수 있다
- [ ] 모든 새 기능에 대한 테스트가 존재한다
