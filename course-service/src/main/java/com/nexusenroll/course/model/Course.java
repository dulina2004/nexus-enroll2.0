package com.nexusenroll.course.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * JPA entity representing a course in the course catalog.
 */
@Entity
@Table(name = "courses", schema = "nexus_course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", nullable = false, unique = true, length = 50)
    private String courseCode;

    @Column(name = "course_number", nullable = false)
    private Integer courseNumber;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer credits;

    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 0;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(length = 50)
    @Builder.Default
    private String level = "100";

    @Column(length = 255)
    private String prerequisites;

    @Column(name = "co_requisites", length = 255)
    private String coRequisites;

    @Column(length = 50)
    @Builder.Default
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
