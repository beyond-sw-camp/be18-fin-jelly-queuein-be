package com.beyond.qiin.domain.iam.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepartmentQueryAdapterImpl implements DepartmentQueryAdapter {

    private final JPAQueryFactory queryFactory;
}
