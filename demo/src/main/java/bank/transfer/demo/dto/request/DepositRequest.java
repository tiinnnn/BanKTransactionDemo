package bank.transfer.demo.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepositRequest {
    private String toAccountNumber;
    private BigDecimal amount;
    private String note;
}