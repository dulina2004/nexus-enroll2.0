package com.nexusenroll.course.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * JPA entity representing a faculty course change request.
 */
@Entity
@Table(name = "course_change_requests", schema = "nexus_course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;

    @Column(name = "request_type", nullable = false, length = 50)
    private String requestType;

    @Column(name = "current_value", columnDefinition = "TEXT")
    private String currentValue;

    @Column(name = "proposed_value", nullable = false, columnDefinition = "TEXT")
    private String proposedValue;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Column(length = 50)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
