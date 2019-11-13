package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanglei
 */
@SpringBootTest
public class JwtTest {

    private static final String pubKeyPath = "D:\\packageLocal\\ideaWorkHome\\temp\\rsa.pub";

    private static final String priKeyPath = "D:\\packageLocal\\ideaWorkHome\\temp\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "where is family!!!!");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1NzM1MzUyNTd9.pZHNWS-orROw1wl3jsBTjhZjFk8Zt5p2h_i3fzf1LF9tBput4Df_Cpi0Yoz2xQ4cN-MgUHTrCQGMazev82iMDH6bLTgq5MYcYwr9VaWHb-FZwqJlkkHXdR7w-FSoykBGh6vS4Ae8DNRGXmP-C7v573Uo52xSyzKhei4EbURmxC2aloupjf_kGJlN-yagLMwmtXl2Vreq5CErEONM6FMVpWARnAVXHT8OTTOVo-uuYNZxF6MtQ6l33aW2kdlztulXgxrKWA1HgCPR3hL9kl32v85QCGOf-JFcvbRzlT7XGKfh2xpIkqMO0WkJXOS6RAuW5WshqkmiLY2cpbnW6P7XBQ";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }

}
