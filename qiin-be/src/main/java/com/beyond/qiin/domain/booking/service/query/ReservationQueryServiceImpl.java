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
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.reservable_asset.ReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.user_reservation.GetUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.week_reservation.WeekReservationDailyResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.week_reservation.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.week_reservation.WeekReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.exception.ReservationException;
import com.beyond.qiin.domain.booking.repository.querydsl.AppliedReservationsQueryRepository;
import com.beyond.qiin.domain.booking.repository.querydsl.ReservationQueryRepository;
import com.beyond.qiin.domain.booking.repository.querydsl.UserReservationsQueryRepository;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.booking.util.AvailableTimeSlotCalculator;
import com.beyond.qiin.domain.booking.vo.DateRange;
import com.beyond.qiin.domain.booking.vo.TimeSlot;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.dto.asset.request.search_condition.AssetSearchCondition;
import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawDescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.enums.AssetType;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetQueryRepository;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable; //jakarta.persistence는 jpa 2차 캐시용

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
    private final AppliedReservationsQueryRepository appliedReservationsQueryRepository;
    private final AssetQueryRepository assetQueryRepository;
    private final ReservationQueryRepository reservationQueryRepository;

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
    @Cacheable(
            value = "user-reservations",
            key = "'user:' + #userId + ':' + #CacheKeyGenerator.cacheKey() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize"
    )
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

        List<RawAppliedReservationResponseDto> rawList = appliedReservationsQueryRepository.search(condition);

        List<GetAppliedReservationResponseDto> appliedReservations = new ArrayList<>();

        for (RawAppliedReservationResponseDto raw : rawList) {

            boolean isAssetAvailable = assetQueryService.isAvailable(raw.getAssetId());

            // 해당 시간대에 예약 가능 유무 판별
            boolean isReservableTime = isReservationTimeAvailable(
                    raw.getReservationId(), raw.getAssetId(), raw.getStartAt(), raw.getEndAt());

            // 모든 신청 내역을 보여주되 가능한지에 대해 정보 제공
            boolean isReservable = isAssetAvailable && isReservableTime;
            appliedReservations.add(GetAppliedReservationResponseDto.fromRaw(raw, isReservable));
        }

        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), appliedReservations.size());

        if (startIdx >= appliedReservations.size()) {
            return PageResponseDto.from(new PageImpl<>(Collections.emptyList(), pageable, appliedReservations.size()));
        }

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

        // asset query repo 사용하기 위한 condition 생성
        AssetSearchCondition assetSearchCondition = new AssetSearchCondition();
        assetSearchCondition.setKeyword(condition.getAssetName());
        assetSearchCondition.setType(condition.getAssetType());
        assetSearchCondition.setStatus(condition.getAssetStatus());
        assetSearchCondition.setRoot(condition.getLayerZero());
        assetSearchCondition.setOneDepth(condition.getLayerOne());
        assetSearchCondition.setCategoryId(condition.getCategoryId());

        // 자원 목록 가져옴
        List<RawDescendantAssetResponseDto> rawList =
                assetQueryRepository.searchDescendantsAsList(assetSearchCondition);

        // 해당 날짜의 예약 가능성 확인용
        LocalDate date = condition.getDate();

        //        List<ReservableAssetResponseDto> filtered = rawList.stream()
        //                .filter(raw -> isAssetReservableOnDate(raw.getAssetId(), date))
        //                .filter(raw -> assetQueryService.isAvailable(raw.getAssetId()))
        //                .map(this::toReservableAssetResponse)
        //                .toList();

        // 해당 날짜에 예약 가능한 시간이 있는 경우

        // 해당 날짜에 자원 상태가 사용 가능인 경우

        if (rawList.isEmpty()) {
            return PageResponseDto.from(new PageImpl<>(List.of(), pageable, 0));
        }

        List<Long> assetIds =
                rawList.stream().map(RawDescendantAssetResponseDto::getAssetId).toList();

        ZoneId zone = ZoneId.of("Asia/Seoul");
        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();

        Map<Long, Integer> assetStatusMap = assetQueryRepository.findStatusMapByIds(assetIds);

        Map<Long, List<Reservation>> reservationMap =
                reservationQueryRepository.findByAssetIdsAndTimeRange(assetIds, dayStart, dayEnd);

        List<ReservableAssetResponseDto> filtered = rawList.stream()
                .filter(raw -> {
                    Integer status = assetStatusMap.get(raw.getAssetId());
                    return status != 1 && status != 2;
                })
                .filter(raw -> {
                    List<Reservation> reservations = reservationMap.getOrDefault(raw.getAssetId(), List.of());
                    return isReservableForDay(date, reservations);
                })
                .map(this::toReservableAssetResponse)
                .toList();

        int total = filtered.size();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<ReservableAssetResponseDto> pageContent = start >= total ? List.of() : filtered.subList(start, end);

        Page<ReservableAssetResponseDto> page = new PageImpl<>(pageContent, pageable, total);

        return PageResponseDto.from(page);
    }

    public ReservableAssetResponseDto toReservableAssetResponse(RawDescendantAssetResponseDto raw) {
        return ReservableAssetResponseDto.builder()
                .isReservable(true)
                .assetId(raw.getAssetId())
                .assetType(AssetType.fromCode(raw.getType()).toName())
                .assetName(raw.getName())
                .categoryName(raw.getCategoryName())
                .needsApproval(raw.getNeedApproval())
                .build();
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

    boolean isReservableForDay(LocalDate date, List<Reservation> reservations) {

        ZoneId zone = ZoneId.of("Asia/Seoul");

        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();

        if (reservations.isEmpty()) {
            return true;
        }

        List<Reservation> sorted = reservations.stream()
                .sorted(Comparator.comparing(Reservation::getStartAt))
                .toList();

        Instant coveredStart = sorted.get(0).getStartAt();
        Instant coveredEnd = sorted.get(0).getEndAt();

        if (coveredStart.isBefore(dayStart)) coveredStart = dayStart;
        if (coveredEnd.isAfter(dayEnd)) coveredEnd = dayEnd;

        for (int i = 1; i < sorted.size(); i++) {
            Instant s = sorted.get(i).getStartAt();
            Instant e = sorted.get(i).getEndAt();

            if (s.isAfter(coveredEnd)) {
                return true; // gap 존재 → 하루 전체를 덮지 못함
            }

            if (e.isAfter(coveredEnd)) {
                coveredEnd = e;
                if (coveredEnd.isAfter(dayEnd)) {
                    coveredEnd = dayEnd;
                }
            }
        }

        // 하루 전체를 덮었는지 확인
        return !(coveredStart.equals(dayStart) && coveredEnd.equals(dayEnd));
    }

    // api x 비즈니스 메서드
    private void validateReservationAvailability(
            final Long reservationId, final Long assetId, final Instant startAt, final Instant endAt) {
        if (!isReservationTimeAvailable(reservationId, assetId, startAt, endAt))
            throw new ReservationException(ReservationErrorCode.RESERVE_TIME_DUPLICATED);
    }

    // test 가능하도록 package private 허용
    // 자원에 대한 예약 가능의 유무 -  비즈니스 책임이므로 command service로
    boolean isReservationTimeAvailable(
            final Long reservationId, final Long assetId, final Instant startAt, final Instant endAt) {

        List<Reservation> reservations = reservationReader.getActiveReservationsByAssetId(assetId);

        for (Reservation reservation : reservations) {

            if (reservationId != null) { // 생성시는 null
                if (reservation.getId().equals(reservationId)) {
                    continue;
                }
            }

            Instant existingStart = reservation.getStartAt();
            Instant existingEnd = reservation.getEndAt();

            // 딱 맞닿는 경우는 허용
            if (startAt.equals(existingEnd) || endAt.equals(existingStart)) {
                continue;
            }

            // 겹침 체크
            boolean overlaps = startAt.isBefore(existingEnd) && endAt.isAfter(existingStart);

            if (overlaps) return false;
        }

        return true;
    }
}
