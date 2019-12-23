package rs.ac.ftn.uns.sep.paypal.service.implementation;

import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.paypal.model.Seller;
import rs.ac.ftn.uns.sep.paypal.service.PaymentService;
import rs.ac.ftn.uns.sep.paypal.service.SellerService;
import rs.ac.ftn.uns.sep.paypal.utils.annotation.LogPayment;
import rs.ac.ftn.uns.sep.paypal.utils.dto.ApprovalUrlDto;
import rs.ac.ftn.uns.sep.paypal.utils.dto.KpRequest;

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
    public ApprovalUrlDto preparePayment(KpRequest kpRequest) {
        ApprovalUrlDto approvalUrlDto = new ApprovalUrlDto();

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

            approvalUrlDto.setApprovalUrl(approvalUrl(createdPayment));
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }

        return approvalUrlDto;
    }

}
