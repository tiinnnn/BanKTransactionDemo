package bank.transfer.demo.controller;

import bank.transfer.demo.dto.request.RegisterRequest;
import bank.transfer.demo.dto.response.AccountInfoResponse;
import bank.transfer.demo.dto.response.ApiResponse;
import bank.transfer.demo.dto.response.RegisterResponse;
import bank.transfer.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @RequestBody RegisterRequest request) {

        RegisterResponse data = accountService.register(request);
        return ResponseEntity.ok(
                ApiResponse.success("Đăng ký thành công, vui lòng chờ admin duyệt", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountInfoResponse>> createAccount(
            @AuthenticationPrincipal UserDetails userDetails) {

        AccountInfoResponse data = accountService
                .createAdditionalAccount(userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success("Yêu cầu mở tài khoản đã gửi, chờ admin duyệt", data));
    }

    //xem tất cả tài khoản của mình
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AccountInfoResponse>>> getMyAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<AccountInfoResponse> data = accountService.getMyAccounts(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Danh sách tài khoản", data));
    }
}