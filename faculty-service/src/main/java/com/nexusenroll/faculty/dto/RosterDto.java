package com.nexusenroll.faculty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RosterDto {
    private long sectionId;
    private String courseCode;
    private String courseTitle;
    private String semester;
    private int year;

    @Builder.Default
    private List<StudentItemDto> students = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentItemDto {
        private long studentId;
        private long enrollmentId;
        private String studentNumber;
        private String firstName;
        private String lastName;
        private String email;
        private String enrollmentStatus;
    }
}
