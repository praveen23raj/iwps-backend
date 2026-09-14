package com.iwps.dto;

import lombok.Data;
import java.util.List;

@Data
public class ClaudeResponse {

    private List<Content> content;

    @Data
    public static class Content {
        private String text;
    }
}