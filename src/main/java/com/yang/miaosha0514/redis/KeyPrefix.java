package com.yang.miaosha0514.redis;

public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
