package com.lulu.auth.service;

import com.lulu.auth.dto.LoginRequest;
import com.lulu.auth.dto.LoginResponse;
import com.lulu.auth.dto.RegisterRequest;
import com.lulu.auth.dto.RegisterResponse;
import com.lulu.auth.model.*;
import com.lulu.auth.repository.*;
import com.lulu.auth.security.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public RegisterResponse register(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("El username ya esta en uso");
        }
        if (userRepository.existsByCorreo(request.getCorreo())){
            throw new RuntimeException("El username ya esta en uso");
        }

        UserModel user = new UserModel();

        user.setNombre(request.getNombre());
        user.setApellidos(request.getApellidos());
        user.setTelefono(request.getTelefono());
        user.setDni(request.getDni());
        user.setUsername(request.getUsername());
        user.setCorreo(request.getCorreo());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        RolModel rolDefect = rolRepository.findByTipoRol("usuario")
                .orElseThrow(()-> new RuntimeException("Rol no encontrado"));
        user.setRol(rolDefect);
        user.setEstado("Active");

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

        String jwt=jwtService.generateToken(savedUser);
        return new RegisterResponse(jwt);
    }

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest){
        UserModel user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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

        List<String> modulos = user.getRol().getModulos()
                .stream().map(ModuloModel::getNombre).toList();

        return new LoginResponse(jwt, user.getUsername(), user.getRol().getTipoRol(), modulos);
    }

}
