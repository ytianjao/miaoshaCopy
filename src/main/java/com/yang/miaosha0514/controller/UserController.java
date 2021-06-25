package com.yang.miaosha0514.controller;

import com.yang.miaosha0514.domain.MiaoShaUser;
import com.yang.miaosha0514.domain.MiaoshaOrder;
import com.yang.miaosha0514.domain.OrderInfo;
import com.yang.miaosha0514.result.CodeMsg;
import com.yang.miaosha0514.result.Result;
import com.yang.miaosha0514.service.GoodsService;
import com.yang.miaosha0514.service.MiaoshaService;
import com.yang.miaosha0514.service.OrderService;
import com.yang.miaosha0514.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;


    @Autowired
    private MiaoshaService miaoshaService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoShaUser> miaosha(Model model, MiaoShaUser user
                          ) {
        model.addAttribute("user", user);
        return Result.sucess(user);
    }
}
