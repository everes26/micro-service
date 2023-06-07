package com.status.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "feeling_post", uniqueConstraints = @UniqueConstraint(name = "unique_postId_userId", columnNames = {"postId", "userId"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FeelingPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long postId;
    private Long userId;

    public FeelingPost(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }
}
