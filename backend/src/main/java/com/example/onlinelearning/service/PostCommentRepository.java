package com.example.onlinelearning.service;

import com.example.onlinelearning.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPostIdAndPostDeletedFalseOrderByCreatedAtAsc(Long postId);

    int countByPostId(Long postId);
}
