package com.beyond.qiin.domain.iam.repository.querydsl;

import static com.beyond.qiin.domain.iam.entity.QRole.role;
import static com.beyond.qiin.domain.iam.entity.QUser.user;
import static com.beyond.qiin.domain.iam.entity.QUserRole.userRole;

import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserLookupDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
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

    @Override
    @Transactional(readOnly = true)
    public Page<RawUserListResponseDto> search(final GetUsersSearchCondition condition, final Pageable pageable) {

        final boolean requireRoleJoin =
                condition.getRoleName() != null && !condition.getRoleName().isBlank();

        final List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);

        JPAQuery<RawUserListResponseDto> selectQuery = jpaQueryFactory
                .select(Projections.constructor(
                        RawUserListResponseDto.class,
                        user.id,
                        user.userName,
                        user.email,
                        user.dptId,
                        role.roleName,
                        user.createdAt,
                        user.phone,
                        user.lastLoginAt))
                .from(user);

        selectQuery.leftJoin(user.userRoles, userRole).leftJoin(userRole.role, role);

        selectQuery.where(
                userNameContains(condition.getUserName()),
                emailContains(condition.getEmail()),
                dptIdEq(condition.getDptId()),
                requireRoleJoin ? roleNameContains(condition.getRoleName()) : null,
                hireDateBetween(condition.getHireDateStart(), condition.getHireDateEnd()));

        selectQuery
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<RawUserListResponseDto> results = selectQuery.fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(
                        userNameContains(condition.getUserName()),
                        emailContains(condition.getEmail()),
                        dptIdEq(condition.getDptId()),
                        phoneContains(condition.getPhone()),
                        hireDateBetween(condition.getHireDateStart(), condition.getHireDateEnd()));

        if (requireRoleJoin) {
            countQuery.where(user.id.in(jpaQueryFactory
                    .select(userRole.user.id)
                    .from(userRole)
                    .leftJoin(userRole.role, role)
                    .where(role.roleName.containsIgnoreCase(condition.getRoleName()))));
        }

        Long total = countQuery.fetchOne();
        if (total == null) total = 0L;

        return new PageImpl<>(results, pageable, total);
    }

    // 참여자용 사용자 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<RawUserLookupDto> lookup(final String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        final String normalized = keyword.trim();

        return jpaQueryFactory
                .select(Projections.constructor(RawUserLookupDto.class, user.id, user.userName, user.email))
                .from(user)
                .where(user.userName.containsIgnoreCase(normalized).or(user.email.containsIgnoreCase(normalized)))
                .limit(10)
                .fetch();
    }

    // -----------------------------
    // 헬퍼 메서드
    // -----------------------------

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

    private BooleanExpression phoneContains(final String phone) {
        return (phone != null && !phone.isBlank()) ? user.phone.containsIgnoreCase(phone) : null;
    }

    // -----------------------------
    // 정렬 메서드
    // -----------------------------
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
            }
        }

        if (orders.isEmpty()) {
            orders.add(user.createdAt.desc());
        }

        return orders;
    }
}
