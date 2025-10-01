package com.example.bankcards.security;

import com.example.bankcards.config.BankCardsRequestContext;
import com.example.bankcards.config.JwtProperty;
import com.example.bankcards.dto.UserContextDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.enumerate.Role;
import com.example.bankcards.exception.ApiException;
import com.example.bankcards.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static com.example.bankcards.enumerate.TokenType.ACCESS;
import static com.example.bankcards.enumerate.TokenType.REFRESH;


@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtProperty jwtProperty;
    private final UserService userServiceImpl;
    private final BankCardsRequestContext bankCardsRequestContext;

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS.name());
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH.name());
    }

    private String generateToken(User user, String type) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("role", user.getRole())
                .claim("type", type)
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(jwtProperty.getSecret().getBytes()))
                .compact();
    }

    public UserContextDto getUserIdFromToken(String token) {
        try {
            Claims claims = getPayloadByToken(token);
            UUID userId = UUID.fromString(claims.getSubject());
            String role = claims.get("role", String.class);

            return UserContextDto.builder()
                    .userId(userId)
                    .role(Role.valueOf(role))
                    .build();
        } catch (ExpiredJwtException e) {
            throw new ApiException("NotValidToken", "Token is expired",
                    "JwtTokenService.getUserIdFromToken", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new ApiException("NotValidToken", "Not valid token",
                    "JwtTokenService.getUserIdFromToken", HttpStatus.BAD_REQUEST);
        }
    }

    public void authenticateToken(String token, HttpServletRequest request) {
        UserContextDto userContextDto = getUserIdFromToken(token);
        User user = userServiceImpl.findById(userContextDto.getUserId());
        SecurityContextService.authenticateUserInContextHolder(user, request);
        bankCardsRequestContext.setUserId(userContextDto.getUserId());
        bankCardsRequestContext.setRole(userContextDto.getRole());
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getPayloadByToken(token);
        return claims.containsKey("type") && claims.get("type").equals(REFRESH.name());
    }

    private Claims getPayloadByToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtProperty.getSecret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}


