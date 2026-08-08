package com.nexusenroll.enrollment.repository;

import com.nexusenroll.enrollment.model.EnrollmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentHistoryRepository extends JpaRepository<EnrollmentHistory, Long> {
    List<EnrollmentHistory> findByEnrollmentId(Long enrollmentId);
}
