package com.hitices.analyzer.common;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangteng
 * @e-mail 1638235292@qq.com
 * @date 2023/5/1
 */
@Getter
public class MResponse<T> {
    private String message;
    private int code;
    private T data;

    public static final int successCode = 0;
    public static final int failedCode = 1;

    private Map<String, Object> valueMap = new HashMap<>();

    public Object get(String key) {
        return this.valueMap.getOrDefault(key, null);
    }

    public MResponse<T> set(String key, Object value) {
        this.valueMap.put(key, value);
        return this;
    }

    public MResponse<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public MResponse<T> setStatus(String status) {
        this.message = status;
        return this;
    }

    public static MResponse successMResponse() {
        return new MResponse().setCode(successCode).setStatus("success");
    }

    public static MResponse failedMResponse() {
        return new MResponse().setCode(failedCode).setStatus("failed");
    }

    public MResponse<T> data(T data) {
        this.data = data;
        return this;
    }
}
