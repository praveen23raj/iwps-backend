package com.iwps.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AiConfig {

    @Value("${ai.anthropic.api-key}")
    private String anthropicApiKey;

    @Value("${ai.anthropic.base-url}")
    private String anthropicBaseUrl;

    @Value("${ai.anthropic.model}")
    private String anthropicModel;

    @Value("${ai.gemini.api-key}")
    private String geminiApiKey;

    @Value("${ai.gemini.base-url}")
    private String geminiBaseUrl;

    @Value("${ai.gemini.model}")
    private String geminiModel;

    @Value("${ai.openai.api-key}")
    private String openaiApiKey;

    @Value("${ai.openai.base-url}")
    private String openaiBaseUrl;

    @Value("${ai.openai.model}")
    private String openaiModel;

}
