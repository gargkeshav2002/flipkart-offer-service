package com.piepay.offer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OfferSaveResponse {
    private int noOfOffersIdentified;
    private int noOfNewOffersCreated;
}
