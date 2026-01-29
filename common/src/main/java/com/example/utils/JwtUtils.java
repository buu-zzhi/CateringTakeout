package com.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    public static String generateJwt(Map<String, Object> claims, String signKey, Long expire) {
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }

    public static Claims parseJwt(String jwt, String signKey) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signKey)
                .build() // ← 注意这里
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
}
