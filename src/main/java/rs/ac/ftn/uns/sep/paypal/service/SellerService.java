package rs.ac.ftn.uns.sep.paypal.service;

import rs.ac.ftn.uns.sep.paypal.model.Seller;

public interface SellerService {
    Seller findByEmail(String sellerEmail);
}
