package bank.transfer.demo.repository;

import bank.transfer.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
            Integer fromAccountId, Integer toAccountId);
}