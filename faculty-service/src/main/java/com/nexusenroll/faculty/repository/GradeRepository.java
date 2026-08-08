package com.nexusenroll.faculty.repository;

import com.nexusenroll.faculty.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByEnrollmentId(Long enrollmentId);
    List<Grade> findBySectionId(Long sectionId);
}
