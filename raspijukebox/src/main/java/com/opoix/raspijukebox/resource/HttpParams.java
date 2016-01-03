package com.opoix.raspijukebox.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class HttpParams {
    private Map<String, List<String>> params;

    private String getSingle(String key) {
        List<String> result = params.get(key);
        if (result == null || result.size() == 0) {
            return null;
        }
        return result.get(0);

    }

    private <T> T defaultVal(T val, T defaultVal, Class<T> clazz) {
        if (val == null) {
            return defaultVal;
        }
        return val;
    }

    public List<String> get(String key) {
        return params.get(key);
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Boolean result = Boolean.parseBoolean(getSingle(key));
        return defaultVal(result, defaultValue, Boolean.class);
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        Long result = Long.parseLong(getSingle(key));
        return defaultVal(result, defaultValue, Long.class);
    }

}
