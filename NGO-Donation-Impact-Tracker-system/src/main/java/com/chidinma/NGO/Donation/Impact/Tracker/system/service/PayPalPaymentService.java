package com.chidinma.NGO.Donation.Impact.Tracker.system.service;

import org.springframework.stereotype.Service;

@Service
public class PayPalPaymentService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    public String createPayment(BigDecimal amount, String currency, String description,
                                String cancelUrl, String successUrl) {
        try {
            APIContext apiContext = new APIContext(clientId, clientSecret, mode);

            Amount paymentAmount = new Amount();
            paymentAmount.setCurrency(currency);
            paymentAmount.setTotal(amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

            Transaction transaction = new Transaction();
            transaction.setDescription(description);
            transaction.setAmount(paymentAmount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(cancelUrl);
            redirectUrls.setReturnUrl(successUrl);
            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(apiContext);

            for (Links link : createdPayment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return link.getHref(); // Redirect donor to PayPal
                }
            }

            throw new PaymentProcessingException("No approval URL found in PayPal response");

        } catch (PayPalRESTException e) {
            throw new PaymentProcessingException("PayPal payment creation failed", e);
        }
    }

    public Payment executePayment(String paymentId, String payerId) {
        try {
            APIContext apiContext = new APIContext(clientId, clientSecret, mode);

            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution execution = new PaymentExecution();
            execution.setPayerId(payerId);

            return payment.execute(apiContext, execution);

        } catch (PayPalRESTException e) {
            throw new PaymentProcessingException("PayPal payment execution failed", e);
        }
    }
}
