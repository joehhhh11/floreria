package com.lulu.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "user_tokens")
public class TokenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserModel user;

    private String refreshToken;

    private String ipAddress;

    private String userAgent;

    private Boolean revoked = false;

    private Boolean expired = false;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

}
