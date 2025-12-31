package com.example.onlinelearning.service;

import com.example.onlinelearning.dto.AuthResponse;
import com.example.onlinelearning.dto.LoginRequest;
import com.example.onlinelearning.dto.RegisterRequest;
import com.example.onlinelearning.dto.UserResponse;
import com.example.onlinelearning.entity.Role;
import com.example.onlinelearning.entity.User;
import com.example.onlinelearning.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        Assert.notNull(request.getRole(), "Role is required");
        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("ADMIN cannot be self-registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.getBindingStatus());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole(), user.getBindingStatus());
    }
}
