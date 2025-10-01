package com.example.bankcards.controller;

import com.example.bankcards.config.BankCardsRequestContext;
import com.example.bankcards.dto.CardBalance;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.enumerate.CardStatus;
import com.example.bankcards.enumerate.Role;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final BankCardsRequestContext bankCardsRequestContext;

    @Operation(summary = "Получить карту по id")
    @GetMapping("/{id}")
    public CardDto getCardById(@PathVariable UUID id) {
        return cardService.findDtoById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новую карту")
    @PostMapping
    public CardDto createCard(@RequestBody @Valid CardDto cardDto) {
        return cardService.create(cardDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить карту")
    @PutMapping
    public CardDto updateCard(@RequestBody @Valid CardDto cardDto) {
        return cardService.update(cardDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить карту по id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable UUID id) {
        cardService.delete(id);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Перевод между картами")
    @PostMapping("/transfer")
    public void transfer(@RequestParam UUID fromCardId,
                         @RequestParam UUID toCardId,
                         @RequestParam BigDecimal amount) {
        UUID userId = bankCardsRequestContext.getUserId();
        cardService.transfer(fromCardId, toCardId, amount, userId);
    }

    @Operation(summary = "Поиск по картам (по номеру, статусу)")
    @GetMapping("/filter")
    public Page<CardDto> search(@RequestParam(required = false) String number,
                                @RequestParam(required = false) CardStatus status,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        UUID userId = Objects.equals(bankCardsRequestContext.getRole(), Role.ROLE_USER) ? bankCardsRequestContext.getUserId() : null;
        return cardService.search(number, status, userId, pageable);
    }

    @Operation(summary = "Просмотр баланса карты")
    @GetMapping("/{id}/balance")
    public CardBalance getBalance(@PathVariable UUID id) {
        return cardService.getBalance(id);
    }

    @Operation(summary = "Активировать карту")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/activate")
    public void activateCard(@PathVariable UUID id) {
        cardService.activateCard(id);
    }

    @Operation(summary = "Заблокировать карту")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/block")
    public void blockCard(@PathVariable UUID id) {
        cardService.blockCard(id);
    }
}