package rs.ac.ftn.uns.sep.paypal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.uns.sep.paypal.service.PaymentService;
import rs.ac.ftn.uns.sep.paypal.service.implementation.PaymentServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/")
public class PaymentController {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private static final String EVERY_30_SECONDS = "0/30 * * * * ?";

    private final PaymentService paymentService;

    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/processPayment")
    public void getProcessPayment(@RequestParam String paymentId, @RequestParam String token, @RequestParam String PayerID, HttpServletResponse response) throws IOException {
        LOGGER.info(String.format("Payment ID: %s, Token: %s, PayerID: %s", paymentId, token, PayerID));

        String redirectUrl = paymentService.executePayment(paymentId, token, PayerID);

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/failedPayment/{id}")
    public void getFailedPayment(@PathVariable Long id, HttpServletResponse response) throws IOException {
        String redirectUrl = paymentService.cancelPayment(id);

        LOGGER.error("Payment canceled. Redirecting to: " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/subscription")
    public void getProcessPayment(@RequestParam String token) {
        LOGGER.info(String.format("Agreement token: %s", token));
        LOGGER.info("Executing payment...");
        paymentService.executeSubscription(token);
    }

    @Scheduled(cron = EVERY_30_SECONDS)
    public void changeStatus() {
        paymentService.checkStatus();
    }

}
