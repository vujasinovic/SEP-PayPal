package rs.ac.ftn.uns.sep.paypal.utils;

import com.paypal.api.payments.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

import static rs.ac.ftn.uns.sep.paypal.constants.Constants.ChargeModel.CHARGE_MODEL_TYPE;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.MerchantPreferences.*;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Patch.OPERATION;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Patch.PATH;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.PaymentDefinition.NAME;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.PaymentDefinition.TYPE;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Paypal.CURRENCY;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Plan.PLAN_TYPE;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Url.CANCEL_URL;
import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Url.RETURN_URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubscriptionUtils {

    public static Plan plan(String planName) {
        return new Plan(planName, "Template creation.", PLAN_TYPE);
    }

    public static PaymentDefinition paymentDefinition(String frequency, String interval, String cycles) {
        PaymentDefinition paymentDefinition = new PaymentDefinition();
        paymentDefinition.setName(NAME);
        paymentDefinition.setType(TYPE);
        paymentDefinition.setFrequency(frequency.toUpperCase());
        paymentDefinition.setFrequencyInterval(interval);
        paymentDefinition.setCycles(cycles);

        return paymentDefinition;
    }

    public static Currency currency(BigDecimal amount) {
        return new Currency(CURRENCY, String.valueOf(amount));
    }

    public static ChargeModels chargeModel(Currency amount) {
        return new ChargeModels(CHARGE_MODEL_TYPE, amount);
    }

    public static MerchantPreferences merchantPreferences(Currency currency) {
        MerchantPreferences merchantPreferences = new MerchantPreferences();

        merchantPreferences.setSetupFee(currency);
        merchantPreferences.setCancelUrl(CANCEL_URL);
        merchantPreferences.setReturnUrl(RETURN_URL);
        merchantPreferences.setMaxFailAttempts(MAXIMUM_FAIL_ATTEMPTS);
        merchantPreferences.setAutoBillAmount(AUTOMATIC_BILL);
        merchantPreferences.setInitialFailAmountAction(FAIL_ACTION);

        return merchantPreferences;
    }

    public static Patch patch(Map<String, String> value) {
        Patch patch = new Patch();

        patch.setOp(OPERATION);
        patch.setPath(PATH);
        patch.setValue(value);

        return patch;
    }
}
