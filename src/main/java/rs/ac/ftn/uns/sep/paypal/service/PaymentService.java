package rs.ac.ftn.uns.sep.paypal.service;

import rs.ac.ftn.uns.sep.paypal.utils.dto.ApprovalUrlDto;
import rs.ac.ftn.uns.sep.paypal.utils.dto.KpRequest;

public interface PaymentService {
    ApprovalUrlDto preparePayment(KpRequest kpRequest);

}
