package com.oognuyh.kanban.security;

import java.time.Instant;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtils {
    private static final String SECRET_KEY = "kanban";
    private static final long EXPIRATION_TIME = 60 * 10;
    private static final long REFRESH_TIME = 60 * 60 * 24 * 7;
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);
    
    public static final String REFRESH_TOKEN_HEADER = "x-refresh-token";
    public static final String AUTH_TOKEN_HEADER = "x-auth-token";
    public static final String PREFIX = "Bearer ";

    public static String generateAuthToken(String email, String[] authorities) {
        return JWT.create()
            .withSubject(email)
            .withClaim("exp", Instant.now().getEpochSecond() + EXPIRATION_TIME)
            .withArrayClaim("authorities", authorities)
            .sign(ALGORITHM);
    }

    public static String generateRefreshToken(String email, String[] authorities) {
        return JWT.create()
            .withSubject(email)
            .withClaim("exp", Instant.now().getEpochSecond() + REFRESH_TIME)
            .withArrayClaim("authorities", authorities)
            .sign(ALGORITHM);
    }

    public static VerifiedResult verify(String token) {
        try {
            DecodedJWT verifiedToken = JWT.require(ALGORITHM)
                .build()
                .verify(token);
            
                return VerifiedResult.OK(verifiedToken.getSubject(), verifiedToken.getClaim("authorities").asArray(String.class));
        } catch (Exception e) {
            DecodedJWT decodedToken = JWT.decode(token);

            return VerifiedResult.ERROR(decodedToken.getSubject(), decodedToken.getClaim("authorities").asArray(String.class));
        }
    }
}
