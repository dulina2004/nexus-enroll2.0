package com.nexusenroll.course.repository;

import com.nexusenroll.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Optional<Course> findByCourseCode(String courseCode);

    @Modifying
    @Query("UPDATE Course c SET c.capacity = :capacity WHERE c.id = :id")
    int updateCapacity(@Param("id") Long id, @Param("capacity") Integer capacity);

    @Modifying
    @Query("UPDATE Course c SET c.status = 'ARCHIVED' WHERE c.id = :id")
    int softDelete(@Param("id") Long id);
}
