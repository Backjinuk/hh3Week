package com.example.hh3week.domain.reservation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReservationSeat is a Querydsl query type for ReservationSeat
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservationSeat extends EntityPathBase<ReservationSeat> {

    private static final long serialVersionUID = -377512870L;

    public static final QReservationSeat reservationSeat = new QReservationSeat("reservationSeat");

    public final NumberPath<Long> concertId = createNumber("concertId", Long.class);

    public final NumberPath<Long> currentReserved = createNumber("currentReserved", Long.class);

    public final NumberPath<Long> maxCapacity = createNumber("maxCapacity", Long.class);

    public final NumberPath<Long> seatId = createNumber("seatId", Long.class);

    public QReservationSeat(String variable) {
        super(ReservationSeat.class, forVariable(variable));
    }

    public QReservationSeat(Path<? extends ReservationSeat> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReservationSeat(PathMetadata metadata) {
        super(ReservationSeat.class, metadata);
    }

}

