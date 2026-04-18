package com.enterprise.engine.controller;

import com.enterprise.engine.domain.FinancialTransaction;
import com.enterprise.engine.service.TransactionService;
import com.enterprise.engine.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TransactionRepository transactionRepository;

    @Test
    public void testTransferEndpoint_Successful() throws Exception {
        FinancialTransaction tx = new FinancialTransaction(1L, 2L, new BigDecimal("100.00"), "USD", LocalDateTime.now(), "COMPLETED");
        tx.setId(100L);

        when(transactionService.transferFunds(eq(1L), eq(2L), any(BigDecimal.class), eq("USD")))
                .thenReturn(tx);

        String jsonPayload = """
                {
                    "sourceAccountId": 1,
                    "destinationAccountId": 2,
                    "amount": 100.00,
                    "currency": "USD"
                }
                """;

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    public void testTransferEndpoint_InvalidInput() throws Exception {
        String jsonPayload = """
                {
                    "sourceAccountId": null,
                    "destinationAccountId": 2,
                    "amount": -50.00,
                    "currency": "USD"
                }
                """;

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }
}
