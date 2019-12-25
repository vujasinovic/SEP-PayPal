package rs.ac.ftn.uns.sep.paypal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.uns.sep.paypal.service.PaymentService;
import rs.ac.uns.ftn.sep.commons.dto.CreatePaymentRequest;
import rs.ac.uns.ftn.sep.commons.dto.CreatePaymentResponse;
import rs.ac.uns.ftn.sep.commons.dto.PaymentStatusRequest;
import rs.ac.uns.ftn.sep.commons.dto.PaymentStatusResponse;

@RequestMapping("/api/payment")
@RestController
@RequiredArgsConstructor
public class PaymentApiController {
    private final PaymentService paymentService;

    @PostMapping
    public CreatePaymentResponse create(@RequestBody CreatePaymentRequest CreatePaymentRequest) {
        return paymentService.preparePayment(CreatePaymentRequest);
    }

    @GetMapping
    public PaymentStatusResponse getPaymentStatus(PaymentStatusRequest request) {
        return paymentService.getPaymentStatus(request);
    }

}
