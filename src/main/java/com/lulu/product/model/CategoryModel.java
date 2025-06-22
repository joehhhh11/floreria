package com.lulu.product.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="categoria")
public class CategoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre")
    private String nombre;

    @Column(name="descripcion")
    private String descripcion;
}
