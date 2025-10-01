package com.example.bankcards.controller;


import com.example.bankcards.dto.AuthRequestDTO;
import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public void register(@RequestBody @Valid AuthRequestDTO dto) {
        authService.register(dto);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    public TokenDTO login(@RequestBody @Valid AuthRequestDTO dto) {
        return authService.login(dto);
    }
}


