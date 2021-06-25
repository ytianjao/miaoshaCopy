package com.yang.miaosha0514.exception;

import com.yang.miaosha0514.result.CodeMsg;

public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 7774045299540022115L;

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
