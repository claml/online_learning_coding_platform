package com.example.onlinelearning.controller;

import com.example.onlinelearning.dto.*;
import com.example.onlinelearning.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.ok(communityService.createPost(request, authentication.getName()));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> listPosts(@RequestParam(value = "sort", required = false) String sort,
                                                        Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(communityService.listPosts(sort, username));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(communityService.getPost(id, username));
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<PostResponse> likePost(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(communityService.likePost(id, authentication.getName()));
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<PostCommentResponse> addComment(@PathVariable Long id,
                                                          @Valid @RequestBody PostCommentRequest request,
                                                          Authentication authentication) {
        return ResponseEntity.ok(communityService.addComment(id, request, authentication.getName()));
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<List<PostCommentResponse>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getComments(id));
    }

    @DeleteMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostResponse> deletePost(@PathVariable Long id,
                                                   @Valid @RequestBody PostDeleteRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.ok(communityService.deletePost(id, request, authentication.getName()));
    }
}
