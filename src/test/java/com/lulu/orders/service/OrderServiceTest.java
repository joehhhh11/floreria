package com.lulu.orders.service;

import com.lulu.auth.model.UserModel;
import com.lulu.auth.security.AuthenticatedUserProvider;
import com.lulu.orders.dto.OrderRequest;
import com.lulu.orders.dto.OrderResponse;
import com.lulu.orders.dto.ProducQuantity;
import com.lulu.orders.mapper.OrderMapper;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.model.OrderModel;
import com.lulu.orders.repository.CuponRepository;
import com.lulu.orders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CuponRepository cuponRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderModel sampleOrder;
    private OrderRequest sampleRequest;
    private OrderResponse sampleResponse;
    private UserModel sampleUser;
    private CuponModel sampleCupon;

    @BeforeEach
    void setUp() {
        sampleUser = new UserModel();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setCorreo("test@example.com");

        sampleCupon = new CuponModel();
        sampleCupon.setId(1L);
        sampleCupon.setCodigo("DESCUENTO10");
        sampleCupon.setDescuentoPorcentaje(10);

        ProducQuantity producto = new ProducQuantity();
        producto.setProductoId(1L);
        producto.setCantidad(2);

        sampleRequest = new OrderRequest();
        sampleRequest.setDireccionEnvio("123 Calle Principal");
        sampleRequest.setTipoEntrega("DELIVERY");
        sampleRequest.setCuponId(1L);
        sampleRequest.setProductos(Arrays.asList(producto));

        sampleOrder = new OrderModel();
        sampleOrder.setId(1L);
        sampleOrder.setDireccionEnvio("123 Calle Principal");
        sampleOrder.setTipoEntrega("DELIVERY");
        sampleOrder.setTotal(100.0);
        sampleOrder.setEstado("PENDIENTE");
        sampleOrder.setFechaCreacion(LocalDateTime.now());
        sampleOrder.setUser(sampleUser);
        sampleOrder.setCuponModel(sampleCupon);

        sampleResponse = new OrderResponse();
        sampleResponse.setPedidoId(1L);
        sampleResponse.setDireccionEnvio("123 Calle Principal");
        sampleResponse.setTipoEntrega("DELIVERY");
        sampleResponse.setSubtotal(100.0);
        sampleResponse.setDescuentoAplicado(10.0);
        sampleResponse.setTotalFinal(90.0);
    }

    @Test
    void createOrder_ShouldReturnOrderResponse_WhenValidRequest() {
        // Arrange
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(sampleUser);
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(sampleCupon));
        when(orderMapper.toEntity(sampleRequest, sampleUser, sampleCupon)).thenReturn(sampleOrder);
        when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);
        when(orderMapper.toResponse(sampleOrder)).thenReturn(sampleResponse);

        // Act
        OrderResponse result = orderService.createOrder(sampleRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getPedidoId());
        assertEquals("123 Calle Principal", result.getDireccionEnvio());
        assertEquals("DELIVERY", result.getTipoEntrega());
        assertEquals(90.0, result.getTotalFinal());

        verify(authenticatedUserProvider).getCurrentUser();
        verify(cuponRepository).findById(1L);
        verify(orderMapper).toEntity(sampleRequest, sampleUser, sampleCupon);
        verify(orderRepository).save(sampleOrder);
        verify(orderMapper).toResponse(sampleOrder);
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserNotAuthenticated() {
        // Arrange
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> orderService.createOrder(sampleRequest)
        );

        assertEquals("Usuario no autenticado o sin id válido", exception.getMessage());
        verify(authenticatedUserProvider).getCurrentUser();
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserHasNoId() {
        // Arrange
        sampleUser.setId(null);
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(sampleUser);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> orderService.createOrder(sampleRequest)
        );

        assertEquals("Usuario no autenticado o sin id válido", exception.getMessage());
        verify(authenticatedUserProvider).getCurrentUser();
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowException_WhenCuponNotFound() {
        // Arrange
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(sampleUser);
        when(cuponRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(sampleRequest)
        );

        assertEquals("❌ Cupón no encontrado con ID: 1", exception.getMessage());
        verify(authenticatedUserProvider).getCurrentUser();
        verify(cuponRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldWork_WhenNoCuponProvided() {
        // Arrange
        sampleRequest.setCuponId(null);
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(sampleUser);
        when(orderMapper.toEntity(sampleRequest, sampleUser, null)).thenReturn(sampleOrder);
        when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);
        when(orderMapper.toResponse(sampleOrder)).thenReturn(sampleResponse);

        // Act
        OrderResponse result = orderService.createOrder(sampleRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getPedidoId());

        verify(authenticatedUserProvider).getCurrentUser();
        verify(cuponRepository, never()).findById(anyLong());
        verify(orderMapper).toEntity(sampleRequest, sampleUser, null);
        verify(orderRepository).save(sampleOrder);
        verify(orderMapper).toResponse(sampleOrder);
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(sampleUser);
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(sampleCupon));
        when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);
        when(orderMapper.toResponse(sampleOrder)).thenReturn(sampleResponse);

        // Act
        OrderResponse result = orderService.updateOrder(1L, sampleRequest);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(authenticatedUserProvider).getCurrentUser();
        verify(cuponRepository).findById(1L);
        verify(orderMapper).updateEntityFromRequest(sampleOrder, sampleRequest, sampleUser, sampleCupon);
        verify(orderRepository).save(sampleOrder);
        verify(orderMapper).toResponse(sampleOrder);
    }

    @Test
    void updateOrder_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.updateOrder(1L, sampleRequest)
        );

        assertEquals("Orden no encontrada con id: 1", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteOrder_ShouldDeleteOrder_WhenOrderExists() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> orderService.deleteOrder(1L));

        // Assert
        verify(orderRepository).existsById(1L);
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteOrder_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.deleteOrder(1L)
        );

        assertEquals("Orden no encontrada con id: 1", exception.getMessage());
        verify(orderRepository).existsById(1L);
        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    void getOrder_ShouldReturnOrder_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderMapper.toResponse(sampleOrder)).thenReturn(sampleResponse);

        // Act
        OrderResponse result = orderService.getOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getPedidoId());
        verify(orderRepository).findById(1L);
        verify(orderMapper).toResponse(sampleOrder);
    }

    @Test
    void getOrder_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrder(1L)
        );

        assertEquals("Orden no encontrado con id: 1", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderMapper, never()).toResponse(any());
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        // Arrange
        List<OrderModel> orders = Arrays.asList(sampleOrder);
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toResponse(sampleOrder)).thenReturn(sampleResponse);

        // Act
        List<OrderResponse> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPedidoId());
        verify(orderRepository).findAll();
        verify(orderMapper).toResponse(sampleOrder);
    }
}
