package com.example.authService.repository;
import com.example.authService.entity.EntityUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<EntityUsers, UUID> {

    Optional<EntityUsers> findByEmail(String email);
    boolean existsByEmail(String email);
}
