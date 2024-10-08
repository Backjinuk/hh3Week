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