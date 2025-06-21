package com.lulu.product.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Table(name = "productos")
@Data
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String name;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "precio", nullable = false)
    private Double price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "imagen_url")
    private String imageUrl;

    @Column(name = "categoria_id")
    private Integer categoriaId;

    @Column(name = "destacado")
    private Boolean destacado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
