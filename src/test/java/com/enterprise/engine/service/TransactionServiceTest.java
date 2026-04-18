package com.enterprise.engine.service;

import com.enterprise.engine.domain.Account;
import com.enterprise.engine.domain.FinancialTransaction;
import com.enterprise.engine.repository.AccountRepository;
import com.enterprise.engine.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void testTransfer_Successful() {
        Account source = new Account(new BigDecimal("1000.00"), "ACTIVE", "user1");
        source.setId(1L);
        Account dest = new Account(new BigDecimal("500.00"), "ACTIVE", "user2");
        dest.setId(2L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(dest));
        when(transactionRepository.save(any(FinancialTransaction.class))).thenAnswer(i -> i.getArguments()[0]);

        FinancialTransaction tx = transactionService.transferFunds(1L, 2L, new BigDecimal("200.00"), "USD");

        assertEquals(new BigDecimal("800.00"), source.getBalance());
        assertEquals(new BigDecimal("700.00"), dest.getBalance());
        assertEquals("COMPLETED", tx.getStatus());
        verify(accountRepository, times(1)).save(source);
        verify(accountRepository, times(1)).save(dest);
    }

    @Test
    public void testTransfer_InsufficientFunds() {
        Account source = new Account(new BigDecimal("100.00"), "ACTIVE", "user1");
        source.setId(1L);
        Account dest = new Account(new BigDecimal("500.00"), "ACTIVE", "user2");
        dest.setId(2L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(dest));

        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transferFunds(1L, 2L, new BigDecimal("200.00"), "USD");
        });

        assertEquals(new BigDecimal("100.00"), source.getBalance());
        assertEquals(new BigDecimal("500.00"), dest.getBalance());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testTransfer_NegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transferFunds(1L, 2L, new BigDecimal("-50.00"), "USD");
        });
    }
}
