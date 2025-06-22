package com.lulu.orders.service;

import com.lulu.orders.dto.ProducQuantity;
import com.lulu.orders.model.OrderDetailModel;
import com.lulu.orders.model.OrderModel;
import com.lulu.orders.repository.OrderDetailRepository;
import com.lulu.product.model.ProductModel;
import com.lulu.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public List<OrderDetailModel> crearDetalles(OrderModel order, List<ProducQuantity> productos) {
        List<OrderDetailModel> detalles = new ArrayList<>();

        for (ProducQuantity pq : productos) {
            ProductModel producto = productRepository.findById(pq.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            OrderDetailModel detalle = new OrderDetailModel();
            detalle.setOrder(order);
            detalle.setProducto(producto);
            detalle.setCantidad(pq.getCantidad());
            detalle.setPrecioUnitario(producto.getPrice());
            detalles.add(detalle);
        }

        return orderDetailRepository.saveAll(detalles);
    }
}
