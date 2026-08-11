package com.nexusenroll.academicrecord.repository;

import com.nexusenroll.academicrecord.model.DegreeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Spring Data repository for {@link DegreeProgress}, adding lookup by student. */
@Repository
public interface DegreeProgressRepository extends JpaRepository<DegreeProgress, Long> {

    Optional<DegreeProgress> findByStudentId(Long studentId);
}
