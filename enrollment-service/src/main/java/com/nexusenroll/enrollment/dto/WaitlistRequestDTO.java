package com.nexusenroll.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request payload for a student joining the waitlist for a full course section.
 */
public class WaitlistRequestDTO {

    @NotNull(message = "Student ID is required")
    @Positive(message = "Student ID must be positive")
    private Long studentId;

    @NotNull(message = "Section ID is required")
    @Positive(message = "Section ID must be positive")
    private Long sectionId;

    public WaitlistRequestDTO() {}

    public WaitlistRequestDTO(Long studentId, Long sectionId) {
        this.studentId = studentId;
        this.sectionId = sectionId;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
}
