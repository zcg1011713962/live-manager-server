package com.hs.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET_KEY = "CrOVilM_mD071w6qXybjTWoLIKcpnJtB";

    public static String createJWT(String roomId, String appId, long currentTimeInSeconds) {


        long expirationTimeInSeconds = currentTimeInSeconds + 300;
        // 设置算法和密钥
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withHeader(Map.of(
                        "typ", "JWT",  // 设置头部字段 typ
                        "alg", "HS256" // 设置头部字段 alg
                ))
                .withClaim("roomId", roomId)
                .withClaim("appId", appId)
                .withClaim("iat", currentTimeInSeconds)
                .withClaim("exp", expirationTimeInSeconds)
                .sign(algorithm);
    }


    public static void main(String[] args) {
        String appId = "tmp_nS4HrIbi2F";
        String roomId = "32429581";
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;

        String token = JwtUtil.createJWT(roomId, appId, currentTimeInSeconds);
        System.out.println(currentTimeInSeconds);
        System.out.println(currentTimeInSeconds + 300);
        System.out.println(token);
    }

}
