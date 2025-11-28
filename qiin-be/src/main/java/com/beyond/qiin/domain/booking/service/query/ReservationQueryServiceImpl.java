package com.beyond.qiin.domain.booking.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetAppliedReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.applied_reservation.GetAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.asset_time.AssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.asset_time.TimeSlotDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.month_reservation.MonthReservationDailyResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.month_reservation.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.month_reservation.MonthReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.reservable_asset.ReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.user_reservation.GetUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.week_reservation.WeekReservationDailyResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.week_reservation.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.week_reservation.WeekReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.repository.querydsl.AppliedReservationsQueryRepository;
import com.beyond.qiin.domain.booking.repository.querydsl.ReservableAssetsQueryRepository;
import com.beyond.qiin.domain.booking.repository.querydsl.UserReservationsQueryRepository;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.booking.util.AvailableTimeSlotCalculator;
import com.beyond.qiin.domain.booking.vo.DateRange;
import com.beyond.qiin.domain.booking.vo.TimeSlot;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationQueryServiceImpl implements ReservationQueryService {

    private final UserReader userReader;
    private final ReservationReader reservationReader;
    private final AssetQueryService assetQueryService;
    private final UserReservationsQueryRepository userReservationsQueryRepository;
    private final ReservableAssetsQueryRepository reservableAssetsQueryRepository;
    private final AppliedReservationsQueryRepository appliedReservationsQueryRepository;

    // 예약 상세 조회 (api용)
    @Override
    @Transactional(readOnly = true)
    public ReservationDetailResponseDto getReservation(final Long userId, final Long reservationId) {

        userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);

        ReservationDetailResponseDto reservationDetailResponseDto =
                ReservationDetailResponseDto.fromEntity(reservation);
        return reservationDetailResponseDto;
    }

    // 사용자 이름으로 예약 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<GetUserReservationResponseDto> getReservationsByUserId(
            final Long userId, final GetUserReservationSearchCondition condition, final Pageable pageable) {
        // 사용자 있는지 확인
        userReader.findById(userId);

        log.info(
                "date={}, status={}, approved={}, assetName={}, assetType={}, layerZero={}",
                condition.getDate(),
                condition.getReservationStatus(),
                condition.getIsApproved(),
                condition.getAssetName(),
                condition.getAssetType(),
                condition.getLayerZero());

        Page<RawUserReservationResponseDto> rawPage =
                userReservationsQueryRepository.search(userId, condition, pageable);

        Page<GetUserReservationResponseDto> page = rawPage.map(GetUserReservationResponseDto::fromRaw);

        return PageResponseDto.from(page);
    }

    // 신청 예약 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<GetAppliedReservationResponseDto> getReservationApplies(
            final Long userId, final GetAppliedReservationSearchCondition condition, Pageable pageable) {

        userReader.findById(userId);
        LocalDate date = condition.getDate();

        List<RawAppliedReservationResponseDto> rawList = appliedReservationsQueryRepository.search(condition);

        List<GetAppliedReservationResponseDto> appliedReservations = new ArrayList<>();

        for (RawAppliedReservationResponseDto raw : rawList) {

            boolean isReservable = isAssetReservableOnDate(raw.getAssetId(), date);

            appliedReservations.add(GetAppliedReservationResponseDto.fromRaw(raw, isReservable));
        }

        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), appliedReservations.size());

        Page<GetAppliedReservationResponseDto> page =
                new PageImpl<>(appliedReservations.subList(startIdx, endIdx), pageable, appliedReservations.size());

        return PageResponseDto.from(page);
    }

    // 예약 가능 자원 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<ReservableAssetResponseDto> getReservableAssets(
            final Long userId, final ReservableAssetSearchCondition condition, Pageable pageable) {

        userReader.findById(userId);

        List<RawReservableAssetResponseDto> rawList = reservableAssetsQueryRepository.search(condition);

        LocalDate date = condition.getDate();

        List<ReservableAssetResponseDto> reservableAssets = new ArrayList<>();

        for (RawReservableAssetResponseDto raw : rawList) {

            boolean isReservable = isAssetReservableOnDate(raw.getAssetId(), date);

            if (isReservable) {
                reservableAssets.add(ReservableAssetResponseDto.fromRaw(raw)); // 생성 조건이 true이므로 인자로 받지 않음
            }
        }

        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), reservableAssets.size());

        List<ReservableAssetResponseDto> pageContent = reservableAssets.subList(startIdx, endIdx);

        Page<ReservableAssetResponseDto> page = new PageImpl<>(pageContent, pageable, reservableAssets.size());

        return PageResponseDto.from(page);
    }

    // 주별 일정 조회
    @Override
    @Transactional(readOnly = true)
    public WeekReservationListResponseDto getWeeklyReservations(final Long userId, final LocalDate date) { // 해당 주의기준날짜
        userReader.findById(userId);

        LocalDate startDate = date.with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);

        Instant start = startDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Instant end = endDate.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

        List<Reservation> reservations = reservationReader.getReservationsByUserAndWeek(userId, start, end);

        Map<LocalDate, List<Reservation>> byDate = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStartAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDate()));

        // 비어있을 수 있음
        List<WeekReservationResponseDto> reservationList = new ArrayList<>();

        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {

            List<Reservation> dailyReservations = byDate.getOrDefault(cursor, List.of());

            WeekReservationResponseDto dto = WeekReservationResponseDto.builder()
                    .date(cursor)
                    .reservations(dailyReservations.stream()
                            .map(WeekReservationDailyResponseDto::fromEntity)
                            .toList())
                    .build();

            reservationList.add(dto);

            cursor = cursor.plusDays(1);
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
            final Long userId, final YearMonth yearMonth) { // 일까지 포함 X이므로 달까지 포함하는 자료형 사용
        userReader.findById(userId);

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        Instant from = start.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Instant to = end.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        // 비어있을 수 있음
        List<Reservation> reservations = reservationReader.getReservationsByUserAndYearMonth(userId, from, to);

        // 예약을 LocalDate 기준으로 그룹핑
        Map<LocalDate, List<Reservation>> byDate = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStartAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDate()));

        // 월의 모든 날짜 dto 생성
        List<MonthReservationResponseDto> dailyDtos = new ArrayList<>();

        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            List<Reservation> dailyReservations = byDate.getOrDefault(cursor, List.of());

            MonthReservationResponseDto dto = MonthReservationResponseDto.builder()
                    .date(cursor)
                    .reservations(dailyReservations.stream()
                            .map(MonthReservationDailyResponseDto::fromEntity) // 필요하면 inside DTO
                            .toList())
                    .build();

            dailyDtos.add(dto);

            cursor = cursor.plusDays(1);
        }

        return MonthReservationListResponseDto.builder().reservations(dailyDtos).build();
    }

    // 자원의 (예약 가능 시간대 포함) 모든 시간대 목록 조회
    @Override
    @Transactional(readOnly = true)
    public AssetTimeResponseDto getAssetTimes(final Long userId, final Long assetId, final LocalDate date) {

        userReader.findById(userId);

        assetQueryService.getAssetById(assetId);

        // 해당 자원의 해당 날짜 예약 목록
        List<Reservation> reservations = reservationReader.getReservationsByAssetAndDate(assetId, date);

        ZoneId zone = ZoneId.of("Asia/Seoul");

        List<TimeSlot> timeSlots = AvailableTimeSlotCalculator.calculateAvailableSlots(reservations, date, zone);

        List<TimeSlotDto> timeSlotDtos =
                timeSlots.stream().map(slot -> TimeSlotDto.create(slot, zone)).toList();

        return AssetTimeResponseDto.create(assetId, timeSlotDtos);
    }

    public DateRange dayToInstant(final String timezone, final LocalDate date) {
        ZoneId zone = ZoneId.of(timezone); // Asia/Seoul
        Instant startOfDay = date.atStartOfDay().atZone(zone).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant();
        return DateRange.create(startOfDay, endOfDay);
    }

    public DateRange monthToInstant(final String timezone, final YearMonth yearMonth) {
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

    private boolean isAssetReservableOnDate(Long assetId, LocalDate date) {

        // 날짜에 해당하는 예약만 조회하는 메서드
        List<Reservation> reservations = reservationReader.getReservationsByAssetAndDate(assetId, date);

        // 하루 기준 gap 존재 여부 판단
        return isReservableForDay(date, reservations);
    }

    private boolean isReservableForDay(LocalDate date, List<Reservation> reservations) {

        ZoneId zone = ZoneId.of("Asia/Seoul");

        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();

        if (reservations.isEmpty()) {
            return true;
        }

        reservations = reservations.stream()
                .sorted(Comparator.comparing(Reservation::getStartAt))
                .toList();

        if (reservations.get(0).getStartAt().isAfter(dayStart)) {
            return true;
        }

        for (int i = 0; i < reservations.size() - 1; i++) {
            Instant currentEnd = reservations.get(i).getEndAt();
            Instant nextStart = reservations.get(i + 1).getStartAt();

            if (currentEnd.isBefore(nextStart)) {
                return true;
            }
        }

        Instant lastEnd = reservations.get(reservations.size() - 1).getEndAt();
        return lastEnd.isBefore(dayEnd);
    }
}
