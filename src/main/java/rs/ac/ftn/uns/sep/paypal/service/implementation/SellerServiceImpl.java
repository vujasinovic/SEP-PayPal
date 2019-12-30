package rs.ac.ftn.uns.sep.paypal.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.paypal.model.Seller;
import rs.ac.ftn.uns.sep.paypal.repository.SellerRepository;
import rs.ac.ftn.uns.sep.paypal.service.SellerService;

@Service
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;

    public SellerServiceImpl(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Override
    public Seller findByEmail(String sellerEmail) {
        return sellerRepository.findByEmail(sellerEmail);
    }
}
