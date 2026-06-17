package bank.transfer.demo.service;

import bank.transfer.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {
    private final AccountRepository accountRepository;

    public String generate() {
        String max = accountRepository.findMaxAccountNumber();

        long next;
        if (max == null) {
            next = 9_000_000_001L;
        } else {
            next = Long.parseLong(max) + 1;
        }

        // Đề phòng trùng (race condition)
        while (accountRepository.existsByAccountNumber(String.valueOf(next))) {
            next++;
        }
        return String.valueOf(next);
    }
}