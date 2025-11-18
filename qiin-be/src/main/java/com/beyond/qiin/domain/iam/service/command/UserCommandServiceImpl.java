package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserRequestDto;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.iam.support.user.UserWriter;
import com.beyond.qiin.security.PasswordGenerator;
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

        final String tempPassword = PasswordGenerator.generate();
        final String encrypted = passwordEncoder.encode(tempPassword);

        // 임시 비밀번호 생성은 서비스에서 / toEntity 제거
        final User user = User.builder()
                .dptId(request.getDptId())
                .userNo(request.getUserNo())
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(encrypted)
                .passwordExpired(true)
                .build();

        userWriter.save(user);
    }

    // 사용자 정보 수정
    @Override
    @Transactional
    public void updateUser(final Long userId, final UpdateUserRequestDto request) {
        User user = userReader.findById(userId);
        user.updateUser(request);
        userWriter.save(user);
    }

    // 임시 비밀번호 수정
    @Override
    @Transactional
    public void changeTempPassword(final Long userId, final String newPassword) {

        final User user = userReader.findById(userId);

        // 임시 비밀번호 사용자만 허용
        if (!Boolean.TRUE.equals(user.getPasswordExpired())) {
            throw UserException.passwordChangeNotAllowed();
        }

        user.updatePassword(passwordEncoder.encode(newPassword));

        userWriter.save(user);
    }

    // 평상시 비밀번호 수정
    @Override
    @Transactional
    public void changePassword(final Long userId, final ChangePwRequestDto request) {

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
