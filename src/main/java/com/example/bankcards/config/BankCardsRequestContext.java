package com.example.bankcards.config;

import com.example.bankcards.enumerate.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Getter
@Setter
@Component
@RequestScope
@AllArgsConstructor
@NoArgsConstructor
public class BankCardsRequestContext {
    private UUID userId;
    private Role role;
}
