package com.beyond.qiin.domain.iam.repository.querydsl;

import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import com.beyond.qiin.domain.iam.entity.QRole;
import com.beyond.qiin.domain.iam.entity.QUser;
import com.beyond.qiin.domain.iam.entity.QUserRole;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private static final QUser user = QUser.user;
    private static final QUserRole userRole = QUserRole.userRole;
    private static final QRole role = QRole.role;

    @Override
    @Transactional(readOnly = true)
    public Page<RawUserListResponseDto> search(final GetUsersSearchCondition condition, final Pageable pageable) {

        final List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);

        final List<RawUserListResponseDto> results = jpaQueryFactory
                .select(Projections.constructor(
                        RawUserListResponseDto.class,
                        user.id,
                        user.userName,
                        user.email,
                        user.dptId,
                        role.roleName,
                        user.createdAt))
                .from(user)
                .leftJoin(user.userRoles, userRole)
                .leftJoin(userRole.role, role)
                .where(
                        userNameContains(condition.getUserName()),
                        emailContains(condition.getEmail()),
                        dptIdEq(condition.getDptId()),
                        roleNameContains(condition.getRoleName()),
                        hireDateBetween(condition.getHireDateStart(), condition.getHireDateEnd()))
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final Long total = jpaQueryFactory
                .select(user.count())
                .from(user)
                .leftJoin(user.userRoles, userRole)
                .leftJoin(userRole.role, role)
                .where(
                        userNameContains(condition.getUserName()),
                        emailContains(condition.getEmail()),
                        dptIdEq(condition.getDptId()),
                        roleNameContains(condition.getRoleName()),
                        hireDateBetween(condition.getHireDateStart(), condition.getHireDateEnd()))
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    // --------------------------------------------
    // 조건문 헬퍼메서드
    // --------------------------------------------

    private BooleanExpression userNameContains(final String userName) {
        return (userName != null && !userName.isBlank()) ? user.userName.containsIgnoreCase(userName) : null;
    }

    private BooleanExpression emailContains(final String email) {
        return (email != null && !email.isBlank()) ? user.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression dptIdEq(final Long dptId) {
        return dptId != null ? user.dptId.eq(dptId) : null;
    }

    private BooleanExpression roleNameContains(final String roleName) {
        return (roleName != null && !roleName.isBlank()) ? role.roleName.containsIgnoreCase(roleName) : null;
    }

    private BooleanExpression hireDateBetween(final LocalDate start, final LocalDate end) {

        if (start == null && end == null) return null;

        final ZoneId zone = ZoneId.of("Asia/Seoul");

        if (start != null && end != null) {
            return user.hireDate.between(
                    start.atStartOfDay(zone).toInstant(),
                    end.plusDays(1).atStartOfDay(zone).toInstant());
        }

        if (start != null) {
            return user.hireDate.goe(start.atStartOfDay(zone).toInstant());
        }

        return user.hireDate.loe(end.plusDays(1).atStartOfDay(zone).toInstant());
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(final Pageable pageable) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {

            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "userName" -> orders.add(new OrderSpecifier<>(direction, user.userName));
                case "email" -> orders.add(new OrderSpecifier<>(direction, user.email));
                case "dptId" -> orders.add(new OrderSpecifier<>(direction, user.dptId));
                case "roleName" -> orders.add(new OrderSpecifier<>(direction, role.roleName));
                case "createdAt" -> orders.add(new OrderSpecifier<>(direction, user.createdAt));

                default -> {
                    /* 무시 또는 기본 정렬 */
                }
            }
        }

        // 기본 정렬 - createdAt DESC
        if (orders.isEmpty()) {
            orders.add(user.createdAt.desc());
        }

        return orders;
    }
}
