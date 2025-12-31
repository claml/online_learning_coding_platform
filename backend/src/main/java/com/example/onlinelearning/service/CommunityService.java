package com.example.onlinelearning.service;

import com.example.onlinelearning.dto.*;
import com.example.onlinelearning.entity.Post;
import com.example.onlinelearning.entity.PostComment;
import com.example.onlinelearning.entity.PostLike;
import com.example.onlinelearning.entity.Role;
import com.example.onlinelearning.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CommunityService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;

    public CommunityService(PostRepository postRepository,
                            PostLikeRepository postLikeRepository,
                            PostCommentRepository postCommentRepository,
                            UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.postCommentRepository = postCommentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PostResponse createPost(PostCreateRequest request, String username) {
        User author = findUserOrThrow(username);
        Post post = Post.builder()
                .author(author)
                .title(request.getTitle())
                .content(request.getContent())
                .deleted(false)
                .build();
        postRepository.save(post);
        return toResponse(post, author, false, Collections.emptyList());
    }

    public List<PostResponse> listPosts(String sort, String username) {
        PostSortOption option = PostSortOption.fromString(sort);
        List<Post> posts = switch (option) {
            case HOTTEST -> postRepository.findActiveOrderByLikes();
            case MOST_COMMENTED -> postRepository.findActiveOrderByComments();
            default -> postRepository.findByDeletedFalseOrderByCreatedAtDesc();
        };
        User currentUser = username == null ? null : findUserOrThrow(username);
        return posts.stream()
                .map(post -> toResponse(post, currentUser, isLikedByCurrentUser(post, currentUser), Collections.emptyList()))
                .toList();
    }

    public PostResponse getPost(Long id, String username) {
        Post post = findActivePostOrThrow(id);
        User currentUser = username == null ? null : findUserOrThrow(username);
        List<PostComment> comments = postCommentRepository.findByPostIdAndPostDeletedFalseOrderByCreatedAtAsc(id);
        return toResponse(post, currentUser, isLikedByCurrentUser(post, currentUser),
                comments.stream().map(this::toCommentResponse).toList());
    }

    @Transactional
    public PostResponse likePost(Long id, String username) {
        Post post = findActivePostOrThrow(id);
        User user = findUserOrThrow(username);
        Optional<PostLike> existing = postLikeRepository.findByPostIdAndUserId(id, user.getId());
        boolean liked;
        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get());
            liked = false;
        } else {
            PostLike like = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(like);
            liked = true;
        }
        post = findPostOrThrow(id);
        return toResponse(post, user, liked, Collections.emptyList());
    }

    @Transactional
    public PostCommentResponse addComment(Long postId, PostCommentRequest request, String username) {
        Post post = findActivePostOrThrow(postId);
        User user = findUserOrThrow(username);
        PostComment comment = PostComment.builder()
                .post(post)
                .author(user)
                .content(request.getContent())
                .build();
        postCommentRepository.save(comment);
        return toCommentResponse(comment);
    }

    public List<PostCommentResponse> getComments(Long postId) {
        Post post = findActivePostOrThrow(postId);
        return postCommentRepository.findByPostIdAndPostDeletedFalseOrderByCreatedAtAsc(post.getId())
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    @Transactional
    public PostResponse deletePost(Long postId, PostDeleteRequest request, String username) {
        Assert.hasText(request.getReason(), "Reason is required");
        User admin = findUserOrThrow(username);
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admins can delete posts");
        }
        Post post = findPostOrThrow(postId);
        post.setDeleted(true);
        post.setDeletedReason(request.getReason());
        return toResponse(post, admin, false, Collections.emptyList());
    }

    private PostResponse toResponse(Post post, User currentUser, boolean liked, List<PostCommentResponse> comments) {
        int likeCount = post.getLikes() != null ? post.getLikes().size() : postLikeRepository.countByPostId(post.getId());
        int commentCount = post.getComments() != null ? post.getComments().size() : postCommentRepository.countByPostId(post.getId());
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                likeCount,
                commentCount,
                liked,
                post.isDeleted(),
                post.getDeletedReason(),
                comments
        );
    }

    private PostCommentResponse toCommentResponse(PostComment comment) {
        return new PostCommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    private boolean isLikedByCurrentUser(Post post, User currentUser) {
        if (currentUser == null) {
            return false;
        }
        return postLikeRepository.findByPostIdAndUserId(post.getId(), currentUser.getId()).isPresent();
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Post findPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    private Post findActivePostOrThrow(Long id) {
        Post post = findPostOrThrow(id);
        if (post.isDeleted()) {
            throw new IllegalArgumentException("Post has been removed");
        }
        return post;
    }
}
