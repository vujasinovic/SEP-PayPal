package rs.ac.ftn.uns.sep.paypal.utils;

public final class SandboxCredentialsUtils {
    private static final String CLIENT_ID = "AdjaSzhPmZladgK-Z6nAKaUZX0adg8S9eVd8s0eDarv7GZnIR_Vafp5sWnFdY4HNzSQw17nCbx7WDuS_";

    private static final String CLIENT_SECRET_ID = "EFQ163lbhW3ib3FSmt69kGMmpGu6N78zyKGvvq7y0T6nweOmhXVEpTBWyCGZ0USVHOe1r0g_SY1nXd-K";

    private SandboxCredentialsUtils() { }

    public static String clientId() {
        return CLIENT_ID;
    }

    public static String clientSecretId() {
        return CLIENT_SECRET_ID;
    }
}
