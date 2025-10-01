package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enumerate.CardStatus;
import com.example.bankcards.exception.ApiException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CardServiceTest {
    @InjectMocks
    private CardService cardService;
    @Mock
    private CardRepository cardRepository;

    private UUID userId;
    private Card fromCard;
    private Card toCard;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .build();

        fromCard = Card.builder()
                .id(UUID.randomUUID())
                .user(user)
                .balance(BigDecimal.valueOf(1000))
                .status(CardStatus.ACTIVE)
                .number("1111")
                .expirationDate("12/26")
                .build();

        toCard = Card.builder()
                .id(UUID.randomUUID())
                .user(user)
                .balance(BigDecimal.valueOf(500))
                .status(CardStatus.ACTIVE)
                .number("2222")
                .expirationDate("11/25")
                .build();
    }

    // Тесты переводов между карт
    @Test
    void testTransfer_Success() {
        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        cardService.transfer(fromCard.getId(), toCard.getId(), BigDecimal.valueOf(200), userId);

        assertEquals(BigDecimal.valueOf(800), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), toCard.getBalance());
        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    void testTransfer_InsufficientFunds() {
        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        ApiException ex = assertThrows(ApiException.class,
                () -> cardService.transfer(fromCard.getId(), toCard.getId(), BigDecimal.valueOf(2000), userId));
        assertEquals("InsufficientFunds", ex.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testTransfer_DifferentUser() {
        UUID otherUserId = UUID.randomUUID();
        toCard.setUser(new User());
        toCard.getUser().setId(otherUserId);

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        ApiException ex = assertThrows(ApiException.class,
                () -> cardService.transfer(fromCard.getId(), toCard.getId(), BigDecimal.valueOf(100), userId));
        assertEquals("TransferDenied", ex.getErrorCode());
    }
}
