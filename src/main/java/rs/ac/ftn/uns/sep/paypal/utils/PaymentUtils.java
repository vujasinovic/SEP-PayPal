package rs.ac.ftn.uns.sep.paypal.utils;

import com.paypal.api.payments.*;

import java.math.BigDecimal;

public final class PaymentUtils {

    private static final String PAYMENT_METHOD = "paypal";
    private static final String RETURN_URL = "http://localhost/paypal/processPayment";
    private static final String CANCEL_URL = "http://localhost/paypal/failedPayment/";
    private static final String CURRENCY = "USD";
    private static final int APPROVAL_URL_INDEX = 1;

    private PaymentUtils() {

    }

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
}
