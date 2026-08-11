package com.nexusenroll.enrollment.repository;

import com.nexusenroll.enrollment.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for {@link Enrollment} records.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndSectionId(Long studentId, Long sectionId);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByStudentIdAndStatus(Long studentId, String status);

    @Query("SELECT e.sectionId FROM Enrollment e WHERE e.studentId = :studentId AND e.status = :status")
    List<Long> findSectionIdsByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") String status);

    boolean existsByStudentIdAndSectionId(Long studentId, Long sectionId);

    boolean existsByStudentIdAndSectionIdAndStatus(Long studentId, Long sectionId, String status);
}
