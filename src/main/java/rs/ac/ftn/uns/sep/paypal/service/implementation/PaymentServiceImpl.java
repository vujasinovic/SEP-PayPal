package rs.ac.ftn.uns.sep.paypal.service.implementation;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.paypal.model.Seller;
import rs.ac.ftn.uns.sep.paypal.service.PaymentService;
import rs.ac.ftn.uns.sep.paypal.service.SellerService;
import rs.ac.ftn.uns.sep.paypal.utils.annotation.LogPayment;
import rs.ac.ftn.uns.sep.paypal.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.paypal.utils.dto.PreparedPaymentDto;

import java.util.ArrayList;
import java.util.List;

import static rs.ac.ftn.uns.sep.paypal.utils.PaymentUtils.*;
import static rs.ac.ftn.uns.sep.paypal.utils.SandboxCredentialsUtils.clientId;
import static rs.ac.ftn.uns.sep.paypal.utils.SandboxCredentialsUtils.clientSecretId;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String INTENT = "authorize";
    private static final String MODE = "sandbox";

    private final SellerService sellerService;

    public PaymentServiceImpl(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    @LogPayment
    public PreparedPaymentDto preparePayment(KpRequest kpRequest) {
        PreparedPaymentDto preparedPaymentDto = new PreparedPaymentDto();

        Seller seller = sellerService.findByEmail(kpRequest.getSellerEmail());

        Transaction transaction = setTransaction(kpRequest.getAmount(), seller.getPaypalEmail());

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(transaction);

        Payment payment = new Payment();
        payment.setIntent(INTENT);
        payment.setPayer(initializePayer());
        payment.setTransactions(transactions);

        payment.setRedirectUrls(setRedirectUrls());

        APIContext apiContext = new APIContext(clientId(), clientSecretId(), MODE);

        try {
            Payment createdPayment = payment.create(apiContext);

            preparedPaymentDto.setApprovalUrl(approvalUrl(createdPayment));
            preparedPaymentDto.setPaymentId(createdPayment.getId());

        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }

        return preparedPaymentDto;
    }

}
