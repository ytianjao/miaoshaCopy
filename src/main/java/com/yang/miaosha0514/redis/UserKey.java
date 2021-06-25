package com.yang.miaosha0514.redis;

import com.yang.miaosha0514.domain.User;

public class UserKey extends BasePrefix{

    private UserKey(String prefix){
        super(prefix);
    }


    public static UserKey getById = new UserKey("id");

    public static UserKey getByName = new UserKey("name");
}
