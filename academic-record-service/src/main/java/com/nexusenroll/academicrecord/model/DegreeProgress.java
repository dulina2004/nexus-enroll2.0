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

/** Entity tracking one student's progress toward degree completion by credit category. */
@Entity
@Table(name = "degree_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DegreeProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private Long studentId;

    @Column(name = "program_id")
    private Long programId;

    @Column(name = "total_credits_required")
    private Integer totalCreditsRequired;

    @Column(name = "total_credits_completed")
    private Integer totalCreditsCompleted;

    @Column(name = "general_ed_completed")
    private Integer generalEdCompleted;

    @Column(name = "major_credits_completed")
    private Integer majorCreditsCompleted;

    @Column(name = "elective_credits_completed")
    private Integer electiveCreditsCompleted;

    @Column(name = "progress_percentage")
    private Double progressPercentage;

    @Column(name = "expected_graduation_date")
    private LocalDate expectedGraduationDate;

    public void calculatePercentage() {
        if (totalCreditsRequired != null && totalCreditsRequired > 0) {
            int completed = totalCreditsCompleted != null ? totalCreditsCompleted : 0;
            this.progressPercentage = Math.min(100.0, ((double) completed / totalCreditsRequired) * 100.0);
        } else {
            this.progressPercentage = 0.0;
        }
    }
}
