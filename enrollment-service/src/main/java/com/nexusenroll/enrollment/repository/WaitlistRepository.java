package com.nexusenroll.enrollment.repository;

import com.nexusenroll.enrollment.model.WaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for {@link WaitlistEntry} records.
 */
@Repository
public interface WaitlistRepository extends JpaRepository<WaitlistEntry, Long> {

    Optional<WaitlistEntry> findByStudentIdAndSectionId(Long studentId, Long sectionId);

    List<WaitlistEntry> findByStudentId(Long studentId);

    List<WaitlistEntry> findBySectionIdAndStatusOrderByPositionAsc(Long sectionId, String status);

    Optional<WaitlistEntry> findFirstBySectionIdAndStatusOrderByPositionAsc(Long sectionId, String status);

    @Query("SELECT COALESCE(MAX(w.position), 0) + 1 FROM WaitlistEntry w WHERE w.sectionId = :sectionId")
    Integer findNextWaitlistPosition(@Param("sectionId") Long sectionId);

    boolean existsByStudentIdAndSectionIdAndStatus(Long studentId, Long sectionId, String status);
}
