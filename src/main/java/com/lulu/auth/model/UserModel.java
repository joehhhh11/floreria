package com.lulu.auth.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
@ToString(exclude = {"rol", "twoFAModel"})
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellidos;
    private String telefono;
    private String dni;
    private String username;
    private String password;
    private String correo;
    private String estado;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    @JsonManagedReference
    private RolModel rol;

    @OneToOne(mappedBy = "user")
    private TwoFAModel user2FA;

    @OneToOne(mappedBy = "user")
    private CredentialsModel userCredentials;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TokenModel> tokens;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}
