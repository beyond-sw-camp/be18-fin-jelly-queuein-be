package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePasswordRequestDto;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.iam.support.user.UserWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserReader userReader;
    private final UserWriter userWriter;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(final Long userId, final ChangePasswordRequestDto request) {

        User user = userReader.findById(userId);

        // 기존 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw UserException.invalidPassword();
        }

        // 새 비밀번호 저장
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        userWriter.save(user);
    }
}
