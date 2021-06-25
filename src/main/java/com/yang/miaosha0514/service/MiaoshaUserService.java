package com.yang.miaosha0514.service;

import com.yang.miaosha0514.dao.MiaoshaUserDao;
import com.yang.miaosha0514.domain.MiaoShaUser;
import com.yang.miaosha0514.exception.GlobalException;
import com.yang.miaosha0514.redis.MiaoshaUserKey;
import com.yang.miaosha0514.redis.RedisService;
import com.yang.miaosha0514.result.CodeMsg;
import com.yang.miaosha0514.utils.MD5Utils;
import com.yang.miaosha0514.utils.UUIDUtil;
import com.yang.miaosha0514.vo.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoShaUser getById(long id){
        MiaoShaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoShaUser.class);
        if (user != null){
            return user;
        }
        user = miaoshaUserDao.getById(id);
        if (user != null){
            redisService.set(MiaoshaUserKey.getById, "" + id, MiaoShaUser.class);
        }

        return user;
    }

    public boolean updatePassword(String token, long id, String formPass){
        MiaoShaUser user = getById(id);
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        MiaoShaUser toBeUpdate = new MiaoShaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Utils.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);

        redisService.delete(MiaoshaUserKey.getById, "" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    public String login(HttpServletResponse response, LoginVO loginVO) {
        if (loginVO == null){
//            return CodeMsg.SERVER_ERROR;

            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();

        MiaoShaUser user = getById(Long.parseLong(mobile));
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDB = user.getSalt();

        String calPass = MD5Utils.formPassToDBPass(password, saltDB);

        if (!calPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASS_ERROR);
        }
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    public MiaoShaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }

        MiaoShaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoShaUser.class);
        if (user != null){
            addCookie(response, token, user);
        }

        return user;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoShaUser user){

        redisService.set(MiaoshaUserKey.token, token, user);

        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);

        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}
