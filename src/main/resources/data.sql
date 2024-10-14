-- User 더미 데이터
INSERT INTO user (user_id, user_name, point_balance)
VALUES
    (1, '유저 A', 100000),
    (2, '유저 B', 50000),
    (3, '유저 C', 20000);

-- Concert 더미 데이터
INSERT INTO concert (concert_id, concert_name, concert_content)
VALUES
    (1, '콘서트 A', '콘서트 A의 내용'),
    (2, '콘서트 B', '콘서트 B의 내용'),
    (3, '콘서트 C', '콘서트 C의 내용');


-- ConcertSchedule 더미 데이터
INSERT INTO concert_schedule (concert_schedule_id, concert_id, concert_schedule_status, concert_price, start_dt, end_dt)
VALUES
    (1, 1, 'AVAILABLE', 10000, '2024-01-01 19:00:00', '2024-01-01 22:00:00'),
    (2, 2, 'AVAILABLE', 15000, '2024-02-01 19:00:00', '2024-02-01 22:00:00'),
    (3, 3, 'AVAILABLE', 20000, '2024-03-01 19:00:00', '2024-03-01 22:00:00');



-- ReservationSeat 더미 데이터
INSERT INTO reservation_seat (seat_id, concert_id, max_capacity, current_reserved)
VALUES
    (1, 1, 50, 0),  -- 콘서트 1에 대한 좌석 정보
    (2, 2, 50, 0),   -- 콘서트 2에 대한 좌석 정보
    (3, 3, 50, 0);   -- 콘서트 3에 대한 좌석 정보


-- 콘서트 1의 좌석 예약 상세 데이터 (50석), user_id는 비어 있고, seat_id는 모두 1로 설정
INSERT INTO reservation_seat_detail (reservation_id, user_id, seat_id, seat_number, reservation_status, payment_amount)
VALUES
    (1, NULL, 1, 'A1', 'AVAILABLE', 10000),
    (2, NULL, 1, 'A2', 'AVAILABLE', 10000),
    (3, NULL, 1, 'A3', 'AVAILABLE', 15000),
    (4, NULL, 1, 'A4', 'AVAILABLE', 15000),
    (5, NULL, 1, 'A5', 'AVAILABLE', 20000),
    (6, NULL, 1, 'A6', 'AVAILABLE', 20000),
    (7, NULL, 1, 'A7', 'AVAILABLE', 10000),
    (8, NULL, 1, 'A8', 'AVAILABLE', 10000),
    (9, NULL, 1, 'A9', 'AVAILABLE', 15000),
    (10, NULL, 1, 'A10', 'AVAILABLE', 15000),
    (11, NULL, 1, 'A11', 'AVAILABLE', 20000),
    (12, NULL, 1, 'A12', 'AVAILABLE', 20000),
    (13, NULL, 1, 'A13', 'AVAILABLE', 10000),
    (14, NULL, 1, 'A14', 'AVAILABLE', 10000),
    (15, NULL, 1, 'A15', 'AVAILABLE', 15000),
    (16, NULL, 1, 'A16', 'AVAILABLE', 15000),
    (17, NULL, 1, 'A17', 'AVAILABLE', 20000),
    (18, NULL, 1, 'A18', 'AVAILABLE', 20000),
    (19, NULL, 1, 'A19', 'AVAILABLE', 10000),
    (20, NULL, 1, 'A20', 'AVAILABLE', 10000),
    (21, NULL, 1, 'A21', 'AVAILABLE', 15000),
    (22, NULL, 1, 'A22', 'AVAILABLE', 15000),
    (23, NULL, 1, 'A23', 'AVAILABLE', 20000),
    (24, NULL, 1, 'A24', 'AVAILABLE', 20000),
    (25, NULL, 1, 'A25', 'AVAILABLE', 10000),
    (26, NULL, 1, 'A26', 'AVAILABLE', 10000),
    (27, NULL, 1, 'A27', 'AVAILABLE', 15000),
    (28, NULL, 1, 'A28', 'AVAILABLE', 15000),
    (29, NULL, 1, 'A29', 'AVAILABLE', 20000),
    (30, NULL, 1, 'A30', 'AVAILABLE', 20000),
    (31, NULL, 1, 'A31', 'AVAILABLE', 10000),
    (32, NULL, 1, 'A32', 'AVAILABLE', 10000),
    (33, NULL, 1, 'A33', 'AVAILABLE', 15000),
    (34, NULL, 1, 'A34', 'AVAILABLE', 15000),
    (35, NULL, 1, 'A35', 'AVAILABLE', 20000),
    (36, NULL, 1, 'A36', 'AVAILABLE', 20000),
    (37, NULL, 1, 'A37', 'AVAILABLE', 10000),
    (38, NULL, 1, 'A38', 'AVAILABLE', 10000),
    (39, NULL, 1, 'A39', 'AVAILABLE', 15000),
    (40, NULL, 1, 'A40', 'AVAILABLE', 15000),
    (41, NULL, 1, 'A41', 'AVAILABLE', 20000),
    (42, NULL, 1, 'A42', 'AVAILABLE', 20000),
    (43, NULL, 1, 'A43', 'AVAILABLE', 10000),
    (44, NULL, 1, 'A44', 'AVAILABLE', 10000),
    (45, NULL, 1, 'A45', 'AVAILABLE', 15000),
    (46, NULL, 1, 'A46', 'AVAILABLE', 15000),
    (47, NULL, 1, 'A47', 'AVAILABLE', 20000),
    (48, NULL, 1, 'A48', 'AVAILABLE', 20000),
    (49, NULL, 1, 'A49', 'AVAILABLE', 10000),
    (50, NULL, 1, 'A50', 'AVAILABLE', 10000);



-- 콘서트 2의 B좌석 예약 상세 데이터 (50석), user_id는 비어 있고, seat_id는 모두 2로 설정
INSERT INTO reservation_seat_detail (reservation_id, user_id, seat_id, seat_number, reservation_status, payment_amount)
VALUES
    (51, NULL, 2, 'B1', 'AVAILABLE', 10000),
    (52, NULL, 2, 'B2', 'AVAILABLE', 10000),
    (53, NULL, 2, 'B3', 'AVAILABLE', 15000),
    (54, NULL, 2, 'B4', 'AVAILABLE', 15000),
    (55, NULL, 2, 'B5', 'AVAILABLE', 20000),
    (56, NULL, 2, 'B6', 'AVAILABLE', 20000),
    (57, NULL, 2, 'B7', 'AVAILABLE', 10000),
    (58, NULL, 2, 'B8', 'AVAILABLE', 10000),
    (59, NULL, 2, 'B9', 'AVAILABLE', 15000),
    (60, NULL, 2, 'B10', 'AVAILABLE', 15000),
    (61, NULL, 2, 'B11', 'AVAILABLE', 20000),
    (62, NULL, 2, 'B12', 'AVAILABLE', 20000),
    (63, NULL, 2, 'B13', 'AVAILABLE', 10000),
    (64, NULL, 2, 'B14', 'AVAILABLE', 10000),
    (65, NULL, 2, 'B15', 'AVAILABLE', 15000),
    (66, NULL, 2, 'B16', 'AVAILABLE', 15000),
    (67, NULL, 2, 'B17', 'AVAILABLE', 20000),
    (68, NULL, 2, 'B18', 'AVAILABLE', 20000),
    (69, NULL, 2, 'B19', 'AVAILABLE', 10000),
    (70, NULL, 2, 'B20', 'AVAILABLE', 10000),
    (71, NULL, 2, 'B21', 'AVAILABLE', 15000),
    (72, NULL, 2, 'B22', 'AVAILABLE', 15000),
    (73, NULL, 2, 'B23', 'AVAILABLE', 20000),
    (74, NULL, 2, 'B24', 'AVAILABLE', 20000),
    (75, NULL, 2, 'B25', 'AVAILABLE', 10000),
    (76, NULL, 2, 'B26', 'AVAILABLE', 10000),
    (77, NULL, 2, 'B27', 'AVAILABLE', 15000),
    (78, NULL, 2, 'B28', 'AVAILABLE', 15000),
    (79, NULL, 2, 'B29', 'AVAILABLE', 20000),
    (80, NULL, 2, 'B30', 'AVAILABLE', 20000),
    (81, NULL, 2, 'B31', 'AVAILABLE', 10000),
    (82, NULL, 2, 'B32', 'AVAILABLE', 10000),
    (83, NULL, 2, 'B33', 'AVAILABLE', 15000),
    (84, NULL, 2, 'B34', 'AVAILABLE', 15000),
    (85, NULL, 2, 'B35', 'AVAILABLE', 20000),
    (86, NULL, 2, 'B36', 'AVAILABLE', 20000),
    (87, NULL, 2, 'B37', 'AVAILABLE', 10000),
    (88, NULL, 2, 'B38', 'AVAILABLE', 10000),
    (89, NULL, 2, 'B39', 'AVAILABLE', 15000),
    (90, NULL, 2, 'B40', 'AVAILABLE', 15000),
    (91, NULL, 2, 'B41', 'AVAILABLE', 20000),
    (92, NULL, 2, 'B42', 'AVAILABLE', 20000),
    (93, NULL, 2, 'B43', 'AVAILABLE', 10000),
    (94, NULL, 2, 'B44', 'AVAILABLE', 10000),
    (95, NULL, 2, 'B45', 'AVAILABLE', 15000),
    (96, NULL, 2, 'B46', 'AVAILABLE', 15000),
    (97, NULL, 2, 'B47', 'AVAILABLE', 20000),
    (98, NULL, 2, 'B48', 'AVAILABLE', 20000),
    (99, NULL, 2, 'B49', 'AVAILABLE', 10000),
    (100, NULL, 2, 'B50', 'AVAILABLE', 10000);


-- 콘서트 3의 C좌석 예약 상세 데이터 (50석), user_id는 비어 있고, seat_id는 모두 3으로 설정
INSERT INTO reservation_seat_detail (reservation_id, user_id, seat_id, seat_number, reservation_status, payment_amount)
VALUES
    (101, NULL, 3, 'C1', 'AVAILABLE', 10000),
    (102, NULL, 3, 'C2', 'AVAILABLE', 10000),
    (103, NULL, 3, 'C3', 'AVAILABLE', 15000),
    (104, NULL, 3, 'C4', 'AVAILABLE', 15000),
    (105, NULL, 3, 'C5', 'AVAILABLE', 20000),
    (106, NULL, 3, 'C6', 'AVAILABLE', 20000),
    (107, NULL, 3, 'C7', 'AVAILABLE', 10000),
    (108, NULL, 3, 'C8', 'AVAILABLE', 10000),
    (109, NULL, 3, 'C9', 'AVAILABLE', 15000),
    (110, NULL, 3, 'C10', 'AVAILABLE', 15000),
    (111, NULL, 3, 'C11', 'AVAILABLE', 20000),
    (112, NULL, 3, 'C12', 'AVAILABLE', 20000),
    (113, NULL, 3, 'C13', 'AVAILABLE', 10000),
    (114, NULL, 3, 'C14', 'AVAILABLE', 10000),
    (115, NULL, 3, 'C15', 'AVAILABLE', 15000),
    (116, NULL, 3, 'C16', 'AVAILABLE', 15000),
    (117, NULL, 3, 'C17', 'AVAILABLE', 20000),
    (118, NULL, 3, 'C18', 'AVAILABLE', 20000),
    (119, NULL, 3, 'C19', 'AVAILABLE', 10000),
    (120, NULL, 3, 'C20', 'AVAILABLE', 10000),
    (121, NULL, 3, 'C21', 'AVAILABLE', 15000),
    (122, NULL, 3, 'C22', 'AVAILABLE', 15000),
    (123, NULL, 3, 'C23', 'AVAILABLE', 20000),
    (124, NULL, 3, 'C24', 'AVAILABLE', 20000),
    (125, NULL, 3, 'C25', 'AVAILABLE', 10000),
    (126, NULL, 3, 'C26', 'AVAILABLE', 10000),
    (127, NULL, 3, 'C27', 'AVAILABLE', 15000),
    (128, NULL, 3, 'C28', 'AVAILABLE', 15000),
    (129, NULL, 3, 'C29', 'AVAILABLE', 20000),
    (130, NULL, 3, 'C30', 'AVAILABLE', 20000),
    (131, NULL, 3, 'C31', 'AVAILABLE', 10000),
    (132, NULL, 3, 'C32', 'AVAILABLE', 10000),
    (133, NULL, 3, 'C33', 'AVAILABLE', 15000),
    (134, NULL, 3, 'C34', 'AVAILABLE', 15000),
    (135, NULL, 3, 'C35', 'AVAILABLE', 20000),
    (136, NULL, 3, 'C36', 'AVAILABLE', 20000),
    (137, NULL, 3, 'C37', 'AVAILABLE', 10000),
    (138, NULL, 3, 'C38', 'AVAILABLE', 10000),
    (139, NULL, 3, 'C39', 'AVAILABLE', 15000),
    (140, NULL, 3, 'C40', 'AVAILABLE', 15000),
    (141, NULL, 3, 'C41', 'AVAILABLE', 20000),
    (142, NULL, 3, 'C42', 'AVAILABLE', 20000),
    (143, NULL, 3, 'C43', 'AVAILABLE', 10000),
    (144, NULL, 3, 'C44', 'AVAILABLE', 10000),
    (145, NULL, 3, 'C45', 'AVAILABLE', 15000),
    (146, NULL, 3, 'C46', 'AVAILABLE', 15000),
    (147, NULL, 3, 'C47', 'AVAILABLE', 20000),
    (148, NULL, 3, 'C48', 'AVAILABLE', 20000),
    (149, NULL, 3, 'C49', 'AVAILABLE', 10000),
    (150, NULL, 3, 'C50', 'AVAILABLE', 10000);
