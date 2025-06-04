package com.jackson.modules.app.cache;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private Integer errorNum;
    /**
     * 过期时间，毫秒值。
     */
    private Long expireTime;

    public CacheModel() {
    }
    public CacheModel(String userId, Integer errorNum, Long expireTime) {
        this.userId = userId;
        this.errorNum = errorNum;
        this.expireTime = expireTime;
    }
}