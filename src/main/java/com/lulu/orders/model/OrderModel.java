package com.lulu.orders.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lulu.auth.model.UserModel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders")
@Data
public class OrderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total")
    private Double total;

    @Column(name = "tipo_entrega")
    private String tipoEntrega ;

    @Column(name = "dirrecion_envio")
    private String direccionEnvio;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "cupon_id", nullable = true)
    private CuponModel cuponModel;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderDetailModel> productos = new ArrayList<>();


}
