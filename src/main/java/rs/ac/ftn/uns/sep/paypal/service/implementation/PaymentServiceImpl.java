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
import rs.ac.ftn.uns.sep.paypal.utils.dto.PreparedPaymentDto;
import rs.ac.ftn.uns.sep.paypal.utils.dto.SubscriptionRequest;
import rs.ac.uns.ftn.sep.commons.dto.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Patch.ACTIVE;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Patch.STATE;
import static rs.ac.ftn.uns.sep.paypal.utils.PaymentUtils.*;
import static rs.ac.ftn.uns.sep.paypal.utils.SandboxCredentialsUtils.clientId;
import static rs.ac.ftn.uns.sep.paypal.utils.SandboxCredentialsUtils.clientSecretId;
import static rs.ac.ftn.uns.sep.paypal.utils.SubscriptionUtils.*;

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
    public CreatePaymentResponse preparePayment(CreatePaymentRequest kpRequest) {
        PreparedPaymentDto preparedPaymentDto = new PreparedPaymentDto();

        rs.ac.ftn.uns.sep.paypal.model.Payment localPayment = new rs.ac.ftn.uns.sep.paypal.model.Payment();

        localPayment = paymentRepository.save(localPayment);

        Seller seller = sellerService.findByEmail(kpRequest.getMerchantName());

        Transaction transaction = setTransaction(BigDecimal.valueOf(kpRequest.getAmount()), seller.getPaypalEmail());

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(transaction);

        Payment payment = new Payment();
        payment.setIntent(INTENT);
        payment.setPayer(initializePayer());
        payment.setTransactions(transactions);

        payment.setRedirectUrls(setRedirectUrls(localPayment.getId()));

        APIContext apiContext = new APIContext(clientId(), clientSecretId(), MODE);

        Payment createdPayment = null;
        try {
            createdPayment = payment.create(apiContext);

            preparedPaymentDto.setApprovalUrl(approvalUrl(createdPayment));
            preparedPaymentDto.setPaymentId(createdPayment.getId());

        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }

        localPayment.setAmount(BigDecimal.valueOf(kpRequest.getAmount()));
        localPayment.setSeller(seller);
        localPayment.setPaymentId(createdPayment.getId());
        localPayment.setRedirectUrl(kpRequest.getRedirectUrl());
        localPayment = paymentRepository.save(localPayment);


        return CreatePaymentResponse.builder().paymentId(localPayment.getId())
                .redirect(preparedPaymentDto.getApprovalUrl()).build();
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
            LOGGER.error("Error happened while executing payment.", e);
        }

        return persistedPayment.getRedirectUrl();
    }

    @Override
    public PaymentStatusResponse getPaymentStatus(PaymentStatusRequest request) {
        rs.ac.ftn.uns.sep.paypal.model.Payment payment = paymentRepository.getOne(request.getPaymentId());

        PaymentStatus status = payment.getSuccessful() ? PaymentStatus.SUCCESS : PaymentStatus.FAIL;

        return new PaymentStatusResponse(payment.getId(), status);
    }

    @Override
    public String cancelPayment(Long id) {
        rs.ac.ftn.uns.sep.paypal.model.Payment payment = paymentRepository.getOne(id);
        payment.setSuccessful(false);

        paymentRepository.save(payment);

        return payment.getRedirectUrl();
    }

    @Override
    public CreatePaymentResponse prepareSubscription(SubscriptionRequest request) {
        APIContext apiContext = new APIContext(clientId(), clientSecretId(), MODE);

        Plan plan = createPlan(request);

        try {
            Plan createdPlan = plan.create(apiContext);

            List<Patch> patches = new ArrayList<>();

            patches.add(patch(Collections.singletonMap(STATE, ACTIVE)));

            createdPlan.update(apiContext, patches);
            LOGGER.info("Plan state: {}", createdPlan.getState());
            LOGGER.info("Created plan id: {}", createdPlan.getId());
        } catch (PayPalRESTException e) {
            LOGGER.error("Could not update billing plan. ", e);
        }

        return new CreatePaymentResponse();
    }

    private Plan createPlan(SubscriptionRequest request) {
        Plan plan = plan(request.getSubject());

        PaymentDefinition paymentDefinition = paymentDefinition(request.getFrequency(),
                request.getInterval(), String.valueOf(request.getCycles()));

        Currency amount = currency(request.getAmount());

        paymentDefinition.setChargeModels(List.of(chargeModel(amount)));

        List<PaymentDefinition> paymentDefinitions = new ArrayList<>();
        paymentDefinitions.add(paymentDefinition);

        plan.setPaymentDefinitions(paymentDefinitions);
        plan.setMerchantPreferences(merchantPreferences(amount));

        return plan;
    }

}
