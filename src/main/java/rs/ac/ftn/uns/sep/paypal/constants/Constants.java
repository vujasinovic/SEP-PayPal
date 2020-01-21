package rs.ac.ftn.uns.sep.paypal.constants;

public interface Constants {
    interface Url {
        String HOST = "http://localhost/paypal";
        String RETURN_URL = HOST + "/processPayment";
        String CANCEL_URL = HOST + "/failedPayment/";
    }

    interface Paypal {
        String PAYMENT_METHOD = "paypal";
        String CURRENCY = "USD";
    }

    interface ApprovalUrl {
        int APPROVAL_URL_INDEX = 1;
    }

    interface Plan {
        String PLAN_TYPE = "fixed";
    }

    interface ChargeModel {
        String CHARGE_MODEL_TYPE = "SHIPPING";
    }

    interface PaymentDefinition {
        String NAME = "Regular payments.";
        String TYPE = "REGULAR";
    }

    interface MerchantPreferences {
        String MAXIMUM_FAIL_ATTEMPTS = "0";
        String AUTOMATIC_BILL = "YES";
        String FAIL_ACTION = "CONTINUE";
    }

    interface Patch {
        String STATE = "state";
        String ACTIVE = "ACTIVE";
        String OPERATION = "replace";
        String PATH = "/";
    }
}
