package com.iwps.utility;

public final class ResponseCleaner {

    private ResponseCleaner() {
    }

    public static String clean(String text) {

        if (text == null || text.isBlank()) {
            return text;
        }

        return text
                // Remove bold / italic markdown
                .replace("**", "")
                .replace("*", "")

                // Remove markdown headings
                .replaceAll("(?m)^#{1,6}\\s*", "")

                // Remove markdown bullet symbols
                .replaceAll("(?m)^\\s*[-•]\\s+", "")

                // Remove extra blank lines
                .replaceAll("\\n{3,}", "\n\n")

                .trim();
    }
}
