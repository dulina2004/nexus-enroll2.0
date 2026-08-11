package com.nexusenroll.academicrecord.model;

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

import java.time.LocalDate;

/** Entity mapping one graded assignment tied to a student's course enrollment. */
@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "course_code", length = 20)
    private String courseCode;

    @Column(name = "course_title", length = 150)
    private String courseTitle;

    @Column(name = "assignment_title", length = 150)
    private String assignmentTitle;

    @Column(name = "max_points")
    private Double maxPoints;

    @Column(name = "points_earned")
    private Double pointsEarned;

    @Column(name = "letter_grade", length = 10)
    private String letterGrade;

    @Column(name = "graded_date")
    private LocalDate gradedDate;

    @Column(name = "graded_by", length = 100)
    private String gradedBy;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;
}
