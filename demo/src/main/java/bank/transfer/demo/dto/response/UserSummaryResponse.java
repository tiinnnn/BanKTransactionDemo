package bank.transfer.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserSummaryResponse {
    private Integer id;
    private String username;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;
}