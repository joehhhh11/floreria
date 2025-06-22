package com.lulu.auth.security;

import com.lulu.auth.model.UserModel;
import com.lulu.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    private final JWTService jwtService;
    private final HttpServletRequest request;
    private final UserRepository userRepository;

    public AuthenticatedUserProvider(JWTService jwtService,
                                     HttpServletRequest request,
                                     UserRepository userRepository) {
        this.jwtService = jwtService;
        this.request = request;
        this.userRepository = userRepository;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
    public Long getCurrentUserId() {
        String token = getTokenFromHeader();
        Long userId = jwtService.extractUserId(token);

        if (userId == null) {
            throw new RuntimeException("El ID del usuario es null. Revisa el token.");
        }

        return userId;
    }

    public UserModel getCurrentUser() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("El ID del usuario es null. Token inválido o mal formado.");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }


    private String getTokenFromHeader() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no presente en el encabezado");
        }
        return authHeader.substring(7); // Remueve "Bearer "
    }
}
