package com.nexusenroll.faculty.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for a single grade record, tracking its points, letter grade and lifecycle status.
 */
@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "assignment_title", length = 150)
    private String assignmentTitle;

    @Column(name = "max_points")
    private Double maxPoints;

    @Column(name = "points_earned")
    private Double pointsEarned;

    @Column(name = "letter_grade", length = 10)
    private String letterGrade;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "graded_by", length = 100)
    private String gradedBy;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}
