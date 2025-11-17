package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePasswordRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserRequestDto;
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

    // 사용자 생성
    @Override
    @Transactional
    public void createUser(final CreateUserRequestDto request) {
        User newUser = CreateUserRequestDto.toEntity(request);
        // 기본 비밀번호 암호화 및 passwordExpired 설정 가능
        userWriter.save(newUser);
    }

    @Override
    @Transactional
    public void updateUser(final Long userId, final UpdateUserRequestDto request) {
        User user = userReader.findById(userId);
        user.updateUser(request);
        userWriter.save(user);
    }

    // 비밀번호 수정
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

    // 사용자 삭제
    @Override
    @Transactional
    public void deleteUser(final Long userId) {
        User user = userReader.findById(userId);
        userWriter.delete(user);
    }
}
