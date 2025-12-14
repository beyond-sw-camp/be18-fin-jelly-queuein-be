package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.user.request.ChangePwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateMyInfoRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserByAdminRequestDto;
import com.beyond.qiin.domain.iam.dto.user.response.CreateUserResponseDto;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserProfile;
import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.domain.iam.support.user.UserProfileReader;
import com.beyond.qiin.domain.iam.support.user.UserProfileWriter;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.iam.support.user.UserWriter;
import com.beyond.qiin.domain.iam.support.userrole.UserRoleWriter;
import com.beyond.qiin.infra.mail.MailService;
import com.beyond.qiin.infra.redis.iam.role.RoleProjectionHandler;
import com.beyond.qiin.security.PasswordGenerator;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserReader userReader;
    private final UserProfileReader userProfileReader;
    private final RoleReader roleReader;

    private final UserWriter userWriter;
    private final UserProfileWriter userProfileWriter;
    private final UserRoleWriter userRoleWriter;

    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final EntityManager entityManager;
    private final RoleProjectionHandler projectionHandler;

    // 사용자 생성
    @Override
    @Transactional
    public CreateUserResponseDto createUser(final CreateUserRequestDto request) {

        // 임시 비밀번호 생성
        final String tempPassword = PasswordGenerator.generate();
        final String encrypted = passwordEncoder.encode(tempPassword);

        String userNo = generateUserNo(request.getHireDate());

        User user = User.create(request, userNo, encrypted);
        User saved = userWriter.save(user);

        Role defaultRole = roleReader.findByRoleName("GENERAL");

        UserRole userRole = UserRole.create(saved, defaultRole);
        userRoleWriter.save(userRole);

        mailService.sendTempPassword(saved.getEmail(), tempPassword);

        return CreateUserResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void updateUserRole(final Long userId, final Long roleId, final Long updaterId) {

        User user = userReader.findById(userId);
        Role newRole = roleReader.findById(roleId);

        // MASTER 보호
        validateAdminCannotModifyMaster(user);

        // 기존 역할 soft delete
        user.getUserRoles().forEach(ur -> ur.softDelete(updaterId));

        // 새 역할 부여
        UserRole newUserRole = UserRole.create(user, newRole);
        userRoleWriter.save(newUserRole);

        projectionHandler.onUserRoleChanged(newRole);
    }

    @Override
    @Transactional
    public void updateUser(final Long userId, final UpdateUserByAdminRequestDto request) {

        User target = userReader.findById(userId);

        // 1) MASTER 보호 — 대상이 MASTER면 ADMIN은 수정 불가
        validateAdminCannotModifyMaster(target);

        // 2) 수정 수행
        target.updateUser(request);
        userWriter.save(target);
    }

    @Override
    @Transactional
    public void updateMyInfo(final Long userId, final UpdateMyInfoRequestDto request) {

        User me = userReader.findById(userId);

        // MASTER 자신 정보 수정은 허용
        me.updateMyInfo(request);
        userWriter.save(me);

        // 프사 기능
        if (request.getProfileImageKey() != null && request.getProfileImageUrl() != null) {

            userProfileReader
                    .findByUser(me)
                    .ifPresentOrElse(
                            // 이미 프로필이 있으면 갱신
                            profile -> profile.updateImage(request.getProfileImageKey(), request.getProfileImageUrl()),
                            // 없으면 생성
                            () -> userProfileWriter.save(UserProfile.create(
                                    me, request.getProfileImageKey(), request.getProfileImageUrl())));
        }
    }

    // 임시 비밀번호 수정
    @Override
    @Transactional
    public void changeTempPassword(final Long userId, final String newPassword) {

        entityManager.flush();
        entityManager.clear();

        final User user = userReader.findById(userId);

        // 임시 비밀번호 사용자만 허용
        if (!Boolean.TRUE.equals(user.getPasswordExpired())) {
            throw UserException.passwordChangeNotAllowed();
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        user.updateLastLoginAt(Instant.now());
        userWriter.save(user);
    }

    // 평상시 비밀번호 수정
    @Override
    @Transactional
    public void changePassword(final Long userId, final ChangePwRequestDto request) {

        final User user = userReader.findById(userId);

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
    public void deleteUser(final Long userId, final Long deleterId) {
        User user = userReader.findById(userId);

        // 개인정보는 하드딜리트
        userProfileWriter.deleteByUser(user);

        user.softDelete(deleterId);
        userWriter.save(user);
    }

    // --------------------------------------
    // 헬퍼 메서드
    // --------------------------------------

    // 사번 생성 로직
    private String generateUserNo(final LocalDate hireDate) {
        Instant hireInstant = hireDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

        String prefix = hireDate.format(DateTimeFormatter.ofPattern("yyyyMM"));

        Optional<String> lastUserNoOpt = userReader.findLastUserNoByPrefix(prefix);

        int nextSeq = lastUserNoOpt
                .map(last -> Integer.parseInt(last.substring(prefix.length())))
                .map(num -> num + 1)
                .orElse(1);

        String seq = String.format("%03d", nextSeq);

        return prefix + seq;
    }

    private void validateAdminCannotModifyMaster(final User targetUser) {

        // 대상의 역할 조회
        String targetRole = targetUser.getUserRoles().stream()
                .findFirst()
                .map(ur -> ur.getRole().getRoleName())
                .orElseThrow(UserException::userNotFound);

        // MASTER는 보호됨
        if ("MASTER".equals(targetRole)) {
            throw UserException.passwordChangeNotAllowed();
        }
    }
}
