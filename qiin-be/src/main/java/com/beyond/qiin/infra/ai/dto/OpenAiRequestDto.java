package com.beyond.qiin.infra.ai.dto;

import java.util.List;
import lombok.Data;

@Data
public class OpenAiRequestDto {

    private String model;
    private List<OpenAiMessage> messages;
    private Double temperature;
}
