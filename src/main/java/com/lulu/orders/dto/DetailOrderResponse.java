package com.lulu.orders.dto;

import com.lulu.product.dto.ProductResponse;
import lombok.Data;

@Data
public class DetailOrderResponse {
    private Long productoId;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private ProductResponse producto;
}
