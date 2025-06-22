package com.lulu.orders.service;

import com.lulu.auth.model.UserModel;
import com.lulu.orders.dto.OrderRequest;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.model.OrderDetailModel;
import com.lulu.orders.model.OrderModel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderFactory {

    public OrderModel createBaseOrder(UserModel usuario, OrderRequest request, CuponModel cupon) {
        OrderModel order = new OrderModel();
        order.setUser(usuario);
        order.setDireccionEnvio(
                request.getDireccionEnvio()
        );
        order.setTipoEntrega(request.getTipoEntrega());
        order.setFechaCreacion(LocalDateTime.now());
        order.setCuponModel(cupon);
        return order;
    }
    public double calcularTotalConDescuento(List<OrderDetailModel> detalles, CuponModel cupon) {
        double total = detalles.stream()
                .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
                .sum();

        if (cupon != null) {
            total -= total * (cupon.getDescuentoPorcentaje() / 100.0);
        }
        return total;
    }
}

