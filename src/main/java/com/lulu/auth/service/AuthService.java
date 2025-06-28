package com.lulu.auth.service;

import com.lulu.auth.dto.*;
import com.lulu.auth.dto.LoginRequest;
import com.lulu.auth.dto.LoginResponse;
import com.lulu.auth.dto.RegisterRequest;
import com.lulu.auth.dto.RegisterResponse;
import com.lulu.auth.model.*;
import com.lulu.auth.repository.*;
import com.lulu.auth.security.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.base.Preconditions; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private User2FARepository user2FARepository;

    @Autowired
    private UserTokensRepository userTokensRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest request) {
        if (StringUtils.isBlank(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario no puede estar vacío");
        }
        if (StringUtils.isBlank(request.getCorreo()) || !StringUtils.contains(request.getCorreo(), "@")) {
            throw new RuntimeException("Correo no válido");
        }

        Preconditions.checkArgument(!userRepository.existsByUsername(request.getUsername()), "El username ya esta en uso");
        Preconditions.checkArgument(!userRepository.existsByCorreo(request.getCorreo()), "El correo ya esta en uso");

        UserModel user = new UserModel();
        user.setNombre(request.getNombre());
        user.setApellidos(request.getApellidos());
        user.setTelefono(request.getTelefono());
        user.setDni(request.getDni());
        user.setUsername(request.getUsername());
        user.setCorreo(request.getCorreo());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        String tipoRol = StringUtils.isBlank(request.getRol()) ? "usuario" : request.getRol();

        RolModel rolSeleccionado = rolRepository.findByTipoRol(tipoRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + tipoRol));

        user.setRol(rolSeleccionado);

        UserModel savedUser = userRepository.save(user);

        CredentialsModel credentials = new CredentialsModel();
        credentials.setUser(savedUser);
        credentials.setPasswordHash(encodedPassword);
        credentials.setSalt(null);
        userCredentialsRepository.save(credentials);

        TwoFAModel twoFA = new TwoFAModel();
        twoFA.setUser(savedUser);
        twoFA.setSecretKey("DEFAULT");
        twoFA.setEnabled(false);
        user2FARepository.save(twoFA);

        String jwt = jwtService.generateToken(savedUser);
        return new RegisterResponse(jwt);
    }
    public RegisterResponse registerWithClerk(ClerkRequest request) {
        if (StringUtils.isBlank(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario no puede estar vacío");
        }
        if (StringUtils.isBlank(request.getCorreo()) || !StringUtils.contains(request.getCorreo(), "@")) {
            throw new RuntimeException("Correo no válido");
        }

        Preconditions.checkArgument(!userRepository.existsByCorreo(request.getCorreo()), "El correo ya está en uso");

        // Set rol por defecto
        RolModel rol = rolRepository.findByTipoRol("usuario")
                .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado"));

        // Crear el usuario sin contraseña
        UserModel user = new UserModel();
        user.setNombre(request.getNombre());
        user.setApellidos(request.getApellidos());
        user.setTelefono(request.getTelefono());
        user.setDni(request.getDni());
        user.setUsername(request.getUsername());
        user.setCorreo(request.getCorreo());
        user.setPassword(null); // No hay contraseña porque Clerk maneja auth
        user.setRol(rol);
        user.setEstado("Active");

        UserModel savedUser = userRepository.save(user);

        // Guardar 2FA por defecto (deshabilitado)
        TwoFAModel twoFA = new TwoFAModel();
        twoFA.setUser(savedUser);
        twoFA.setSecretKey("DEFAULT");
        twoFA.setEnabled(false);
        user2FARepository.save(twoFA);

        // Generar token
        String jwt = jwtService.generateToken(savedUser);
        return new RegisterResponse(jwt);
    }

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Optional<UserModel> userOptional = Optional.ofNullable(userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));

        UserModel user = userOptional.get();

        if (user.getUser2FA() != null && user.getUser2FA().getEnabled()) {
            throw new RuntimeException("2FA requerido");
        }

        String jwt = jwtService.generateToken(user);

        TokenModel tokenRecord = new TokenModel();
        tokenRecord.setUser(user);
        tokenRecord.setRefreshToken(jwt);
        tokenRecord.setRevoked(false);
        tokenRecord.setExpired(false);
        tokenRecord.setIpAddress(httpRequest.getRemoteAddr());
        tokenRecord.setUserAgent(httpRequest.getHeader("User-Agent"));
        userTokensRepository.save(tokenRecord);

        List<String> modulos = Lists.newArrayList();
        if (user.getRol() != null && user.getRol().getModulos() != null) {
            modulos = Lists.transform(user.getRol().getModulos(), modulo -> modulo.getNombre());
        }

        return new LoginResponse(jwt, user.getUsername(), user.getRol().getTipoRol(), modulos);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            UserResponse dto = new UserResponse();
            dto.setId(user.getId());
            dto.setNombre(user.getNombre());
            dto.setApellidos(user.getApellidos());
            dto.setTelefono(user.getTelefono());
            dto.setDni(user.getDni());
            dto.setUsername(user.getUsername());
            dto.setCorreo(user.getCorreo());
            dto.setRol(user.getRol().getTipoRol());
            dto.setModulos(
                    user.getRol().getModulos().stream()
                            .map(ModuloModel::getNombre)
                            .toList()
            );
            return dto;
        }).toList();
    }

}
