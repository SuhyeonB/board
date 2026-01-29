package com.example.board.global.security.jwt;

import com.example.board.domain.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";

    // 일단 AccessToken만 (10분)
    private static final long ACCESS_TOKEN_TIME = 10 * 60 * 1000L;

    @Value("${jwt.secret.key}")
    private String secretKey;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        // secretKey는 base64 인코딩된 문자열이어야 함
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT "순수 토큰"만 반환 (Bearer 붙이지 않음)
    public String createAccessToken(Long userId, String email, UserRole userRole) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(email) //  subject=email (UserDetailsService로 로드하기 쉬움)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_ROLE, userRole.name()) // 문자열로 저장
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // 토큰 검증 (유효하면 true)
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.debug("Invalid JWT signature/token", e);
        } catch (ExpiredJwtException e) {
            log.debug("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.debug("JWT claims string is empty", e);
        }
        return false;
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Long getUserId(String token) {
        return extractClaims(token).get(CLAIM_USER_ID, Long.class);
    }

    public UserRole getUserRole(String token) {
        String role = extractClaims(token).get(CLAIM_ROLE, String.class);
        return UserRole.of(role); // 너 enum of()가 ROLE_USER/ROLE_ADMIN만 받는 전제면 OK
    }
}
