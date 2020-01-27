package rs.ac.ftn.uns.sep.paypal.service;

import rs.ac.ftn.uns.sep.paypal.utils.dto.SubscriptionRequest;
import rs.ac.uns.ftn.sep.commons.dto.CreatePaymentRequest;
import rs.ac.uns.ftn.sep.commons.dto.CreatePaymentResponse;
import rs.ac.uns.ftn.sep.commons.dto.PaymentStatusRequest;
import rs.ac.uns.ftn.sep.commons.dto.PaymentStatusResponse;

public interface PaymentService {
    CreatePaymentResponse preparePayment(CreatePaymentRequest kpRequest);

    String executePayment(String paymentId, String token, String payerId);

    PaymentStatusResponse getPaymentStatus(PaymentStatusRequest request);

    String cancelPayment(Long id);

    CreatePaymentResponse prepareSubscription(SubscriptionRequest subscriptionRequest);

    void executeSubscription(String token);

    void checkStatus();
}
