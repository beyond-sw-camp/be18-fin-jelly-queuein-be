package com.beyond.qiin.infra.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiRequestDto {

    private String model;
    private List<OpenAiMessage> messages;
    private Double temperature;

}
