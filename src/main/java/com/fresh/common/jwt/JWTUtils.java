package com.fresh.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 *JWS token认证
 * 1、后端系统提供认证(登录)接口，认证通过后签发token
 * 2、定义认证注解、切面/拦截器，校验token
 *
 *compare with arch-oauth2-auth's spring security + OAuth2 的 authorization-server实现
 */
public abstract class JWTUtils {

    /**
     * 1、确认通信方: 请求方携带token，服务方校验token
     * 2、信息明文: token加密
     * 3、信息篡改: token加密时自定义的密钥串
     * 4、token被拦截: https整个报文加密
     */

    /**
     * 不同用户签发的token不同，同一用户先后签发的token不同
     * 认证接口签发token，并判断是否携带token访问认证接口
     * 定义认证注解，切面/拦截器，获取并校验token
     *
     * claims字段
     *  1、token的claims中存用户基本不会改变的字段，如userId,phone...
     *  2、一般不存容易改变的字段，如username,password,email
     *  3、这就意味着token中只能拿到userId,phone等这些有限的字段
     *
     * token过期
     *  1、token设置过期时间，校验token是否过期  or
     *  2、token的过期由redis控制: token存放到redis，并设置过期时间           √
     * token多端在线
     *  1、同一用户先后签发的token不同
     *  2、同一用户后面签发的token顶掉redis中先签发的token
     */

    private static final String TOKEN_SECRET = "anNvbl93ZWJfdG9rZW4=";
    private static Logger log = LoggerFactory.getLogger(JWTUtils.class);

    public static String token(String userId, String phone) {
        String token = null;
        try {
            LocalDateTime localDateTime = LocalDateTime.now();
            Date issueAt = new Date(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            //long expireTime = localDateTime.plusSeconds(CommonConstants.TOKEN_EXPIRE).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            //Date expireAt = new Date(expireTime);
            token = JWT.create()
                    .withIssuedAt(issueAt)
                    //.withExpiresAt(expireAt)
                    .withClaim("userId", userId)
                    .withClaim("phone", phone)
                    .sign(getAlgorithm(userId));
        } catch (Exception e) {
            log.error("创建token失败, {}", e);
            token = null;
        }
        return token;
    }

    //获取token签发日期
    public static Date getIssuedAt(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getIssuedAt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
    //获取token中userId
    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
    //获取token中phone
    public static String getPhone(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("phone").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static void verify(String token, String userId) {
         JWTVerifier verifier = JWT.require(getAlgorithm(userId)).build();
         verifier.verify(token);
    }

    public static void main(String argv[]) {
        String token = JWTUtils.token("1", "15623236821");
        System.out.println(token);

        try {
            JWTUtils.verify(token, "1");
        } catch(TokenExpiredException e) {
            //token已过期
            System.out.println("token已过期");
        } catch(Exception e) {
            System.out.println("token校验失败");
        }

        System.out.println(LocalDateTime.ofInstant(JWTUtils.getIssuedAt(token).toInstant(), ZoneId.systemDefault()));
        System.out.println(JWTUtils.getUserId(token));
        System.out.println(JWTUtils.getPhone(token));

    }


    private static Algorithm getAlgorithm(String userId) {
        Algorithm algorithm = Algorithm.HMAC256(userId + "." + TOKEN_SECRET);
        return algorithm;
    }
}
