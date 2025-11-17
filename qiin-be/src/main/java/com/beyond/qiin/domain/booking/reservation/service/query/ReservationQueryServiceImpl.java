package com.beyond.qiin.domain.booking.reservation.service.query;

import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.TimeSlotDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationException;
import com.beyond.qiin.domain.booking.reservation.repository.ReservationJpaRepository;
import com.beyond.qiin.domain.booking.reservation.util.AvailableTimeSlotCalculator;
import com.beyond.qiin.domain.booking.reservation.vo.DateRange;
import com.beyond.qiin.domain.booking.reservation.vo.TimeSlot;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationQueryServiceImpl implements ReservationQueryService {
    private final ReservationJpaRepository reservationJpaRepository;

    // 예약 상세 조회 (api용)
    @Override
    @Transactional(readOnly = true)
    public ReservationDetailResponseDto getReservation(Long id) {
        Reservation reservation = getReservationById(id);

        ReservationDetailResponseDto reservationDetailResponseDto =
                ReservationDetailResponseDto.fromEntity(reservation, statusToString(reservation.getStatus()));

        return reservationDetailResponseDto;
    }

    // 사용자 이름으로 예약 목록 조회
    @Transactional(readOnly = true)
    @Override
    public GetUserReservationListResponseDto getReservationsByUserId(Long userId, LocalDate date) {
        // 사용자 있는지 확인

        List<Reservation> reservations = getReservationsByUserAndDate(userId, date);

        List<GetUserReservationResponseDto> reservationList = new ArrayList<>();

        for (Reservation reservation : reservations) {
            GetUserReservationResponseDto getUserReservationResponseDto =
                    GetUserReservationResponseDto.fromEntity(reservation, statusToString(reservation.getStatus()));
            reservationList.add(getUserReservationResponseDto);
        }

        GetUserReservationListResponseDto reservationListResponseDto = GetUserReservationListResponseDto.builder()
                .reservations(reservationList)
                .build();

        return reservationListResponseDto;
    }

    // 예약 가능 자원 목록 조회, 날짜 기준의 조회
    @Override
    @Transactional(readOnly = true)
    public ReservableAssetListResponseDto getReservableAssets(LocalDate date) {
        // 사용 가능 상태의 자원들을 가져옴 - 빌 수 있음
        List<Asset> assets = assetRepository.findAvailableAssets();

        // 예약 가능한 자원들을 담는 용도
        List<ReservableAssetResponseDto> responseList = new ArrayList<>();

        for (Asset asset : assets) {
            // 해당 날짜의 해당 자원의 목록 조회
            List<Reservation> reservations = getReservationsByAssetAndDate(asset.getId(), date);

            // 모든 시간에 대해 예약이 차있지 않은 경우만 추가
            boolean reservableAsset =
                    AvailableTimeSlotCalculator.isReservable(reservations, date, ZoneId.of("Asia/Seoul"));

            if (reservableAsset) {
                // 시간대가 있는 경우 해당 자원에 대해 dto 추가

                ReservableAssetResponseDto reservableAssetResponseDto =
                        ReservableAssetResponseDto.fromEntity(asset, statusToString(asset.getType()));
                responseList.add(reservableAssetResponseDto);
            }
        }

        ReservableAssetListResponseDto reservationListResponseDto = ReservableAssetListResponseDto.builder()
                .reservations(responseList)
                .build();

        return reservationListResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservableAssetTimeResponseDto getReservableAssetTimes(Long assetId, LocalDate date) {
        // assetId 유효검증
        List<Reservation> reservations = getReservationsByAssetAndDate(assetId, date);

        List<TimeSlot> timeSlots =
                AvailableTimeSlotCalculator.calculateAvailableSlots(reservations, date, ZoneId.of("Asia/Seoul"));

        List<TimeSlotDto> timeSlotDtos = timeSlots.stream()
                .map(slot -> TimeSlotDto.create(slot, "Asia/Seoul"))
                .toList();

        return ReservableAssetTimeResponseDto.create(assetId, timeSlotDtos);
    }

    // 주별 일정 조회
    // TODO: 날짜
    @Override
    @Transactional(readOnly = true)
    public WeekReservationListResponseDto getWeeklyReservations(Long userId, LocalDate date) { // 해당 주의 기준날짜
        // user 있는지 확인

        List<Reservation> reservations = getReservationsByUserAndDate(userId, date);
        // 비어있을 수 있음
        List<WeekReservationResponseDto> reservationList = new ArrayList<>();

        for (Reservation reservation : reservations) {
            WeekReservationResponseDto reservationResponseDto = WeekReservationResponseDto.fromEntity(reservation);
            reservationList.add(reservationResponseDto);
        }

        WeekReservationListResponseDto weekReservationListResponseDto = WeekReservationListResponseDto.builder()
                .reservations(reservationList)
                .build();

        return weekReservationListResponseDto;
    }

    // 월별 일정 조회
    @Override
    @Transactional(readOnly = true)
    public MonthReservationListResponseDto getMonthlyReservations(
            Long userId, YearMonth yearMonth) { // 일까지 포함 X이므로 달까지 포함하는 자료형 사용
        // user 있는지 확인

        // 비어있을 수 있음
        List<Reservation> reservations = getReservationsByUserAndYearMonth(userId, yearMonth);

        List<MonthReservationResponseDto> reservationList = new ArrayList<>();

        for (Reservation reservation : reservations) {
            MonthReservationResponseDto reservationResponseDto = MonthReservationResponseDto.fromEntity(reservation);
            reservationList.add(reservationResponseDto);
        }

        MonthReservationListResponseDto monthReservationListResponseDto = MonthReservationListResponseDto.builder()
                .reservations(reservationList)
                .build();

        return monthReservationListResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public GetAppliedReservationListResponseDto getReservationApplies(LocalDate date) {
        // 관리자 권한 확인

        // status == pending인 경우
        List<Reservation> reservations = getReservationsPendingAndDate(date);

        List<GetAppliedReservationResponseDto> reservationList = new ArrayList<>();

        for (Reservation reservation : reservations) {
            GetAppliedReservationResponseDto reservationResponseDto =
                    GetAppliedReservationResponseDto.fromEntity(reservation);
            reservationList.add(reservationResponseDto);
        }

        GetAppliedReservationListResponseDto reservationListResponseDto = GetAppliedReservationListResponseDto.builder()
                .reservations(reservationList)
                .build();

        return reservationListResponseDto;
    }

    // 자원 자체 (예외처리 포함) -> command service용
    @Override
    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {
        Reservation reservation = reservationJpaRepository
                .findById(id)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
        return reservation;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUserAndDate(Long userId, LocalDate date) {

        DateRange dateRange = dayToInstant("Asia/Seoul", date);

        List<Reservation> reservations =
                reservationJpaRepository.findByUserIdAndDate(userId, dateRange.getStartDay(), getEndDay());
        return reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByAssetAndDate(Long assetId, LocalDate date) {

        // assetId 유효한지 확인

        DateRange dateRange = dayToInstant("Asia/Seoul", date);

        List<Reservation> reservations = reservationJpaRepository.findAllByAssetIdAndDate(
                asset.getId(), dateRange.getStartDay(), dateRange.getEndDay());
        return reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUserAndYearMonth(Long userId, YearMonth yearMonth) {

        // userId 유효한지 확인

        DateRange dateRange = monthToInstant("Asia/Seoul", yearMonth);

        List<Reservation> reservations =
                reservationJpaRepository.findByUserIdAndYearMonth(userId, dateRange.getStartDay(), getEndDay());
        return reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsPendingAndDate(LocalDate date) {

        DateRange dateRange = dayToInstant("Asia/Seoul", date);

        List<Reservation> reservations = reservationJpaRepository.findAllWithStatusPendingAndDate(
                dateRange.getStartDay(), dateRange.getEndDay());
        return reservations;
    }

    public static String statusToString(Integer status) {
        if (status == 0) {
            return "PENDING";
        } else if (status == 1) {
            return "APPROVED";
        } else if (status == 2) {
            return "USING";
        } else if (status == 3) {
            return "REJECTED";
        } else if (status == 4) {
            return "CANCELED";
        } else {
            return "COMPLETED";
        }
    }

    public DateRange dayToInstant(String timezone, LocalDate date) {
        ZoneId zone = ZoneId.of(timezone); // Asia/Seoul
        Instant startOfDay = date.atStartOfDay().atZone(zone).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant();
        return DateRange.create(startOfDay, endOfDay);
    }

    public DateRange monthToInstant(String timezone, YearMonth yearMonth) {
        ZoneId zone = ZoneId.of(timezone); // Asia/Seoul
        Instant startDay = yearMonth.atDay(1).atStartOfDay(zone).toInstant();

        Instant endDay = yearMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay(zone)
                .minusNanos(1)
                .toInstant();
        return DateRange.create(startDay, endDay);
    }
}
