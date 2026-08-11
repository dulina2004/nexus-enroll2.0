package com.nexusenroll.course.repository;

import com.nexusenroll.course.model.CourseChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for {@link CourseChangeRequest}, adding lookups by status and
 * all requests ordered by creation date (most recent first).
 */
@Repository
public interface CourseChangeRequestRepository extends JpaRepository<CourseChangeRequest, Long> {

    List<CourseChangeRequest> findByStatusOrderByCreatedAtDesc(String status);

    List<CourseChangeRequest> findAllByOrderByCreatedAtDesc();
}
