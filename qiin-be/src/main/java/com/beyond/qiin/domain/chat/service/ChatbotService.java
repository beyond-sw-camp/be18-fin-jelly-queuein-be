package com.beyond.qiin.domain.chat.service;

import com.beyond.qiin.domain.chat.dto.IntentResultDto;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
import com.beyond.qiin.domain.inventory.service.query.CategoryQueryService;
import com.beyond.qiin.infra.ai.service.ChatbotClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatbotClient chatbotClient;

    private final AssetQueryService assetQueryService;
    private final CategoryQueryService categoryQueryService;

    private final ObjectMapper objectMapper;

    public String ask(String message) {
        return chatbotClient.sendMessage(message);
    }

    public String handleUserMessage(String userMessage) {

        // 1) Intent 분석 요청
        IntentResultDto intentResult = chatbotClient.extractIntent(userMessage);

        // 2) Intent Dispatcher 실행
        return dispatchIntent(intentResult);
    }

    private String dispatchIntent(IntentResultDto intentResult) {

        String intent = intentResult.intent();

        switch (intent) {
            case "LIST_ASSETS_BY_CATEGORY":
                return handleListAssetsByCategory(intentResult);

                //            case "GET_ASSET_DETAIL":
                //                return handleGetAssetDetail(intentResult);
                //
                //            case "LIST_AVAILABLE_ASSETS":
                //                return handleListAvailableAssets(intentResult);
                //
                //            case "GET_ASSET_LOCATION":
                //                return handleGetAssetLocation(intentResult);
                //
                //            case "GET_ASSET_STATUS":
                //                return handleGetAssetStatus(intentResult);
                //
                //            case "LIST_ALL_CATEGORIES":
                //                return handleListAllCategories();

            case "UNKNOWN":
            default:
                return handleUnknown();
        }
    }

    // 카테고리에 속한 자원 조회
    private String handleListAssetsByCategory(IntentResultDto intent) {
        Long categoryId = extractLong(intent.params().get("categoryId"));

        var assets = assetQueryService.findAssetsByCategory(categoryId);

        System.out.println("categoryId = " + categoryId);
        System.out.println("assets = " + toJson(assets));

        return chatbotClient.sendMessage(
                """
            다음은 카테고리 %d의 자원 목록입니다. 사용자가 이해하기 쉽게 자연스럽게 요약해주세요:
            %s
            """
                        .formatted(categoryId, toJson(assets)));
    }

    //    // 특정 자원 상세 조회
    //    private String handleGetAssetDetail(IntentResultDto intent) {
    //        Long assetId = extractLong(intent.params().get("assetId"));
    //        String assetName = (String) intent.params().get("assetName");
    //
    //        var asset = (assetId != null)
    //                ? assetQueryService.getAssetDetail(assetId)
    //                : assetQueryService.findAssetDetailByName(assetName);
    //
    //        return chatbotClient.sendMessage("""
    //            아래 자원의 상세 정보를 사용자에게 자연스럽게 설명해주세요:
    //            %s
    //            """.formatted(toJson(asset)));
    //    }
    //
    //    // 자원 상태가 예약 가능인 자원 조회
    //    private String handleListAvailableAssets(IntentResultDto intent) {
    //        Integer categoryId = (Integer) intent.params().get("categoryId");
    //        String keyword = (String) intent.params().get("keyword");
    //
    //        var assets = assetQueryService.findAvailableAssets(categoryId, keyword);
    //
    //        return chatbotClient.sendMessage("""
    //            아래는 예약 가능한 자원 목록입니다. 사용자에게 자연스럽게 설명해주세요:
    //            %s
    //            """.formatted(toJson(assets)));
    //    }
    //
    //    // 자원의 위치 조회
    //    private String handleGetAssetLocation(IntentResultDto intent) {
    //        Long assetId = extractLong(intent.params().get("assetId"));
    //        String assetName = (String) intent.params().get("assetName");
    //
    //        Long id = (assetId != null)
    //                ? assetId
    //                : assetQueryService.findIdByName(assetName);
    //
    //        var path = assetQueryService.findParentPath(id);
    //
    //        return chatbotClient.sendMessage("""
    //            이 자원의 전체 위치 경로를 자연스럽게 설명해주세요:
    //            %s
    //            """.formatted(toJson(path)));
    //    }
    //
    //    // 자원 상태 조회
    //    private String handleGetAssetStatus(IntentResultDto intent) {
    //        Long assetId = extractLong(intent.params().get("assetId"));
    //        String assetName = (String) intent.params().get("assetName");
    //
    //        Long id = (assetId != null)
    //                ? assetId
    //                : assetQueryService.findIdByName(assetName);
    //
    //        var status = assetQueryService.findStatus(id);
    //
    //        return chatbotClient.sendMessage("""
    //            이 자원의 상태를 사용자에게 이해하기 쉽게 설명해주세요:
    //            %s
    //            """.formatted(toJson(status)));
    //    }
    //
    //    private String handleListAllCategories() {
    //        var categories = categoryQueryService.findAllCategories();
    //
    //        return chatbotClient.sendMessage("""
    //            아래는 전체 카테고리 목록입니다. 사용자에게 자연스럽게 요약해주세요:
    //            %s
    //            """.formatted(toJson(categories)));
    //    }

    private String handleUnknown() {
        return "죄송합니다, 이해하지 못했어요. 다시 질문해 주세요!";
    }

    // ==========================
    // 유틸
    // ==========================

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    private Long extractLong(Object value) {
        return value == null ? null : Long.valueOf(value.toString());
    }
}
