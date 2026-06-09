package bank.transfer.demo.repository;

import bank.transfer.demo.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUserId(Integer userId);
    boolean existsByAccountNumber(String accountNumber);
}
