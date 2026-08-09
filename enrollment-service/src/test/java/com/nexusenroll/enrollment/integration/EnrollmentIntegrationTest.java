package com.nexusenroll.enrollment.integration;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.client.CourseServiceClient;
import com.nexusenroll.enrollment.client.NotificationClient;
import com.nexusenroll.enrollment.facade.EnrollmentFacade;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;
import com.nexusenroll.enrollment.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EnrollmentIntegrationTest {

    @Autowired
    private EnrollmentFacade enrollmentFacade;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @MockBean
    private CourseServiceClient courseServiceClient;

    @MockBean
    private NotificationClient notificationClient;

    private CourseSectionSnapshot testSection;
    private final Long STUDENT_ID = 100L;
    private final Long SECTION_ID = 200L;

    @BeforeEach
    void setUp() {
        testSection = new CourseSectionSnapshot();
        testSection.setSectionId(SECTION_ID);
        testSection.setCourseCode("CS101");
        testSection.setCapacity(30);
        testSection.setEnrolledCount(10);
        testSection.setScheduleDays("MWF");
        testSection.setStartTime(Time.valueOf("09:00:00"));
        testSection.setEndTime(Time.valueOf("10:00:00"));
    }

    @Test
    void shouldSuccessfullyEnrollStudent() throws ServiceException {
        when(courseServiceClient.getSectionSnapshot(SECTION_ID)).thenReturn(testSection);
        
        Enrollment result = enrollmentFacade.enroll(STUDENT_ID, SECTION_ID);
        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("ENROLLED", result.getStatus());
        
        Optional<Enrollment> saved = enrollmentRepository.findById(result.getId());
        assertTrue(saved.isPresent());
        assertEquals(STUDENT_ID, saved.get().getStudentId());
        assertEquals(SECTION_ID, saved.get().getSectionId());
        
        verify(courseServiceClient, times(1)).reserveSeat(SECTION_ID);
        verify(notificationClient, times(1)).publishEnrollmentCreated(any(), any());
    }

    @Test
    void shouldPreventDuplicateEnrollment() throws ServiceException {
        when(courseServiceClient.getSectionSnapshot(SECTION_ID)).thenReturn(testSection);
        
        // First enrollment
        enrollmentFacade.enroll(STUDENT_ID, SECTION_ID);
        
        // Second enrollment should fail
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            enrollmentFacade.enroll(STUDENT_ID, SECTION_ID);
        });
        
        assertTrue(exception.getMessage().contains("already enrolled"));
        
        // Should only reserve seat once
        verify(courseServiceClient, times(1)).reserveSeat(SECTION_ID);
    }
    
    @Test
    void shouldRollbackOnTransactionFailure() throws ServiceException {
        when(courseServiceClient.getSectionSnapshot(SECTION_ID)).thenReturn(testSection);
        
        // Simulate a remote failure that throws ServiceException
        doThrow(new ServiceException("Remote failure", "ERR_REMOTE", 500))
            .when(courseServiceClient).reserveSeat(SECTION_ID);
            
        long initialCount = enrollmentRepository.count();
        
        assertThrows(ServiceException.class, () -> {
            enrollmentFacade.enroll(STUDENT_ID, SECTION_ID);
        });
        
        // Verify rollback: count should remain the same, no duplicate
        long finalCount = enrollmentRepository.count();
        assertEquals(initialCount, finalCount, "Transaction should rollback and not save enrollment");
        
        // Verify no notification sent
        verify(notificationClient, never()).publishEnrollmentCreated(any(), any());
    }
    
    @Test
    void shouldPreventTimeConflict() throws ServiceException {
        // First enrollment MWF 09:00 - 10:00
        when(courseServiceClient.getSectionSnapshot(SECTION_ID)).thenReturn(testSection);
        enrollmentFacade.enroll(STUDENT_ID, SECTION_ID);
        
        // Conflicting section MWF 09:30 - 10:30
        Long conflictingSectionId = 201L;
        CourseSectionSnapshot conflictingSection = new CourseSectionSnapshot();
        conflictingSection.setSectionId(conflictingSectionId);
        conflictingSection.setCourseCode("CS102");
        conflictingSection.setCapacity(30);
        conflictingSection.setEnrolledCount(10);
        conflictingSection.setScheduleDays("MWF");
        conflictingSection.setStartTime(Time.valueOf("09:30:00"));
        conflictingSection.setEndTime(Time.valueOf("10:30:00"));
        
        when(courseServiceClient.getSectionSnapshot(conflictingSectionId)).thenReturn(conflictingSection);
        when(courseServiceClient.getSectionSnapshots(List.of(SECTION_ID))).thenReturn(List.of(testSection));
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            enrollmentFacade.enroll(STUDENT_ID, conflictingSectionId);
        });
        
        assertTrue(exception.getMessage().contains("time conflicts"));
        verify(courseServiceClient, never()).reserveSeat(conflictingSectionId);
    }
}
