package com.yang.miaosha0514.controller;

import com.yang.miaosha0514.domain.MiaoShaUser;
import com.yang.miaosha0514.domain.OrderInfo;
import com.yang.miaosha0514.redis.RedisService;
import com.yang.miaosha0514.result.CodeMsg;
import com.yang.miaosha0514.result.Result;
import com.yang.miaosha0514.service.GoodsService;
import com.yang.miaosha0514.service.OrderService;
import com.yang.miaosha0514.vo.GoodsVo;
import com.yang.miaosha0514.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {


    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;


    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderVo> info(Model model, MiaoShaUser user, @RequestParam("orderId") long orderId){
        if (user == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }

        Long goodsId = order.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        OrderVo orderVo = new OrderVo();
        orderVo.setGoodsVo(goodsVo);
        orderVo.setOrderInfo(order);
        return Result.sucess(orderVo);
    }
}
