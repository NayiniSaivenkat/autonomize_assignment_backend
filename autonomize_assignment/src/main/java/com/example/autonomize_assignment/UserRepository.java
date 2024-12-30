package com.example.autonomize_assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findBySoftDeletedFalse();
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.location LIKE %:keyword%")
    List<User> searchByKeyword(@Param("keyword") String keyword);
}