package com.lulu.reviews.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private String comentario;
    private Integer puntuacion;
    private String usuario; // o userId, según qué quieras mostrar
    private LocalDateTime fechaReview;
}

