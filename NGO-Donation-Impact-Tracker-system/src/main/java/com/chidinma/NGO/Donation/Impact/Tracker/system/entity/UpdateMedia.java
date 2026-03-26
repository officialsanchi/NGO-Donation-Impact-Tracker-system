package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;
@Entity
@Table(name = "update_media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_id", nullable = false)
    private ProjectUpdate update;

    private String mediaType; // IMAGE, VIDEO, DOCUMENT

    private String fileUrl;

    private String caption;

    @CreationTimestamp
    private LocalDateTime uploadedAt;
}
