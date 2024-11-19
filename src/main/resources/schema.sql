drop table if exists concert;
drop table if exists concert_schedule;
drop table if exists payment_history;
drop table if exists reservation_seat;
drop table if exists reservation_seat_detail;
drop table if exists token;
drop table if exists user;
drop table if exists user_point_history;
drop table if exists waiting_queue;

create table concert
(
    concert_id      bigint not null auto_increment,
    concert_content varchar(255),
    concert_name    varchar(255),
    primary key (concert_id)
) engine = InnoDB;
create table concert_schedule
(
    concert_id              bigint not null,
    concert_price           bigint not null,
    concert_schedule_id     bigint not null auto_increment,
    end_dt                  datetime(6),
    start_dt                datetime(6),
    version                 bigint not null default 0,
    concert_schedule_status varchar(255),
    primary key (concert_schedule_id)
) engine = InnoDB;
create table payment_history
(
    payment_amount bigint not null,
    payment_dt     datetime(6),
    payment_id     bigint not null auto_increment,
    reservation_id bigint not null,
    user_id        bigint not null,
    payment_status varchar(255),
    primary key (payment_id)
) engine = InnoDB;
create table reservation_seat
(
    concert_id       bigint not null,
    current_reserved bigint not null,
    max_capacity     bigint not null,
    seat_id          bigint not null auto_increment,
    primary key (seat_id)
) engine = InnoDB;
create table reservation_seat_detail
(
    seat_detail_id     bigint not null auto_increment,
    seat_id            bigint not null,
    seat_price         bigint not null,
    user_id            bigint not null,
    version            bigint not null default 0,
    seat_code          varchar(255),
    reservation_status varchar(255),
    primary key (seat_detail_id)
) engine = InnoDB;
create table token
(
    expires_at datetime(6),
    issued_at  datetime(6),
    token_id   bigint not null auto_increment,
    user_id    bigint not null,
    version    bigint not null default 0,
    token      varchar(255),
    primary key (token_id)
) engine = InnoDB;
create table user
(
    point_balance bigint not null,
    user_id       bigint not null auto_increment,
    version        bigint not null default 0,
    user_name     varchar(255),
    primary key (user_id)
) engine = InnoDB;
create table user_point_history
(
    history_id   bigint not null auto_increment,
    point_amount bigint not null,
    point_dt     datetime(6),
    user_id      bigint not null,
    version      bigint not null default 0,
    point_status varchar(255),
    primary key (history_id)
) engine = InnoDB;
create table waiting_queue
(
    priority       bigint not null,
    reservation_dt datetime(6),
    seat_detail_id bigint not null,
    user_id        bigint not null,
    version        bigint not null default 0,
    waiting_id     bigint not null auto_increment,
    waiting_status varchar(255),
    primary key (waiting_id)
) engine = InnoDB;



-- CREATE INDEX idx_seatDetailId_userId ON waiting_queue (user_id, seat_detail_id);
-- CREATE INDEX idx_seatDetailId_priority ON waiting_queue (seat_detail_id, priority);







