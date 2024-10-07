**`기본`**  **잔액 충전 / 조회 API**

- 결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성합니다.
- 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.
```mermaid
graph TD
    User -->|충전 요청| UserPointHistory
    UserPointHistory -->|포인트 추가| User
    UserPointHistory -->|충전 완료 통보| User

    User -->|잔액 조회 요청| UserPointHistory
    UserPointHistory -->|잔액 조회 결과| User
```

**`기본` 예약 가능 날짜 / 좌석 API**

- 예약가능한 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성합니다.
- 예약 가능한 날짜 목록을 조회할 수 있습니다.
- 날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있습니다.

> 좌석 정보는 1 ~ 50 까지의 좌석번호로 관리됩니다.

```mermaid
graph TD
    User -->| 예약가능한 날짜,좌석 요청| Consert
    Consert --> |예약가능한 날짜 조회후 좌석 정보 요청 | ConsertSchedule
    ConsertSchedule --> | 예약가능한 좌석 정보 응답 | Consert 
    Consert --> |예약가능한 날짜,좌석 응답 | User


```