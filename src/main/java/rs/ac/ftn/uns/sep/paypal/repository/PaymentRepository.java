package rs.ac.ftn.uns.sep.paypal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.ftn.uns.sep.paypal.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByPaymentId(String paymentId);

    @Query(value = "SELECT * FROM PAYMENT  WHERE STATUS LIKE 'CREATED' AND CREATED_ON < SYSDATE - INTERVAL '10' MINUTE",
            nativeQuery = true)
    List<Payment> findAllByStatusNotCompleted();
}
