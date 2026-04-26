package com.job.portal.util;

public class InputValidator {

    /**
     * Returns false if input contains dangerous patterns.
     * Used to reject obviously malicious inputs.
     */
    public static boolean isClean(String input) {
        if (input == null) return true;
        String lower = input.toLowerCase();
        return !lower.contains("<script>")
            && !lower.contains("</script>")
            && !lower.contains("javascript:");
    }

    /**
     * Escapes special characters so they render as visible text,
     * not as executable HTML/JavaScript.
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }
}
