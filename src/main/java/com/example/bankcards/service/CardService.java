package com.example.bankcards.service;

import com.example.bankcards.dto.CardBalance;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enumerate.CardStatus;
import com.example.bankcards.exception.ApiException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserService userServiceImpl;

    public Card findById(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ApiException("CardNotFound", "Card with id=" + cardId + " not found",
                        "CardService.findById", HttpStatus.NOT_FOUND));
    }

    public CardDto findDtoById(UUID cardId) {
        return cardMapper.toMaskedDto(findById(cardId));
    }

    public List<CardDto> findAll() {
        return cardMapper.toDtoList(cardRepository.findAll());
    }

    public CardDto create(CardDto dto) {
        User user = userServiceImpl.findById(dto.getUserId());
        Card card = cardMapper.toEntity(dto);
        card.setUser(user);
        return cardMapper.toMaskedDto(cardRepository.save(card));
    }

    public CardDto update(CardDto dto) {
        Card existing = findById(dto.getId());

        existing.setNumber(dto.getNumber());
        existing.setExpirationDate(dto.getExpirationDate());
        existing.setStatus(dto.getStatus());
        existing.setBalance(dto.getBalance());

        if (dto.getUserId() != null) {
            User user = userServiceImpl.findById(dto.getUserId());
            existing.setUser(user);
        }

        return cardMapper.toMaskedDto(cardRepository.save(existing));
    }

    public void delete(UUID cardId) {
        Card card = findById(cardId);
        cardRepository.delete(card);
    }

    @Transactional
    public void transfer(UUID fromCardId, UUID toCardId, BigDecimal amount, UUID userId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("InvalidAmount", "Transfer amount must be positive",
                    "CardService.transfer", HttpStatus.BAD_REQUEST);
        }

        Card fromCard = findById(fromCardId);
        Card toCard = findById(toCardId);

        if (!Objects.equals(fromCard.getUser().getId(), toCard.getUser().getId())
                || !Objects.equals(fromCard.getUser().getId(), userId)) {
            throw new ApiException("TransferDenied", "Transfer allowed only between own cards",
                    "CardService.transfer", HttpStatus.BAD_REQUEST);
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new ApiException("InsufficientFunds", "Not enough balance on the card",
                    "CardService.transfer", HttpStatus.BAD_REQUEST);
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    public Page<CardDto> search(String number, CardStatus status, UUID userId, Pageable pageable) {
        String numberFilter = (number != null && !number.isBlank()) ? "%" + number + "%" : null;
        String statusFilter = (status != null) ? status.name() : null;
        return cardRepository.findByNumberAndStatus(numberFilter, statusFilter, userId, pageable)
                .map(cardMapper::toMaskedDto);
    }

    public CardBalance getBalance(UUID cardId) {
        Card card = findById(cardId);
        return CardBalance.builder()
                .balance(card.getBalance())
                .build();
    }

    public void activateCard(UUID cardId) {
        Card card = findById(cardId);
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    public void blockCard(UUID cardId) {
        Card card = findById(cardId);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

}


