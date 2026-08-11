package com.nexusenroll.student.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * JPA entity representing a document uploaded by or on behalf of a student.
 */
@Entity
@Table(name = "student_documents", schema = "nexus_student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "document_type", length = 100)
    private String documentType;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "upload_date")
    private Instant uploadDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
