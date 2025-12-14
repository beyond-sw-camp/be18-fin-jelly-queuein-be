package com.beyond.qiin.domain.iam.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateMyInfoRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserByAdminRequestDto;
import com.beyond.qiin.domain.iam.dto.user.response.CreateUserResponseDto;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.exception.UserException;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.domain.iam.support.user.UserProfileReader;
import com.beyond.qiin.domain.iam.support.user.UserProfileWriter;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.iam.support.user.UserWriter;
import com.beyond.qiin.domain.iam.support.userrole.UserRoleWriter;
import com.beyond.qiin.infra.mail.MailService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("UserCommandServiceImpl 단위 테스트")
public class UserCommandServiceImplTest {

    @Mock
    private UserReader userReader;

    @Mock
    private RoleReader roleReader;

    @Mock
    private UserProfileReader userProfileReader;

    @Mock
    private UserWriter userWriter;

    @Mock
    private UserProfileWriter userProfileWriter;

    @Mock
    private UserRoleWriter userRoleWriter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용자 등록 성공 단위 테스트")
    void createUser_success() {
        // given
        CreateUserRequestDto request = new CreateUserRequestDto(
                1L, LocalDate.of(2025, 1, 15), "홍길동", "hong@example.com", "01012341234", "1999-02-10");

        when(passwordEncoder.encode(anyString())).thenReturn("ENC_TEMP_PW");
        when(userReader.findLastUserNoByPrefix("202501")).thenReturn(Optional.empty());

        User saved = mock(User.class);
        when(saved.getId()).thenReturn(10L);
        when(saved.getDptId()).thenReturn(1L);
        when(saved.getUserNo()).thenReturn("202501001");
        when(saved.getUserName()).thenReturn("홍길동");
        when(saved.getEmail()).thenReturn("hong@example.com");
        when(saved.getPasswordExpired()).thenReturn(true);

        when(userWriter.save(any(User.class))).thenReturn(saved);

        Role role = mock(Role.class);
        when(roleReader.findByRoleName("GENERAL")).thenReturn(role);

        // when
        CreateUserResponseDto dto = userCommandService.createUser(request);

        // then
        verify(userWriter).save(any(User.class));
        verify(userRoleWriter).save(any(UserRole.class));
        verify(mailService).sendTempPassword(eq("hong@example.com"), anyString());

        assertThat(dto.getUserId()).isEqualTo(10L);
        assertThat(dto.getEmail()).isEqualTo("hong@example.com");
    }

    @Test
    @DisplayName("관리자가 사용자 정보 수정 단위 테스트")
    void updateUser_success() {

        UpdateUserByAdminRequestDto req = mock(UpdateUserByAdminRequestDto.class);

        User user = mock(User.class);
        Role generalRole = mock(Role.class);
        UserRole userRole = mock(UserRole.class);

        // 역할명 설정
        when(generalRole.getRoleName()).thenReturn("GENERAL");
        when(userRole.getRole()).thenReturn(generalRole);
        when(user.getUserRoles()).thenReturn(List.of(userRole));

        // UserReader mocking
        when(userReader.findById(1L)).thenReturn(user);

        userCommandService.updateUser(1L, req);

        verify(user).updateUser(req);
        verify(userWriter).save(user);
    }

    @Test
    @DisplayName("본인 정보 수정")
    void updateMyInfo_success() {

        UpdateMyInfoRequestDto req = mock(UpdateMyInfoRequestDto.class);
        User me = mock(User.class);

        when(userReader.findById(1L)).thenReturn(me);

        userCommandService.updateMyInfo(1L, req);

        verify(me).updateMyInfo(req);
        verify(userWriter).save(me);
    }

    @Test
    @DisplayName("임시 비밀번호 수정 단위 테스트")
    void changeTempPassword_success() {
        User user = mock(User.class);
        when(userReader.findById(1L)).thenReturn(user);
        when(user.getPasswordExpired()).thenReturn(true);
        when(passwordEncoder.encode("NEWPASS")).thenReturn("ENC_NEWPASS");

        userCommandService.changeTempPassword(1L, "NEWPASS");

        verify(entityManager).flush();
        verify(entityManager).clear();

        verify(user).updatePassword("ENC_NEWPASS");
        verify(userWriter).save(user);
    }

    @Test
    @DisplayName("이미지 삭제 성공 단위 테스트")
    void updateMyInfo_deleteImage_success() {

        UpdateMyInfoRequestDto req = mock(UpdateMyInfoRequestDto.class);
        User me = mock(User.class);

        when(userReader.findById(1L)).thenReturn(me);
        when(req.getImageDeleted()).thenReturn(true);

        userCommandService.updateMyInfo(1L, req);

        verify(userProfileWriter).deleteByUser(me);
    }

    @Test
    @DisplayName("임시 비밀번호 수정실패 단위 테스트")
    void changeTempPassword_fail_notExpired() {
        User user = mock(User.class);
        when(userReader.findById(1L)).thenReturn(user);
        when(user.getPasswordExpired()).thenReturn(false);

        assertThatThrownBy(() -> userCommandService.changeTempPassword(1L, "NEWPASS"))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("사용자 삭제 성공 단위 테스트")
    void deleteUser_success() {
        User user = mock(User.class);
        when(userReader.findById(1L)).thenReturn(user);

        userCommandService.deleteUser(1L, 9L);

        verify(userProfileWriter).deleteByUser(user);
        verify(user).softDelete(9L);
        verify(userWriter).save(user);
    }
}
