package com.example.bankcards.dto;

import com.example.bankcards.enumerate.CardStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private UUID id;
    @NotBlank(message = "Card number must not be blank")
    private String number;
    @NotBlank(message = "Expiration date must not be blank")
    private String expirationDate;
    private CardStatus status;
    private BigDecimal balance;
    private UUID userId;

    public void maskNumber() {
        if (number != null && number.length() >= 4) {
            int len = number.length();
            this.number = "*".repeat(len - 4) + number.substring(len - 4);
        }
    }
}