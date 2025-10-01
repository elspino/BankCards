package com.example.bankcards.dto;

import com.example.bankcards.enumerate.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserContextDto {
    private UUID userId;
    private Role role;
}