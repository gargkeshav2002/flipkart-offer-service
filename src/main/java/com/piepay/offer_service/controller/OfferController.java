package com.piepay.offer_service.controller;

import com.piepay.offer_service.dto.HighestDiscountResponse;
import com.piepay.offer_service.dto.OfferSaveResponse;
import com.piepay.offer_service.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Offer Management", description = "Operations related to offers like saving and fetching highest discounts")
public class OfferController {

    private final OfferService offerService;

    @Operation(summary = "Save offers from Flipkart API response")
    @PostMapping("/offer")
    public ResponseEntity<OfferSaveResponse> saveOffers(@RequestBody Object rawFlipkartResponse) {
        OfferSaveResponse response = offerService.processAndSaveOffers(rawFlipkartResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Find best offer and return highest discount")
    @GetMapping("/highest-discount")
    public ResponseEntity<HighestDiscountResponse> getHighestDiscount(@RequestParam int amountToPay,
                                                                      @RequestParam String bankName, @RequestParam String paymentInstrument) {
        return ResponseEntity.ok(offerService.getBestDiscountAmount(amountToPay, bankName, paymentInstrument));
    }
}
