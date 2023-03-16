package com.xxfs.fsapigateway.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xxfs.fsapicommon.model.entity.User;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

/**
 * @author admin
 */
public class JWTUtils {

    /**
     * 获取token
     *
     * @param user
     * @return token
     */
    public static String getToken(User user) {
        Calendar instance = Calendar.getInstance();
        //默认令牌过期时间7天
        instance.add(Calendar.DATE, 7);

        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("userId", user.getId())
                .withClaim("username", user.getUserName());

        return builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(user.getSecretKey()));
    }

    /**
     * 验证token合法性 成功返回token
     */
    public static DecodedJWT verify(String token, String secretKey) throws Exception {
        if (StringUtils.isEmpty(token)) {
            throw new Exception("token不能为空");
        }

        //获取登录用户真正的密码假如数据库查出来的是123456
        JWTVerifier build = JWT.require(Algorithm.HMAC256(secretKey)).build();
        return build.verify(token);
    }

}

