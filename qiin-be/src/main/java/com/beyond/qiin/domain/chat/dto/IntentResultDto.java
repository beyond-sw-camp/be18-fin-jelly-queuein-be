package com.beyond.qiin.domain.chat.dto;

import java.util.Map;

public record IntentResultDto (
    String intent,
    Map<String, Object> params
) { }
