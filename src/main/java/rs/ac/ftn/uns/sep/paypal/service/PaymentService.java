package rs.ac.ftn.uns.sep.paypal.service;

import rs.ac.ftn.uns.sep.paypal.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.paypal.utils.dto.PreparedPaymentDto;

public interface PaymentService {
    PreparedPaymentDto preparePayment(KpRequest kpRequest);

}
