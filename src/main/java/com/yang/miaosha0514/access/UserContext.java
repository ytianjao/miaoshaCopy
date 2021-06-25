package com.yang.miaosha0514.access;

import com.yang.miaosha0514.domain.MiaoShaUser;

public class UserContext {

    private static ThreadLocal<MiaoShaUser> userHolder = new ThreadLocal<>();

    public static void setUser(MiaoShaUser user){
        userHolder.set(user);
    }

    public static MiaoShaUser getUser(){
        return userHolder.get();
    }
}
