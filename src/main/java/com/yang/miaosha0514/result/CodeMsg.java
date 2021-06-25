package com.yang.miaosha0514.result;

public class CodeMsg {
    private int code;
    private String msg;

    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "sucess");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验错误: %s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "请求错误");
    public static CodeMsg ACCESS_LIMIT = new CodeMsg(500103, "请求频繁");



    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500201, "密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500202, "手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500203, "手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500204, "手机号不存在");
    public static CodeMsg PASS_ERROR = new CodeMsg(500205, "密码错误");




    public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "商品秒杀完毕");
    public static CodeMsg MIAO_REPEATE = new CodeMsg(500501, "不能重复秒杀");
    public static CodeMsg MIAO_Fail = new CodeMsg(500502, "秒杀失败");


    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500401, "订单不存在");

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
}
