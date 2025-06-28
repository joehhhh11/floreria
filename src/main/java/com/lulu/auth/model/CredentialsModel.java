package com.lulu.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="user_credentials")
public class CredentialsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserModel user;
    private String passwordHash;
    private String salt;
    private LocalDateTime lastPasswordChange;
    private Integer failedAttempts = 0;
    private Boolean locked = false;
}
