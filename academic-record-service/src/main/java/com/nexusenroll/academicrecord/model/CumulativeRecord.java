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

/** Entity holding one student's cumulative GPA, credit totals and graduation status. */
@Entity
@Table(name = "cumulative_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CumulativeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private Long studentId;

    @Column(name = "cumulative_gpa")
    private Double cumulativeGpa;

    @Column(name = "total_credits_earned")
    private Integer totalCreditsEarned;

    @Column(name = "total_credits_attempted")
    private Integer totalCreditsAttempted;

    @Column(name = "total_credits_passed")
    private Integer totalCreditsPassed;

    @Column(name = "total_credits_failed")
    private Integer totalCreditsFailed;

    @Column(name = "graduation_status", length = 50)
    private String graduationStatus;
}
