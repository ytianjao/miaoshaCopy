package com.yang.miaosha0514.access;

import com.alibaba.fastjson.JSON;
import com.yang.miaosha0514.domain.MiaoShaUser;
import com.yang.miaosha0514.redis.AccessKey;
import com.yang.miaosha0514.redis.RedisService;
import com.yang.miaosha0514.result.CodeMsg;
import com.yang.miaosha0514.result.Result;
import com.yang.miaosha0514.service.MiaoshaUserService;
import com.yang.miaosha0514.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){

            MiaoShaUser user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null){
                return true;
            }

            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if (needLogin){
                if (user == null){
                    render(response, CodeMsg.SERVER_ERROR);
                    return false;
                }
                key += "_" + user.getId();

            }

            AccessKey ak = AccessKey.withExpire(second);
            Integer count = redisService.get(ak, key, Integer.class);

            if (count == null){
                redisService.set(ak, key, 1);
            }else if (count < maxCount){
                redisService.incr(ak, key);
            }else {
                render(response, CodeMsg.ACCESS_LIMIT);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(cm);
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private MiaoShaUser getUser(HttpServletRequest request, HttpServletResponse response){
        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)? cookieToken: paramToken;
        return userService.getByToken(response,token);
    }


    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0){
            return null;
        }
        for (Cookie cookie:
                cookies) {
            if (cookie.getName().equals(cookieNameToken)){
                return cookie.getValue();
            }
        }

        return null;
    }
}
