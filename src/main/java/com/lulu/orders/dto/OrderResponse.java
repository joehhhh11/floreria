package com.lulu.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long pedidoId;
    private String fechaCreacion;
    private String direccionEnvio;
    private String tipoEntrega;
    private Double subtotal;
    private Double descuentoAplicado;
    private Double totalFinal;
    private String cuponAplicado;
    private UserResumen user;
    private List<DetailOrderResponse> detalles;
}
