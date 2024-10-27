# 헥사고날 아키텍처 기반 Java 프로젝트 구조 개요
헥사고날 아키텍처를 채택한 Java 프로젝트의 디렉토리 구조와 각 레이어의 역활을 간략히 소개합니다.

## 프로젝트 디렉토리 구조
```markdown
java
└── com
    └── example
        └── hh3week
            ├── Hh3weekApplication.java
            |
            ├── adapter
            │   ├── driving
            │   │   ├── controller
            │   │   │   ├── ConcertController.java
            │   │   │   ├── PaymentController.java
            │   │   │   ├── ReservationController.java
            │   │   │   └── UserController.java
            │   │   │
            │   │   └── dto
            │   │       ├── concert
            │   │       │   ├── ConcertDto.java
            │   │       │   └── ConcertScheduleDto.java
            │   │       │
            │   │       ├── payment
            │   │       │   └── PaymentHistoryDto.java
            │   │       │
            │   │       ├── reservation
            │   │       │   ├── ReservationSeatDetailDto.java
            │   │       │   └── ReservationSeatDto.java
            │   │       │
            │   │       ├── user
            │   │       │   ├── UserDto.java
            │   │       │   └── UserPointHistoryDto.java
            │   │       │
            │   │       └── validation
            │   │           └── DtoValidation.java  # DTO 검증 로직
            │   │
            │   └── driven
            │       ├── persistence
            │       │   ├── ConcertRepositoryImpl.java
            │       │   ├── PaymentRepositoryImpl.java
            │       │   ├── ReservationSeatDetailRepositoryImpl.java
            │       │   ├── ReservationSeatRepositoryImpl.java
            │       │   └── UserRepositoryImpl.java
            │
            ├── application
            │   ├── port
            │   │   ├── in
            │   │   │   ├── ConcertUseCase.java
            │   │   │   ├── PaymentUseCase.java
            │   │   │   ├── ReservationUseCase.java
            │   │   │   └── UserUseCase.java
            │   │   │
            │   │   └── out
            │   │       ├── ConcertRepositoryPort.java
            │   │       ├── PaymentRepositoryPort.java
            │   │       ├── ReservationSeatRepositoryPort.java
            │   │       └── UserRepositoryPort.java
            │   │
            │   ├── usecase
            │   │   ├── ConcertUseCaseInteractor.java
            │   │   ├── PaymentUseCaseInteractor.java
            │   │   ├── ReservationUseCaseInteractor.java
            │   │   └── UserUseCaseInteractor.java
            │   │
            │   └── service
            │       ├── ConcertService.java
            │       ├── PaymentService.java
            │       ├── ReservationService.java
            │       └── UserService.java
            │
            ├── domain
            │   ├── concert
            │   │   ├── entity
            │   │   │   ├── Concert.java
            │   │   │   ├── ConcertSchedule.java
            │   │   │   └── ConcertScheduleStatus.java
            │   │
            │   ├── payment
            │   │   ├── entity
            │   │   │   ├── PaymentHistory.java
            │   │   │   └── PaymentStatus.java
            │   │
            │   ├── reservation
            │   │   ├── entity
            │   │   │   ├── ReservationSeat.java
            │   │   │   ├── ReservationSeatDetail.java
            │   │   │   └── ReservationStatus.java
            │   │
            │   └── user
            │       ├── entity
            │       │   ├── User.java
            │       │   ├── UserPointHistory.java
            │       │   └── PointStatus.java
            │
            ├── common
            │   ├── config
            │   │   └── QueryDslConfig.java
            │   │
            │   └── util
            │       └── [공통 유틸리티 클래스]
            │
```


# 레이어별 구성
## 1. Adapter 레이어
   - Driving Adapter
     - Controller: 사용자 요청을 처리하는 웹 컨트롤러.
     - DTO: 데이터 전송 객체로, 계층 간 데이터 교환을 담당.
     - Validation: DTO의 데이터 검증 로직. 
   - Driven Adapter
     - Persistence: 데이터베이스와의 상호작용을 담당하는 리포지토리 구현체.
## 2. Application 레이어
   - Port
     - In Port: 도메인에 대한 입력 인터페이스.
     - Out Port: 외부 리소스와의 상호작용을 위한 출력 인터페이스.
   - UseCase
     - Interactor: 비즈니스 로직을 실행하는 유스케이스 구현체.
   - Service
     - 비즈니스 로직과 컨트롤러 간의 중재 역할.
## 3. Domain 레이어
   - Concert, Payment, Reservation, User
     - 각 도메인별 엔티티와 관련 상태 열거형을 포함.
## 4.Common 레이어
   - Config
     - QueryDslConfig.java: QueryDSL 설정.
     - Util
       - 프로젝트 전반에서 사용되는 공통 유틸리티 클래스.


## 요약
본 프로젝트는 헥사고날 아키텍처를 기반으로 명확한 레이어 분리를 통해 유지보수성과 확장성을 높이도록 설계하였습니다.<br>
주요 레이어로는 Adapter, Application, Domain, Common이 있으며, 각 레이어는 특정 역할과 책임을 가지고 있습니다. <br>
이러한 구조는 비즈니스 로직과 기술적 구현을 효과적으로 분리하여, 시스템의 유연성과 테스트 용이성을 향상시키는 기대 효과가 있습니다..