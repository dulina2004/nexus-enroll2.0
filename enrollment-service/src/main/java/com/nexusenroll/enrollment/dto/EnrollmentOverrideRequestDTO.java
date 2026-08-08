package com.nexusenroll.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class EnrollmentOverrideRequestDTO {

    @NotNull(message = "Student ID is required")
    @Positive(message = "Student ID must be positive")
    private Long studentId;

    @NotNull(message = "Section ID is required")
    @Positive(message = "Section ID must be positive")
    private Long sectionId;

    private Long adminUserId;

    private String reason;

    public EnrollmentOverrideRequestDTO() {}

    public EnrollmentOverrideRequestDTO(Long studentId, Long sectionId, Long adminUserId, String reason) {
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.adminUserId = adminUserId;
        this.reason = reason;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public Long getAdminUserId() { return adminUserId; }
    public void setAdminUserId(Long adminUserId) { this.adminUserId = adminUserId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
