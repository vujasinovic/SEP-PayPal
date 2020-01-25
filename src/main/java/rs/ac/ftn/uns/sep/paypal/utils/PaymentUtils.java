package rs.ac.ftn.uns.sep.paypal.utils;

import com.paypal.api.payments.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.ac.ftn.uns.sep.paypal.utils.dto.SubscriptionRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static rs.ac.ftn.uns.sep.paypal.constants.Constants.ApprovalUrl.APPROVAL_URL_INDEX;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Paypal.CURRENCY;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Paypal.PAYMENT_METHOD;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Url.CANCEL_URL;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Url.RETURN_URL;
import static rs.ac.ftn.uns.sep.paypal.utils.SubscriptionUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentUtils {

    private static final String COUNTRY_CODE = "RS";

    public static Amount setAmount(BigDecimal total) {
        Amount amount = new Amount();

        amount.setCurrency(CURRENCY);
        amount.setTotal(total.toString());

        return amount;
    }

    /**
     * @param email - email of paypal account
     * @return created payee object
     */
    private static Payee setPayee(String email) {
        Payee payee = new Payee();

        payee.setEmail(email);

        return payee;
    }

    public static Payer initializePayer() {
        Payer payer = new Payer();

        payer.setPaymentMethod(PAYMENT_METHOD);

        return payer;
    }

    public static RedirectUrls setRedirectUrls(Long id) {
        RedirectUrls redirectUrls = new RedirectUrls();

        redirectUrls.setReturnUrl(RETURN_URL);
        redirectUrls.setCancelUrl(CANCEL_URL + id);

        return redirectUrls;
    }

    public static Transaction setTransaction(BigDecimal total, String paypalEmail) {
        Amount amount = setAmount(total);

        Payee payee = setPayee(paypalEmail);

        Transaction transaction = new Transaction();

        transaction.setAmount(amount);
        transaction.setPayee(payee);

        return transaction;
    }

    public static String approvalUrl(Payment createdPayment) {
        return createdPayment.getLinks().get(APPROVAL_URL_INDEX).getHref();
    }

    public static Plan createPlan(SubscriptionRequest request) {
        Plan plan = plan(request.getSubject());

        PaymentDefinition paymentDefinition = paymentDefinition(request.getFrequency(),
                request.getInterval(), String.valueOf(request.getCycles()));

        Currency amount = currency(request.getAmount());

        paymentDefinition.setChargeModels(List.of(chargeModel(amount)));
        paymentDefinition.setAmount(amount);

        List<PaymentDefinition> paymentDefinitions = new ArrayList<>();
        paymentDefinitions.add(paymentDefinition);

        plan.setPaymentDefinitions(paymentDefinitions);
        plan.setMerchantPreferences(merchantPreferences(amount));

        return plan;
    }

    public static Agreement createAgreement(String planId) {
        Agreement agreement = new Agreement();
        agreement.setName("Subscription agreement");
        agreement.setDescription("Starting new subscription agreement");
        agreement.setStartDate(DateHelper.tomorrow());

        Plan plan = new Plan();
        plan.setId(planId);
        agreement.setPlan(plan);

        agreement.setPayer(initializePayer());

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setLine1("Trg Dositeja Obradovica 3");
        shippingAddress.setCity("Novi Sad");
        shippingAddress.setState("Serbia");
        shippingAddress.setPostalCode("21000");
        shippingAddress.setCountryCode(COUNTRY_CODE);

        agreement.setShippingAddress(shippingAddress);

        return agreement;
    }
}
