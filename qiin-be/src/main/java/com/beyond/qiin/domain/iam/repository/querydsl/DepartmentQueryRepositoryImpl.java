package com.beyond.qiin.domain.iam.repository.querydsl;

import static com.beyond.qiin.domain.iam.entity.QDepartment.department;
import static com.beyond.qiin.domain.iam.entity.QUser.user;

import com.beyond.qiin.domain.iam.dto.department.response.DepartmentResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepartmentQueryRepositoryImpl implements DepartmentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DepartmentResponseDto> findAllWithUserCount() {

        return queryFactory
                .select(Projections.constructor(
                        DepartmentResponseDto.class, department.id, department.dptName, user.id.countDistinct()))
                .from(department)
                .leftJoin(user)
                .on(user.department.eq(department))
                .where(department.deletedAt.isNull())
                .groupBy(department.id)
                .orderBy(department.dptName.asc())
                .fetch();
    }
}
