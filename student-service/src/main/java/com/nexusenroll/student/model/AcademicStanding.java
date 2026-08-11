package com.nexusenroll.student.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * JPA entity representing a student's academic standing relative to a GPA threshold.
 */
@Entity
@Table(name = "academic_standing", schema = "nexus_student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicStanding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private Long studentId;

    @Column(length = 50)
    @Builder.Default
    private String standing = "GOOD";

    @Column(name = "gpa_threshold", nullable = false, precision = 3, scale = 2)
    private BigDecimal gpaThreshold;

    @Column(name = "last_reviewed_date")
    private LocalDate lastReviewedDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
