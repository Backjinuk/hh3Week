package com.example.hh3week.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserPointHistory is a Querydsl query type for UserPointHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPointHistory extends EntityPathBase<UserPointHistory> {

    private static final long serialVersionUID = -2129145943L;

    public static final QUserPointHistory userPointHistory = new QUserPointHistory("userPointHistory");

    public final NumberPath<Long> historyId = createNumber("historyId", Long.class);

    public final NumberPath<Long> pointAmount = createNumber("pointAmount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> pointDt = createDateTime("pointDt", java.time.LocalDateTime.class);

    public final EnumPath<PointStatus> pointStatus = createEnum("pointStatus", PointStatus.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserPointHistory(String variable) {
        super(UserPointHistory.class, forVariable(variable));
    }

    public QUserPointHistory(Path<? extends UserPointHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPointHistory(PathMetadata metadata) {
        super(UserPointHistory.class, metadata);
    }

}

