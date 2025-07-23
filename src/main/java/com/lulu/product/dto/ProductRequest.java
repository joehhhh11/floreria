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
    
    // Hacer los campos opcionales para evitar errores de conversión
    @Schema(type = "array", format = "binary", description = "Imágenes del producto")
    private List<MultipartFile> imagenes = new ArrayList<>();
    
    @JsonProperty("categoryId")  
    private Long categoriaId;
    
    private Boolean destacado = false; // Valor por defecto
    private List<String> imageUrls = new ArrayList<>();
    
    // Getter personalizado para imagenes que maneja null/empty
    public List<MultipartFile> getImagenes() {
        return imagenes != null ? imagenes : new ArrayList<>();
    }
    
    // Getter personalizado para imageUrls que maneja null/empty
    public List<String> getImageUrls() {
        return imageUrls != null ? imageUrls : new ArrayList<>();
    }
}
