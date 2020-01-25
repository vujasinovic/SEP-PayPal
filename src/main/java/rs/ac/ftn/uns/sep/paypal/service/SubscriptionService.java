package rs.ac.ftn.uns.sep.paypal.service;

import rs.ac.ftn.uns.sep.paypal.model.Subscription;

import java.util.Collection;

public interface SubscriptionService {
    Subscription save(Subscription subscription);

    Subscription getOne(Long id);

    Collection<Subscription> findAll();

    Subscription findByAgreementToken(String agreementToken);
}
