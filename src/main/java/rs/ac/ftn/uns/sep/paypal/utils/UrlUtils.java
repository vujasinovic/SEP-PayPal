package rs.ac.ftn.uns.sep.paypal.utils;

import com.paypal.api.payments.Links;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static rs.ac.ftn.uns.sep.paypal.constants.Constants.Url.APPROVAL_URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlUtils {

    @SneakyThrows(MalformedURLException.class)
    public static URL getApprovalUrl(List<Links> links) {
        URL url = null;

        for (Links link : links) {
            if (APPROVAL_URL.equalsIgnoreCase(link.getRel())) {
                url = new URL(link.getHref());
                break;
            }
        }

        return url;
    }

    public static String parseAgreementToken(URL url) {
        String retVal = "";
        String token = url.getQuery().split("&")[1];

        String[] tokenParam = token.split("=");

        if (tokenParam[0].equalsIgnoreCase("token")) {
            retVal = tokenParam[1];
        }

        return retVal;
    }
}
