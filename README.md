# 콘서트 예약 서비스
* * *
- 
- `콘서트 예약 서비스`를 구현해 봅니다.
- 대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있도록 해야합니다.
- 사용자는 좌석예약 시에 미리 충전한 잔액을 이용합니다.
- 좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 합니다.

> ## 프로젝트 아케텍쳐 <br>
>프로젝트 아케텍쳐는 [프로젝트 아키텍쳐 바로가기](./docs/Architecture.md) 에서 확인하실 수 있습니다.

> ## MileStone <br>
> MileStone 은 <a href="https://github.com/users/Backjinuk/projects/5/views/1">Github Projects</a>에서 확인하실 수 있습니다.

> ## DB락 과 Redis락의 성능 분석록 <br>
> DB락 과 Redis락의 성능 분석록은 <a href="https://velog.io/@backj123/DB%EB%9D%BD%EA%B3%BC-Redis%EB%9D%BD-%EB%B6%84%EC%84%9D%EB%A1%9D">블로그 바로가기</a>에서 확인하실 수 있습니다. <br>
> 
> Redis에 관한 분석록은 <a href="https://velog.io/@backj123/Redis%EB%A1%9C-%EB%8C%80%EA%B8%B0%EC%97%B4-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0">블로그 바로가기</a>에서 확인하실 수 있습니다.


## 목차
- - -
[1. 요구사항 별 시퀸스 다이어그램](#1-요구사항_별-시퀸스-다이어-그램)

[2. ERD](#2-ERD)

[3. API 명세서](#3-api-명세서--swagger-ui)

[4. 기술스택](#4-기술스택)


* * *
## 1. 요구사항 별 시퀸스 다이어그램/ 클래스 다이어그램
> 요구사항 별 시퀸스 다이어그램은 [여기에서](./docs/SequenceDiagram.md) 확인하실 수 있습니다.<br>
* * *

## 2. ERD
```mermaid
erDiagram
  User {
    int user_id PK
    string user_name
    long point_balance
  }

  UserPointHistory {
    int history_id PK
    int user_id FK
    long point_amount
    string point_status
    timestamp point_date
  }

  PaymentHistory {
    int payment_id PK
    int user_id FK
    int reservation_id FK
    long payment_amount
    string payment_status
  }

  ReservationSeat {
    int seat_id PK
    int concert_schedule_id FK
    int max_capacity
    int current_reserved
    string seat_status
  }

  ReservationSeatDetail {
    int seat_detail_id PK
    int user_id FK
    int seat_id FK
    string reservation_status
    float seat_price
  }

  Concert {
    int concert_id PK
    string concert_name
    string concert_content
  }

  ConcertSchedule {
    int concert_schedule_id PK
    int concert_id FK
    string reservable_status
    long ticket_price
    timestamp start_dt
    timestamp end_dt
  }

  Token {
    string token_id PK
    int user_id FK
    string token
    timestamp issued_at
    timestamp expires_at
  }

  WaitingQueue {
    int queue_id PK
    int user_id FK
    int concert_schedule_id FK
    int position
    string queue_token
  }

  User ||--o{ UserPointHistory : ""
  User ||--o{ PaymentHistory : ""
  User ||--o{ ReservationSeatDetail : ""
  User ||--o{ WaitingQueue : ""
  User ||--o{ Token : ""

  Concert ||--o{ ConcertSchedule : ""
  ConcertSchedule ||--o{ ReservationSeat : ""
  ConcertSchedule ||--o{ WaitingQueue : ""

  ReservationSeat ||--o{ ReservationSeatDetail : ""
  ReservationSeatDetail ||--o{ PaymentHistory : ""

```
* * *

## 3. API 명세서 / Swagger Ui
> Swagger Ui 명세서는 [여기에서](./docs/SwaggerUi.md) 확인하실 수 있습니다.<br>
> 
> API 명세서는 [여기에서](./docs/ApiSpac.md) 확인하실 수 있습니다.<br>
>
> http 파일은 [여기에서](./src/main/resources/http/Mock-API.http) 확인하실 수 있습니다.
* * *


## 4. 기술스택
### 1. Web Application Server
- Java 17
- Spring Boot
- Spring Web
- Spring Security
- JWT (Json Web Token)
### 2. Database
- H2 (Domain)
- Spring Data JPA
- QueryDSL
### 3. Caching
- Redis (Caching)
### 5. Monitoring System
- Prometheus
### 6. Testing
Spring Boot Test