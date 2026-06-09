package bank.transfer.demo.dto.request;

import bank.transfer.demo.entity.Account;
import lombok.Data;

@Data
public class ApproveRequest {
    private Account.Status status;
}