package com.example.bankcards.controller;

import com.example.bankcards.config.BankCardsRequestContext;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CardControllerTest {

    @Mock
    private CardService cardService;

    @Mock
    private BankCardsRequestContext bankCardsRequestContext;

    @InjectMocks
    private CardController cardController;

    private MockMvc mockMvc;

    private UUID userId;
    private UUID fromCardId;
    private UUID toCardId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();

        userId = UUID.randomUUID();
        fromCardId = UUID.randomUUID();
        toCardId = UUID.randomUUID();
    }

    @Test
    void testTransfer() throws Exception {
        when(bankCardsRequestContext.getUserId()).thenReturn(userId);

        mockMvc.perform(post("/api/v1/cards/transfer")
                        .param("fromCardId", fromCardId.toString())
                        .param("toCardId", toCardId.toString())
                        .param("amount", "200"))
                .andExpect(status().isOk());

        verify(cardService).transfer(fromCardId, toCardId, BigDecimal.valueOf(200), userId);
    }
}
