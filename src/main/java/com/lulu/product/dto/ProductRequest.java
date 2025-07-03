package com.lulu.product.dto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data

public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private List<MultipartFile> imagenes;
    private Long categoriaId;
    private Boolean destacado;
    private List<String> imageUrls = new ArrayList<>();
}
