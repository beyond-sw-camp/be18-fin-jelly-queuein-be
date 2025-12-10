package com.beyond.qiin.domain.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.Attendant;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.booking.event.ReservationEventPublisher;
import com.beyond.qiin.domain.booking.repository.AttendantJpaRepository;
import com.beyond.qiin.domain.booking.repository.ReservationJpaRepository;
import com.beyond.qiin.domain.booking.service.command.ReservationCommandService;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.Category;
import com.beyond.qiin.domain.inventory.repository.AssetJpaRepository;
import com.beyond.qiin.domain.inventory.repository.CategoryJpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ApplyReservationIntegrationTest {

  @Autowired
  private ReservationCommandService reservationCommandService;

  @Autowired
  private ReservationJpaRepository reservationJpaRepository;

  @Autowired
  private AttendantJpaRepository attendantJpaRepository;

  @Autowired
  private UserJpaRepository userJpaRepository;

  @Autowired
  private AssetJpaRepository assetJpaRepository;

  @Autowired
  private CategoryJpaRepository categoryJpaRepository;

  @MockBean
  private ReservationEventPublisher reservationEventPublisher;

  @Test
  void 승인예약_정상_생성() {
    // given

    User applicant = userJpaRepository.save(
        User.builder()
            .dptId(1L)
            .userNo("U001")
            .userName("신청자")
            .email("applicant@test.com")
            .password("encrypted")       // 테스트니까 그냥 아무 텍스트면 됨
            .hireDate(Instant.now())
            .build()
    );
    User attendant1 = userJpaRepository.save(
        User.builder()
            .dptId(1L)
            .userNo("A001")
            .userName("참석자1")
            .email("att1@test.com")
            .password("encrypted")
            .hireDate(Instant.now())
            .build()
    );
    User attendant2 = userJpaRepository.save(
        User.builder()
            .dptId(1L)
            .userNo("A002")
            .userName("참석자2")
            .email("att2@test.com")
            .password("encrypted")
            .hireDate(Instant.now())
            .build()
    );

    Category category = categoryJpaRepository.save(
        Category.builder()
            .name("공간")
            .description("공간 카테고리")
            .build()
    );


    Asset asset = assetJpaRepository.save(
        Asset.builder()
            .category(category)
            .name("회의실 A")
            .description("테스트 회의실")
            .image(null)
            .status(1)              // AssetStatus.ACTIVE 등 코드값
            .type(1)                // 회의실 등 AssetType 코드값
            .accessLevel(1)
            .needsApproval(false)
            .costPerHour(BigDecimal.ZERO)
            .periodCost(BigDecimal.ZERO)
            .build()
    );

    CreateReservationRequestDto dto = CreateReservationRequestDto.builder()
        .applicantId(applicant.getId())
        .startAt(Instant.parse("2025-01-01T10:00:00Z"))
        .endAt(Instant.parse("2025-01-01T11:00:00Z"))
        .description("테스트 예약")
        .attendantIds(List.of(attendant1.getId(), attendant2.getId()))
        .build();


    // when
    ReservationResponseDto response =
        reservationCommandService.applyReservation(applicant.getId(), asset.getId(), dto);

    // then
    Reservation saved = reservationJpaRepository.findById(response.getReservationId()).orElseThrow();

    assertThat(saved.getApplicant().getId()).isEqualTo(applicant.getId());
    assertThat(saved.getStatus()).isEqualTo(ReservationStatus.PENDING);
    assertThat(saved.getDescription()).isEqualTo("test description");

    assertThat(saved.getAttendants()).hasSize(2);
  }
}
