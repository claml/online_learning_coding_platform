package com.example.onlinelearning.service;

import com.example.onlinelearning.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByDeletedFalseOrderByCreatedAtDesc();

    List<Post> findByAuthorUsernameAndDeletedFalseOrderByCreatedAtDesc(String username);

    @Query("SELECT p FROM Post p WHERE p.deleted = false ORDER BY size(p.likes) DESC, p.createdAt DESC")
    List<Post> findActiveOrderByLikes();

    @Query("SELECT p FROM Post p WHERE p.deleted = false ORDER BY size(p.comments) DESC, p.createdAt DESC")
    List<Post> findActiveOrderByComments();
}
