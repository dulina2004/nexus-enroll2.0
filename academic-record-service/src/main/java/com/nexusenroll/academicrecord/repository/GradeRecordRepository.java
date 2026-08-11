package com.nexusenroll.academicrecord.repository;

import com.nexusenroll.academicrecord.model.GradeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Spring Data repository for {@link GradeRecord}, adding lookup by enrollment and by student. */
@Repository
public interface GradeRecordRepository extends JpaRepository<GradeRecord, Long> {

    List<GradeRecord> findByEnrollmentIdOrderByIdDesc(Long enrollmentId);

    List<GradeRecord> findByStudentIdOrderByIdDesc(Long studentId);
}
