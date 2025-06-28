package com.lulu.auth.service;

import com.lulu.auth.dto.RegisterRequest;
import com.lulu.auth.dto.RegisterResponse;
import com.lulu.auth.model.*;
import com.lulu.auth.repository.*;
import com.lulu.auth.security.JWTService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RolRepository rolRepository;
    @Mock private UserCredentialsRepository userCredentialsRepository;
    @Mock private User2FARepository user2FARepository;
    @Mock private UserTokensRepository userTokensRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JWTService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRegisterNewUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Lulu");
        request.setApellidos("García");
        request.setTelefono("999999999");
        request.setDni("12345678");
        request.setUsername("lulu123");
        request.setCorreo("lulu@mail.com");
        request.setPassword("secreto");

        when(userRepository.existsByUsername("lulu123")).thenReturn(false);
        when(userRepository.existsByCorreo("lulu@mail.com")).thenReturn(false);

        RolModel rol = new RolModel();
        rol.setTipoRol("usuario");
        when(rolRepository.findByTipoRol("usuario")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("secreto")).thenReturn("encodedPassword");

        UserModel savedUser = new UserModel();
        savedUser.setUsername("lulu123");
        savedUser.setRol(rol);
        savedUser.setCorreo("lulu@mail.com");

        when(userRepository.save(any(UserModel.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("mocked.jwt.token");

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());

        verify(userRepository).save(any(UserModel.class));
        verify(jwtService).generateToken(savedUser);
    }
}
