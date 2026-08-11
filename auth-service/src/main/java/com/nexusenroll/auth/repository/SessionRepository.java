package com.nexusenroll.auth.repository;

import com.nexusenroll.auth.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for {@link Session}, adding lookup by token hash.
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByTokenHash(String tokenHash);
}
