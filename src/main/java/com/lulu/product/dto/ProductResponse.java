package com.lulu.product.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Integer categoriaId;
    private Boolean destacado;
    private LocalDateTime fechaCreacion;
}
