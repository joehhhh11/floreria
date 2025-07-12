package com.lulu.orders.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CuponRequestDTO {
    private Boolean active;
    private String codigo;
    private Integer descuentoPorcentaje;
    private LocalDateTime fechaExpiracion;
}
