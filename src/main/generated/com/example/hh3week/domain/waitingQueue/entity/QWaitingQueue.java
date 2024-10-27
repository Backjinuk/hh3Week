package com.example.hh3week.domain.waitingQueue.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWaitingQueue is a Querydsl query type for WaitingQueue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWaitingQueue extends EntityPathBase<WaitingQueue> {

    private static final long serialVersionUID = -1243029097L;

    public static final QWaitingQueue waitingQueue = new QWaitingQueue("waitingQueue");

    public final NumberPath<Long> priority = createNumber("priority", Long.class);

    public final DateTimePath<java.time.LocalDateTime> reservationDt = createDateTime("reservationDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> seatDetailId = createNumber("seatDetailId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Long> waitingId = createNumber("waitingId", Long.class);

    public final EnumPath<WaitingStatus> waitingStatus = createEnum("waitingStatus", WaitingStatus.class);

    public QWaitingQueue(String variable) {
        super(WaitingQueue.class, forVariable(variable));
    }

    public QWaitingQueue(Path<? extends WaitingQueue> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWaitingQueue(PathMetadata metadata) {
        super(WaitingQueue.class, metadata);
    }

}

