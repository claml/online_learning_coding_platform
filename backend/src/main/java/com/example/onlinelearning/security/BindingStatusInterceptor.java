package com.example.onlinelearning.security;

import com.example.onlinelearning.entity.BindingStatus;
import com.example.onlinelearning.entity.Role;
import com.example.onlinelearning.entity.User;
import com.example.onlinelearning.service.UserRepository;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class BindingStatusInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final Map<String, Set<String>> protectedPatterns = new HashMap<>() {{
        put("/courses/**", Set.of(HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name()));
        put("/posts/**", Set.of(HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name()));
        put("/comments/**", Set.of(HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name()));
        put("/likes/**", Set.of(HttpMethod.POST.name(), HttpMethod.DELETE.name()));
    }};

    public BindingStatusInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!matchesProtectedPath(request)) {
            return true;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return true;
        }
        User user = userRepository.findByUsername(authentication.getName())
                .orElse(null);
        if (user == null || user.getRole() == Role.ADMIN) {
            return true;
        }
        if (user.getBindingStatus() != BindingStatus.BOUND) {
            reject(response);
            return false;
        }
        return true;
    }

    private boolean matchesProtectedPath(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        return protectedPatterns.entrySet().stream()
                .anyMatch(entry -> pathMatcher.match(entry.getKey(), path) && entry.getValue().contains(method));
    }

    private void reject(HttpServletResponse response) throws IOException {
        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().print("Binding required");
        response.flushBuffer();
    }
}
