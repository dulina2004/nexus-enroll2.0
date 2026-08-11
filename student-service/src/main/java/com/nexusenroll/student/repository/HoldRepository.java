package com.nexusenroll.student.repository;

import com.nexusenroll.student.model.Hold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for {@link Hold} records.
 */
@Repository
public interface HoldRepository extends JpaRepository<Hold, Long> {
    List<Hold> findByStudentId(Long studentId);
}
