package rs.ac.ftn.uns.sep.paypal.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Seller seller;

    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    private String redirectUrl;

    private Boolean successful;
}
