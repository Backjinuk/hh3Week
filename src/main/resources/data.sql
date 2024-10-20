-- User 더미 데이터
INSERT INTO user (user_id, user_name, point_balance)
VALUES
    (1, '유저 A', 100000),
    (2, '유저 B', 50000),
    (3, '유저 C', 20000),
   (101, '유저 C', 20000);

-- src/test/resources/test-data-payment-history.sql

-- PaymentHistory 더미 데이터
INSERT INTO payment_history (user_id, reservation_id, payment_amount, payment_dt, payment_status)
VALUES
    (101, 1001, 10000, '2024-01-01 10:00:00', 'COMPLETED'),
    (101, 1002, 5000, '2024-02-01 11:00:00', 'FAILED'),
    (102, 1003, 20000, '2024-03-01 12:00:00', 'COMPLETED');


-- Concert 더미 데이터
INSERT INTO concert (concert_id, concert_name, concert_content)
VALUES
    (1, '콘서트 A', '콘서트 A의 내용'),
    (2, '콘서트 B', '콘서트 B의 내용'),
    (3, '콘서트 C', '콘서트 C의 내용');

-- src/test/resources/test-data-token.sql

-- Token 더미 데이터
INSERT INTO token (token_id, user_id, token, issued_at, expires_at)
VALUES
    (1, 101, 'token123', '2024-01-01 10:00:00', '2025-01-12 10:00:00'),
    (2, 102, 'token456', '2024-01-01 10:00:00', '2024-01-01 10:00:00'); -- 이미 만료된 토큰


-- ConcertSchedule 더미 데이터
INSERT INTO concert_schedule (concert_schedule_id, concert_id, concert_schedule_status, concert_price, start_dt, end_dt)
VALUES
    (1, 1, 'AVAILABLE', 10000, '2024-01-01 19:00:00', '2024-01-01 22:00:00'),
    (2, 2, 'AVAILABLE', 15000, '2024-02-01 19:00:00', '2024-02-01 22:00:00'),
    (3, 3, 'AVAILABLE', 20000, '2024-03-01 19:00:00', '2024-03-01 22:00:00');


INSERT INTO user_point_history (user_id, point_amount, point_status, point_dt)
VALUES
    (1, 10000, 'EARN', '2024-01-05 10:00:00'),
    (1, 5000, 'USE', '2024-01-10 15:30:00'),
    (1, 20000, 'EARN', '2024-02-14 09:45:00'),
    (1, 7000, 'USE', '2024-03-01 18:20:00'),
    (1, 15000, 'EARN', '2024-04-22 12:00:00');

-- 사용자 2의 포인트 히스토리
INSERT INTO user_point_history (user_id, point_amount, point_status, point_dt)
VALUES
    (2, 8000, 'EARN', '2024-01-08 11:15:00'),
    (2, 3000, 'USE', '2024-01-20 14:50:00'),
    (2, 12000, 'EARN', '2024-02-25 16:40:00'),
    (2, 4000, 'USE', '2024-03-15 20:10:00'),
    (2, 18000, 'EARN', '2024-04-30 08:30:00');

-- 사용자 3의 포인트 히스토리
INSERT INTO user_point_history (user_id, point_amount, point_status, point_dt)
VALUES
    (3, 5000, 'EARN', '2024-01-12 09:00:00'),
    (3, 2500, 'USE', '2024-01-25 17:25:00'),
    (3, 10000, 'EARN', '2024-02-18 13:55:00'),
    (3, 6000, 'USE', '2024-03-22 19:35:00'),
    (3, 14000, 'EARN', '2024-04-10 10:45:00');


-- src/test/resources/test-data-waiting-queue.sql

-- WaitingQueue 더미 데이터
INSERT INTO waiting_queue (waiting_id, user_id, seat_detail_id, reservation_dt, waiting_status, priority)
VALUES
    (1, 101, 1, '2024-01-01 10:00:00', 'WAITING', 5),
    (2, 102, 1, '2024-01-01 10:05:00', 'WAITING', 3),
    (3, 103, 2, '2024-01-02 11:00:00', 'SERVED', 4);


-- ReservationSeat 더미 데이터
INSERT INTO reservation_seat (seat_id, concert_id, max_capacity, current_reserved)
VALUES
    (1, 1, 50, 0),  -- 콘서트 1에 대한 좌석 정보
    (2, 2, 50, 0),   -- 콘서트 2에 대한 좌석 정보
    (3, 3, 50, 0);   -- 콘서트 3에 대한 좌석 정보


-- 콘서트 1의 좌석 예약 상세 데이터 (50석), user_id는 비어 있고, seat_id는 모두 1로 설정
INSERT INTO reservation_seat_detail (seat_detail_id, user_id, seat_id, seat_code, reservation_status, seat_price)
VALUES
    (1, 0, 1, 'A1', 'AVAILABLE', 10000),
    (2, 0, 1, 'A2', 'AVAILABLE', 10000),
    (3, 0, 1, 'A3', 'AVAILABLE', 15000),
    (4, 0, 1, 'A4', 'AVAILABLE', 15000),
    (5, 0, 1, 'A5', 'AVAILABLE', 20000),
    (6, 0, 1, 'A6', 'AVAILABLE', 20000),
    (7, 0, 1, 'A7', 'AVAILABLE', 10000),
    (8, 0, 1, 'A8', 'AVAILABLE', 10000),
    (9, 0, 1, 'A9', 'AVAILABLE', 15000),
    (10, 0, 1, 'A10', 'AVAILABLE', 15000),
    (11, 0, 1, 'A11', 'AVAILABLE', 20000),
    (12, 0, 1, 'A12', 'AVAILABLE', 20000),
    (13, 0, 1, 'A13', 'AVAILABLE', 10000),
    (14, 0, 1, 'A14', 'AVAILABLE', 10000),
    (15, 0, 1, 'A15', 'AVAILABLE', 15000),
    (16, 0, 1, 'A16', 'AVAILABLE', 15000),
    (17, 0, 1, 'A17', 'AVAILABLE', 20000),
    (18, 0, 1, 'A18', 'AVAILABLE', 20000),
    (19, 0, 1, 'A19', 'AVAILABLE', 10000),
    (20, 0, 1, 'A20', 'AVAILABLE', 10000),
    (21, 0, 1, 'A21', 'AVAILABLE', 15000),
    (22, 0, 1, 'A22', 'AVAILABLE', 15000),
    (23, 0, 1, 'A23', 'AVAILABLE', 20000),
    (24, 0, 1, 'A24', 'AVAILABLE', 20000),
    (25, 0, 1, 'A25', 'AVAILABLE', 10000),
    (26, 0, 1, 'A26', 'AVAILABLE', 10000),
    (27, 0, 1, 'A27', 'AVAILABLE', 15000),
    (28, 0, 1, 'A28', 'AVAILABLE', 15000),
    (29, 0, 1, 'A29', 'AVAILABLE', 20000),
    (30, 0, 1, 'A30', 'AVAILABLE', 20000),
    (31, 0, 1, 'A31', 'AVAILABLE', 10000),
    (32, 0, 1, 'A32', 'AVAILABLE', 10000),
    (33, 0, 1, 'A33', 'AVAILABLE', 15000),
    (34, 0, 1, 'A34', 'AVAILABLE', 15000),
    (35, 0, 1, 'A35', 'AVAILABLE', 20000),
    (36, 0, 1, 'A36', 'AVAILABLE', 20000),
    (37, 0, 1, 'A37', 'AVAILABLE', 10000),
    (38, 0, 1, 'A38', 'AVAILABLE', 10000),
    (39, 0, 1, 'A39', 'AVAILABLE', 15000),
    (40, 0, 1, 'A40', 'AVAILABLE', 15000),
    (41, 0, 1, 'A41', 'AVAILABLE', 20000),
    (42, 0, 1, 'A42', 'AVAILABLE', 20000),
    (43, 0, 1, 'A43', 'AVAILABLE', 10000),
    (44, 0, 1, 'A44', 'AVAILABLE', 10000),
    (45, 0, 1, 'A45', 'AVAILABLE', 15000),
    (46, 0, 1, 'A46', 'AVAILABLE', 15000),
    (47, 0, 1, 'A47', 'AVAILABLE', 20000),
    (48, 0, 1, 'A48', 'AVAILABLE', 20000),
    (49, 0, 1, 'A49', 'AVAILABLE', 10000),
    (50, 0, 1, 'A50', 'AVAILABLE', 10000);



-- 콘서트 2의 B좌석 예약 상세 데이터 (50석), user_id는 비어 있고, seat_id는 모두 2로 설정
INSERT INTO reservation_seat_detail (seat_detail_id, user_id, seat_id, seat_code, reservation_status, seat_price)
VALUES
    (51, 0, 2, 'B1', 'AVAILABLE', 10000),
    (52, 0, 2, 'B2', 'AVAILABLE', 10000),
    (53, 0, 2, 'B3', 'AVAILABLE', 15000),
    (54, 0, 2, 'B4', 'AVAILABLE', 15000),
    (55, 0, 2, 'B5', 'AVAILABLE', 20000),
    (56, 0, 2, 'B6', 'AVAILABLE', 20000),
    (57, 0, 2, 'B7', 'AVAILABLE', 10000),
    (58, 0, 2, 'B8', 'AVAILABLE', 10000),
    (59, 0, 2, 'B9', 'AVAILABLE', 15000),
    (60, 0, 2, 'B10', 'AVAILABLE', 15000),
    (61, 0, 2, 'B11', 'AVAILABLE', 20000),
    (62, 0, 2, 'B12', 'AVAILABLE', 20000),
    (63, 0, 2, 'B13', 'AVAILABLE', 10000),
    (64, 0, 2, 'B14', 'AVAILABLE', 10000),
    (65, 0, 2, 'B15', 'AVAILABLE', 15000),
    (66, 0, 2, 'B16', 'AVAILABLE', 15000),
    (67, 0, 2, 'B17', 'AVAILABLE', 20000),
    (68, 0, 2, 'B18', 'AVAILABLE', 20000),
    (69, 0, 2, 'B19', 'AVAILABLE', 10000),
    (70, 0, 2, 'B20', 'AVAILABLE', 10000),
    (71, 0, 2, 'B21', 'AVAILABLE', 15000),
    (72, 0, 2, 'B22', 'AVAILABLE', 15000),
    (73, 0, 2, 'B23', 'AVAILABLE', 20000),
    (74, 0, 2, 'B24', 'AVAILABLE', 20000),
    (75, 0, 2, 'B25', 'AVAILABLE', 10000),
    (76, 0, 2, 'B26', 'AVAILABLE', 10000),
    (77, 0, 2, 'B27', 'AVAILABLE', 15000),
    (78, 0, 2, 'B28', 'AVAILABLE', 15000),
    (79, 0, 2, 'B29', 'AVAILABLE', 20000),
    (80, 0, 2, 'B30', 'AVAILABLE', 20000),
    (81, 0, 2, 'B31', 'AVAILABLE', 10000),
    (82, 0, 2, 'B32', 'AVAILABLE', 10000),
    (83, 0, 2, 'B33', 'AVAILABLE', 15000),
    (84, 0, 2, 'B34', 'AVAILABLE', 15000),
    (85, 0, 2, 'B35', 'AVAILABLE', 20000),
    (86, 0, 2, 'B36', 'AVAILABLE', 20000),
    (87, 0, 2, 'B37', 'AVAILABLE', 10000),
    (88, 0, 2, 'B38', 'AVAILABLE', 10000),
    (89, 0, 2, 'B39', 'AVAILABLE', 15000),
    (90, 0, 2, 'B40', 'AVAILABLE', 15000),
    (91, 0, 2, 'B41', 'AVAILABLE', 20000),
    (92, 0, 2, 'B42', 'AVAILABLE', 20000),
    (93, 0, 2, 'B43', 'AVAILABLE', 10000),
    (94, 0, 2, 'B44', 'AVAILABLE', 10000),
    (95, 0, 2, 'B45', 'AVAILABLE', 15000),
    (96, 0, 2, 'B46', 'AVAILABLE', 15000),
    (97, 0, 2, 'B47', 'AVAILABLE', 20000),
    (98, 0, 2, 'B48', 'AVAILABLE', 20000),
    (99, 0, 2, 'B49', 'AVAILABLE', 10000),
    (100, 0, 2, 'B50', 'AVAILABLE', 10000);


-- 콘서트 3의 C좌석 예약 상세 데이터 (50석), user_id는 비어 있고, seat_id는 모두 3으로 설정
INSERT INTO reservation_seat_detail (seat_detail_id, user_id, seat_id, seat_code, reservation_status, seat_price)
VALUES
    (101, 0, 3, 'C1', 'AVAILABLE', 10000),
    (102, 0, 3, 'C2', 'AVAILABLE', 10000),
    (103, 0, 3, 'C3', 'AVAILABLE', 15000),
    (104, 0, 3, 'C4', 'AVAILABLE', 15000),
    (105, 0, 3, 'C5', 'AVAILABLE', 20000),
    (106, 0, 3, 'C6', 'AVAILABLE', 20000),
    (107, 0, 3, 'C7', 'AVAILABLE', 10000),
    (108, 0, 3, 'C8', 'AVAILABLE', 10000),
    (109, 0, 3, 'C9', 'AVAILABLE', 15000),
    (110, 0, 3, 'C10', 'AVAILABLE', 15000),
    (111, 0, 3, 'C11', 'AVAILABLE', 20000),
    (112, 0, 3, 'C12', 'AVAILABLE', 20000),
    (113, 0, 3, 'C13', 'AVAILABLE', 10000),
    (114, 0, 3, 'C14', 'AVAILABLE', 10000),
    (115, 0, 3, 'C15', 'AVAILABLE', 15000),
    (116, 0, 3, 'C16', 'AVAILABLE', 15000),
    (117, 0, 3, 'C17', 'AVAILABLE', 20000),
    (118, 0, 3, 'C18', 'AVAILABLE', 20000),
    (119, 0, 3, 'C19', 'AVAILABLE', 10000),
    (120, 0, 3, 'C20', 'AVAILABLE', 10000),
    (121, 0, 3, 'C21', 'AVAILABLE', 15000),
    (122, 0, 3, 'C22', 'AVAILABLE', 15000),
    (123, 0, 3, 'C23', 'AVAILABLE', 20000),
    (124, 0, 3, 'C24', 'AVAILABLE', 20000),
    (125, 0, 3, 'C25', 'AVAILABLE', 10000),
    (126, 0, 3, 'C26', 'AVAILABLE', 10000),
    (127, 0, 3, 'C27', 'AVAILABLE', 15000),
    (128, 0, 3, 'C28', 'AVAILABLE', 15000),
    (129, 0, 3, 'C29', 'AVAILABLE', 20000),
    (130, 0, 3, 'C30', 'AVAILABLE', 20000),
    (131, 0, 3, 'C31', 'AVAILABLE', 10000),
    (132, 0, 3, 'C32', 'AVAILABLE', 10000),
    (133, 0, 3, 'C33', 'AVAILABLE', 15000),
    (134, 0, 3, 'C34', 'AVAILABLE', 15000),
    (135, 0, 3, 'C35', 'AVAILABLE', 20000),
    (136, 0, 3, 'C36', 'AVAILABLE', 20000),
    (137, 0, 3, 'C37', 'AVAILABLE', 10000),
    (138, 0, 3, 'C38', 'AVAILABLE', 10000),
    (139, 0, 3, 'C39', 'AVAILABLE', 15000),
    (140, 0, 3, 'C40', 'AVAILABLE', 15000),
    (141, 0, 3, 'C41', 'AVAILABLE', 20000),
    (142, 0, 3, 'C42', 'AVAILABLE', 20000),
    (143, 0, 3, 'C43', 'AVAILABLE', 10000),
    (144, 0, 3, 'C44', 'AVAILABLE', 10000),
    (145, 0, 3, 'C45', 'AVAILABLE', 15000),
    (146, 0, 3, 'C46', 'AVAILABLE', 15000),
    (147, 0, 3, 'C47', 'AVAILABLE', 20000),
    (148, 0, 3, 'C48', 'AVAILABLE', 20000),
    (149, 0, 3, 'C49', 'AVAILABLE', 10000),
    (150, 0, 3, 'C50', 'AVAILABLE', 10000);
