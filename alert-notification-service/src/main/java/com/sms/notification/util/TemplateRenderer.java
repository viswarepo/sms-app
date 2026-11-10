package com.sms.notification.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal {{placeholder}} substitution. Deliberately not a full templating
 * engine (no loops/conditionals) - notification bodies are short and this
 * keeps the service dependency-free for something this simple.
 */
public final class TemplateRenderer {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([\\w.]+)\\s*}}");

    private TemplateRenderer() {
    }

    public static String render(String template, Map<String, String> variables) {
        if (template == null) {
            return null;
        }
        Map<String, String> vars = variables == null ? Map.of() : variables;

        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = vars.getOrDefault(key, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
