package com.bookstore.user.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    @Query("SELECT ct FROM ConfirmationToken ct WHERE ct.token=:token")
    Optional<ConfirmationToken> findByToken(String token);
}
