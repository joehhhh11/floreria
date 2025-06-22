package com.lulu.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="user_2fa")
public class TwoFAModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id" ,referencedColumnName = "id")
    private UserModel user;

    private String secretKey;
    private Boolean enabled = false;
    private LocalDateTime createdAt;

}
