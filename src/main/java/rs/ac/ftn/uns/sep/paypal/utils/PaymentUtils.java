package rs.ac.ftn.uns.sep.paypal.utils;

import com.paypal.api.payments.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static rs.ac.ftn.uns.sep.paypal.constants.Constants.ApprovalUrl.APPROVAL_URL_INDEX;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Paypal.CURRENCY;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Paypal.PAYMENT_METHOD;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Url.CANCEL_URL;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Url.RETURN_URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentUtils {

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
