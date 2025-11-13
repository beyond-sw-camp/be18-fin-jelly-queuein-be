package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserJpaRepository userJpaRepository;
}
