package com.chidinma.NGO.Donation.Impact.Tracker.system.service;

import org.springframework.stereotype.Service;

@Service
public class DonorService {

    private final DonorRepository donorRepository;
    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public DonorService(DonorRepository donorRepository,
                        DonationRepository donationRepository,
                        ProjectRepository projectRepository,
                        PasswordEncoder passwordEncoder,
                        NotificationService notificationService) {
        this.donorRepository = donorRepository;
        this.donationRepository = donationRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    // ==================== DONOR MANAGEMENT ====================

    @Transactional
    public DonorDTOs.DonorProfileResponse registerDonor(DonorDTOs.RegisterDonorRequest request) {
        if (donorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        String donorNumber = generateDonorNumber();

        Donor donor = Donor.builder()
                .donorNumber(donorNumber)
                .donorType(request.getDonorType())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .organizationName(request.getOrganizationName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .preferredCurrency(request.getPreferredCurrency() != null ?
                        request.getPreferredCurrency() : "USD")
                .anonymousDonor(request.getAnonymousDonor())
                .interests(request.getInterests())
                .newsletterSubscriber(true)
                .role(UserRole.DONOR)
                .engagementScore(0)
                .build();

        Donor saved = donorRepository.save(donor);

        // Send welcome email
        notificationService.sendWelcomeEmail(saved);

        return mapToProfileResponse(saved);
    }

    private String generateDonorNumber() {
        int year = LocalDate.now().getYear();
        Long count = donorRepository.countByYear(year) + 1;
        return String.format("D-%d-%05d", year, count);
    }

    public DonorDTOs.DonorProfileResponse getDonorProfile(Long donorId) {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        return mapToProfileResponse(donor);
    }

    // ==================== DONATION PROCESSING ====================

    @Transactional
    public DonationDTOs.DonationResponse processDonation(DonationDTOs.CreateDonationRequest request) {
        Donor donor = donorRepository.findById(request.getDonorId())
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        Project project = null;
        if (request.getProjectId() != null) {
            project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        }

        // Create donation record
        Donation donation = Donation.builder()
                .transactionId(generateTransactionId())
                .donor(donor)
                .designatedProject(project)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(DonationStatus.PENDING)
                .isRecurring(request.getIsRecurring())
                .isAnonymous(request.getIsAnonymous())
                .dedicationType(request.getDedicationType())
                .dedicationName(request.getDedicationName())
                .dedicationMessage(request.getDedicationMessage())
                .designation(project != null ? "PROJECT_SPECIFIC" : "UNRESTRICTED")
                .donationDate(LocalDate.now())
                .build();

        Donation saved = donationRepository.save(donation);

        // Process payment based on method
        DonationDTOs.PaymentIntentResponse paymentResponse = null;

        switch (request.getPaymentMethod()) {
            case CREDIT_CARD:
            case STRIPE:
                paymentResponse = processStripePayment(saved, request.getStripeToken());
                break;
            case PAYPAL:
                paymentResponse = processPayPalPayment(saved, request.getPaypalOrderId());
                break;
            case BANK_TRANSFER:
                // Mark as pending manual verification
                saved.setStatus(DonationStatus.ON_HOLD);
                break;
            default:
                saved.setStatus(DonationStatus.PENDING);
        }

        // Calculate fees
        BigDecimal fee = calculateProcessingFee(saved);
        saved.setFeeAmount(fee);
        saved.calculateNetAmount();

        donationRepository.save(saved);

        // If completed immediately, send receipt
        if (saved.getStatus() == DonationStatus.COMPLETED) {
            completeDonation(saved);
        }

        return DonationDTOs.DonationResponse.builder()
                .id(saved.getId())
                .transactionId(saved.getTransactionId())
                .amount(saved.getAmount())
                .currency(saved.getCurrency().name())
                .status(saved.getStatus())
                .paymentMethod(saved.getPaymentMethod())
                .gatewayTransactionId(saved.getGatewayTransactionId())
                .receiptUrl(saved.getStatus() == DonationStatus.COMPLETED ?
                        "/receipts/" + saved.getReceiptNumber() : null)
                .thankYouMessage(generateThankYouMessage(saved))
                .build();
    }

    private DonationDTOs.PaymentIntentResponse processStripePayment(Donation donation, String token) {
        // Integration with StripePaymentService
        return null;
    }

    private DonationDTOs.PaymentIntentResponse processPayPalPayment(Donation donation, String orderId) {
        // Integration with PayPalPaymentService
        return null;
    }

    @Transactional
    public void completeDonation(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        completeDonation(donation);
    }

    private void completeDonation(Donation donation) {
        donation.setStatus(DonationStatus.COMPLETED);
        donation.setProcessedAt(LocalDateTime.now());
        donation.setReceiptNumber(generateReceiptNumber());

        // Update donor statistics
        Donor donor = donation.getDonor();
        if (donor.getFirstDonationDate() == null) {
            donor.setFirstDonationDate(LocalDate.now());
        }
        donor.setLastDonationDate(LocalDate.now());
        donor.recalculateLifetimeValue();

        // Update project funding if designated
        if (donation.getDesignatedProject() != null) {
            Project project = donation.getDesignatedProject();
            project.recalculateFinancials();
            projectRepository.save(project);
        }

        donationRepository.save(donation);
        donorRepository.save(donor);

        // Send receipt and thank you
        notificationService.sendDonationReceipt(donation);
        notificationService.sendThankYouMessage(donation);

        // Check for milestones
        checkDonorMilestones(donor);
    }

    private void checkDonorMilestones(Donor donor) {
        int donationCount = donor.getTotalDonations();

        if (donationCount == 1) {
            notificationService.sendFirstDonationWelcome(donor);
        } else if (donationCount == 5) {
            notificationService.sendLoyaltyRecognition(donor, "5 donations");
        } else if (donor.getLifetimeValue().compareTo(new BigDecimal("1000")) >= 0 &&
                donor.getDonorSegment().equals("MID_LEVEL")) {
            notificationService.sendMajorDonorInvitation(donor);
        }
    }

    private BigDecimal calculateProcessingFee(Donation donation) {
        // Simplified fee calculation
        switch (donation.getPaymentMethod()) {
            case CREDIT_CARD:
            case STRIPE:
                return donation.getAmount().multiply(new BigDecimal("0.029")).add(new BigDecimal("0.30"));
            case PAYPAL:
                return donation.getAmount().multiply(new BigDecimal("0.029")).add(new BigDecimal("0.30"));
            case BANK_TRANSFER:
                return new BigDecimal("0"); // No fee for ACH
            default:
                return BigDecimal.ZERO;
        }
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateReceiptNumber() {
        return "R-" + LocalDate.now().getYear() + "-" +
                donationRepository.countByYear(LocalDate.now().getYear());
    }

    private String generateThankYouMessage(Donation donation) {
        String base = "Thank you for your generous donation of " +
                donation.getCurrency().getSymbol() + donation.getAmount();

        if (donation.getDesignatedProject() != null) {
            base += " to support " + donation.getDesignatedProject().getProjectName();
        }

        base += ". Your contribution makes a real difference!";

        if (donation.getDedicationType() != null) {
            base += " This donation is made " + donation.getDedicationType().toLowerCase() +
                    " " + donation.getDedicationName() + ".";
        }

        return base;
    }

    // ==================== ANALYTICS ====================

    public DonationDTOs.DonationAnalytics getDonationAnalytics(LocalDate startDate, LocalDate endDate) {
        List<Donation> donations = donationRepository
                .findByDonationDateBetweenAndStatus(startDate, endDate, DonationStatus.COMPLETED);

        BigDecimal totalRaised = donations.stream()
                .map(Donation::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Group by payment method
        Map<PaymentMethod, List<Donation>> byMethod = donations.stream()
                .collect(Collectors.groupingBy(Donation::getPaymentMethod));

        List<DonationDTOs.PaymentMethodBreakdown> methodBreakdown = byMethod.entrySet().stream()
                .map(e -> {
                    BigDecimal amount = e.getValue().stream()
                            .map(Donation::getNetAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return DonationDTOs.PaymentMethodBreakdown.builder()
                            .method(e.getKey())
                            .amount(amount)
                            .count(e.getValue().size())
                            .percentage(amount.multiply(BigDecimal.valueOf(100))
                                    .divide(totalRaised, 2, BigDecimal.ROUND_HALF_UP).doubleValue())
                            .build();
                })
                .collect(Collectors.toList());

        // Daily trends
        Map<LocalDate, List<Donation>> byDate = donations.stream()
                .collect(Collectors.groupingBy(Donation::getDonationDate));

        List<DonationDTOs.TimeSeriesPoint> trends = byDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> DonationDTOs.TimeSeriesPoint.builder()
                        .date(e.getKey().toString())
                        .amount(e.getValue().stream()
                                .map(Donation::getNetAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .count(e.getValue().size())
                        .build())
                .collect(Collectors.toList());

        // Calculate MRR (Monthly Recurring Revenue)
        BigDecimal mrr = donationRepository.findActiveRecurringDonations().stream()
                .map(RecurringDonation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DonationDTOs.DonationAnalytics.builder()
                .totalRaised(totalRaised)
                .totalThisMonth(calculateThisMonth(donations))
                .totalThisYear(calculateThisYear(donations))
                .donationCount(donations.size())
                .averageDonation(totalRaised.divide(
                        BigDecimal.valueOf(Math.max(donations.size(), 1)), 2, BigDecimal.ROUND_HALF_UP))
                .recurringRevenue(mrr)
                .byPaymentMethod(methodBreakdown)
                .dailyTrends(trends)
                .build();
    }

    private BigDecimal calculateThisMonth(List<Donation> donations) {
        LocalDate now = LocalDate.now();
        return donations.stream()
                .filter(d -> d.getDonationDate().getMonth() == now.getMonth() &&
                        d.getDonationDate().getYear() == now.getYear())
                .map(Donation::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateThisYear(List<Donation> donations) {
        int year = LocalDate.now().getYear();
        return donations.stream()
                .filter(d -> d.getDonationDate().getYear() == year)
                .map(Donation::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ==================== MAPPERS ====================

    private DonorDTOs.DonorProfileResponse mapToProfileResponse(Donor donor) {
        List<DonorDTOs.DonationSummary> recentDonations = donor.getDonations().stream()
                .sorted(Comparator.comparing(Donation::getCreatedAt).reversed())
                .limit(5)
                .map(d -> DonorDTOs.DonationSummary.builder()
                        .transactionId(d.getTransactionId())
                        .amount(d.getAmount())
                        .currency(d.getCurrency().name())
                        .status(d.getStatus())
                        .donationDate(d.getDonationDate())
                        .projectName(d.getDesignatedProject() != null ?
                                d.getDesignatedProject().getProjectName() : "General Fund")
                        .receiptNumber(d.getReceiptNumber())
                        .build())
                .collect(Collectors.toList());

        return DonorDTOs.DonorProfileResponse.builder()
                .id(donor.getId())
                .donorNumber(donor.getDonorNumber())
                .displayName(donor.getDisplayName())
                .donorType(donor.getDonorType())
                .email(donor.getEmail())
                .country(donor.getCountry())
                .donorSegment(donor.getDonorSegment())
                .totalDonations(donor.getTotalDonations())
                .lifetimeValue(donor.getLifetimeValue())
                .preferredCurrency(donor.getPreferredCurrency())
                .newsletterSubscriber(donor.getNewsletterSubscriber())
                .recentDonations(recentDonations)
                .build();
    }
}
