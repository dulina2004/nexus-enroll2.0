package com.nexusenroll.faculty.repository;

import com.nexusenroll.faculty.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for {@link Faculty} lookups by primary key, user ID and faculty ID.
 */
@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByUserId(Long userId);
    Optional<Faculty> findByFacultyId(String facultyId);
}
