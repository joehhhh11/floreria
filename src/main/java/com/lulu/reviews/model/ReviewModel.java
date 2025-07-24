package com.lulu.reviews.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lulu.auth.model.UserModel;
import com.lulu.product.model.ProductModel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class ReviewModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int puntuacion; // Ej: de 1 a 5 estrellas

    @Column(length = 1000)
    private String comentario;

    private LocalDateTime fechaReview = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "producto_id")
    private ProductModel producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UserModel user;
}
