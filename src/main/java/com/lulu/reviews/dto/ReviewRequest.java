package com.lulu.reviews.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long productoId;
    private String comentario;
    private Integer puntuacion;
}
