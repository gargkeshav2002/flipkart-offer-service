package com.piepay.offer_service.repository;

import com.piepay.offer_service.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, String> {
    Optional<Offer> findByTitle(String title);
    List<Offer> findByBankNameIgnoreCaseAndPaymentInstrumentIgnoreCase(String bankName, String paymentInstrument);
}