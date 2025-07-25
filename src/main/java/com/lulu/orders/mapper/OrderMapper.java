package com.lulu.orders.mapper;

import com.lulu.auth.model.UserModel;
import com.lulu.orders.dto.DetailOrderResponse;
import com.lulu.orders.dto.OrderRequest;
import com.lulu.orders.dto.OrderResponse;
import com.lulu.orders.dto.UserResumen;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.model.OrderDetailModel;
import com.lulu.orders.model.OrderModel;
import com.lulu.product.dto.CategoryResponse;
import com.lulu.product.dto.ProductResponse;
import com.lulu.product.model.ProductModel;
import com.lulu.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private static final Logger logger = LoggerFactory.getLogger(OrderMapper.class);

    @Autowired
    private ProductRepository productRepository;

    public OrderModel toEntity(OrderRequest request, UserModel user, CuponModel cupon) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Usuario o ID de usuario inválido");
        }
        if (cupon != null && cupon.getId() == null) {
            throw new IllegalArgumentException("Cupón con ID inválido");
        }
        logger.info("Creando nueva orden para usuario: {} con {} productos",
                user.getUsername(), request.getProductos().size());
        OrderModel order = new OrderModel();
        updateEntityFromRequest(order, request, user, cupon);
        return order;
    }

    public void updateEntityFromRequest(OrderModel order, OrderRequest request, UserModel user, CuponModel cupon) {

        if (request.getProductos() != null) {
            logger.debug("Actualizando orden con productos: {}",
                    request.getProductos().stream()
                            .map(p -> "ID:" + p.getProductoId())
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("ninguno"));

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
                    }).collect(Collectors.toList());

            order.setProductos(detalles);

            double subtotal = detalles.stream()
                    .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                    .sum();

            double descuento = 0.0;
            if (cupon != null && cupon.getDescuentoPorcentaje() != null) {
                descuento = cupon.getDescuentoPorcentaje(); // Monto fijo
            }
            order.setTotal(subtotal - descuento);
        }

        order.setDireccionEnvio(request.getDireccionEnvio());
        order.setTipoEntrega(request.getTipoEntrega());
        order.setEstado(request.getEstado());
        order.setUser(user);
        order.setCuponModel(cupon);
        order.setFechaCreacion(LocalDateTime.now());
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
        response.setDescuentoAplicado(descuento); // Monto del descuento aplicado
        response.setEstado(order.getEstado());
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
                    ProductModel producto = d.getProducto();
                    det.setProductoId(producto.getId());

                    ProductResponse productoResponse = new ProductResponse();
                    productoResponse.setId(producto.getId());
                    productoResponse.setName(producto.getName());
                    productoResponse.setDescription(producto.getDescription());
                    productoResponse.setPrice(producto.getPrice());
                    productoResponse.setStock(producto.getStock());

                    if (producto.getCategoria() != null) {
                        CategoryResponse categoria = new CategoryResponse();
                        categoria.setId(producto.getCategoria().getId());
                        categoria.setNombre(producto.getCategoria().getNombre());
                        categoria.setDescripcion(producto.getCategoria().getDescripcion());
                        productoResponse.setCategoria(categoria);
                    }

                    det.setProducto(productoResponse);
                    det.setCantidad(d.getCantidad());
                    det.setPrecioUnitario(d.getPrecioUnitario());
                    det.setSubtotal(d.getCantidad() * d.getPrecioUnitario());

                    return det;
                }).collect(Collectors.toList());

        response.setDetalles(detalles);

        return response;
    }
}
