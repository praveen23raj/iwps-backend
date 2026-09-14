package com.iwps.serviceImpl;

import com.google.genai.Client;
import com.iwps.config.AiConfig;
import com.iwps.dto.ClaudeResponse;
import com.iwps.dto.GeminiApiResponse;
import com.iwps.dto.GeminiResponse;
import com.iwps.dto.OpenAIImageResponse;
import com.iwps.service.AIService;
import com.iwps.utility.ResponseCleaner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final RestTemplate restTemplate;
    private final AiConfig config;

    @Override
    public String chat(String prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", config.getAnthropicApiKey());
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> body = Map.of(
                "model", config.getAnthropicModel(),
                "max_tokens", 500,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<ClaudeResponse> response =
                restTemplate.postForEntity(
                        config.getAnthropicBaseUrl(),
                        entity,
                        ClaudeResponse.class
                );

        String text = response.getBody()
                .getContent()
                .get(0)
                .getText();

        return ResponseCleaner.clean(text);
    }


    @Override
    public GeminiResponse generateImage(String prompt) {

        String url = config.getGeminiBaseUrl()
                + "/models/"
                + config.getGeminiModel()
                + ":generateContent?key="
                + config.getGeminiApiKey();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "responseModalities", List.of("IMAGE")
                )
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<GeminiApiResponse> response =
                restTemplate.postForEntity(
                        url,
                        entity,
                        GeminiApiResponse.class
                );

        return convertToGeminiResponse(response.getBody());
    }

    private GeminiResponse convertToGeminiResponse(
            GeminiApiResponse response) {

        GeminiResponse result = new GeminiResponse();

        List<GeminiResponse.Step> steps = new ArrayList<>();

        if (response == null ||
                response.getCandidates() == null) {

            result.setSteps(steps);
            return result;
        }

        for (GeminiApiResponse.Candidate candidate :
                response.getCandidates()) {

            GeminiResponse.Step step =
                    new GeminiResponse.Step();

            step.setType("image_generation");

            List<GeminiResponse.Content> contents =
                    new ArrayList<>();

            if (candidate.getContent() != null &&
                    candidate.getContent().getParts() != null) {

                for (GeminiApiResponse.Part part :
                        candidate.getContent().getParts()) {

                    if (part.getInlineData() != null) {

                        GeminiResponse.Content content =
                                new GeminiResponse.Content();

                        content.setType("image");

                        content.setData(
                                part.getInlineData().getData()
                        );

                        contents.add(content);
                    }
                }
            }

            step.setContent(contents);
            steps.add(step);
        }

        result.setSteps(steps);

        return result;
    }


    @Override
    public GeminiResponse generateOpenAIImage(String prompt) {

        String url = config.getOpenaiBaseUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getOpenaiApiKey());

        Map<String, Object> body = Map.of(
                "model", config.getOpenaiModel(),
                "prompt", prompt,
                "size", "1024x1024"
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        try {

            ResponseEntity<OpenAIImageResponse> response =
                    restTemplate.postForEntity(
                            url,
                            entity,
                            OpenAIImageResponse.class
                    );

            return convertOpenAIResponse(response.getBody());

        } catch (HttpClientErrorException.BadRequest e) {

            String errorBody = e.getResponseBodyAsString();

            System.out.println("OpenAI Image Error: " + errorBody);

            if (errorBody.contains("moderation_blocked")) {

                throw new RuntimeException(
                        "This image request was blocked by OpenAI's safety system. " +
                                "Please try a different image prompt."
                );
            }

            throw e;
        }
    }


    private GeminiResponse convertOpenAIResponse(
            OpenAIImageResponse response) {

        GeminiResponse result = new GeminiResponse();

        List<GeminiResponse.Step> steps = new ArrayList<>();

        GeminiResponse.Step step =
                new GeminiResponse.Step();

        step.setType("image_generation");

        List<GeminiResponse.Content> contents =
                new ArrayList<>();

        if (response != null &&
                response.getData() != null &&
                !response.getData().isEmpty()) {

            for (OpenAIImageResponse.ImageData image :
                    response.getData()) {

                if (image.getB64_json() != null) {

                    GeminiResponse.Content content =
                            new GeminiResponse.Content();

                    content.setType("image");
                    content.setData(image.getB64_json());

                    contents.add(content);
                }
            }
        }

        step.setContent(contents);

        steps.add(step);

        result.setSteps(steps);

        return result;
    }



}