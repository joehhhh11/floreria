package com.lulu.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@Table(name="imagen")
public class ImageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imagenUrl", nullable = false)
    private String imagenUrl;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    @JsonIgnore
    private ProductModel producto;
}
