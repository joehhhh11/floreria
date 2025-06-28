package com.lulu.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String dni;
    private String username;
    private String correo;
    private String rol;
    private List<String> modulos;
}
