package rs.ac.ftn.uns.sep.paypal.utils.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KpRequest {
    private String sellerEmail;

    private BigDecimal amount;

    private String redirectUrl;
}
