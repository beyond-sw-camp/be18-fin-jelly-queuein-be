package com.beyond.qiin.infra.ai.dto;

import lombok.Data;

@Data
public class OpenAiResponseDto {

    private Choice[] choices;

    @Data
    public static class Choice {
        private Message message;

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }
}
