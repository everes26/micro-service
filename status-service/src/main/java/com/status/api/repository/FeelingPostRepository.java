package com.status.api.repository;

import com.status.api.entity.FeelingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeelingPostRepository extends JpaRepository<FeelingPost, Long> {

    FeelingPost findFeelingPostByPostIdAndUserId(Long postId, Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
