package bank.transfer.demo.controller;

import bank.transfer.demo.dto.request.LoginRequest;
import bank.transfer.demo.dto.response.ApiResponse;
import bank.transfer.demo.dto.response.LoginResponse;
import bank.transfer.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER")
                .replace("ROLE_", "");

        String token = jwtUtil.generateToken(auth.getName(), role);
        LoginResponse data = new LoginResponse(token, auth.getName(), role);

        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", data));
    }
}