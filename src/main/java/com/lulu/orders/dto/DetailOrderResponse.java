package com.lulu.orders.dto;

import lombok.Data;

@Data
public class DetailOrderResponse {
    private Long productoId;
    private String nombreProducto;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
