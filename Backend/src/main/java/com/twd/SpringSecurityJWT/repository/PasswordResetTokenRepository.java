package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.PasswordResetToken;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(OurUsers user);
    void deleteByUser(OurUsers user);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :now")
    void deleteAllExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM PasswordResetToken t WHERE t.user = :user AND t.expiryDate > :now")
    boolean hasValidToken(@Param("user") OurUsers user, @Param("now") LocalDateTime now);
}