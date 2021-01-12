package com.cmlx.transaction.oauth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;

/**
 * @Author cmlx
 * @Date -> 2021/1/12 14:35
 * @Desc ->
 **/
public class OauthTest {

    public static void main(String[] args) {

        // 登录密码
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("cmlx1218.");
        System.out.println(encode);

        // 登录客户端 Authorization
        base64("coachApp:AimyFitness");

    }

    /**
     * Base64
     */
    public static void base64(String str) {
        byte[] bytes = str.getBytes();

        //Base64 加密
        String encoded = Base64.getEncoder().encodeToString(bytes);
        System.out.println("Base 64 加密后：" + encoded);

        //Base64 解密
        byte[] decoded = Base64.getDecoder().decode(encoded);

        String decodeStr = new String(decoded);
        System.out.println("Base 64 解密后：" + decodeStr);

        System.out.println();


    }
}
