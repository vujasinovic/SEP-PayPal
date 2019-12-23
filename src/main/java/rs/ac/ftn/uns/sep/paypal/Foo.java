package rs.ac.ftn.uns.sep.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import java.util.ArrayList;
import java.util.List;

public class Foo {

    public static void main(String[] args) {
        String clientId = "AdjaSzhPmZladgK-Z6nAKaUZX0adg8S9eVd8s0eDarv7GZnIR_Vafp5sWnFdY4HNzSQw17nCbx7WDuS_";
        String clientSecret = "EFQ163lbhW3ib3FSmt69kGMmpGu6N78zyKGvvq7y0T6nweOmhXVEpTBWyCGZ0USVHOe1r0g_SY1nXd-K";

        Amount amount = new Amount("USD", "50.00");

        Transaction transaction = new Transaction();

        Payee payee = new Payee();
        payee.setEmail("zikozikic@personal.example.com");

        transaction.setAmount(amount);
        transaction.setPayee(payee);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();


        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("https://example.com/cancel");
        redirectUrls.setReturnUrl("https://example.com/return");
        payment.setRedirectUrls(redirectUrls);

        APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
        try {
            Payment createdPayment = payment.create(apiContext);

            String approvalUrl = createdPayment.getLinks().get(1).getHref();

            System.out.println(approvalUrl);

            System.out.println(createdPayment);
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }
}
