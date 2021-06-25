package com.yang.miaosha0514.redis;

public class AccessKey extends BasePrefix{

    private AccessKey(int expireSecond, String prefix){
        super(expireSecond,prefix);
    }


    public static AccessKey access = new AccessKey(5,"access");

    public static AccessKey withExpire(int expireSecond){
        return new AccessKey(expireSecond, "access");
    }

}
