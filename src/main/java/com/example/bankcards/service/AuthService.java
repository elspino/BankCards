package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequestDTO;
import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ApiException;
import com.example.bankcards.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userServiceImpl;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public void register(AuthRequestDTO dto) {
        if (userServiceImpl.existsByUsername(dto.getUsername())) {
            throw new ApiException("UserExists", "User already exists", "AuthService.register", HttpStatus.BAD_REQUEST);
        }
        userServiceImpl.createUser(dto);
    }

    public TokenDTO login(AuthRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        return new TokenDTO(
                jwtTokenService.generateAccessToken(user),
                jwtTokenService.generateRefreshToken(user)
        );
    }
}


