package com.nexusenroll.student.repository;

import com.nexusenroll.student.model.AcademicStanding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicStandingRepository extends JpaRepository<AcademicStanding, Long> {
    Optional<AcademicStanding> findByStudentId(Long studentId);
}
