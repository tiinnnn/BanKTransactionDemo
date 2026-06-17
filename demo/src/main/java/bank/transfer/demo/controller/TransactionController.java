package bank.transfer.demo.controller;

import bank.transfer.demo.dto.request.DepositRequest;
import bank.transfer.demo.dto.request.TransferRequest;
import bank.transfer.demo.dto.response.ApiResponse;
import bank.transfer.demo.dto.response.TransactionResponse;
import bank.transfer.demo.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /** POST /api/transactions/deposit */
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DepositRequest request) {

        TransactionResponse data = transactionService.deposit(
                userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Nạp tiền thành công", data));
    }

    /** POST /api/transactions/transfer */
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TransferRequest request) {

        TransactionResponse data = transactionService.transfer(
                userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Chuyển tiền thành công", data));
    }

    /** GET /api/transactions/{accountNumber} */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String accountNumber) {

        List<TransactionResponse> data = transactionService.getHistory(
                userDetails.getUsername(), accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Lịch sử giao dịch", data));
    }
}