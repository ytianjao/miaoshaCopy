package com.yang.miaosha0514.vo;

import com.yang.miaosha0514.domain.MiaoShaUser;

public class GoodsDetailVo {
    private int miaoshaStatu;

    private int remainSec;

    private GoodsVo goods;

    private MiaoShaUser user;

    public int getMiaoshaStatu() {
        return miaoshaStatu;
    }

    public void setMiaoshaStatu(int miaoshaStatu) {
        this.miaoshaStatu = miaoshaStatu;
    }

    public int getRemainSec() {
        return remainSec;
    }

    public void setRemainSec(int remainSec) {
        this.remainSec = remainSec;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public MiaoShaUser getUser() {
        return user;
    }

    public void setUser(MiaoShaUser user) {
        this.user = user;
    }
}
