package com.yang.miaosha0514.controller;


import com.yang.miaosha0514.result.CodeMsg;
import com.yang.miaosha0514.result.Result;
import com.yang.miaosha0514.service.MiaoshaUserService;
import com.yang.miaosha0514.utils.ValidatUtil;
import com.yang.miaosha0514.vo.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {


    @Autowired
    private MiaoshaUserService userService;
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);
    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVO loginVO){
        logger.info(loginVO.toString());

//        String mobile = loginVO.getMobile();
//        String inputPass = loginVO.getPassword();
//        if (StringUtils.isEmpty(inputPass)){
//            return Result.error(CodeMsg.PASSWORD_EMPTY);
//        }
//
//        if (StringUtils.isEmpty(mobile)){
//            return Result.error(CodeMsg.MOBILE_EMPTY);
//        }
//
//        if (!ValidatUtil.isMobile(mobile)){
//            return Result.error(CodeMsg.MOBILE_ERROR);
//        }

        String token = userService.login(response, loginVO);
//        if (cm.getCode() == 0){
//            return Result.sucess(true);
//        }else {
//            return Result.error(cm);
//        }
        return Result.sucess(token);

    }
}
