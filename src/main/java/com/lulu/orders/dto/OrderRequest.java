package com.lulu.orders.dto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data

public class OrderRequest {
    private String direccionEnvio;

    private String tipoEntrega;

    private Long cuponId;
    private List<ProducQuantity> productos;
}
