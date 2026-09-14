package com.iwps.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeminiApiResponse {

    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;
    }

    @Data
    public static class Content {
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private InlineData inlineData;
    }

    @Data
    public static class InlineData {
        private String mimeType;
        private String data;
    }
}
