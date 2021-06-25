package com.yang.miaosha0514.controller;


import com.yang.miaosha0514.domain.MiaoShaUser;
import com.yang.miaosha0514.redis.GoodsKey;
import com.yang.miaosha0514.redis.RedisService;
import com.yang.miaosha0514.result.Result;
import com.yang.miaosha0514.service.GoodsService;
import com.yang.miaosha0514.service.MiaoshaUserService;
import com.yang.miaosha0514.vo.GoodsDetailVo;
import com.yang.miaosha0514.vo.GoodsVo;
import com.yang.miaosha0514.vo.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {


    @Autowired
    private MiaoshaUserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    private static Logger logger = LoggerFactory.getLogger(GoodsController.class);
//    @RequestMapping("/to_list")
//    public String list(HttpServletResponse response,Model model,
////                          @CookieValue(value = MiaoshaUserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
////                          @RequestParam(value = MiaoshaUserService.COOKIE_NAME_TOKEN, required = false) String paramToken,
//                       MiaoShaUser user){
////        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
////            return "login";
////        }
////        String token = StringUtils.isEmpty(paramToken)? cookieToken: paramToken;
////        MiaoShaUser user = userService.getByToken(response,token);
//        model.addAttribute("user", user);
//
//        List<GoodsVo> goodsList = goodsService.listGoodsVo();
//        model.addAttribute("goodsList", goodsList);
//        return "goods_list";
//    }

    //5000*10
    //
    // 缓存前QPS3000
    //缓存后5000*10QPS7000
    //1000*10QPS11600
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String list(Model model, MiaoShaUser user,
                       HttpServletRequest request, HttpServletResponse response){
        model.addAttribute("user", user);

//        return "goods_list";

        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);

        IWebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(),
                model.asMap());

        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList, "", html);
        }

        return html;

    }


    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response,
                         Model model, MiaoShaUser user, @PathVariable("goodsId") long goodsId){
        model.addAttribute("user", user);

        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatu = 0;

        int remainSec = 0;
        if (now < startAt){
            miaoshaStatu = 0;
            remainSec = (int) ((startAt - now)/1000);
        }else if (now > endAt){
            miaoshaStatu = 2;
            remainSec = -1;
        }else {
            miaoshaStatu = 1;
            remainSec = 0;

        }

        model.addAttribute("miaoshaStatu", miaoshaStatu);
        model.addAttribute("remainSec", remainSec);


        IWebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(),
                model.asMap());

        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
        }
        return html;
    }



    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                        Model model, MiaoShaUser user, @PathVariable("goodsId") long goodsId){

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatu = 0;

        int remainSec = 0;
        if (now < startAt){
            miaoshaStatu = 0;
            remainSec = (int) ((startAt - now)/1000);
        }else if (now > endAt){
            miaoshaStatu = 2;
            remainSec = -1;
        }else {
            miaoshaStatu = 1;
            remainSec = 0;

        }

        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setMiaoshaStatu(miaoshaStatu);
        vo.setRemainSec(remainSec);
        vo.setUser(user);

        return Result.sucess(vo);
    }

}
