package com.status.api.repository;

import com.status.api.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatusReposetory extends JpaRepository<UserStatus, Long> {
    boolean existsByUserId(Long userId);
}
