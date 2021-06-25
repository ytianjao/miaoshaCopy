package com.yang.miaosha0514.redis;

public class MiaoshaKey extends BasePrefix{

    private MiaoshaKey(int expireSecond, String prefix){
        super(expireSecond,prefix);
    }


    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"go");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"mp");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300,"vc");

}
