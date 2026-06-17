package bank.transfer.demo.controller;

import bank.transfer.demo.dto.request.ApproveRequest;
import bank.transfer.demo.dto.response.AccountInfoResponse;
import bank.transfer.demo.dto.response.ApiResponse;
import bank.transfer.demo.dto.response.TransactionResponse;
import bank.transfer.demo.dto.response.UserSummaryResponse;
import bank.transfer.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /** GET /api/admin/users */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.success("Danh sách user", adminService.getAllUsers()));
    }

    /** GET /api/admin/accounts/pending */
    @GetMapping("/accounts/pending")
    public ResponseEntity<ApiResponse<List<AccountInfoResponse>>> getPendingAccounts() {
        return ResponseEntity.ok(
                ApiResponse.success("Danh sách tài khoản chờ duyệt",
                        adminService.getPendingAccounts()));
    }

    /** PATCH /api/admin/accounts/{id} */
    @PatchMapping("/accounts/{id}")
    public ResponseEntity<ApiResponse<AccountInfoResponse>> approveAccount(
            @PathVariable Integer id,
            @RequestBody ApproveRequest request) {

        AccountInfoResponse data = adminService.approveAccount(id, request);
        String message = switch (request.getStatus()) {
            case ACTIVE -> "Tài khoản đã được duyệt";
            case CLOSED -> "Tài khoản đã bị khóa";
            default     -> "Cập nhật trạng thái thành công";
        };
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    /** GET /api/admin/transactions */
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAllTransactions() {
        return ResponseEntity.ok(
                ApiResponse.success("Danh sách giao dịch", adminService.getAllTransactions()));
    }

    /** GET /api/admin/transactions/{id} */
    @GetMapping("/transactions/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionDetail(
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success("Chi tiết giao dịch", adminService.getTransactionById(id)));
    }
}