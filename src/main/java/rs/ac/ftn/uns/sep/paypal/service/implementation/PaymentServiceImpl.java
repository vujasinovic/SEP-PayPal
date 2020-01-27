package rs.ac.ftn.uns.sep.paypal.service.implementation;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.paypal.model.Seller;
import rs.ac.ftn.uns.sep.paypal.model.Subscription;
import rs.ac.ftn.uns.sep.paypal.repository.PaymentRepository;
import rs.ac.ftn.uns.sep.paypal.service.PaymentService;
import rs.ac.ftn.uns.sep.paypal.service.SellerService;
import rs.ac.ftn.uns.sep.paypal.service.SubscriptionService;
import rs.ac.ftn.uns.sep.paypal.utils.PaymentUtils;
import rs.ac.ftn.uns.sep.paypal.utils.UrlUtils;
import rs.ac.ftn.uns.sep.paypal.utils.annotation.LogPayment;
import rs.ac.ftn.uns.sep.paypal.utils.dto.PreparedPaymentDto;
import rs.ac.ftn.uns.sep.paypal.utils.dto.SubscriptionRequest;
import rs.ac.ftn.uns.sep.paypal.utils.enumeration.SubscriptionStatus;
import rs.ac.uns.ftn.sep.commons.dto.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Patch.ACTIVE;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Patch.STATE;
import static rs.ac.ftn.uns.sep.paypal.constants.PaymentStatus.*;
import static rs.ac.ftn.uns.sep.paypal.utils.PaymentUtils.*;
import static rs.ac.ftn.uns.sep.paypal.utils.SandboxCredentialsUtils.clientId;
import static rs.ac.ftn.uns.sep.paypal.utils.SandboxCredentialsUtils.clientSecretId;
import static rs.ac.ftn.uns.sep.paypal.utils.SubscriptionUtils.patch;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final APIContext API_CONTEXT = new APIContext(clientId(), clientSecretId(), MODE);

    private static final String INTENT = "authorize";
    private static final String MODE = "sandbox";

    private final SellerService sellerService;

    private final PaymentRepository paymentRepository;

    private final SubscriptionService subscriptionService;

    @Override
    @LogPayment
    @SneakyThrows(PayPalRESTException.class)
    public CreatePaymentResponse preparePayment(CreatePaymentRequest kpRequest) {
        PreparedPaymentDto preparedPaymentDto = new PreparedPaymentDto();

        rs.ac.ftn.uns.sep.paypal.model.Payment localPayment = new rs.ac.ftn.uns.sep.paypal.model.Payment();
        localPayment.setStatus(CREATED);

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

        Payment createdPayment = payment.create(API_CONTEXT);

        preparedPaymentDto.setApprovalUrl(approvalUrl(createdPayment));
        preparedPaymentDto.setPaymentId(createdPayment.getId());

        localPayment.setAmount(BigDecimal.valueOf(kpRequest.getAmount()));
        localPayment.setSeller(seller);
        localPayment.setPaymentId(createdPayment.getId());
        localPayment.setRedirectUrl(kpRequest.getRedirectUrl());
        localPayment = paymentRepository.save(localPayment);

        return CreatePaymentResponse.builder().paymentId(localPayment.getId())
                .redirect(preparedPaymentDto.getApprovalUrl()).build();
    }

    @Override
    @SneakyThrows(PayPalRESTException.class)
    public String executePayment(String paymentId, String token, String payerId) {
        rs.ac.ftn.uns.sep.paypal.model.Payment persistedPayment = paymentRepository.findByPaymentId(paymentId);

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment executedPayment = payment.execute(API_CONTEXT, paymentExecution);

        Authorization authorization = executedPayment.getTransactions().get(0).getRelatedResources().get(0).getAuthorization();

        Amount amount = setAmount(persistedPayment.getAmount());

        Capture capture = new Capture();
        capture.setAmount(amount);

        capture.setIsFinalCapture(true);

        Capture responseCapture = authorization.capture(API_CONTEXT, capture);

        LOGGER.info("Capture id=" + responseCapture.getId() + " and status=" + responseCapture.getState());

        persistedPayment.setStatus(SUCCESS);
        paymentRepository.save(persistedPayment);

        LOGGER.info("Executed payment - Request: \n" + Payment.getLastRequest());
        LOGGER.info("Executed payment - Response: \n" + Payment.getLastResponse());

        return persistedPayment.getRedirectUrl();
    }

    @Override
    public PaymentStatusResponse getPaymentStatus(PaymentStatusRequest request) {
        rs.ac.ftn.uns.sep.paypal.model.Payment payment = paymentRepository.getOne(request.getPaymentId());

        PaymentStatus status = payment.getStatus().equalsIgnoreCase(SUCCESS) ? PaymentStatus.SUCCESS : PaymentStatus.FAIL;

        return new PaymentStatusResponse(payment.getId(), status);
    }

    @Override
    public String cancelPayment(Long id) {
        rs.ac.ftn.uns.sep.paypal.model.Payment payment = paymentRepository.getOne(id);
        payment.setStatus(FAILED);

        paymentRepository.save(payment);

        return payment.getRedirectUrl();
    }

    @Override
    @SneakyThrows({PayPalRESTException.class, MalformedURLException.class, UnsupportedEncodingException.class})
    public CreatePaymentResponse prepareSubscription(SubscriptionRequest request) {
        Plan plan = PaymentUtils.createPlan(request);

        LOGGER.info("Creating plan...");
        Plan createdPlan = plan.create(API_CONTEXT);

        List<Patch> patches = new ArrayList<>();

        patches.add(patch(Collections.singletonMap(STATE, ACTIVE)));

        LOGGER.info("Setting plan state to active");
        createdPlan.update(API_CONTEXT, patches);
        String planId = createdPlan.getId();

        LOGGER.info("Plan state: {}", createdPlan.getState());
        LOGGER.info("Created plan ID: {}", planId);

        Agreement agreement = createAgreement(planId);

        LOGGER.info("Creating agreement...");
        agreement = agreement.create(API_CONTEXT);
        LOGGER.info("Agreement created");

        URL url = UrlUtils.getApprovalUrl(agreement.getLinks());

        String agreementToken = UrlUtils.parseAgreementToken(requireNonNull(url));
        Seller seller = sellerService.findByEmail(request.getMerchantEmail());

        Subscription subscription = createSubscription(request, planId, seller);
        subscription.setAgreementToken(agreementToken);
        subscription = subscriptionService.save(subscription);

        LOGGER.info("Approval Url: " + url);

        return CreatePaymentResponse.builder().paymentId(subscription.getId()).redirect(String.valueOf(url)).build();
    }

    @SneakyThrows
    @Override
    public void executeSubscription(String token) {
        Subscription subscription = subscriptionService.findByAgreementToken(token);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionService.save(subscription);

        Agreement activatedAgreement = Agreement.execute(API_CONTEXT, token);

        LOGGER.info("Agreement created with ID: {} and was successfully executed.", activatedAgreement.getId());
    }

    @SneakyThrows
    @Override
    public void checkStatus() {
        List<rs.ac.ftn.uns.sep.paypal.model.Payment> notCompletedPayments = paymentRepository.findAllByStatusNotCompleted();

        for (rs.ac.ftn.uns.sep.paypal.model.Payment localPayment : notCompletedPayments) {
            Payment payment = Payment.get(API_CONTEXT, localPayment.getPaymentId());

            String state = payment.getState().toUpperCase();

            if (state.equalsIgnoreCase(CREATED)) {
                LOGGER.info(String.format("Status of payment with id %s set to: %s", payment.getId(), FAILED));
                changeStatus(localPayment, FAILED);
            }
        }
    }

    private void changeStatus(rs.ac.ftn.uns.sep.paypal.model.Payment payment, String status) {
        payment.setStatus(status.toUpperCase());
        paymentRepository.save(payment);
    }

    private Subscription createSubscription(SubscriptionRequest request, String planId, Seller seller) {
        Subscription subscription = new Subscription();

        subscription.setAmount(request.getAmount());
        subscription.setCycles(request.getCycles());
        subscription.setFrequency(request.getFrequency());
        subscription.setFrequencyInterval(request.getInterval());
        subscription.setPlanId(planId);
        subscription.setRedirectUrl(request.getRedirectUrl());
        subscription.setSubject(request.getSubject());
        subscription.setSeller(seller);
        subscription.setStatus(SubscriptionStatus.CREATED);

        return subscription;
    }
}
