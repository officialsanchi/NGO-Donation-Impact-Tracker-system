package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;
@Entity
@Table(name = "project_updates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String updateTitle;

    @Column(length = 10000)
    private String updateContent;

    private String updateType; // PROGRESS, MILESTONE, CHALLENGE, SUCCESS_STORY

    @OneToMany(mappedBy = "update", cascade = CascadeType.ALL)
    private List<UpdateMedia> media = new ArrayList<>();

    private Boolean isPublic = true;

    private String createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
