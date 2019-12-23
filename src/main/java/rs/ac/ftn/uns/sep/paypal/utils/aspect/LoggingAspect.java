package rs.ac.ftn.uns.sep.paypal.utils.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.ftn.uns.sep.paypal.model.Payment;
import rs.ac.ftn.uns.sep.paypal.model.Seller;
import rs.ac.ftn.uns.sep.paypal.repository.PaymentRepository;
import rs.ac.ftn.uns.sep.paypal.service.SellerService;
import rs.ac.ftn.uns.sep.paypal.utils.dto.KpRequest;

@Aspect
@Component
public class LoggingAspect {
    private final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    private final SellerService sellerService;

    private final PaymentRepository paymentRepository;

    public LoggingAspect(SellerService sellerService, PaymentRepository paymentRepository) {
        this.sellerService = sellerService;
        this.paymentRepository = paymentRepository;
    }

    @After("@annotation(rs.ac.ftn.uns.sep.paypal.utils.annotation.LogPayment))")
    public void logAfterPreparePayment(JoinPoint joinPoint) {
        if (joinPoint.getArgs().length == 1) {
            for (Object o : joinPoint.getArgs()) {
                if (o instanceof KpRequest) {
                    LOGGER.info("Creating payment object..");
                    Payment payment = new Payment();

                    KpRequest kpRequest = (KpRequest) o;
                    LOGGER.info("KP Request: " + kpRequest.toString());

                    Seller seller = sellerService.findByEmail(kpRequest.getSellerEmail());
                    LOGGER.info("Seller: " + seller.toString());

                    payment.setSeller(seller);
                    payment.setAmount(kpRequest.getAmount());
                    payment.setSuccessful(false);
                    payment.setRedirectUrl(kpRequest.getRedirectUrl());
                    LOGGER.info("Persisting payment...");

                    Payment persistedPayment = paymentRepository.save(payment);
                    LOGGER.info("Payment persisted." + persistedPayment.toString());
                }
            }
        }
    }
}
