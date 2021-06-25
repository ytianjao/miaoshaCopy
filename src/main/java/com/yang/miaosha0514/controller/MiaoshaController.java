package com.yang.miaosha0514.controller;

import com.yang.miaosha0514.access.AccessLimit;
import com.yang.miaosha0514.domain.MiaoShaUser;
import com.yang.miaosha0514.domain.MiaoshaOrder;
import com.yang.miaosha0514.domain.OrderInfo;
import com.yang.miaosha0514.rabbitmq.MQSender;
import com.yang.miaosha0514.rabbitmq.MiaoShaMessage;
import com.yang.miaosha0514.redis.*;
import com.yang.miaosha0514.result.CodeMsg;
import com.yang.miaosha0514.result.Result;
import com.yang.miaosha0514.service.GoodsService;
import com.yang.miaosha0514.service.MiaoshaService;
import com.yang.miaosha0514.service.OrderService;
import com.yang.miaosha0514.utils.MD5Utils;
import com.yang.miaosha0514.utils.UUIDUtil;
import com.yang.miaosha0514.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;


    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }

        for (GoodsVo goods :
                goodsVoList) {
            redisService.set(GoodsKey.getMiaoshaStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoShaUser user,
                                   @RequestParam("goodsId") long goodsId,
                                   @PathVariable("path") String path) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        boolean check = miaoshaService.check(user, goodsId, path);

        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        Boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        long stock = redisService.decr(GoodsKey.getMiaoshaStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        MiaoShaMessage message = new MiaoShaMessage();
        message.setGoodsId(goodsId);
        message.setUser(user);

        mqSender.sendMiaoshaMessage(message);

        return Result.sucess(0);
//
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        int count = goods.getStockCount();
//        if (count <= 0) {
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
//
//        MiaoshaOrder order = orderService.getMiaoShaOrderByUserIdGoodsId(user.getId(), goodsId);
//        if (order != null) {
//            return Result.error(CodeMsg.MIAO_REPEATE);
//        }
//
//        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
//        return Result.sucess(orderInfo);


    }

    @AccessLimit(second = 5, maxCount = 10, needLogin = true)
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoShaUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.sucess(result);
    }


    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        for (GoodsVo goods :
                goodsVoList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaStock, "" + goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }

        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsVoList);
        return Result.sucess(true);
    }


    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request,MiaoShaUser user,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam("verifyCode") int verifyCode) {

        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        String uri = request.getRequestURI();
        String key = uri + "_" + user.getId();
//        Integer count = redisService.get(AccessKey.access, key, Integer.class);
//
//        if (count == 0){
//            redisService.set(AccessKey.access, key, 1);
//        }else if (count < 5){
//            redisService.incr(AccessKey.access, key);
//        }else {
//            return Result.error(CodeMsg.ACCESS_LIMIT);
//        }

        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        String path = miaoshaService.createMiaoshaPath(user, goodsId);


        return Result.sucess(path);
    }


    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, Model model, MiaoShaUser user,
                                               @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);

        try {
            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        return null;

    }


}
