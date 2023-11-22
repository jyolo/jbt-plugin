package com.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CacheUtil {
    private static final Map<String, AutocompleteResponse> COMPLETION_CACHE = new HashMap<>();
    private static final Map<String, String> LAST_BEFORE = new HashMap<>();
    private static final String lastBefore = "lastBefore";
    private static final String latestCompletion = "latestCompletion";
    public static List<Map<String, Object>> languageExtensions = new ArrayList<>();

    public static void cacheCompletion(AutocompleteResponse value) { COMPLETION_CACHE.put(latestCompletion, value); }

    public static AutocompleteResponse getCacheCompletion() {
        return COMPLETION_CACHE.get(latestCompletion);
    }

    public static void cacheLastBefore(String value) { LAST_BEFORE.put(lastBefore, value); }

    public static String getCacheLastBefore() {
        return LAST_BEFORE.get(lastBefore);
    }
}
