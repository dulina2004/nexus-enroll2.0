package com.nexusenroll.auth.service;

import com.nexusenroll.auth.model.Session;
import com.nexusenroll.auth.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Persists login sessions in their own transaction so a session-write
 * failure can never roll back the register/login transaction that triggered it.
 */
@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * REQUIRES_NEW isolates this write in its own transaction, and saveAndFlush
     * (rather than save) forces the INSERT to happen inside this try block
     * instead of being deferred to commit time - both are needed for the
     * catch below to actually contain the failure instead of just hiding it
     * while still leaving the caller's transaction marked rollback-only.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSession(Long userId, String tokenHash, Instant expiresAt) {
        try {
            if (sessionRepository.findByTokenHash(tokenHash).isPresent()) {
                return;
            }
            Session session = Session.builder()
                    .userId(userId)
                    .tokenHash(tokenHash)
                    .expiresAt(expiresAt)
                    .lastActivityAt(Instant.now())
                    .build();
            sessionRepository.saveAndFlush(session);
        } catch (Exception e) {
            log.warn("Failed to persist session record for user {}: {}", userId, e.getMessage());
        }
    }
}
