package bank.transfer.demo.repository;

import bank.transfer.demo.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    List<Account> findByUserId(Integer userId);
    long countByUserIdAndStatusNot(Integer userId, Account.Status status);

    // Lấy số tài khoản lớn nhất để tự sinh số mới
    @Query("SELECT MAX(a.accountNumber) FROM Account a")
    String findMaxAccountNumber();
}