package com.example.hh3week.domain.reservation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReservationSeatDetail is a Querydsl query type for ReservationSeatDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservationSeatDetail extends EntityPathBase<ReservationSeatDetail> {

    private static final long serialVersionUID = -317033141L;

    public static final QReservationSeatDetail reservationSeatDetail = new QReservationSeatDetail("reservationSeatDetail");

    public final EnumPath<ReservationStatus> reservationStatus = createEnum("reservationStatus", ReservationStatus.class);

    public final StringPath seatCode = createString("seatCode");

    public final NumberPath<Long> seatDetailId = createNumber("seatDetailId", Long.class);

    public final NumberPath<Long> seatId = createNumber("seatId", Long.class);

    public final NumberPath<Long> seatPrice = createNumber("seatPrice", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QReservationSeatDetail(String variable) {
        super(ReservationSeatDetail.class, forVariable(variable));
    }

    public QReservationSeatDetail(Path<? extends ReservationSeatDetail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReservationSeatDetail(PathMetadata metadata) {
        super(ReservationSeatDetail.class, metadata);
    }

}

