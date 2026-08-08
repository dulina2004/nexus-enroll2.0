package com.nexusenroll.faculty.state;

import com.nexusenroll.common.exception.ValidationException;

/**
 * Context class maintaining the current GradeState and grade attributes.
 */
public class GradeContext {
    private Long id;
    private long enrollmentId;
    private long studentId;
    private long sectionId;
    private String assignmentTitle;
    private double pointsEarned;
    private double maxPoints;
    private String letterGrade;
    private String comments;
    private String gradedBy;
    private GradeState state;

    public GradeContext() {
        this.state = new DraftState();
    }

    public GradeContext(Long id, long enrollmentId, long studentId, long sectionId,
                        String assignmentTitle, double pointsEarned, double maxPoints,
                        String letterGrade, String comments, String gradedBy, String status) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.assignmentTitle = assignmentTitle;
        this.pointsEarned = pointsEarned;
        this.maxPoints = maxPoints;
        this.letterGrade = letterGrade;
        this.comments = comments;
        this.gradedBy = gradedBy;
        this.state = resolveState(status);
    }

    public void submit() throws ValidationException {
        state.submit(this);
    }

    public void approve() throws ValidationException {
        state.approve(this);
    }

    public void reject() throws ValidationException {
        state.reject(this);
    }

    public static GradeState resolveState(String status) {
        if (status == null) {
            return new DraftState();
        }
        return switch (status.toUpperCase()) {
            case "PENDING" -> new PendingState();
            case "APPROVED" -> new ApprovedState();
            default -> new DraftState();
        };
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(long enrollmentId) { this.enrollmentId = enrollmentId; }

    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }

    public long getSectionId() { return sectionId; }
    public void setSectionId(long sectionId) { this.sectionId = sectionId; }

    public String getAssignmentTitle() { return assignmentTitle; }
    public void setAssignmentTitle(String assignmentTitle) { this.assignmentTitle = assignmentTitle; }

    public double getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(double pointsEarned) { this.pointsEarned = pointsEarned; }

    public double getMaxPoints() { return maxPoints; }
    public void setMaxPoints(double maxPoints) { this.maxPoints = maxPoints; }

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getGradedBy() { return gradedBy; }
    public void setGradedBy(String gradedBy) { this.gradedBy = gradedBy; }

    public GradeState getState() { return state; }
    public void setState(GradeState state) { this.state = state; }

    public String getStatusName() { return state.getStatusName(); }
    public boolean canEdit() { return state.canEdit(); }
}
