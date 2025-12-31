package com.example.onlinelearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "binding_requests")
public class BindingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BindingStatus status = BindingStatus.PENDING;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @Column(length = 255)
    private String rejectReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
