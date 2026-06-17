package bank.transfer.demo.service;

import bank.transfer.demo.dto.request.RegisterRequest;
import bank.transfer.demo.dto.response.AccountInfoResponse;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final int MAX_ACCOUNTS_PER_USER = 3;

    private final AppUserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountNumberGenerator accountNumberGenerator;

    // ── UC1: Đăng ký lần đầu ────────────────────────────────────────────────
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername()))
            throw new IllegalArgumentException("Username đã tồn tại: " + request.getUsername());

        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email đã được sử dụng: " + request.getEmail());

        AppUser newUser = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(AppUser.Role.USER)
                .status(AppUser.Status.PENDING)
                .build();
        userRepository.save(newUser);

        String accountNumber = accountNumberGenerator.generate();

        Account newAccount = Account.builder()
                .user(newUser)
                .accountNumber(accountNumber)
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

    // ── UC_NEW: Mở thêm tài khoản (tối đa 3) ────────────────────────────────
    @Transactional
    public AccountInfoResponse createAdditionalAccount(String username) {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User không tồn tại"));

        if (user.getStatus() != AppUser.Status.ACTIVE)
            throw new IllegalArgumentException("Tài khoản user chưa được kích hoạt");

        long currentCount = accountRepository
                .countByUserIdAndStatusNot(user.getId(), Account.Status.CLOSED);

        if (currentCount >= MAX_ACCOUNTS_PER_USER)
            throw new IllegalArgumentException(
                    "Mỗi user chỉ được tối đa " + MAX_ACCOUNTS_PER_USER + " tài khoản");

        String accountNumber = accountNumberGenerator.generate();

        Account newAccount = Account.builder()
                .user(user)
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .status(Account.Status.PENDING)
                .build();
        accountRepository.save(newAccount);

        return toAccountInfoResponse(newAccount);
    }

    // ── UC3: Xem tài khoản của mình ─────────────────────────────────────────
    public List<AccountInfoResponse> getMyAccounts(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User không tồn tại"));

        return accountRepository.findByUserId(user.getId())
                .stream()
                .map(this::toAccountInfoResponse)
                .toList();
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    public AccountInfoResponse toAccountInfoResponse(Account account) {
        return new AccountInfoResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getStatus().name(),
                account.getCreatedAt()
        );
    }
}