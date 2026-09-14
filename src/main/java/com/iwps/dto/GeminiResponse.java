package com.iwps.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GeminiResponse {

    private List<Step> steps;

    @Data
    public static class Step {
        private String type;
        private List<Content> content;
    }

    @Data
    public static class Content {
        private String type;
        private String data;
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

