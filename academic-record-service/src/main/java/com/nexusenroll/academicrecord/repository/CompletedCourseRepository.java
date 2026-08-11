package com.nexusenroll.academicrecord.repository;

import com.nexusenroll.academicrecord.model.CompletedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Spring Data repository for {@link CompletedCourse}, adding lookup by student. */
@Repository
public interface CompletedCourseRepository extends JpaRepository<CompletedCourse, Long> {

    List<CompletedCourse> findByStudentIdOrderByYearDescSemesterDesc(Long studentId);
}
