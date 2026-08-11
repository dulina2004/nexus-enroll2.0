package com.nexusenroll.faculty.service;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.faculty.dto.FacultyDto;
import com.nexusenroll.faculty.dto.RosterDto;
import com.nexusenroll.faculty.model.Faculty;
import com.nexusenroll.faculty.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Looks up faculty profiles and builds class rosters for a section.
 * Called by {@link com.nexusenroll.faculty.controller.FacultyController}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Transactional(readOnly = true)
    public RosterDto getClassRoster(long sectionId) {
        if (sectionId <= 0) {
            throw new ValidationException("Invalid section ID: " + sectionId);
        }

        log.info("Fetching class roster for section ID: {}", sectionId);

        return RosterDto.builder()
                .sectionId(sectionId)
                .courseCode("SEC-" + sectionId)
                .courseTitle("Course Section " + sectionId)
                .semester("SPRING")
                .year(2026)
                .students(new ArrayList<>())
                .build();
    }

    @Transactional(readOnly = true)
    public FacultyDto getFaculty(long id) {
        if (id <= 0) {
            throw new ValidationException("Invalid faculty ID");
        }

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + id));

        return mapToDto(faculty);
    }

    @Transactional(readOnly = true)
    public FacultyDto getFacultyByUserId(long userId) {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID");
        }

        Faculty faculty = facultyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found for user ID: " + userId));

        return mapToDto(faculty);
    }

    private FacultyDto mapToDto(Faculty f) {
        return FacultyDto.builder()
                .id(f.getId())
                .userId(f.getUserId())
                .facultyId(f.getFacultyId())
                .title(f.getTitle())
                .departmentId(f.getDepartmentId())
                .officeLocation(f.getOfficeLocation())
                .officePhone(f.getOfficePhone())
                .researchInterests(f.getResearchInterests())
                .bio(f.getBio())
                .degreeLevel(f.getDegreeLevel())
                .yearsExperience(f.getYearsExperience())
                .employmentType(f.getEmploymentType())
                .status(f.getStatus())
                .hireDate(f.getHireDate())
                .build();
    }
}
