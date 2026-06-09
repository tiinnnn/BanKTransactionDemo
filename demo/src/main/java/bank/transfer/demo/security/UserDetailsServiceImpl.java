package bank.transfer.demo.security;

import bank.transfer.demo.entity.AppUser;
import bank.transfer.demo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy user: " + username));

        // Kiểm tra trạng thái tài khoản
        if (user.getStatus() == AppUser.Status.PENDING) {
            throw new UsernameNotFoundException("Tài khoản chưa được duyệt");
        }
        if (user.getStatus() == AppUser.Status.LOCKED) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa");
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}