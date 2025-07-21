package com.piepay.offer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HighestDiscountResponse {
    private int highestDiscountAmount;
}
