package com.yang.miaosha0514.redis;

public class GoodsKey extends BasePrefix{

    private GoodsKey(int expireSeconds, String prefix){
        super(expireSeconds,prefix);
    }


    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd");
    public static GoodsKey getMiaoshaStock = new GoodsKey(0,"gs");

}
