package rs.ac.ftn.uns.sep.paypal.controller;

import com.paypal.api.payments.Payer;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.uns.sep.paypal.service.PaymentService;
import rs.ac.ftn.uns.sep.paypal.service.implementation.PaymentServiceImpl;
import rs.ac.ftn.uns.sep.paypal.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.paypal.utils.dto.PreparedPaymentDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/")
public class PaymentController {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PreparedPaymentDto postPreparePayment(KpRequest kpRequest) {
        LOGGER.info("Processing KP Request: " + kpRequest);
        return paymentService.preparePayment(kpRequest);
    }

    @GetMapping("/processPayment")
    public void getProcessPayment(@RequestParam(required = false) String paymentId, @RequestParam String token, @RequestParam(required = false) String PayerID, HttpServletResponse response) {
        LOGGER.info("Processing payment...");
        LOGGER.info(String.format("Payment ID: %s, Token: %s, PayerID: %s", paymentId, token, PayerID));

        String redirectUrl = paymentService.executePayment(paymentId, token, PayerID);

        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
