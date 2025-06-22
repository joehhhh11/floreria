package com.lulu.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String apellidos;
    private String telefono;
    private String dni;
    private String username;
    private String password;
    private String correo;
    private String rol;
}
