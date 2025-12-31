package com.example.onlinelearning.service;

import com.example.onlinelearning.entity.BindingRequest;
import com.example.onlinelearning.entity.BindingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BindingRequestRepository extends JpaRepository<BindingRequest, Long> {
    List<BindingRequest> findAllByStatusOrderByCreatedAtAsc(BindingStatus status);
}
