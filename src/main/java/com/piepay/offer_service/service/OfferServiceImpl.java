package com.piepay.offer_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piepay.offer_service.dto.HighestDiscountResponse;
import com.piepay.offer_service.dto.OfferSaveResponse;
import com.piepay.offer_service.model.*;
import com.piepay.offer_service.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.*;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final ObjectMapper objectMapper;

    @Override
    public OfferSaveResponse processAndSaveOffers(Object rawApiResponse) {
        int identified = 0, created = 0;

        JsonNode root = objectMapper.convertValue(rawApiResponse, JsonNode.class);
        List<Offer> extractedOffers = new ArrayList<>();

        List<String> promoMessages = new ArrayList<>();
        collectPromotionMessages(root, promoMessages);
        for (String msg : promoMessages) {
            extractedOffers.add(parsePromotionMessage(msg));
        }

        JsonNode emiNode = root.at("/pricingData/noCostEmi");
        if (!emiNode.isMissingNode() && emiNode.isArray()) {
            for (JsonNode emi : emiNode) {
                String text = emi.path("description").asText();
                if (!text.isEmpty()) {
                    extractedOffers.add(parseSpecialOffer(text, OfferCategory.NO_COST_EMI));
                }
            }
        }

        JsonNode widgets = root.path("widgets");
        if (widgets.isArray()) {
            for (JsonNode widget : widgets) {
                String title = widget.path("title").asText("").toLowerCase();
                if (title.contains("emi") || title.contains("pay later")) {
                    String content = widget.path("message").asText();
                    extractedOffers.add(parseSpecialOffer(content, OfferCategory.DEFERRED_PAYMENT));
                }
            }
        }

        for (Offer offer : extractedOffers) {
            identified++;
            if (offerRepository.findByTitle(offer.getTitle()).isEmpty()) {
                offerRepository.save(offer);
                created++;
            }
        }

        return new OfferSaveResponse(identified, created);
    }

    @Override
    public HighestDiscountResponse getBestDiscountAmount(int amountToPay, String bankName, String paymentInstrument) {
        List<Offer> offers = offerRepository.findByBankNameIgnoreCaseAndPaymentInstrumentIgnoreCase(
                bankName, paymentInstrument);
        int maxDiscount = 0;

        for (Offer offer : offers) {
            int discount = calculateDiscountFromOffer(offer, amountToPay);
            maxDiscount = Math.max(maxDiscount, discount);
        }

        return new HighestDiscountResponse(maxDiscount);
    }

    private int calculateDiscountFromOffer(Offer offer, int amountToPay) {
        switch (offer.getDiscountType()) {
            case FLAT:
                return Optional.ofNullable(offer.getDiscountValue()).orElse(0);
            case PERCENTAGE:
                int percentage = Optional.ofNullable(offer.getDiscountValue()).orElse(0);
                int calculated = (amountToPay * percentage) / 100;
                return Math.min(calculated, Optional.ofNullable(offer.getMaxDiscount()).orElse(Integer.MAX_VALUE));
            case NONE:
            default:
                if (offer.getOfferCategory() == OfferCategory.NO_COST_EMI) return 500;
                if (offer.getOfferCategory() == OfferCategory.CASHBACK) return 300;
                return 0;
        }
    }

    private void collectPromotionMessages(JsonNode node, List<String> messages) {
        if (node == null) return;
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                if ("promotionMessage".equals(entry.getKey()) && entry.getValue().isTextual()) {
                    messages.add(entry.getValue().asText());
                } else {
                    collectPromotionMessages(entry.getValue(), messages);
                }
            });
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                collectPromotionMessages(item, messages);
            }
        }
    }

    private Offer parsePromotionMessage(String msg) {
        Offer.OfferBuilder builder = Offer.builder();
        builder.title(msg);
        builder.offerId("OFFER_" + UUID.randomUUID());
        builder.bankName(detectBankName(msg));
        builder.paymentInstrument(detectInstrument(msg));
        builder.offerCategory(OfferCategory.DISCOUNT);

        if (msg.toLowerCase().contains("flat")) {
            builder.discountType(DiscountType.FLAT);
            Matcher matcher = Pattern.compile("Flat ₹?(\\d+)").matcher(msg);
            if (matcher.find()) builder.discountValue(Integer.parseInt(matcher.group(1)));
        } else if (msg.contains("%")) {
            builder.discountType(DiscountType.PERCENTAGE);
            Matcher matcher = Pattern.compile("(\\d+)%").matcher(msg);
            if (matcher.find()) builder.discountValue(Integer.parseInt(matcher.group(1)));

            Matcher maxMatcher = Pattern.compile("up to ₹?(\\d+)").matcher(msg);
            if (maxMatcher.find()) builder.maxDiscount(Integer.parseInt(maxMatcher.group(1)));
        } else {
            builder.discountType(DiscountType.NONE);
            builder.discountValue(0);
        }

        builder.validTill(LocalDateTime.now().plusMonths(1));
        return builder.build();
    }

    private Offer parseSpecialOffer(String msg, OfferCategory category) {
        return Offer.builder()
                .offerId("OFFER_" + UUID.randomUUID())
                .title(msg)
                .bankName(detectBankName(msg))
                .paymentInstrument(detectInstrument(msg))
                .discountType(DiscountType.NONE)
                .discountValue(0)
                .offerCategory(category)
                .validTill(LocalDateTime.now().plusMonths(1))
                .build();
    }

    private static final Set<String> KNOWN_BANKS = Set.of(
            "HDFC", "AXIS", "IDFC", "ICICI", "SBI", "KOTAK", "BOB", "PNB", "HSBC", "YES", "BAJAJ", "AU", "RBL", "FEDERAL", "INDUSIND"
    );

    private static final Set<String> KNOWN_INSTRUMENTS = Set.of(
            "CREDIT", "DEBIT", "EMI", "EMI_OPTIONS", "UPI", "NETBANKING", "WALLET"
    );


    private String detectBankName(String msg) {
        msg = msg.toUpperCase();
        for (String bank : KNOWN_BANKS) {
            if (msg.toUpperCase().contains(bank)) return bank;
        }
        return "UNKNOWN";
    }

    private String detectInstrument(String msg) {
        msg = msg.toUpperCase();
        for (String instrument : KNOWN_INSTRUMENTS) {
            if (msg.contains(instrument)) return instrument;
        }

        if (msg.contains("EMI")) return "EMI_OPTIONS";
        return "UNKNOWN";
    }
}