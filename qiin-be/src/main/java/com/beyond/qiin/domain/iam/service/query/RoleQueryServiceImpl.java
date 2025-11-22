package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.support.role.RoleReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleQueryServiceImpl implements RoleQueryService {

    private final RoleReader roleReader;
}
