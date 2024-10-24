package com.example.hh3week.domain.concert.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QConcertSchedule is a Querydsl query type for ConcertSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConcertSchedule extends EntityPathBase<ConcertSchedule> {

    private static final long serialVersionUID = 959767724L;

    public static final QConcertSchedule concertSchedule = new QConcertSchedule("concertSchedule");

    public final NumberPath<Long> concertId = createNumber("concertId", Long.class);

    public final NumberPath<Long> concertPrice = createNumber("concertPrice", Long.class);

    public final NumberPath<Long> concertScheduleId = createNumber("concertScheduleId", Long.class);

    public final EnumPath<ConcertScheduleStatus> concertScheduleStatus = createEnum("concertScheduleStatus", ConcertScheduleStatus.class);

    public final DateTimePath<java.time.LocalDateTime> endDt = createDateTime("endDt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> startDt = createDateTime("startDt", java.time.LocalDateTime.class);

    public QConcertSchedule(String variable) {
        super(ConcertSchedule.class, forVariable(variable));
    }

    public QConcertSchedule(Path<? extends ConcertSchedule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QConcertSchedule(PathMetadata metadata) {
        super(ConcertSchedule.class, metadata);
    }

}

