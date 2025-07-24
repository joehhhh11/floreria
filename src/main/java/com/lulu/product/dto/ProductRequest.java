package com.lulu.product.dto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    
    @Schema(type = "array", format = "binary", description = "Imágenes del producto")
    private List<MultipartFile> imagenes = new ArrayList<>();
    
    @JsonProperty("categoryId")  
    private Long categoriaId;
    
    private Boolean destacado = false; // Valor por defecto
    private List<String> imageUrls = new ArrayList<>();
    
    public List<MultipartFile> getImagenes() {
        return imagenes != null ? imagenes : new ArrayList<>();
    }
    
    public List<String> getImageUrls() {
        return imageUrls != null ? imageUrls : new ArrayList<>();
    }
}
