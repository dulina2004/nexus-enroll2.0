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

/** Entity mapping one course a student has finished, with its grade and credits. */
@Entity
@Table(name = "academic_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_code", nullable = false, length = 20)
    private String courseCode;

    @Column(name = "course_title", length = 150)
    private String courseTitle;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "grade", length = 10)
    private String grade;

    @Column(name = "semester", length = 20)
    private String semester;

    @Column(name = "`year`")
    private Integer year;

    @Column(name = "instructor_name", length = 100)
    private String instructorName;
}
