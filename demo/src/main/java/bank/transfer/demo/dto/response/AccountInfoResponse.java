package bank.transfer.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountInfoResponse {
    private Integer id;
    private String accountNumber;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
}