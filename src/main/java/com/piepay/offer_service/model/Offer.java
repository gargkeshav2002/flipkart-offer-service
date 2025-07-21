package com.piepay.offer_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Offer {
    @Id
    private String offerId;

    private String title;
    private String bankName;
    private String paymentInstrument;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Integer discountValue;
    private Integer maxDiscount;

    @Enumerated(EnumType.STRING)
    private OfferCategory offerCategory;

    private LocalDateTime validTill;
}