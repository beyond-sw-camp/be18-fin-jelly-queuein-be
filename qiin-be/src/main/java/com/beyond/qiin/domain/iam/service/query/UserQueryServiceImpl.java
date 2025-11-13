package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
    private final UserJpaRepository userJpaRepository;
}
