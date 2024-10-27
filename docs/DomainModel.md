## API Specs

1️⃣ **`주요` 유저 대기열 토큰 기능**

- 서비스를 이용할 토큰을 발급받는 API를 작성합니다.
- 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함합니다.
- 이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능합니다.

```mermaid
sequenceDiagram
    actor client as 사용자
    participant Token as Token
    participant user as user
    participant WaitingQueue as WaitingQueue

    client->>Token: 토큰 발급 요청
    activate Token
        Token->>user: 사용자 정보 조회
        activate user
            user-->>Token: 사용자 정보 반환
        deactivate user

        Token->>WaitingQueue: 대기열 정보 조회
        activate WaitingQueue
            WaitingQueue ->>WaitingQueue: 대기열 등록
            WaitingQueue-->>Token: 대기열 정보 반환
        deactivate WaitingQueue

        Token->>Token: JWT 생성 및 토큰 등록
        Token-->>client: 토큰 반환
    deactivate Token
```


**2️⃣ `기본` 예약 가능 날짜 / 좌석 API**

- 예약가능한 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성합니다.
- 예약 가능한 날짜 목록을 조회할 수 있습니다.
- 날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있습니다.

```mermaid
sequenceDiagram
    actor client as 사용자
    participant ConcertSchedule as ConcertSchedule
    participant ReservationSeat as ReservationSeat
    participant ReservationSeatDetail as ReservationSeatDetail

    client->>+ConcertSchedule: 예약 가능한 날짜 요청
    ConcertSchedule-->>-client: 예약 가능한 날짜 반환

    client->>+ReservationSeat: 예약 날짜의 좌석 요청
    ReservationSeat->>ReservationSeat: 남은 좌석 확인

        ReservationSeat->>+ReservationSeatDetail: 좌석 목록 조회
        ReservationSeatDetail-->>-ReservationSeat: 좌석 목록 반환
    
    ReservationSeat-->>-client: 예약가능한 날짜의 좌석 목록 반환
```



3️⃣ **`주요` 좌석 예약 요청 API**

- 날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API 를 작성합니다.
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 약 5분간 임시 배정됩니다. ( 시간은 정책에 따라 자율적으로 정의합니다. )
- 만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되어야 하며 다른 사용자는 예약할 수 없어야 한다.
```mermaid
sequenceDiagram
    actor client as 사용자
    participant ConcertSchedule as ConcertSchedule
    participant ReservationSeat as ReservationSeat
    participant ReservationSeatDetail as ReservationSeatDetail
    participant PaymentHistory as PaymentHistory
    participant User as User
    participant UserPointHistory as UserPointHistory

    %% 좌석 예약 요청
    client->>+ConcertSchedule: 좌석 예약 요청(날짜, 좌석정보)
    activate ConcertSchedule

        ConcertSchedule->>ReservationSeat: 특정 콘서트의 좌석 예약 요청(콘서트seq, 좌석 정보)
        activate ReservationSeat

            ReservationSeat->>ReservationSeatDetail: 좌석 예약 요청(좌석 정보)
            activate ReservationSeatDetail

                ReservationSeatDetail->>ReservationSeatDetail: 5분간 임시 배정 및 타이머 시작
                ReservationSeatDetail-->>client: 임시 좌석 반환

                %% 결제 처리 또는 타이머 만료
                alt 타이머 제한시간내에 결제 성공
                    %% 결제 요청 프로세스
                    client->>+PaymentHistory: 결제 요청
                    activate PaymentHistory

                        PaymentHistory->>+User: 포인트 조회
                        activate User

                            User->>User: 포인트 검증
                            alt 포인트 충분함
                                User->>User: 포인트 차감
                                User->>UserPointHistory: 히스토리 등록
                                activate UserPointHistory
                                UserPointHistory-->>User: 히스토리 등록 완료
                                deactivate UserPointHistory
                                User-->>PaymentHistory: 포인트 차감 완료
                            else 포인트 부족
                                User-->>PaymentHistory: 포인트 부족
                            end
                        deactivate User

                        %% 결제 완료 여부 확인
                        alt 결제 완료
                            PaymentHistory-->>ReservationSeatDetail: 결제 완료 
                            ReservationSeatDetail-->>ReservationSeat: 좌석 배정 완료
                        else 결제 실패
                            PaymentHistory-->>ReservationSeatDetail: 결제 실패
                            ReservationSeatDetail->>ReservationSeatDetail: 임시 배정 해제
                            ReservationSeatDetail-->>ReservationSeat: 임시 배정 해제 완료
                        end

                    deactivate PaymentHistory
                else 타이머 만료
                    %% 타이머가 만료되었을 때 좌석 임시 배정 해제
                    ReservationSeatDetail->>ReservationSeatDetail: 임시 배정 해제
                    ReservationSeatDetail-->>ReservationSeat: 임시 배정 해제 완료
                end

            deactivate ReservationSeatDetail

            %% 좌석 예약 상태 업데이트
            ReservationSeat-->>ConcertSchedule: 좌석 예약 상태 업데이트
        deactivate ReservationSeat

    %% 좌석 예약 반환 메시지
    ConcertSchedule-->>client: 좌석 예약 반환
    deactivate ConcertSchedule

```


4️⃣ **`기본`**  **잔액 충전 / 조회 API**

- 결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성합니다.
- 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.

```mermaid
sequenceDiagram
    actor client as 사용자
    participant User as User
    participant UserPointHistory as UserPointHistory

%% 충전 요청
    client->>+User: 충전 요청(충전 금액)
    activate User

    User->>User: 충전 금액 검증
    alt 충전 성공
        User->>User: 충전 처리
        User->>+UserPointHistory: 충전 히스토리 등록
        UserPointHistory-->>User: 히스토리 등록 완료
    else 충전 실패
        User-->>client: 충전 실패 메시지 반환
    end

%% 충전 상태 반환
    User-->>client: 충전 완료 상태 반환
    deactivate User

```

5️⃣ **`주요` 결제 API**

- 결제 처리하고 결제 내역을 생성하는 API 를 작성합니다.
- 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료시킵니다.

```mermaid
sequenceDiagram
    actor client as 사용자
    participant User as User
    participant ReservationSeatDetail as ReservationSeatDetail
    participant WaitingQueue as WaitingQueue
    participant PaymentHistory as PaymentHistory

%% 결제 요청
    client->>+User: 결제 요청
    activate User

%% 결제 처리
    User->>User: 결제 처리

    alt 결제 성공
    %% 결제 내역 생성
        User->>+PaymentHistory: 결제 내역 생성
        PaymentHistory-->>User: 결제 내역 생성 완료

    %% 좌석 소유권 배정
        User->>+ReservationSeatDetail: 좌석 소유권 배정
        ReservationSeatDetail-->>User: 소유권 배정 완료

    %% 대기열 토큰 만료
        User->>+WaitingQueue: 대기열 토큰 만료 처리
        WaitingQueue-->>User: 토큰 만료 완료

        User-->>client: 결제 성공 메시지 반환
    else 결제 실패
        User-->>client: 결제 실패 메시지 반환
    end

    deactivate User
```