package com.chidinma.NGO.Donation.Impact.Tracker.system.service;

import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ImpactIndicatorRepository indicatorRepository;
    private final ProjectUpdateRepository updateRepository;
    private final DonationRepository donationRepository;
    private final FileStorageService fileStorageService;

    public ProjectService(ProjectRepository projectRepository,
                          ImpactIndicatorRepository indicatorRepository,
                          ProjectUpdateRepository updateRepository,
                          DonationRepository donationRepository,
                          FileStorageService fileStorageService) {
        this.projectRepository = projectRepository;
        this.indicatorRepository = indicatorRepository;
        this.updateRepository = updateRepository;
        this.donationRepository = donationRepository;
        this.fileStorageService = fileStorageService;
    }

    // ==================== PROJECT MANAGEMENT ====================

    @Transactional
    public ProjectDTOs.ProjectResponse createProject(ProjectDTOs.CreateProjectRequest request) {
        String projectCode = generateProjectCode(request.getSector(), request.getCountry());

        Project project = Project.builder()
                .projectCode(projectCode)
                .projectName(request.getProjectName())
                .description(request.getDescription())
                .sector(request.getSector())
                .country(request.getCountry())
                .region(request.getRegion())
                .targetBeneficiaries(request.getTargetBeneficiaries())
                .targetPopulation(request.getTargetPopulation())
                .status(ProjectStatus.PLANNING)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalBudget(request.getTotalBudget())
                .fundsRaised(BigDecimal.ZERO)
                .fundsSpent(BigDecimal.ZERO)
                .fundsRemaining(BigDecimal.ZERO)
                .implementingPartner(request.getImplementingPartner())
                .build();

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    private String generateProjectCode(String sector, String country) {
        String sectorCode = sector != null ? sector.substring(0, 3).toUpperCase() : "GEN";
        String countryCode = country != null ? country.substring(0, 3).toUpperCase() : "INT";
        int year = LocalDate.now().getYear();
        Long count = projectRepository.countByYearAndSector(year, sector) + 1;
        return String.format("PRJ-%d-%s-%s-%03d", year, sectorCode, countryCode, count);
    }

    public List<ProjectDTOs.ProjectResponse> listActiveProjects() {
        return projectRepository.findByStatusIn(Arrays.asList(
                        ProjectStatus.FUNDRAISING,
                        ProjectStatus.IMPLEMENTATION,
                        ProjectStatus.MONITORING
                )).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== IMPACT TRACKING ====================

    @Transactional
    public void recordImpactMeasurement(Long indicatorId, Integer value,
                                        String evidenceUrl, String measuredBy) {
        ImpactIndicator indicator = indicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new RuntimeException("Indicator not found"));

        ImpactMeasurement measurement = ImpactMeasurement.builder()
                .indicator(indicator)
                .value(value)
                .measurementPeriodStart(LocalDate.now().withDayOfMonth(1))
                .measurementPeriodEnd(LocalDate.now())
                .evidenceUrl(evidenceUrl)
                .measuredBy(measuredBy)
                .build();

        indicator.getMeasurements().add(measurement);

        // Update actual value (latest or cumulative depending on type)
        indicator.setActualValue(calculateActualValue(indicator));
        indicator.setMeasurementDate(LocalDate.now());

        indicatorRepository.save(indicator);

        // Update project reach if applicable
        if (indicator.getIndicatorName().toLowerCase().contains("people") ||
                indicator.getIndicatorName().toLowerCase().contains("beneficiaries")) {
            Project project = indicator.getProject();
            project.setActualPopulationReached(value);
            projectRepository.save(project);
        }
    }

    private Integer calculateActualValue(ImpactIndicator indicator) {
        // Logic depends on indicator type
        if (indicator.getIndicatorType() == ImpactIndicatorType.OUTPUT) {
            // Sum all measurements
            return indicator.getMeasurements().stream()
                    .mapToInt(ImpactMeasurement::getValue)
                    .sum();
        } else {
            // Take latest measurement for outcome/impact
            return indicator.getMeasurements().stream()
                    .max(Comparator.comparing(ImpactMeasurement::getMeasurementPeriodEnd))
                    .map(ImpactMeasurement::getValue)
                    .orElse(0);
        }
    }

    // ==================== REPORTING ====================

    public ProjectDTOs.ImpactReportDTO generateImpactReport(Long projectId,
                                                            LocalDate startDate,
                                                            LocalDate endDate) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Financial breakdown
        List<ProjectDTOs.FinancialBreakdown> financialBreakdown = project.getExpenses().stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ))
                .entrySet().stream()
                .map(e -> {
                    BigDecimal budgeted = BigDecimal.ZERO; // Would come from project budget allocation
                    BigDecimal actual = e.getValue();
                    return ProjectDTOs.FinancialBreakdown.builder()
                            .category(e.getKey())
                            .budgeted(budgeted)
                            .actual(actual)
                            .variance(budgeted.subtract(actual))
                            .build();
                })
                .collect(Collectors.toList());

        // Impact achievements
        List<ProjectDTOs.ImpactAchievement> achievements = project.getImpactIndicators().stream()
                .map(i -> ProjectDTOs.ImpactAchievement.builder()
                        .indicatorName(i.getIndicatorName())
                        .target(i.getTargetValue())
                        .actual(i.getActualValue() != null ? i.getActualValue() : 0)
                        .narrative(generateNarrative(i))
                        .build())
                .collect(Collectors.toList());

        return ProjectDTOs.ImpactReportDTO.builder()
                .projectName(project.getProjectName())
                .reportingPeriod(startDate + " to " + endDate)
                .fundsUtilized(project.getFundsSpent())
                .achievements(achievements)
                .financialBreakdown(financialBreakdown)
                .nextPeriodPlan("Continue implementation of remaining activities")
                .build();
    }

    private String generateNarrative(ImpactIndicator indicator) {
        int actual = indicator.getActualValue() != null ? indicator.getActualValue() : 0;
        int target = indicator.getTargetValue() != null ? indicator.getTargetValue() : 1;
        double percent = (actual * 100.0) / target;

        if (percent >= 100) {
            return String.format("Target exceeded or met. %d %s achieved against target of %d.",
                    actual, indicator.getUnitOfMeasurement(), target);
        } else if (percent >= 75) {
            return String.format("Good progress at %.0f%%. %d %s achieved.",
                    percent, actual, indicator.getUnitOfMeasurement());
        } else {
            return String.format("Behind target at %.0f%%. Acceleration needed.", percent);
        }
    }

    // ==================== PROJECT UPDATES ====================

    @Transactional
    public void addProjectUpdate(Long projectId, String title, String content,
                                 String updateType, List<MultipartFile> mediaFiles) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectUpdate update = ProjectUpdate.builder()
                .project(project)
                .updateTitle(title)
                .updateContent(content)
                .updateType(updateType)
                .isPublic(true)
                .build();

        // Handle media uploads
        if (mediaFiles != null) {
            for (MultipartFile file : mediaFiles) {
                String url = fileStorageService.storeFile(file);
                UpdateMedia media = UpdateMedia.builder()
                        .update(update)
                        .mediaType(determineMediaType(file.getContentType()))
                        .fileUrl(url)
                        .build();
                update.getMedia().add(media);
            }
        }

        updateRepository.save(update);

        // Notify donors
        notifyDonorsOfUpdate(project, update);
    }

    private String determineMediaType(String contentType) {
        if (contentType == null) return "DOCUMENT";
        if (contentType.startsWith("image/")) return "IMAGE";
        if (contentType.startsWith("video/")) return "VIDEO";
        return "DOCUMENT";
    }

    private void notifyDonorsOfUpdate(Project project, ProjectUpdate update) {
        // Get donors who contributed to this project
        List<Donor> donors = donationRepository.findDonorsByProject(project.getId());

        for (Donor donor : donors) {
            // Send personalized update
        }
    }

    // ==================== MAPPERS ====================

    private ProjectDTOs.ProjectResponse mapToResponse(Project project) {
        List<ProjectDTOs.MilestoneDTO> milestones = project.getMilestones().stream()
                .map(m -> ProjectDTOs.MilestoneDTO.builder()
                        .id(m.getId())
                        .milestoneName(m.getMilestoneName())
                        .description(m.getDescription())
                        .targetDate(m.getTargetDate())
                        .status(m.getStatus())
                        .budgetAllocation(m.getBudgetAllocation())
                        .build())
                .collect(Collectors.toList());

        List<ProjectDTOs.ImpactIndicatorDTO> indicators = project.getImpactIndicators().stream()
                .map(i -> {
                    Double achievement = null;
                    if (i.getTargetValue() != null && i.getTargetValue() > 0 && i.getActualValue() != null) {
                        achievement = (i.getActualValue() * 100.0) / i.getTargetValue();
                    }

                    return ProjectDTOs.ImpactIndicatorDTO.builder()
                            .id(i.getId())
                            .indicatorName(i.getIndicatorName())
                            .description(i.getDescription())
                            .indicatorType(i.getIndicatorType() != null ? i.getIndicatorType().name() : null)
                            .unitOfMeasurement(i.getUnitOfMeasurement())
                            .baselineValue(i.getBaselineValue())
                            .targetValue(i.getTargetValue())
                            .actualValue(i.getActualValue())
                            .achievementPercentage(achievement)
                            .build();
                })
                .collect(Collectors.toList());

        List<ProjectDTOs.ProjectUpdateDTO> updates = project.getUpdates().stream()
                .sorted(Comparator.comparing(ProjectUpdate::getCreatedAt).reversed())
                .limit(3)
                .map(u -> ProjectDTOs.ProjectUpdateDTO.builder()
                        .id(u.getId())
                        .updateTitle(u.getUpdateTitle())
                        .updateContent(u.getUpdateContent())
                        .updateType(u.getUpdateType())
                        .mediaUrls(u.getMedia().stream().map(UpdateMedia::getFileUrl).collect(Collectors.toList()))
                        .createdAt(u.getCreatedAt().toLocalDate())
                        .build())
                .collect(Collectors.toList());

        return ProjectDTOs.ProjectResponse.builder()
                .id(project.getId())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .description(project.getDescription())
                .sector(project.getSector())
                .country(project.getCountry())
                .region(project.getRegion())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .totalBudget(project.getTotalBudget())
                .fundsRaised(project.getFundsRaised())
                .fundsSpent(project.getFundsSpent())
                .fundsRemaining(project.getFundsRemaining())
                .fundingPercentage(project.getFundingPercentage())
                .targetPopulation(project.getTargetPopulation())
                .actualPopulationReached(project.getActualPopulationReached())
                .milestones(milestones)
                .impactIndicators(indicators)
                .recentUpdates(updates)
                .build();
    }
}
