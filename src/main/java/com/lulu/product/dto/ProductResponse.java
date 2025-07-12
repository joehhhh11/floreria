package com.lulu.product.dto;

import com.lulu.reviews.dto.ReviewResponse;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private List<String> imageUrls;
    private Long categoriaId;
    private Boolean destacado;
    private CategoryResponse categoria;
    private LocalDateTime fechaCreacion;
    private List<ReviewResponse> reviews;

}
