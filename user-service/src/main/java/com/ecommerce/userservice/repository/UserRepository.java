package com.ecommerce.userservice.repository;

import com.ecommerce.userservice.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
