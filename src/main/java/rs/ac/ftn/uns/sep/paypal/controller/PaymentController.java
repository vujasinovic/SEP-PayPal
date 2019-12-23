package rs.ac.ftn.uns.sep.paypal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.uns.sep.paypal.service.PaymentService;
import rs.ac.ftn.uns.sep.paypal.service.implementation.PaymentServiceImpl;
import rs.ac.ftn.uns.sep.paypal.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.paypal.utils.dto.PreparedPaymentDto;

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
        return paymentService.preparePayment(kpRequest);
    }

    @GetMapping("/processPayment")
    public void getProcessPayment(@RequestParam String paymentId, @RequestParam String token, @RequestParam String PayerID) {
        System.out.println(String.format("PaymentId: %s, token: %s, payerId: %s", paymentId, token, PayerID));

    }
}
