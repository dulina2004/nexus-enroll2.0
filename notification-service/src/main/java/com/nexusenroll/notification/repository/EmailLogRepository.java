package com.nexusenroll.notification.repository;

import com.nexusenroll.notification.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Spring Data repository providing lookup of {@link EmailLog} records by recipient user. */
@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

    List<EmailLog> findByRecipientUserIdOrderByIdDesc(Long recipientUserId);
}
