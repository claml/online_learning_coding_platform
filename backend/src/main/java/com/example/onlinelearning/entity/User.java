package com.example.onlinelearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BindingStatus bindingStatus;

    @Column(length = 100)
    private String bindingIdentifier;

    @Column(length = 100)
    private String studentId;

    @Column(length = 100)
    private String teacherId;

    private LocalDateTime bindingUpdatedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (bindingStatus == null) {
            bindingStatus = role == Role.ADMIN ? BindingStatus.BOUND : BindingStatus.UNBOUND;
        }
    }
}
