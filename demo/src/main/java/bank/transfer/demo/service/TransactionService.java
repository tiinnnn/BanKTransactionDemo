package bank.transfer.demo.service;

import bank.transfer.demo.dto.request.DepositRequest;
import bank.transfer.demo.dto.request.TransferRequest;
import bank.transfer.demo.dto.response.TransactionResponse;
import bank.transfer.demo.entity.Account;
import bank.transfer.demo.entity.AppUser;
import bank.transfer.demo.entity.Transaction;
import bank.transfer.demo.repository.AccountRepository;
import bank.transfer.demo.repository.AppUserRepository;
import bank.transfer.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final AppUserRepository userRepository;
    private final TransactionRepository transactionRepository;

    // ── UC4: Nạp tiền ────────────────────────────────────────────────────────
    @Transactional
    public TransactionResponse deposit(String username, DepositRequest request) {

        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Số tiền nạp phải lớn hơn 0");

        // Tài khoản đích phải thuộc về user đang login
        Account toAccount = getActiveAccountOfUser(username, request.getToAccountNumber());

        // Cộng tiền
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(toAccount);

        // Ghi transaction
        Transaction tx = Transaction.builder()
                .fromAccount(null)
                .toAccount(toAccount)
                .amount(request.getAmount())
                .type(Transaction.Type.DEPOSIT)
                .status(Transaction.Status.SUCCESS)
                .note(request.getNote())
                .build();
        transactionRepository.save(tx);

        return toResponse(tx);
    }

    // ── UC5: Chuyển tiền ─────────────────────────────────────────────────────
    @Transactional
    public TransactionResponse transfer(String username, TransferRequest request) {

        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Số tiền chuyển phải lớn hơn 0");

        // Không tự chuyển cho mình
        if (request.getFromAccountNumber().equals(request.getToAccountNumber()))
            throw new IllegalArgumentException("Không thể chuyển tiền vào chính tài khoản đó");

        // Tài khoản nguồn phải thuộc về user đang login
        Account fromAccount = getActiveAccountOfUser(username, request.getFromAccountNumber());

        // Tài khoản đích phải tồn tại và ACTIVE (có thể của bất kỳ user nào)
        Account toAccount = accountRepository
                .findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new IllegalStateException(
                        "Tài khoản đích không tồn tại: " + request.getToAccountNumber()));

        if (toAccount.getStatus() != Account.Status.ACTIVE)
            throw new IllegalArgumentException("Tài khoản đích chưa được kích hoạt");

        // Kiểm tra số dư
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0)
            throw new IllegalArgumentException("Số dư không đủ để thực hiện giao dịch");

        // Thực hiện chuyển tiền
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Ghi transaction
        Transaction tx = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(request.getAmount())
                .type(Transaction.Type.TRANSFER)
                .status(Transaction.Status.SUCCESS)
                .note(request.getNote())
                .build();
        transactionRepository.save(tx);

        return toResponse(tx);
    }

    // ── UC6: Lịch sử giao dịch theo số tài khoản ────────────────────────────
    public List<TransactionResponse> getHistory(String username, String accountNumber) {

        // Chỉ được xem lịch sử tài khoản của chính mình
        Account account = getActiveAccountOfUser(username, accountNumber);

        return transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
                        account.getId(), account.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Helper: lấy tài khoản ACTIVE thuộc về đúng user ─────────────────────
    private Account getActiveAccountOfUser(String username, String accountNumber) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User không tồn tại"));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalStateException(
                        "Tài khoản không tồn tại: " + accountNumber));

        if (!account.getUser().getId().equals(user.getId()))
            throw new IllegalArgumentException("Tài khoản không thuộc về bạn");

        if (account.getStatus() != Account.Status.ACTIVE)
            throw new IllegalArgumentException("Tài khoản chưa được kích hoạt");

        return account;
    }

    // ── Helper: entity → response DTO ────────────────────────────────────────
    private TransactionResponse toResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getType().name(),
                tx.getFromAccount() != null ? tx.getFromAccount().getAccountNumber() : null,
                tx.getToAccount().getAccountNumber(),
                tx.getAmount(),
                tx.getStatus().name(),
                tx.getNote(),
                tx.getCreatedAt()
        );
    }
}