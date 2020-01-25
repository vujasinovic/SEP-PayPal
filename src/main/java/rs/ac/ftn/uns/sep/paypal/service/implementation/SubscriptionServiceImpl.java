package rs.ac.ftn.uns.sep.paypal.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.paypal.model.Subscription;
import rs.ac.ftn.uns.sep.paypal.repository.SubscriptionRepository;
import rs.ac.ftn.uns.sep.paypal.service.SubscriptionService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription getOne(Long id) {
        return subscriptionRepository.getOne(id);
    }

    @Override
    public Collection<Subscription> findAll() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Subscription findByAgreementToken(String agreementToken) {
        return subscriptionRepository.findByAgreementToken(agreementToken);
    }
}
