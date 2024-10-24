package com.example.hh3week.domain.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentHistory is a Querydsl query type for PaymentHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentHistory extends EntityPathBase<PaymentHistory> {

    private static final long serialVersionUID = 1429186527L;

    public static final QPaymentHistory paymentHistory = new QPaymentHistory("paymentHistory");

    public final NumberPath<Long> paymentAmount = createNumber("paymentAmount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> paymentDt = createDateTime("paymentDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> paymentId = createNumber("paymentId", Long.class);

    public final EnumPath<PaymentStatus> paymentStatus = createEnum("paymentStatus", PaymentStatus.class);

    public final NumberPath<Long> reservationId = createNumber("reservationId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPaymentHistory(String variable) {
        super(PaymentHistory.class, forVariable(variable));
    }

    public QPaymentHistory(Path<? extends PaymentHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentHistory(PathMetadata metadata) {
        super(PaymentHistory.class, metadata);
    }

}

