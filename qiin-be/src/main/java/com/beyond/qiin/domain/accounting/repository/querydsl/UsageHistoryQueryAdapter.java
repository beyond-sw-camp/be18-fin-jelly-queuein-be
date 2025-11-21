package com.beyond.qiin.domain.accounting.repository.querydsl;

import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistorySearchRequest;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryResponse;
import com.beyond.qiin.domain.accounting.repository.UsageHistoryJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsageHistoryQueryAdapter implements UsageHistoryJpaRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UsageHistoryResponse> searchUsageHistory(UsageHistorySearchRequest req) {

        // 🔥 여기에 QueryDSL 로직 들어갈 예정 (페이징 + 조인 + projection)

        // 지금은 기본 구조만 만들고, 다음 단계에서 실제 쿼리 채워넣자
        return Page.empty();
    }
}
