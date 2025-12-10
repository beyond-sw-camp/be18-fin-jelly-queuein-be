package com.beyond.qiin.domain.booking.service;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beyond.qiin.domain.booking.controller.ReservationController;
import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.attendant.AttendantResponseDto;
import com.beyond.qiin.domain.booking.service.command.ReservationCommandService;
import com.beyond.qiin.domain.booking.service.query.ReservationQueryService;
import com.beyond.qiin.security.CustomUserDetails;
import com.beyond.qiin.security.config.SecurityConfig;
import com.beyond.qiin.security.jwt.JwtFilter;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.jwt.RedisTokenRepository;
import com.beyond.qiin.security.resolver.AccessToken;
import com.beyond.qiin.security.resolver.ArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = ReservationController.class,
        excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ReservationCommandService reservationCommandService;

  @MockBean
  private ReservationQueryService reservationQueryService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  private RedisTokenRepository redisTokenRepository;

  @MockBean
  private ArgumentResolver argumentResolver;

  @Test
  @WithMockUser(
      username = "testUser",
      roles = {"GENERAL"})
  void applyReservation_success() throws Exception {
    Instant start = Instant.parse("2025-12-12T10:00:00Z");
    Instant end = Instant.parse("2025-12-12T11:00:00Z");

    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

    CustomUserDetails mockUser = new CustomUserDetails(1L, "tester", authorities);

    CreateReservationRequestDto request = CreateReservationRequestDto.builder()
        .applicantId(1L)
        .startAt(start)
        .endAt(end)
        .description("팀 회의")
        .attendantIds(List.of(2L, 3L))
        .build();

    AttendantResponseDto at1 = AttendantResponseDto.builder()
        .attendantId(2L)
        .attendantName("홍길동")
        .build();

    AttendantResponseDto at2 = AttendantResponseDto.builder()
        .attendantId(3L)
        .attendantName("김철수")
        .build();

    ReservationResponseDto response = ReservationResponseDto.builder()
        .reservationId(100L)
        .assetName("회의실 A")
        .applicantName("사용자1")
        .startAt(start)
        .endAt(end)
        .actualStartAt(null)
        .actualEndAt(null)
        .description("팀 회의")
        .reason(null)
        .version(1L)
        .isApproved(false)
        .status("WAITING")
        .createdAt(Instant.now())
        .createdBy(1L)
        .updatedAt(Instant.now())
        .updatedBy(1L)
        .attendants(List.of(at1, at2))
        .build();

    when(jwtTokenProvider.getUserId(any())).thenReturn(1L);
    when(reservationCommandService.applyReservation(any(), any(), any())).thenReturn(response);

    mockMvc.perform(
            post("/api/v1/reservations/1/apply") // assetId = 1
                .with(authentication(new UsernamePasswordAuthenticationToken(
                    mockUser, null, mockUser.getAuthorities())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.reservationId").value(100L))
        .andExpect(jsonPath("$.applicantName").value("사용자1"))
        .andExpect(jsonPath("$.assetName").value("회의실 A"))
        .andExpect(jsonPath("$.description").value("팀 회의"))
        .andExpect(jsonPath("$.status").value("WAITING"))
        // attendants 검증
        .andExpect(jsonPath("$.attendants[0].attendantId").value(2L))
        .andExpect(jsonPath("$.attendants[0].attendantName").value("홍길동"))
        .andExpect(jsonPath("$.attendants[1].attendantId").value(3L))
        .andExpect(jsonPath("$.attendants[1].attendantName").value("김철수"));
  }
  @Test
  @WithMockUser(username = "testUser", roles = {"GENERAL"})
  void getReservation_success() throws Exception {

    Long reservationId = 30L;

    List<GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_GENERAL"));

    CustomUserDetails mockUser = new CustomUserDetails(
        1L,
        "tester",
        authorities
    );

    ReservationDetailResponseDto detailDto = ReservationDetailResponseDto.builder()
        .reservationId(reservationId)
        .assetName("회의실 B")
        .applicantName("홍길동")
        .description("팀 브리핑")
        .reservationStatus("APPROVED")
        .startAt(Instant.parse("2025-12-12T09:00:00Z"))
        .endAt(Instant.parse("2025-12-12T10:00:00Z"))
        .attendants(List.of())   // null 방지
        .build();

    // jwt mocking
    when(jwtTokenProvider.getUserId(any())).thenReturn(1L);

    // query mocking
    when(reservationQueryService.getReservation(any(), any()))
        .thenReturn(detailDto);

    mockMvc.perform(
            get("/api/v1/reservations/" + reservationId)
                .header("accessToken", "mock-token")   // ★ 중요 ★
                .with(authentication(
                    new UsernamePasswordAuthenticationToken(
                        mockUser, null, mockUser.getAuthorities()
                    )
                ))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reservationId").value(reservationId))
        .andExpect(jsonPath("$.assetName").value("회의실 B"))
        .andExpect(jsonPath("$.applicantName").value("홍길동"))
        .andExpect(jsonPath("$.description").value("팀 브리핑"))
        .andExpect(jsonPath("$.reservationStatus").value("APPROVED"));
  }

  @Test
  @WithMockUser(username = "testUser", roles = {"MASTER"})
  void updateReservation_success() throws Exception {

    Long reservationId = 10L;

    Instant newStart = Instant.parse("2025-12-12T13:00:00Z");
    Instant newEnd = Instant.parse("2025-12-12T14:00:00Z");

    List<GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("MASTER"));

    CustomUserDetails mockUser =
        new CustomUserDetails(1L, "testUser", authorities);

    UpdateReservationRequestDto request = UpdateReservationRequestDto.builder()
        .version(1L)
        .description("설명 수정됨")
        .startAt(newStart)
        .endAt(newEnd)
        .attendantIds(List.of(5L, 6L))
        .build();

    ReservationResponseDto response = ReservationResponseDto.builder()
        .reservationId(reservationId)
        .assetName("회의실 A")
        .applicantName("testerUser")
        .description("설명 수정됨")
        .status("PENDING")
        .startAt(newStart)
        .endAt(newEnd)
        .attendants(List.of(
            AttendantResponseDto.builder().attendantId(5L).attendantName("참여자1").build(),
            AttendantResponseDto.builder().attendantId(6L).attendantName("참여자2").build()
        ))
        .build();

    when(jwtTokenProvider.getUserId(any())).thenReturn(1L);
    when(reservationCommandService.updateReservation(any(), any(), any()))
        .thenReturn(response);

    mockMvc.perform(
            patch("/api/v1/reservations/" + reservationId)
                .with(authentication(new UsernamePasswordAuthenticationToken(
                    mockUser, null, mockUser.getAuthorities())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reservationId").value(reservationId))
        .andExpect(jsonPath("$.description").value("설명 수정됨"))
        .andExpect(jsonPath("$.startAt").value(newStart.toString()))
        .andExpect(jsonPath("$.endAt").value(newEnd.toString()))
        .andExpect(jsonPath("$.attendants[0].attendantId").value(5L))
        .andExpect(jsonPath("$.attendants[1].attendantId").value(6L));
  }


  @Test
  @WithMockUser(username = "testUser", roles = {"MASTER"})
  void deleteReservation_success() throws Exception {

    Long reservationId = 20L;

    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("MASTER"));

    CustomUserDetails mockUser = new CustomUserDetails(
        1L,
        "tester",
        authorities
    );

    // JWT mock
    when(jwtTokenProvider.getUserId(any())).thenReturn(1L);

    // softDeleteReservation은 void → doNothing 사용
    doNothing().when(reservationCommandService).softDeleteReservation(any(), any());

    mockMvc.perform(
            delete("/api/v1/reservations/" + reservationId)
                .with(authentication(
                    new UsernamePasswordAuthenticationToken(
                        mockUser, null, mockUser.getAuthorities()
                    )
                ))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isNoContent());
  }

}
