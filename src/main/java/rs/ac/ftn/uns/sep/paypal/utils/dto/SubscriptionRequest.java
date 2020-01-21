package rs.ac.ftn.uns.sep.paypal.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    private String merchantEmail;

    private BigDecimal amount;

    private String redirectUrl;

    private String subject;

    private String frequency;

    private String interval;

    private Integer cycles;

}
