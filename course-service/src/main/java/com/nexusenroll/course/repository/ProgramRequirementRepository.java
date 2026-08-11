package com.nexusenroll.course.repository;

import com.nexusenroll.course.model.ProgramRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for {@link ProgramRequirement}, adding lookups by program
 * and by a program-and-course combination.
 */
@Repository
public interface ProgramRequirementRepository extends JpaRepository<ProgramRequirement, Long> {

    List<ProgramRequirement> findByProgramId(Long programId);

    Optional<ProgramRequirement> findByProgramIdAndCourseId(Long programId, Long courseId);
}
