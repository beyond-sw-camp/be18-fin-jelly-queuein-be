package com.beyond.qiin.domain.iam.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.iam.dto.user.request.*;
import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.*;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import com.beyond.qiin.domain.iam.service.command.UserCommandService;
import com.beyond.qiin.domain.iam.service.query.UserQueryService;
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

    @BeforeEach
    void setUp() {
        commandService = mock(UserCommandService.class);
        queryService = mock(UserQueryService.class);
        controller = new UserController(commandService, queryService);
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
    @DisplayName("updateUser - 성공")
    void updateUser_success() {
        UpdateUserRequestDto req = createInstance(UpdateUserRequestDto.class);
        ReflectionTestUtils.setField(req, "userName", "수정됨");

        ResponseEntity<Void> result = controller.updateUser(5L, req);

        verify(commandService).updateUser(5L, req);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("changeTempPassword - 성공")
    void changeTempPassword_success() {
        ChangeTempPwRequestDto req = createInstance(ChangeTempPwRequestDto.class);
        ReflectionTestUtils.setField(req, "newPassword", "New1234");

        ResponseEntity<Void> res = controller.changeTempPassword(req, 7L);

        verify(commandService).changeTempPassword(7L, "New1234");
        assertThat(res.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("changeMyPassword - 성공")
    void changeMyPassword_success() {
        ChangePwRequestDto req = createInstance(ChangePwRequestDto.class);
        ReflectionTestUtils.setField(req, "currentPassword", "oldpw");
        ReflectionTestUtils.setField(req, "newPassword", "newpw");

        ResponseEntity<Void> res = controller.changeMyPassword(req, 8L);

        verify(commandService).changePassword(8L, req);
        assertThat(res.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("deleteUser - 성공")
    void deleteUser_success() {
        ResponseEntity<Void> res = controller.deleteUser(4L, 99L);

        verify(commandService).deleteUser(4L, 99L);
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
        UserLookupResponseDto dto = UserLookupResponseDto.builder()
                .userId(1L)
                .userName("홍길동")
                .email("hong@test.com")
                .build();

        when(queryService.lookupUsers("홍")).thenReturn(List.of(dto));

        ResponseEntity<List<UserLookupResponseDto>> res = controller.lookupUsers("홍");

        assertThat(res.getBody().get(0).getUserName()).isEqualTo("홍길동");
        verify(queryService).lookupUsers("홍");
    }

    @Test
    @DisplayName("getUser - 성공")
    void getUser_success() {
        DetailUserResponseDto dto =
                DetailUserResponseDto.builder().userId(1L).userName("홍길동").build();

        when(queryService.getUser(1L)).thenReturn(dto);

        DetailUserResponseDto res = controller.getUser(1L);

        verify(queryService).getUser(1L);
        assertThat(res.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getMe - 성공")
    void getMe_success() {
        DetailUserResponseDto dto =
                DetailUserResponseDto.builder().userId(10L).userName("나").build();

        when(queryService.getUser(10L)).thenReturn(dto);

        DetailUserResponseDto res = controller.getMe(10L);

        verify(queryService).getUser(10L);
        assertThat(res.getUserId()).isEqualTo(10L);
    }
}
