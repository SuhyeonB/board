package com.example.board.global.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlImageExtractor {

    private static final Pattern IMG_SRC_PATTERN = Pattern.compile("<img[^>]+src=[\"'](/files/[^\"']+)[\"']");

    private static final String PREFIX = "/files/";

    public static List<String> extractKeys(String html) {
        List<String> keys = new ArrayList<>();
        if (html == null || html.isBlank()) return keys;

        Matcher matcher = IMG_SRC_PATTERN.matcher(html);

        while (matcher.find()) {
            String src = matcher.group(1);
            if (src.startsWith(PREFIX)) {
                String key = src.substring(PREFIX.length());
                keys.add(key);
            }
        }

        return keys;
    }
}
