package com.beyond.qiin.domain.iam.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.iam.dto.user.request.search_condition.GetUsersSearchCondition;
import com.beyond.qiin.domain.iam.dto.user.response.DetailUserResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.UserLookupResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserLookupDto;
import com.beyond.qiin.domain.iam.entity.Department;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.entity.UserRole;
import com.beyond.qiin.domain.iam.repository.querydsl.UserQueryRepository;
import com.beyond.qiin.domain.iam.support.department.DepartmentReader;
import com.beyond.qiin.domain.iam.support.user.UserProfileReader;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DisplayName("UserQueryServiceImplTest 단위 테스트")
public class UserQueryServiceImplTest {

    @Mock
    private UserReader userReader;

    @Mock
    private UserProfileReader userProfileReader;

    @Mock
    private UserQueryRepository userQueryRepository;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    @Mock
    private DepartmentReader departmentReader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용자 목록 조회 단위 테스트")
    void searchUsers_success() {
        // given
        GetUsersSearchCondition cond = new GetUsersSearchCondition();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        RawUserListResponseDto dto = new RawUserListResponseDto(
                1L, "홍길동", "hong@example.com", 2L, "GENERAL", Instant.now(), "01012341234", null, null);

        Page<RawUserListResponseDto> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(userQueryRepository.search(any(GetUsersSearchCondition.class), any(Pageable.class)))
                .thenReturn(page);

        // when
        PageResponseDto<RawUserListResponseDto> result = userQueryService.searchUsers(cond, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getUserName()).isEqualTo("홍길동");
        verify(userQueryRepository).search(cond, pageable);
    }

    @Test
    @DisplayName("참여자용 사용자 검색 단위 테스트 - 정상 keyword")
    void lookupUsers_success() {
        // given
        RawUserLookupDto raw = new RawUserLookupDto(1L, "김철수", "kim@example.com");
        when(userQueryRepository.lookup("철수")).thenReturn(List.of(raw));

        // when
        List<UserLookupResponseDto> list = userQueryService.lookupUsers(" 철수 ");

        // then
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getUserName()).isEqualTo("김철수");
        verify(userQueryRepository).lookup("철수");
    }

    @Test
    @DisplayName("참여자용 사용자 검색 단위 테스트 - 빈 keyword 처리")
    void lookupUsers_emptyKeyword() {
        // keyword 가 null 또는 공백이면 바로 빈 리스트 반환
        List<UserLookupResponseDto> list1 = userQueryService.lookupUsers(null);
        List<UserLookupResponseDto> list2 = userQueryService.lookupUsers("  ");

        assertThat(list1).isEmpty();
        assertThat(list2).isEmpty();
        verify(userQueryRepository, never()).lookup(anyString());
    }

    @Test
    @DisplayName("사용자 상세 조회 단위 테스트")
    void getUser_success() {
        // given
        User user = mock(User.class);
        Department department = mock(Department.class);
        Role role = mock(Role.class);
        UserRole userRole = mock(UserRole.class);

        when(role.getId()).thenReturn(999L);
        when(role.getRoleName()).thenReturn("GENERAL");
        when(userRole.getRole()).thenReturn(role);
        when(user.getUserRoles()).thenReturn(List.of(userRole));

        when(user.getId()).thenReturn(10L);
        when(department.getId()).thenReturn(3L);
        when(user.getDepartment()).thenReturn(department);
        when(user.getUserNo()).thenReturn("202501001");
        when(user.getUserName()).thenReturn("테스터");
        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getPasswordExpired()).thenReturn(false);

        when(userReader.findById(10L)).thenReturn(user);
        when(userProfileReader.findByUser(user)).thenReturn(Optional.empty());

        // when
        DetailUserResponseDto dto = userQueryService.getUser(10L);

        // then
        assertThat(dto.getRoleId()).isEqualTo(999L);
        assertThat(dto.getRoleName()).isEqualTo("GENERAL");
    }
}
