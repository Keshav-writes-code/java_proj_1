package com.enterprise.engine.repository;

import com.enterprise.engine.domain.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<FinancialTransaction, Long> {
}
