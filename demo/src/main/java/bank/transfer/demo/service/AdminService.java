package bank.transfer.demo.service;

import bank.transfer.demo.dto.request.ApproveRequest;
import bank.transfer.demo.dto.response.AccountInfoResponse;
import bank.transfer.demo.dto.response.UserSummaryResponse;
import bank.transfer.demo.entity.Account;
import bank.transfer.demo.entity.AppUser;
import bank.transfer.demo.repository.AccountRepository;
import bank.transfer.demo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AppUserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserSummaryResponse(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole().name(),
                        u.getStatus().name(),
                        u.getCreatedAt()))
                .toList();
    }

    public List<AccountInfoResponse> getPendingAccounts() {
        return accountRepository.findAll()
                .stream()
                .filter(a -> a.getStatus() == Account.Status.PENDING)
                .map(accountService::toAccountInfoResponse)
                .toList();
    }

    // ── Duyệt / Khóa tài khoản ─────────────────────────────────────────────
    @Transactional
    public AccountInfoResponse approveAccount(Integer accountId, ApproveRequest request) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException(
                        "Không tìm thấy tài khoản id: " + accountId));

        // Chỉ cho phép chuyển từ PENDING
        if (account.getStatus() != Account.Status.PENDING) {
            throw new IllegalArgumentException(
                    "Tài khoản không ở trạng thái PENDING, không thể duyệt");
        }

        // Cập nhật Account
        account.setStatus(request.getStatus());
        accountRepository.save(account);

        // Cập nhật AppUser cùng lúc
        AppUser user = account.getUser();
        if (request.getStatus() == Account.Status.ACTIVE) {
            user.setStatus(AppUser.Status.ACTIVE);
        } else if (request.getStatus() == Account.Status.CLOSED) {
            user.setStatus(AppUser.Status.LOCKED);
        }
        userRepository.save(user);

        return accountService.toAccountInfoResponse(account);
    }
}