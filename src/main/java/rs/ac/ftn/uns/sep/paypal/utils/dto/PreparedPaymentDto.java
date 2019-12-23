package rs.ac.ftn.uns.sep.paypal.utils.dto;

import lombok.Data;

@Data
public class PreparedPaymentDto {
    private String approvalUrl;

    private String PaymentId;
}
