package com.nexusenroll.enrollment.model;

import java.sql.Time;

/**
 * DTO representing a snapshot of a course section fetched from Course Service.
 */
public class CourseSectionSnapshot {
    private Long sectionId;
    private Long courseId;
    private String courseCode;
    private String title;
    private Integer courseNumber;
    private Integer credits;
    private Integer capacity;
    private Integer enrolledCount;
    private String scheduleDays;
    private Time startTime;
    private Time endTime;
    private String semester;
    private Integer year;
    private String status;
    private String prerequisites;
    private String coRequisites;
    private Long instructorId;

    public CourseSectionSnapshot() {}

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getCourseNumber() { return courseNumber; }
    public void setCourseNumber(Integer courseNumber) { this.courseNumber = courseNumber; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(Integer enrolledCount) { this.enrolledCount = enrolledCount; }

    public String getScheduleDays() { return scheduleDays; }
    public void setScheduleDays(String scheduleDays) { this.scheduleDays = scheduleDays; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }

    public String getCoRequisites() { return coRequisites; }
    public void setCoRequisites(String coRequisites) { this.coRequisites = coRequisites; }

    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }
}
