package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;
@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private LocalDate expenseDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private String currency;

    private String category; // PERSONNEL, SUPPLIES, TRAVEL, EQUIPMENT, OVERHEAD

    private String description;

    private String vendor;

    private String receiptUrl;

    private String approvedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
