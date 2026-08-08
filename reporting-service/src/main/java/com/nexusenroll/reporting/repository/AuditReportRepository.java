package com.nexusenroll.reporting.repository;

import com.nexusenroll.reporting.model.AuditReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditReportRepository extends JpaRepository<AuditReport, Long> {

    List<AuditReport> findByReportTypeOrderByGeneratedAtDesc(String reportType);

    List<AuditReport> findBySemesterAndYearOrderByGeneratedAtDesc(String semester, Integer year);
}
