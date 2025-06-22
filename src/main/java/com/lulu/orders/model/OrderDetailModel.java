package com.lulu.orders.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lulu.product.model.ProductModel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="orders_detalles")
public class OrderDetailModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private OrderModel order;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductModel producto;

    private Integer cantidad;
    private Double precioUnitario;
}
