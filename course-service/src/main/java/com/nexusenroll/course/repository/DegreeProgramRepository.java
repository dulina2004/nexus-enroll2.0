package com.nexusenroll.course.repository;

import com.nexusenroll.course.model.DegreeProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for {@link DegreeProgram}, adding lookup by program code.
 */
@Repository
public interface DegreeProgramRepository extends JpaRepository<DegreeProgram, Long> {

    Optional<DegreeProgram> findByCode(String code);
}
