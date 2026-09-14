package com.iwps.service;

import com.iwps.dto.GeminiResponse;

public interface AIService {
    String chat(String prompt);
    GeminiResponse generateImage(String prompt);
    GeminiResponse generateOpenAIImage(String prompt);
}