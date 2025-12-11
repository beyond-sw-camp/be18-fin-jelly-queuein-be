package com.beyond.qiin.infra.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpenAiMessage {
    private String role; // "user", "assistant", "system"
    private String content;
}
