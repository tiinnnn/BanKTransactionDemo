package bank.transfer.demo.controller;

import bank.transfer.demo.dto.request.RegisterRequest;
import bank.transfer.demo.dto.response.ApiResponse;
import bank.transfer.demo.dto.response.RegisterResponse;
import bank.transfer.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @RequestBody RegisterRequest request) {

        RegisterResponse data = accountService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công, vui lòng chờ admin duyệt", data));
    }
}