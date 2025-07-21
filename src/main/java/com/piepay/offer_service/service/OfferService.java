package com.piepay.offer_service.service;

import com.piepay.offer_service.dto.HighestDiscountResponse;
import com.piepay.offer_service.dto.OfferSaveResponse;

public interface OfferService {
    OfferSaveResponse processAndSaveOffers(Object rawApiResponse);
    HighestDiscountResponse getBestDiscountAmount(int amountToPay, String bankName, String paymentInstrument);
}