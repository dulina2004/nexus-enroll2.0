package com.nexusenroll.course.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * JPA entity representing a course requirement for a degree program.
 */
@Entity
@Table(name = "program_requirements", schema = "nexus_course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "requirement_type", nullable = false, length = 20)
    @Builder.Default
    private String requirementType = "CORE";

    @Column(name = "minimum_grade", length = 5)
    @Builder.Default
    private String minimumGrade = "C";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
