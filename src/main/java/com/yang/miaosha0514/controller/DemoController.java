package com.yang.miaosha0514.controller;


import com.yang.miaosha0514.domain.User;
import com.yang.miaosha0514.rabbitmq.MQSender;
import com.yang.miaosha0514.redis.RedisService;
import com.yang.miaosha0514.redis.UserKey;
import com.yang.miaosha0514.result.Result;
import com.yang.miaosha0514.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/demo")
@Controller
public class DemoController {


    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;


    @Autowired
    MQSender mqSender;


    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topic(){
        mqSender.sendTopic("hello, world");
        return Result.sucess("hello, world");
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        mqSender.send("hello, world");
        return Result.sucess("hello, world");
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "yang1");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.sucess(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbtx(){
        userService.tx();
        return Result.sucess(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById,"" + 1, User.class);
        return Result.sucess(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User();
        user.setName("11111");
        user.setId(1);
        redisService.set(UserKey.getById, "" + 1, user);
        return Result.sucess(true);
    }
}
