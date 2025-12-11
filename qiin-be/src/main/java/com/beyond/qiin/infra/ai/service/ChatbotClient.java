package com.beyond.qiin.infra.ai.service;

import com.beyond.qiin.domain.chat.dto.IntentResultDto;
import com.beyond.qiin.infra.ai.dto.OpenAiMessage;
import com.beyond.qiin.infra.ai.dto.OpenAiRequestDto;
import com.beyond.qiin.infra.ai.dto.OpenAiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ChatbotClient {

    private final RestClient openAiRestClient;

    private final ObjectMapper objectMapper;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.temperature}")
    private Double temperature;

    public String sendMessage(String userMessage) {

        // 요청 DTO 구성
        OpenAiRequestDto request = new OpenAiRequestDto();
        request.setModel(model);
        request.setTemperature(temperature);
        request.setMessages(List.of(
                new OpenAiMessage("system", "당신은 QueueIn 예약 시스템 비서입니다."), new OpenAiMessage("user", userMessage)));

        // API 호출
        OpenAiResponseDto response = openAiRestClient
                .post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(OpenAiResponseDto.class);

        return response.getChoices()[0].getMessage().getContent();
    }

    public IntentResultDto extractIntent(String userMessage) {

        // Intent 프롬프트
        String intentPrompt =
                """
                당신은 QueueIn 예약 시스템의 챗봇 분석 엔진입니다.
                사용자의 자연어 입력을 읽고, 아래 7개의 Intent 중 하나로 반드시 분류하여 JSON 형태로 출력하세요.

                반드시 아래 JSON 스키마를 사용하세요:

                {
                  "intent": "INTENT_NAME",
                  "params": { ... }
                }

                ========================================
                [가능한 Intent 목록과 규칙]
                ========================================

                1) LIST_ASSETS_BY_CATEGORY
                - 사용자가 "카테고리 X"라고 말하며 해당 카테고리의 자원 목록을 요청할 때 사용합니다.
                - params: { "categoryId": number }

                2) GET_ASSET_DETAIL
                - 특정 자원의 상세 정보를 요청할 때 사용합니다.
                - params: { "assetId": number, "assetName": string }
                - assetId 또는 assetName 중 하나만 있어도 됩니다.

                3) LIST_AVAILABLE_ASSETS
                - 사용자가 “예약 가능한”, “사용 가능한”, “빌릴 수 있는” 등의 표현을 사용하여 자원을 찾을 때 선택합니다.
                - params: { "categoryId": number (optional), "keyword": string (optional) }

                4) GET_ASSET_LOCATION
                - 사용자가 특정 자원의 위치(상위 카테고리 경로)를 물어볼 때 사용합니다.
                - params: { "assetId": number, "assetName": string }

                5) GET_ASSET_STATUS
                - 사용자가 특정 자원의 현재 상태(예: AVAILABLE, UNAVAILABLE 등)를 물어볼 때 사용합니다.
                - params: { "assetId": number, "assetName": string }

                6) LIST_ALL_CATEGORIES
                - 사용자가 전체 카테고리 목록을 요청할 때 사용합니다.
                - params: {}

                7) UNKNOWN  (매우 중요)
                - 아래 조건 중 하나라도 해당되면 반드시 UNKNOWN을 선택하세요:
                  - 질문이 자원/카테고리/상태/위치/예약 가능 여부와 관련이 없음
                  - 위 Intent 목록 중 어느 것에도 명확하게 속하지 않음
                  - 사용자가 주제를 변경하여 일반 대화(“너는 뭐야?”, “날씨 어때?” 등)를 한 경우
                  - 자원/카테고리/번호를 언급했지만 의도를 판단할 수 없는 경우
                - params: {}

                ========================================
                [출력 규칙]
                ========================================
                - JSON 외 텍스트는 절대 출력하지 마십시오.
                - intent는 반드시 위 목록 중 정확히 하나를 선택하십시오.
                - 숫자는 숫자로, 문자열은 문자열로 출력하십시오.
                - assetId, categoryId는 숫자로만 출력하십시오.
                - 이름 기반 요청은 params.assetName에 그대로 넣으십시오.
                - 해석이 불가능하면 반드시 UNKNOWN을 선택하십시오.

                ========================================
                [예시]
                ========================================

                입력: "카테고리 8번 자원 목록 보여줘"
                출력:
                {
                  "intent": "LIST_ASSETS_BY_CATEGORY",
                  "params": { "categoryId": 8 }
                }

                입력: "카메라 A 어디 있어?"
                출력:
                {
                  "intent": "GET_ASSET_LOCATION",
                  "params": { "assetName": "카메라 A" }
                }

                입력: "자원 3번 상태 알려줘"
                출력:
                {
                  "intent": "GET_ASSET_STATUS",
                  "params": { "assetId": 3 }
                }

                입력: "전체 카테고리 좀 보여줘"
                출력:
                {
                  "intent": "LIST_ALL_CATEGORIES",
                  "params": {}
                }

                입력: "오늘 날씨 어때?"
                출력:
                {
                  "intent": "UNKNOWN",
                  "params": {}
                }

                반드시 JSON만 출력하십시오.

        """;

        // 요청 DTO 구성
        OpenAiRequestDto request = new OpenAiRequestDto();
        request.setModel(model);
        request.setTemperature(0.0); // Intent 분석은 항상 deterministic
        request.setMessages(List.of(new OpenAiMessage("system", intentPrompt), new OpenAiMessage("user", userMessage)));

        // API 호출
        OpenAiResponseDto response = openAiRestClient
                .post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(OpenAiResponseDto.class);

        String raw = response.getChoices()[0].getMessage().getContent();

        // ==== JSON 정제 ====
        String json = cleanJson(raw);

        try {
            // ==== JSON → DTO 변환 ====
            return objectMapper.readValue(json, IntentResultDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Intent JSON 파싱 실패: " + json, e);
        }
    }

    private String cleanJson(String raw) {

        if (raw == null) {
            throw new RuntimeException("LLM 응답이 비어있습니다.");
        }

        String cleaned = raw.replace("```json", "").replace("```", "").trim();

        int start = cleaned.indexOf("{");
        int end = cleaned.lastIndexOf("}");

        if (start == -1 || end == -1) {
            throw new RuntimeException("LLM 응답에서 JSON 블록을 찾을 수 없습니다: " + cleaned);
        }

        return cleaned.substring(start, end + 1);
    }
}
