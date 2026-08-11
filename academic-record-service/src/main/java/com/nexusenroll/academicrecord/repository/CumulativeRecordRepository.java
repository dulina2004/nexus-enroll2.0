package com.nexusenroll.academicrecord.repository;

import com.nexusenroll.academicrecord.model.CumulativeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Spring Data repository for {@link CumulativeRecord}, adding lookup by student. */
@Repository
public interface CumulativeRecordRepository extends JpaRepository<CumulativeRecord, Long> {

    Optional<CumulativeRecord> findByStudentId(Long studentId);
}
