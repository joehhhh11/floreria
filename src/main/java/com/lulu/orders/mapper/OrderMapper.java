package com.lulu.orders.mapper;

import com.lulu.auth.model.UserModel;
import com.lulu.orders.dto.DetailOrderResponse;
import com.lulu.orders.dto.OrderRequest;
import com.lulu.orders.dto.OrderResponse;
import com.lulu.orders.dto.UserResumen;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.model.OrderDetailModel;
import com.lulu.orders.model.OrderModel;
import com.lulu.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    @Autowired
    private ProductRepository productRepository;

    public OrderModel toEntity(OrderRequest request, UserModel user, CuponModel cupon) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Usuario o ID de usuario inválido");
        }
        if (cupon != null && cupon.getId() == null) {
            throw new IllegalArgumentException("Cupón con ID inválido");
        }
        System.out.println("📦LLEGOOO");
        OrderModel order = new OrderModel();
        updateEntityFromRequest(order, request, user, cupon);
        return order;
    }

    public void updateEntityFromRequest(OrderModel order, OrderRequest request, UserModel user, CuponModel cupon) {
        // Logs para debuguear productoId en cada detalle antes de procesar
        request.getProductos().forEach(p -> {
            System.out.println("ProductoId recibido: " + p.getProductoId());
        });

        order.setDireccionEnvio(request.getDireccionEnvio());
        order.setTipoEntrega(request.getTipoEntrega());
        order.setUser(user);
        order.setCuponModel(cupon);
        order.setFechaCreacion(LocalDateTime.now());

        List<OrderDetailModel> detalles = request.getProductos().stream()
                .map(pq -> {
                    var producto = productRepository.findById(pq.getProductoId())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
                    OrderDetailModel detalle = new OrderDetailModel();
                    detalle.setOrder(order);
                    detalle.setProducto(producto);
                    detalle.setCantidad(pq.getCantidad());
                    detalle.setPrecioUnitario(producto.getPrice());
                    return detalle;
                })
                .collect(Collectors.toList());

        order.setProductos(detalles);

        double subtotal = detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();

        double descuento = (cupon != null) ? cupon.getDescuentoPorcentaje() : 0.0;
        order.setTotal(subtotal - descuento);
    }


    public OrderResponse toResponse(OrderModel order) {
        double subtotal = order.getProductos().stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();

        double descuento = (order.getCuponModel() != null)
                ? order.getCuponModel().getDescuentoPorcentaje()
                : 0.0;

        OrderResponse response = new OrderResponse();
        response.setPedidoId(order.getId());
        response.setDireccionEnvio(order.getDireccionEnvio());
        response.setTipoEntrega(order.getTipoEntrega());
        response.setFechaCreacion(order.getFechaCreacion().toString());
        response.setSubtotal(subtotal);
        response.setDescuentoAplicado(descuento);
        response.setTotalFinal(subtotal - descuento);

        if (order.getCuponModel() != null) {
            response.setCuponAplicado(order.getCuponModel().getCodigo());
        }

        if (order.getUser() != null) {
            UserResumen user = new UserResumen();
            user.setId(order.getUser().getId());
            user.setNombre(order.getUser().getNombre());
            user.setCorreo(order.getUser().getCorreo());
            response.setUser(user);
        }

        List<DetailOrderResponse> detalles = order.getProductos().stream()
                .map(d -> {
                    DetailOrderResponse det = new DetailOrderResponse();
                    det.setProductoId(d.getProducto().getId());
                    det.setNombreProducto(d.getProducto().getName());
                    det.setCantidad(d.getCantidad());
                    det.setPrecioUnitario(d.getPrecioUnitario());
                    det.setSubtotal(d.getCantidad() * d.getPrecioUnitario());
                    return det;
                })
                .collect(Collectors.toList());

        response.setDetalles(detalles);
        return response;
    }
}
