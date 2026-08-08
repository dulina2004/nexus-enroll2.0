package com.nexusenroll.course.repository;

import com.nexusenroll.course.model.CourseChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseChangeRequestRepository extends JpaRepository<CourseChangeRequest, Long> {

    List<CourseChangeRequest> findByStatusOrderByCreatedAtDesc(String status);

    List<CourseChangeRequest> findAllByOrderByCreatedAtDesc();
}
