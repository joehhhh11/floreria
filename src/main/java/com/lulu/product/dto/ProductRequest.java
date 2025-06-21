package com.lulu.product.dto;
import lombok.Data;
@Data

public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Integer categoriaId;
    private Boolean destacado;
}
