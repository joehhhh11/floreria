package com.lulu.payment;

import com.lulu.orders.model.OrderDetailModel;
import com.lulu.orders.model.OrderModel;
import com.lulu.orders.repository.OrderRepository;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ProductModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentController paymentController;

    private OrderModel sampleOrder;
    private OrderDetailModel sampleOrderDetail;
    private ProductModel sampleProduct;
    private CategoryModel sampleCategory;

    @BeforeEach
    void setUp() {
        // Configurar las URLs usando ReflectionTestUtils
        ReflectionTestUtils.setField(paymentController, "successUrl", "http://localhost:5173");
        ReflectionTestUtils.setField(paymentController, "cancelUrl", "http://localhost:5173");

        sampleCategory = new CategoryModel();
        sampleCategory.setId(1L);
        sampleCategory.setNombre("Rosas");

        sampleProduct = new ProductModel();
        sampleProduct.setId(1L);
        sampleProduct.setName("Rosa Roja");
        sampleProduct.setPrice(25.99);
        sampleProduct.setCategoria(sampleCategory);

        sampleOrderDetail = new OrderDetailModel();
        sampleOrderDetail.setId(1L);
        sampleOrderDetail.setProducto(sampleProduct);
        sampleOrderDetail.setCantidad(2);
        sampleOrderDetail.setPrecioUnitario(25.99);

        sampleOrder = new OrderModel();
        sampleOrder.setId(1L);
        sampleOrder.setTotal(51.98);
        sampleOrder.setEstado("PENDIENTE");
        sampleOrder.setProductos(Arrays.asList(sampleOrderDetail));
    }

    @Test
    void confirmPayment_ShouldUpdateOrderStatus_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(OrderModel.class))).thenReturn(sampleOrder);

        // Act
        String result = paymentController.confirmPayment(1L);

        // Assert
        assertEquals("Orden marcada como pagada correctamente.", result);
        assertEquals("PAGADA", sampleOrder.getEstado());
        
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void confirmPayment_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> paymentController.confirmPayment(1L)
        );

        assertEquals("Orden no encontrada", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void confirmPayment_ShouldChangeStatusFromPendingToPaid() {
        // Arrange
        sampleOrder.setEstado("PENDIENTE");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(OrderModel.class))).thenReturn(sampleOrder);

        // Act
        paymentController.confirmPayment(1L);

        // Assert
        assertEquals("PAGADA", sampleOrder.getEstado());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void confirmPayment_ShouldHandleDifferentOrderStates() {
        // Arrange
        sampleOrder.setEstado("CANCELADA");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(OrderModel.class))).thenReturn(sampleOrder);

        // Act
        paymentController.confirmPayment(1L);

        // Assert - Debería cambiar el estado sin importar el estado anterior
        assertEquals("PAGADA", sampleOrder.getEstado());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void createCheckoutSession_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            RuntimeException.class,
            () -> paymentController.createCheckoutSession(1L)
        );

        verify(orderRepository).findById(1L);
    }

    @Test
    void createCheckoutSession_ShouldFindOrder_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        // Act & Assert
        try {
            paymentController.createCheckoutSession(1L);
        } catch (Exception e) {
            // Es esperado que falle debido a Stripe, pero debe haber encontrado la orden
        }

        verify(orderRepository).findById(1L);
    }

    @Test
    void confirmPayment_ShouldWork_WithValidOrderId() {
        // Arrange
        Long validOrderId = 999L;
        OrderModel orderToUpdate = new OrderModel();
        orderToUpdate.setId(validOrderId);
        orderToUpdate.setEstado("PENDIENTE");

        when(orderRepository.findById(validOrderId)).thenReturn(Optional.of(orderToUpdate));
        when(orderRepository.save(orderToUpdate)).thenReturn(orderToUpdate);

        // Act
        String result = paymentController.confirmPayment(validOrderId);

        // Assert
        assertNotNull(result);
        assertEquals("Orden marcada como pagada correctamente.", result);
        assertEquals("PAGADA", orderToUpdate.getEstado());
    }
}
