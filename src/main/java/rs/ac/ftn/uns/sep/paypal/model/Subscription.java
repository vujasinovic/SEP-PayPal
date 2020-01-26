package rs.ac.ftn.uns.sep.paypal.model;

import lombok.Data;
import rs.ac.ftn.uns.sep.paypal.utils.enumeration.SubscriptionStatus;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Entity
@Data
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String planId;

    private String agreementToken;

    @ManyToOne
    private Seller seller;

    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    private String subject;

    private String frequency;

    private String frequencyInterval;

    private Integer cycles;

    private String redirectUrl;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
}
