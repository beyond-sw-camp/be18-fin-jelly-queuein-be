package com.beyond.qiin.domain.booking.reservation.service.query;

import com.beyond.qiin.domain.booking.dto.reservation.response.AssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.TimeSlotDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationException;
import com.beyond.qiin.domain.booking.reservation.repository.ReservationJpaRepository;
import com.beyond.qiin.domain.booking.reservation.repository.querydsl.AppliedReservationsQueryRepository;
import com.beyond.qiin.domain.booking.reservation.repository.querydsl.ReservableAssetsQueryRepository;
import com.beyond.qiin.domain.booking.reservation.repository.querydsl.UserReservationsQueryRepository;
import com.beyond.qiin.domain.booking.reservation.util.AvailableTimeSlotCalculator;
import com.beyond.qiin.domain.booking.reservation.vo.DateRange;
import com.beyond.qiin.domain.booking.reservation.vo.TimeSlot;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
import com.beyond.qiin.infra.redis.reservation.ReservationRedisRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationQueryServiceImpl implements ReservationQueryService {
    private final ReservationJpaRepository reservationJpaRepository;
    private final UserReader userReader;
    private final AssetQueryService assetQueryService;
    private final UserReservationsQueryRepository userReservationsQueryRepository;
    private final ReservableAssetsQueryRepository reservableAssetsQueryRepository;
    private final AppliedReservationsQueryRepository appliedReservationsQueryRepository;
    private final ReservationRedisRepository reservationRedisRepository;

    // 예약 상세 조회 (api용)
    @Override
    @Transactional(readOnly = true)
    public ReservationDetailResponseDto getReservation(final Long userId, final Long reservationId) {

        userReader.findById(userId);
        Reservation reservation = getReservationById(reservationId);

        ReservationDetailResponseDto reservationDetailResponseDto =
                ReservationDetailResponseDto.fromEntity(reservation);
        return reservationDetailResponseDto;
    }
    //
    //    // 사용자 이름으로 예약 목록 조회
    //    @Override
    //    @Transactional(readOnly = true)
    //    public PageResponseDto<GetUserReservationResponseDto> getReservationsByUserId(
    //            final Long userId, final GetUserReservationSearchCondition condition, final Pageable pageable) {
    //        // 사용자 있는지 확인
    //        userReader.findById(userId);
    //
    //        // condition 자체의 필드들은 모두 null 가능
    //        // status는 int가 아닌 Integer이 됨
    //        // Integer reservationStatus = statusToInt(condition.getReservationStatus());
    //
    //        // Page<GetUserReservationResponseDto> page =
    //        //        userReservationsQueryRepository.search(userId, condition, reservationStatus, pageable);
    //        log.info(
    //                "date={}, status={}, approved={}, assetName={}, assetType={}, layerZero={}",
    //                condition.getDate(),
    //                condition.getReservationStatus(),
    //                condition.getIsApproved(),
    //                condition.getAssetName(),
    //                condition.getAssetType(),
    //                condition.getLayerZero());
    //
    //        Page<RawUserReservationResponseDto> rawPage =
    //                userReservationsQueryRepository.search(userId, condition, pageable);
    //
    //        Page<GetUserReservationResponseDto> page = rawPage.map(GetUserReservationResponseDto::fromRaw);
    //
    //        return PageResponseDto.from(page);
    //
    //        //        List<Reservation> reservations = getReservationsByUserAndDate(userId, date);
    //        //        List<GetUserReservationResponseDto> reservationList = new ArrayList<>();
    //        //        for (Reservation reservation : reservations) {
    //        //            GetUserReservationResponseDto getUserReservationResponseDto =
    //        //                    GetUserReservationResponseDto.fromEntity(reservation,
    //        // statusToString(reservation.getStatus()));
    //        //            reservationList.add(getUserReservationResponseDto);
    //        //        }
    //        //
    //        //        GetUserReservationListResponseDto reservationListResponseDto =
    //        // GetUserReservationListResponseDto.builder()
    //        //                .reservations(reservationList)
    //        //                .build();
    //        //
    //        //        return reservationListResponseDto;
    //    }
    //
    //    // TODO : 목록 조회 - querydsl 대상
    //    // 예약 가능 자원 목록 조회
    //    @Override
    //    @Transactional(readOnly = true)
    //    public PageResponseDto<ReservableAssetResponseDto> getReservableAssets(
    //            final Long userId, final ReservableAssetSearchCondition condition, Pageable pageable) {
    //
    //        userReader.findById(userId);
    //
    //        Page<RawReservableAssetResponseDto> rawPage = reservableAssetsQueryRepository.search(condition, pageable);
    //
    //        Page<ReservableAssetResponseDto> page = rawPage.map(ReservableAssetResponseDto::fromRaw);
    //
    //        return PageResponseDto.from(page);
    //
    //        //        // 사용 가능 상태의 자원들을 가져옴 - 빌 수 있음
    //        //        List<Asset> assets = assetRepository.findAvailableAssets();
    //        //
    //        //        // 예약 가능한 자원들을 담는 용도
    //        //        List<ReservableAssetResponseDto> responseList = new ArrayList<>();
    //        //
    //        //        for (Asset asset : assets) {
    //        //            // 해당 날짜의 해당 자원의 목록 조회
    //        //            List<Reservation> reservations = getReservationsByAssetAndDate(asset.getId(), date);
    //        //
    //        //            // 모든 시간에 대해 예약이 차있지 않은 경우만 추가
    //        //            boolean reservableAsset =
    //        //                    AvailableTimeSlotCalculator.isReservable(reservations, date,
    // ZoneId.of("Asia/Seoul"));
    //        //
    //        //            if (reservableAsset) {
    //        //                // 시간대가 있는 경우 해당 자원에 대해 dto 추가
    //        //
    //        //                ReservableAssetResponseDto reservableAssetResponseDto =
    //        //                        ReservableAssetResponseDto.fromEntity(asset, statusToString(asset.getType()));
    //        //                responseList.add(reservableAssetResponseDto);
    //        //            }
    //        //        }
    //        //
    //        //        ReservableAssetListResponseDto reservationListResponseDto =
    // ReservableAssetListResponseDto.builder()
    //        //                .reservations(responseList)
    //        //                .build();
    //        //
    //        //        return reservableAssetListResponseDto;
    //    }
    //
    //    // 신청 예약 목록 조회
    //    @Override
    //    @Transactional(readOnly = true)
    //    public PageResponseDto<GetAppliedReservationResponseDto> getReservationApplies(
    //            final Long userId, final GetAppliedReservationSearchCondition condition, Pageable pageable) {
    //        userReader.findById(userId);
    //
    //        Page<RawAppliedReservationResponseDto> rawPage = appliedReservationsQueryRepository.search(condition,
    // pageable);
    //
    //        Page<GetAppliedReservationResponseDto> page = rawPage.map(GetAppliedReservationResponseDto::fromRaw);
    //
    //        return PageResponseDto.from(page);
    //
    //        //
    //        //        // status == pending인 경우
    //        //        List<Reservation> reservations = getReservationsPendingAndDate(date);
    //        //
    //        //        List<GetAppliedReservationResponseDto> reservationList = new ArrayList<>();
    //        //
    //        //        for (Reservation reservation : reservations) {
    //        //            GetAppliedReservationResponseDto reservationResponseDto =
    //        //                GetAppliedReservationResponseDto.fromEntity(reservation,
    //        // isAssetReservableToday(reservation.getAssetId()));
    //        //            reservationList.add(reservationResponseDto);
    //        //        }
    //        //
    //        //        GetAppliedReservationResponseDto reservationListResponseDto =
    //        // GetAppliedReservationResponseDto.builder()
    //        //            .reservations(reservationList)
    //        //            .build();
    //        //
    //        //        return reservationListResponseDto;
    //    }

    // 주별 일정 조회
    @Override
    @Transactional(readOnly = true)
    public WeekReservationListResponseDto getWeeklyReservations(
            final Long userId, final Instant start, final Instant end) { // 해당 주의 기준날짜
        userReader.findById(userId);

        // 비어있을 수 있음
        List<WeekReservationResponseDto> reservationList = new ArrayList<>();

        List<Reservation> reservations = getReservationsByUserAndWeek(userId, start, end);

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
            final Long userId, final Instant from, final Instant to) { // 일까지 포함 X이므로 달까지 포함하는 자료형 사용
        userReader.findById(userId);

        // 비어있을 수 있음
        List<Reservation> reservations = getReservationsByUserAndYearMonth(userId, from, to);

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
    public AssetTimeResponseDto getAssetTimes(final Long userId, final Long assetId, final LocalDate date) {

        userReader.findById(userId);

        Asset asset = assetQueryService.getAssetById(assetId);

        // 해당 자원의 해당 날짜 예약 목록
        List<Reservation> reservations = getReservationsByAssetAndDate(assetId, date);

        ZoneId zone = ZoneId.of("Asia/Seoul");

        List<TimeSlot> timeSlots = AvailableTimeSlotCalculator.calculateAvailableSlots(reservations, date, zone);

        List<TimeSlotDto> timeSlotDtos =
                timeSlots.stream().map(slot -> TimeSlotDto.create(slot, zone)).toList();

        return AssetTimeResponseDto.create(assetId, timeSlotDtos);
    }

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
    public List<Reservation> getReservationsByUserAndYearMonth(
            final Long userId, final Instant from, final Instant to) {

        // userId 유효한지 확인
        userReader.findById(userId);

        List<Reservation> reservations = reservationJpaRepository.findByUserIdAndYearMonth(userId, from, to);
        return reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUserAndWeek(final Long userId, final Instant start, final Instant end) {
        userReader.findById(userId);
        List<Reservation> reservations = reservationJpaRepository.findByUserIdAndWeek(userId, start, end);

        return reservations;
    }

    // 자원 목록 (예외처리 포함)
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByAssetId(final Long assetId) {
        // assetId 유효한지 확인
        assetQueryService.getAssetById(assetId);

        List<Reservation> reservations = reservationJpaRepository.findByAssetId(assetId);
        return reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByAssetAndDate(final Long assetId, final LocalDate date) {

        // assetId 유효한지 확인
        Asset asset = assetQueryService.getAssetById(assetId);

        ZoneId zone = ZoneId.of("Asia/Seoul");

        Instant startOfDay = date.atStartOfDay(zone).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant();

        return reservationJpaRepository.findAllByAssetIdAndDate(
            assetId,
            startOfDay,
            endOfDay
        );
    }

    // status 자체는 null x이므로 int
    public static String statusToString(final int status) {
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

    public static Integer statusToInt(final String status) {
        if (status == null || status.isBlank()) return null;

        return switch (status.toUpperCase()) {
            case "PENDING" -> 0;
            case "APPROVED" -> 1;
            case "USING" -> 2;
            case "REJECTED" -> 3;
            case "CANCELED" -> 4;
            case "COMPLETED" -> 5;
            default -> null;
        };
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

    public boolean isAssetReservableToday(Long assetId) {

        Instant start =
                LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        Instant end = LocalDate.now()
                .plusDays(1)
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        List<Reservation> reservations = getReservationsByAssetId(assetId);

        if (reservations.isEmpty()) return true;

        // 시작 시간 순으로 정렬
        reservations.sort(Comparator.comparing(Reservation::getStartAt).reversed());

        // TODO: 첫 예약이 오늘 시작 시간 이후면 앞쪽에 빈 구간 있음
        if (reservations.get(0).getStartAt().isAfter(start)) return true;

        for (int i = 0; i < reservations.size(); i++) {
            Instant currentEnd = reservations.get(i).getEndAt();
            Instant nextStart = reservations.get(i + 1).getStartAt();

            if (currentEnd.isBefore(nextStart)) {
                return true; // 사이에 빈 구간 존재
            }
        }

        Instant lastEnd = reservations.get(reservations.size() - 1).getEndAt();
        if (lastEnd.isBefore(end)) {
            return true;
        }
        return false;
    }

    //    // TODO : querydsl 시 x
    //    @Override
    //    @Transactional(readOnly = true)
    //    public Page<Reservation> getReservationsByUserAndDate(
    //            final Long userId, final LocalDate date, final Pageable pageable) {
    //
    //        userReader.findById(userId);
    //
    //        DateRange dateRange = dayToInstant("Asia/Seoul", date);
    //
    //        //        List<Reservation> reservations =
    //        //                reservationJpaRepository.findByUserIdAndDate(userId, dateRange.getStartDay(),
    // getEndDay());
    //        //        return reservations;
    //
    //        return reservationJpaRepository.findByUserIdAndDate(
    //                userId, dateRange.getStartDay(), dateRange.getEndDay(), pageable);
    //    }

    //    // TODO : querydsl 시 x
    //    @Override
    //    @Transactional(readOnly = true)
    //    public Page<Reservation> getReservationsPendingAndDate(final Long userId, final LocalDate date, Pageable
    // pageable) {
    //        userReader.findById(userId);
    //        DateRange dateRange = dayToInstant("Asia/Seoul", date);
    //
    //        return reservationJpaRepository.findAllWithStatusPendingAndDate(
    //                dateRange.getStartDay(), dateRange.getEndDay(), pageable);
    //    }
}
