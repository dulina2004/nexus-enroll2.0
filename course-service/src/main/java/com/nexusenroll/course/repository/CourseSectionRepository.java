package com.nexusenroll.course.repository;

import com.nexusenroll.course.model.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {

    List<CourseSection> findByCourseId(Long courseId);

    @Query("SELECT COALESCE(SUM(cs.enrolledCount), 0) FROM CourseSection cs WHERE cs.courseId = :courseId AND cs.status != 'CANCELLED'")
    int sumActiveEnrolledCountByCourseId(@Param("courseId") Long courseId);
}
