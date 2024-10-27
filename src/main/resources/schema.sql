
-- 2. 모든 테이블 삭제
DROP TABLE IF EXISTS waiting_queue;
DROP TABLE IF EXISTS token;
DROP TABLE IF EXISTS concert_schedule;
DROP TABLE IF EXISTS concert;
DROP TABLE IF EXISTS reservation_seat_detail;
DROP TABLE IF EXISTS reservation_seat;
DROP TABLE IF EXISTS payment_history;
DROP TABLE IF EXISTS user_point_history;
DROP TABLE IF EXISTS user;

-- 3. 외래 키 검사 활성화 (필요 시)
SET FOREIGN_KEY_CHECKS = 1;

-- 4. 테이블 생성

-- User 테이블
CREATE TABLE user (
                      user_id INT PRIMARY KEY AUTO_INCREMENT,
                      user_name VARCHAR(100) NOT NULL,
                      point_balance BIGINT NOT NULL
);

-- PointHistory 테이블
CREATE TABLE user_point_history (
                               history_id INT PRIMARY KEY AUTO_INCREMENT,
                               user_id INT,
                               point_amount BIGINT NOT NULL,
                               point_status VARCHAR(50) NOT NULL,
                               point_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PaymentHistory 테이블
CREATE TABLE payment_history (
                                 payment_id INT PRIMARY KEY AUTO_INCREMENT,
                                 user_id INT,
                                 reservation_id INT,
                                 payment_amount BIGINT NOT NULL,
                                 payment_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 payment_status VARCHAR(50) NOT NULL

);

-- ReservationSeat 테이블
CREATE TABLE reservation_seat (
                                  seat_id INT PRIMARY KEY AUTO_INCREMENT,
                                  concert_id INT,
                                  max_capacity INT NOT NULL,
                                  current_reserved INT NOT NULL
);

-- ReservationSeatDetail 테이블
CREATE TABLE reservation_seat_detail (
                                         seat_detail_id INT PRIMARY KEY AUTO_INCREMENT,
                                         user_id INT,
                                         seat_id INT,
                                         seat_code VARCHAR(10) NOT NULL,
                                         reservation_status VARCHAR(50) NOT NULL,
                                         seat_price FLOAT NOT NULL
);

-- Concert 테이블
CREATE TABLE concert (
                         concert_id INT PRIMARY KEY AUTO_INCREMENT,
                         concert_name VARCHAR(255) NOT NULL,
                         concert_content TEXT NOT NULL
);

-- ConcertSchedule 테이블
CREATE TABLE concert_schedule (
                                  concert_schedule_id INT PRIMARY KEY AUTO_INCREMENT,
                                  concert_id INT,
                                  concert_schedule_status VARCHAR(50) NOT NULL,
                                  concert_price BIGINT NOT NULL,
                                  start_dt TIMESTAMP NOT NULL,
                                  end_dt TIMESTAMP NOT NULL
);

-- Token 테이블
CREATE TABLE token (
                       token_id INT PRIMARY KEY AUTO_INCREMENT,
                       user_id INT,
                       token VARCHAR(255) NOT NULL,
                       issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       expires_at TIMESTAMP NOT NULL
);

-- WaitingQueue 테이블
CREATE TABLE waiting_queue (
                               waiting_id INT PRIMARY KEY AUTO_INCREMENT,
                               user_id INT,
                               seat_detail_id INT,
                               reservation_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               waiting_status VARCHAR(50) NOT NULL,
                               priority INT NOT NULL
                           );
