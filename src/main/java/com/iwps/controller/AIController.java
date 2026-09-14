package com.iwps.controller;

import com.iwps.dto.ChatRequest;
import com.iwps.dto.GeminiResponse;
import com.iwps.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AIController {

    private final AIService aiService;

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return aiService.chat(request.getPrompt());
    }

    @PostMapping("/image")
    public GeminiResponse generateImage(
            @RequestBody ChatRequest request) {

        return aiService.generateImage(
                request.getPrompt()
        );
    }
    @PostMapping("/openai/image")
    public ResponseEntity<?> generateOpenAIImage(
            @RequestBody ChatRequest request) {

        try {

            GeminiResponse response =
                    aiService.generateOpenAIImage(
                            request.getPrompt()
                    );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", e.getMessage()
                    ));
        }
    }


    @PostMapping(
            value = "/openai/image/test",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<?> testOpenAIImage(
            @RequestBody ChatRequest request) {

        try {

            GeminiResponse response =
                    aiService.generateOpenAIImage(
                            request.getPrompt()
                    );

            String base64 =
                    response.getSteps()
                            .get(0)
                            .getContent()
                            .get(0)
                            .getData();

            if (base64 == null || base64.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "No image data was returned by OpenAI."
                        ));
            }

            byte[] imageBytes =
                    Base64.getDecoder().decode(base64);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", e.getMessage()
                    ));
        }
    }



}