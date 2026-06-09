package bank.transfer.demo.service;

import bank.transfer.demo.dto.request.RegisterRequest;
import bank.transfer.demo.dto.response.RegisterResponse;
import bank.transfer.demo.entity.Account;
import bank.transfer.demo.entity.AppUser;
import bank.transfer.demo.repository.AccountRepository;
import bank.transfer.demo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AppUserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng: " + request.getEmail());
        }
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new IllegalArgumentException("Số tài khoản đã tồn tại: " + request.getAccountNumber());
        }

        AppUser newUser = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(AppUser.Role.USER)
                .status(AppUser.Status.PENDING)
                .build();
        userRepository.save(newUser);

        Account newAccount = Account.builder()
                .user(newUser)
                .accountNumber(request.getAccountNumber())
                .balance(BigDecimal.ZERO)
                .status(Account.Status.PENDING)
                .build();
        accountRepository.save(newAccount);

        return new RegisterResponse(
                newUser.getUsername(),
                newAccount.getAccountNumber(),
                newAccount.getStatus().name()
        );
    }
}