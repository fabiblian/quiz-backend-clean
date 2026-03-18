package com.wiss.quizbackend.repository;

import com.wiss.quizbackend.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByEmailAndPassword(
            String email, String password);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}