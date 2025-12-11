package com.beyond.qiin.domain.notification.util;

import com.beyond.qiin.domain.notification.enums.NotificationType;

public class NotificationPayloadFactory {
    public static String createPayload(NotificationType type, Object data) {
        switch (type) {
            case RESERVATION_APPROVED:
                return createApprovedPayload(data);
            case RESERVATION_REJECTED:
                return createRejectedPayload(data);
            case RESERVATION_PLANNED:
                return createPlannedPayload(data);
            case RESERVATION_CREATED:
                return createCreatedPayload(data);
            case RESERVATION_UNAVAILABLE:
                return createUnavailablePayload(data);
            default:
                throw new IllegalArgumentException("Unknown NotificationType: " + type);
        }
    }

    private static String createApprovedPayload(Object data) {
        // 데이터를 기반으로 RESERVATION_APPROVED 타입에 맞는 JSON 생성
        return "{\"status\": \"approved\", \"data\": \"" + data.toString() + "\"}";
    }

    private static String createRejectedPayload(Object data) {
        return "{\"status\": \"rejected\", \"data\": \"" + data.toString() + "\"}";
    }

    private static String createPlannedPayload(Object data) {
        return "{\"status\": \"planned\", \"data\": \"" + data.toString() + "\"}";
    }

    private static String createCreatedPayload(Object data) {
        return "{\"status\": \"created\", \"data\": \"" + data.toString() + "\"}";
    }

    private static String createUnavailablePayload(Object data) {
        return "{\"status\": \"unavailable\", \"data\": \"" + data.toString() + "\"}";
    }
}
