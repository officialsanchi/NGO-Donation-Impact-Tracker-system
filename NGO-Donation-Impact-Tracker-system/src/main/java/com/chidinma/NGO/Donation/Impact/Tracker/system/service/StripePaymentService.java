package com.chidinma.NGO.Donation.Impact.Tracker.system.service;

import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public DonationDTOs.PaymentIntentResponse createPaymentIntent(Donation donation) {
        try {
            // Convert to smallest currency unit (cents)
            long amountInCents = donation.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(donation.getCurrency().name().toLowerCase())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putMetadata("donation_id", donation.getId().toString())
                    .putMetadata("donor_id", donation.getDonor().getId().toString())
                    .putMetadata("project_id", donation.getDesignatedProject() != null ?
                            donation.getDesignatedProject().getId().toString() : "unrestricted")
                    .setDescription("Donation to NGO: " +
                            (donation.getDesignatedProject() != null ?
                                    donation.getDesignatedProject().getProjectName() : "General Fund"))
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return DonationDTOs.PaymentIntentResponse.builder()
                    .clientSecret(intent.getClientSecret())
                    .paymentIntentId(intent.getId())
                    .amount(donation.getAmount())
                    .currency(donation.getCurrency().name())
                    .status(intent.getStatus())
                    .build();

        } catch (Exception e) {
            throw new PaymentProcessingException("Failed to create Stripe payment intent", e);
        }
    }

    public void confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            if ("succeeded".equals(intent.getStatus())) {
                // Update donation status
                Long donationId = Long.parseLong(intent.getMetadata().get("donation_id"));
                // Call donation service to complete
            }
        } catch (Exception e) {
            throw new PaymentProcessingException("Failed to confirm payment", e);
        }
    }

    public BigDecimal calculateFee(BigDecimal amount, String currency) {
        // Stripe pricing: 2.9% + 30¢ for cards
        BigDecimal percentageFee = amount.multiply(new BigDecimal("0.029"));
        BigDecimal fixedFee = new BigDecimal("0.30");
        return percentageFee.add(fixedFee);
    }
}
