package com.iwps.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIImageResponse {

    private List<ImageData> data;

    @Data
    public static class ImageData {

        private String b64_json;

        private String url;
    }
}
