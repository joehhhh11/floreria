package com.lulu.orders.service;

import com.lulu.auth.model.UserModel;
import com.lulu.auth.repository.UserRepository;
import com.lulu.auth.security.AuthenticatedUserProvider;
import com.lulu.orders.dto.OrderRequest;
import com.lulu.orders.dto.OrderResponse;
import com.lulu.orders.mapper.OrderMapper;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.model.OrderModel;
import com.lulu.orders.repository.CuponRepository;
import com.lulu.orders.repository.OrderRepository;
import com.lulu.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CuponRepository cuponRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private UserRepository userRepository;
    @Override
    public OrderResponse createOrder(OrderRequest request) {
            logger.info("Iniciando creación de orden para usuario autenticado");
            
            UserModel currentUser = authenticatedUserProvider.getCurrentUser();
            if (currentUser == null || currentUser.getId() == null) {
                logger.error("Intento de crear orden sin usuario autenticado válido");
                throw new RuntimeException("Usuario no autenticado o sin id válido");
            }
            
            logger.info("Usuario autenticado: {} creando orden", currentUser.getUsername());

            CuponModel cupon = null;
            Long cuponId = request.getCuponId();

            if (cuponId != null) {
                cupon = cuponRepository.findById(cuponId)
                        .orElseThrow(() -> new IllegalArgumentException("❌ Cupón no encontrado con ID: " + cuponId));
            }
            OrderModel order = orderMapper.toEntity(request, currentUser, cupon);

            OrderModel savedOrder = orderRepository.save(order);
            return orderMapper.toResponse(savedOrder);


    }
    @Override
    public OrderResponse actualizarEstadoOrden(Long id, String nuevoEstado) {
        OrderModel order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con id: " + id));

        order.setEstado(nuevoEstado);
        OrderModel updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }
    @Override
    public List<OrderResponse> getMyOrders() {
        UserModel currentUser = authenticatedUserProvider.getCurrentUser();

        if (currentUser == null || currentUser.getId() == null) {
            throw new RuntimeException("Usuario no autenticado o sin ID válido");
        }

        return orderRepository.findByUserId(currentUser.getId()).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        OrderModel order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con id: " + id));

        UserModel currentUser = authenticatedUserProvider.getCurrentUser();
        CuponModel cupon = (request.getCuponId() != null)
                ? cuponRepository.findById(request.getCuponId()).orElse(null)
                : null;

        orderMapper.updateEntityFromRequest(order, request, currentUser, cupon);
        OrderModel updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Orden no encontrada con id: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Override
    public OrderResponse getOrder(Long id) {
        OrderModel order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrado con id: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }
}
