package rs.ac.ftn.uns.sep.paypal.service.implementation;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.paypal.model.Seller;
import rs.ac.ftn.uns.sep.paypal.repository.PaymentRepository;
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
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private static final String INTENT = "authorize";
    private static final String MODE = "sandbox";

    private final SellerService sellerService;

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(SellerService sellerService, PaymentRepository paymentRepository) {
        this.sellerService = sellerService;
        this.paymentRepository = paymentRepository;
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

    @Override
    public String executePayment(String paymentId, String token, String payerId) {
        rs.ac.ftn.uns.sep.paypal.model.Payment persistedPayment = paymentRepository.findByPaymentId(paymentId);

        APIContext apiContext = new APIContext(clientId(), clientSecretId(), MODE);

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        try {
            Payment executedPayment = payment.execute(apiContext, paymentExecution);

            Authorization authorization = executedPayment.getTransactions().get(0).getRelatedResources().get(0).getAuthorization();

            Amount amount = setAmount(persistedPayment.getAmount());

            Capture capture = new Capture();
            capture.setAmount(amount);

            capture.setIsFinalCapture(true);

            Capture responseCapture = authorization.capture(apiContext, capture);

            LOGGER.info("Capture id=" + responseCapture.getId() + " and status=" + responseCapture.getState());

            persistedPayment.setSuccessful(true);
            paymentRepository.save(persistedPayment);

            LOGGER.info("Executed payment - Request: \n" + Payment.getLastRequest());
            LOGGER.info("Executed payment - Response: \n" + Payment.getLastResponse());

        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }

        return persistedPayment.getRedirectUrl();
    }

}
