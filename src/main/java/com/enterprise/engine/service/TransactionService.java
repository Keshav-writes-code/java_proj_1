package com.enterprise.engine.service;

import com.enterprise.engine.aop.AuditLog;
import com.enterprise.engine.domain.Account;
import com.enterprise.engine.domain.FinancialTransaction;
import com.enterprise.engine.repository.AccountRepository;
import com.enterprise.engine.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @AuditLog
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public FinancialTransaction transferFunds(Long sourceId, Long destId, BigDecimal amount, String currency) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        // Lock accounts or retrieve them
        // In a real highly-concurrent system with pessimistic locking we'd use SELECT ... FOR UPDATE
        // But we are using Optimistic Locking (@Version) on the Account entity as requested
        Account sourceAccount = accountRepository.findById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account destAccount = accountRepository.findById(destId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in source account");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destAccount.setBalance(destAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destAccount);

        FinancialTransaction transaction = new FinancialTransaction(
                sourceId,
                destId,
                amount,
                currency,
                LocalDateTime.now(),
                "COMPLETED"
        );

        return transactionRepository.save(transaction);
    }
}
