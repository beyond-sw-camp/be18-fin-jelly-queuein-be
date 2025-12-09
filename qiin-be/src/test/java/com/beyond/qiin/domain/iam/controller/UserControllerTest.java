package com.beyond.qiin.domain.iam.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.iam.dto.user.request.ChangePwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.ChangeTempPwRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.CreateUserRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateMyInfoRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.UpdateUserByAdminRequestDto;
import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.CreateUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.UserLookupResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import com.beyond.qiin.domain.iam.dto.user_role.request.UpdateUserRoleRequestDto;
import com.beyond.qiin.domain.iam.service.command.UserCommandService;
import com.beyond.qiin.domain.iam.service.query.UserQueryService;
import com.beyond.qiin.security.resolver.CurrentUserContext;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UserControllerTest 단위 테스트")
class UserControllerTest {

    private UserController controller;
    private UserCommandService commandService;
    private UserQueryService queryService;

    // CurrentUserContext의 목 객체를 위한 상수 ID
    private static final Long CURRENT_USER_ID = 7L;
    private static final Long DELETER_ID = 99L;
    private static final Long TEST_USER_ID = 5L;

    private CurrentUserContext currentUserContext;

    @BeforeEach
    void setUp() {
        commandService = mock(UserCommandService.class);
        queryService = mock(UserQueryService.class);
        controller = new UserController(commandService, queryService);

        // CurrentUserContext 객체 대신 Mock 객체를 생성하고, getUserId() 호출 시 상수 ID를 반환하도록 설정
        currentUserContext = mock(CurrentUserContext.class);
        when(currentUserContext.getUserId()).thenReturn(CURRENT_USER_ID);
    }

    private <T> T createInstance(Class<T> clazz) {
        try {
            Constructor<T> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Page<T> emptyPage() {
        return new Page<T>() {
            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 10;
            }

            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public boolean isFirst() {
                return true;
            }

            @Override
            public boolean isLast() {
                return true;
            }

            @Override
            public List<T> getContent() {
                return List.of();
            }

            @Override
            public Sort getSort() {
                return Sort.unsorted();
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Pageable getPageable() {
                return Pageable.unpaged();
            }

            @Override
            public Pageable nextPageable() {
                return Pageable.unpaged();
            }

            @Override
            public Pageable previousPageable() {
                return Pageable.unpaged();
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public <U> Page<U> map(java.util.function.Function<? super T, ? extends U> converter) {
                return emptyPage();
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public java.util.Iterator<T> iterator() {
                return List.<T>of().iterator();
            }
        };
    }

    @Test
    @DisplayName("createUser - 성공")
    void createUser_success() {
        CreateUserRequestDto req = createInstance(CreateUserRequestDto.class);
        ReflectionTestUtils.setField(req, "dptId", 1L);
        ReflectionTestUtils.setField(req, "hireDate", LocalDate.now());
        ReflectionTestUtils.setField(req, "userName", "홍길동");
        ReflectionTestUtils.setField(req, "email", "test@test.com");

        CreateUserResponseDto dto = CreateUserResponseDto.builder()
                .userId(1L)
                .dptId(1L)
                .userNo("U001")
                .userName("홍길동")
                .email("test@test.com")
                .passwordExpired(true)
                .hireDate(Instant.now())
                .build();

        when(commandService.createUser(req)).thenReturn(dto);

        ResponseEntity<CreateUserResponseDto> result = controller.createUser(req);

        assertThat(result.getBody().getUserName()).isEqualTo("홍길동");
        verify(commandService).createUser(req);
    }

    @Test
    @DisplayName("updateUserRole - 성공")
    void updateUserRole_success() {
        Long userId = 1L;
        Long roleId = 2L;
        UpdateUserRoleRequestDto req = createInstance(UpdateUserRoleRequestDto.class);
        ReflectionTestUtils.setField(req, "roleId", roleId);

        ResponseEntity<Void> result = controller.updateUserRole(userId, req, currentUserContext);

        verify(commandService).updateUserRole(userId, roleId, CURRENT_USER_ID);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("updateUser (관리자) 단위 테스트")
    void updateUser_success() {

        UpdateUserByAdminRequestDto req = createInstance(UpdateUserByAdminRequestDto.class);
        ReflectionTestUtils.setField(req, "dptId", 10L);
        ReflectionTestUtils.setField(
                req, "retireDate", LocalDate.of(2025, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC));

        ResponseEntity<Void> result = controller.updateUser(TEST_USER_ID, req);

        verify(commandService).updateUser(TEST_USER_ID, req);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("updateMyInfo (본인) - 성공")
    void updateMyInfo_success() {

        UpdateMyInfoRequestDto req = createInstance(UpdateMyInfoRequestDto.class);
        ReflectionTestUtils.setField(req, "userName", "변경된이름");
        ReflectionTestUtils.setField(req, "phone", "01022223333");

        ResponseEntity<Void> result = controller.updateMe(currentUserContext, req);

        verify(commandService).updateMyInfo(CURRENT_USER_ID, req);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("changeTempPassword - 성공")
    void changeTempPassword_success() {
        ChangeTempPwRequestDto req = createInstance(ChangeTempPwRequestDto.class);
        String newPassword = "New1234";
        ReflectionTestUtils.setField(req, "newPassword", newPassword);

        ResponseEntity<Void> res = controller.changeTempPassword(currentUserContext, req);

        verify(commandService).changeTempPassword(CURRENT_USER_ID, newPassword);
        assertThat(res.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("changeMyPassword - 성공")
    void changeMyPassword_success() {
        ChangePwRequestDto req = createInstance(ChangePwRequestDto.class);
        ReflectionTestUtils.setField(req, "currentPassword", "oldpw");
        ReflectionTestUtils.setField(req, "newPassword", "newpw");

        // ChangeMyPassword는 userId=8L을 사용하므로, 별도의 Mock Context가 필요하거나, CURRENT_USER_CONTEXT를 8L로 변경해야 합니다.
        // 여기서는 새로운 Mock Context를 사용합니다.
        Long userIdForChangePw = 8L;
        CurrentUserContext changePwUserContext = mock(CurrentUserContext.class);
        when(changePwUserContext.getUserId()).thenReturn(userIdForChangePw);

        ResponseEntity<Void> res = controller.changeMyPassword(changePwUserContext, req);

        verify(commandService).changePassword(userIdForChangePw, req);
        assertThat(res.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("deleteUser - 성공")
    void deleteUser_success() {
        Long userIdToDelete = 4L;

        // CurrentUserContext mock 준비
        CurrentUserContext mockUser = mock(CurrentUserContext.class);
        when(mockUser.getUserId()).thenReturn(DELETER_ID);

        // Controller 호출
        ResponseEntity<Void> res = controller.deleteUser(userIdToDelete, mockUser);

        // 검증
        verify(commandService).deleteUser(userIdToDelete, DELETER_ID);
        assertThat(res.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    @DisplayName("searchUsers - 성공")
    void searchUsers_success() {
        GetUsersSearchCondition cond = new GetUsersSearchCondition();

        Page<RawUserListResponseDto> page = emptyPage();
        PageResponseDto<RawUserListResponseDto> dto = PageResponseDto.from(page);

        when(queryService.searchUsers(eq(cond), any(Pageable.class))).thenReturn(dto);

        PageResponseDto<RawUserListResponseDto> res = controller.searchUsers(cond, Pageable.unpaged());

        verify(queryService).searchUsers(eq(cond), any());
        assertThat(res.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("lookupUsers - 성공")
    void lookupUsers_success() {
        String keyword = "홍";
        UserLookupResponseDto dto = UserLookupResponseDto.builder()
                .userId(1L)
                .userName("홍길동")
                .email("hong@test.com")
                .build();

        when(queryService.lookupUsers(keyword)).thenReturn(List.of(dto));

        ResponseEntity<List<UserLookupResponseDto>> res = controller.lookupUsers(keyword);

        assertThat(res.getBody().get(0).getUserName()).isEqualTo("홍길동");
        verify(queryService).lookupUsers(keyword);
    }

    @Test
    @DisplayName("getUser - 성공")
    void getUser_success() {
        Long userId = 1L;
        DetailUserResponseDto dto =
                DetailUserResponseDto.builder().userId(userId).userName("홍길동").build();

        when(queryService.getUser(userId)).thenReturn(dto);

        DetailUserResponseDto res = controller.getUser(userId);

        verify(queryService).getUser(userId);
        assertThat(res.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("getMe - 성공")
    void getMe_success() {
        Long userId = 10L;
        // getMe는 CurrentUserContext를 인수로 받습니다.
        CurrentUserContext getMeUserContext = mock(CurrentUserContext.class);
        when(getMeUserContext.getUserId()).thenReturn(userId);

        DetailUserResponseDto dto =
                DetailUserResponseDto.builder().userId(userId).userName("나").build();

        when(queryService.getUser(userId)).thenReturn(dto);

        // UserController의 getMe 메서드 시그니처에 맞게 CurrentUserContext Mock 객체를 전달
        DetailUserResponseDto res = controller.getMe(getMeUserContext);

        verify(queryService).getUser(userId);
        assertThat(res.getUserId()).isEqualTo(userId);
    }
}
