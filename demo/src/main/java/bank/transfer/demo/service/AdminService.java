package bank.transfer.demo.service;

import bank.transfer.demo.dto.request.ApproveRequest;
import bank.transfer.demo.dto.response.AccountInfoResponse;
import bank.transfer.demo.dto.response.TransactionResponse;
import bank.transfer.demo.dto.response.UserSummaryResponse;
import bank.transfer.demo.entity.Account;
import bank.transfer.demo.entity.AppUser;
import bank.transfer.demo.entity.Transaction;
import bank.transfer.demo.repository.AccountRepository;
import bank.transfer.demo.repository.AppUserRepository;
import bank.transfer.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AppUserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserSummaryResponse(
                        u.getId(), u.getUsername(), u.getEmail(),
                        u.getRole().name(), u.getStatus().name(), u.getCreatedAt()))
                .toList();
    }

    public List<AccountInfoResponse> getPendingAccounts() {
        return accountRepository.findAll()
                .stream()
                .filter(a -> a.getStatus() == Account.Status.PENDING)
                .map(accountService::toAccountInfoResponse)
                .toList();
    }

    // ── Duyệt / khóa tài khoản ──────────────────────────────────────────────
    @Transactional
    public AccountInfoResponse approveAccount(Integer accountId, ApproveRequest request) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException(
                        "Không tìm thấy tài khoản id: " + accountId));

        if (account.getStatus() != Account.Status.PENDING)
            throw new IllegalArgumentException("Tài khoản không ở trạng thái PENDING");

        account.setStatus(request.getStatus());
        accountRepository.save(account);

        // Đồng bộ trạng thái AppUser
        AppUser user = account.getUser();
        if (request.getStatus() == Account.Status.ACTIVE)
            user.setStatus(AppUser.Status.ACTIVE);
        else if (request.getStatus() == Account.Status.CLOSED)
            user.setStatus(AppUser.Status.LOCKED);
        userRepository.save(user);

        return accountService.toAccountInfoResponse(account);
    }

    // ── Tất cả giao dịch ─────────────────────────────────────────────────────
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository
                .findAll(org.springframework.data.domain.Sort
                        .by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toTransactionResponse)
                .toList();
    }

    // ── Chi tiết 1 giao dịch ─────────────────────────────────────────────────
    public TransactionResponse getTransactionById(Integer id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Không tìm thấy giao dịch id: " + id));
        return toTransactionResponse(tx);
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    private TransactionResponse toTransactionResponse(Transaction tx) {
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