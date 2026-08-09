package com.nexusenroll.enrollment.repository;

import com.nexusenroll.enrollment.model.Enrollment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void testSaveAndFindByStudentIdAndSectionId() {
        Enrollment enrollment = new Enrollment(100L, 200L, LocalDate.now(), "ENROLLED");
        enrollmentRepository.save(enrollment);

        Optional<Enrollment> found = enrollmentRepository.findByStudentIdAndSectionId(100L, 200L);
        assertTrue(found.isPresent());
        assertEquals("ENROLLED", found.get().getStatus());
    }

    @Test
    void testFindSectionIdsByStudentIdAndStatus() {
        Enrollment e1 = new Enrollment(100L, 201L, LocalDate.now(), "ENROLLED");
        Enrollment e2 = new Enrollment(100L, 202L, LocalDate.now(), "COMPLETED");
        Enrollment e3 = new Enrollment(100L, 203L, LocalDate.now(), "ENROLLED");

        enrollmentRepository.saveAll(List.of(e1, e2, e3));

        List<Long> enrolledSections = enrollmentRepository.findSectionIdsByStudentIdAndStatus(100L, "ENROLLED");
        assertEquals(2, enrolledSections.size());
        assertTrue(enrolledSections.contains(201L));
        assertTrue(enrolledSections.contains(203L));
        assertFalse(enrolledSections.contains(202L));
    }
}
