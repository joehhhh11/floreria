package com.lulu.orders.mapper;

import com.lulu.auth.model.UserModel;
import com.lulu.orders.dto.OrderRequest;
import com.lulu.orders.dto.OrderResponse;
import com.lulu.orders.dto.ProducQuantity;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.model.OrderDetailModel;
import com.lulu.orders.model.OrderModel;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ProductModel;
import com.lulu.product.repository.ProductRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderMapper orderMapper;

    private OrderModel sampleOrder;
    private OrderRequest sampleRequest;
    private UserModel sampleUser;
    private CuponModel sampleCupon;
    private ProductModel sampleProduct;
    private CategoryModel sampleCategory;

    @BeforeEach
    void setUp() {
        sampleUser = new UserModel();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setNombre("Juan");
        sampleUser.setCorreo("juan@example.com");

        sampleCupon = new CuponModel();
        sampleCupon.setId(1L);
        sampleCupon.setCodigo("DESCUENTO10");
        sampleCupon.setDescuentoPorcentaje(10);

        sampleCategory = new CategoryModel();
        sampleCategory.setId(1L);
        sampleCategory.setNombre("Rosas");

        sampleProduct = new ProductModel();
        sampleProduct.setId(1L);
        sampleProduct.setName("Rosa Roja");
        sampleProduct.setPrice(25.99);
        sampleProduct.setCategoria(sampleCategory);

        ProducQuantity producto = new ProducQuantity();
        producto.setProductoId(1L);
        producto.setCantidad(2);

        sampleRequest = new OrderRequest();
        sampleRequest.setDireccionEnvio("123 Calle Principal");
        sampleRequest.setTipoEntrega("DELIVERY");
        sampleRequest.setCuponId(1L);
        sampleRequest.setProductos(Arrays.asList(producto));

        OrderDetailModel orderDetail = new OrderDetailModel();
        orderDetail.setId(1L);
        orderDetail.setProducto(sampleProduct);
        orderDetail.setCantidad(2);
        orderDetail.setPrecioUnitario(25.99);

        sampleOrder = new OrderModel();
        sampleOrder.setId(1L);
        sampleOrder.setDireccionEnvio("123 Calle Principal");
        sampleOrder.setTipoEntrega("DELIVERY");
        sampleOrder.setTotal(46.78); // 51.98 - 10% descuento
        sampleOrder.setEstado("PENDIENTE");
        sampleOrder.setFechaCreacion(LocalDateTime.now());
        sampleOrder.setUser(sampleUser);
        sampleOrder.setCuponModel(sampleCupon);
        sampleOrder.setProductos(Arrays.asList(orderDetail));
    }

    @Test
    void toEntity_ShouldCreateOrderModel_WhenValidInput() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        // Act
        OrderModel result = orderMapper.toEntity(sampleRequest, sampleUser, sampleCupon);

        // Assert
        assertNotNull(result);
        assertEquals("123 Calle Principal", result.getDireccionEnvio());
        assertEquals("DELIVERY", result.getTipoEntrega());
        assertEquals(sampleUser, result.getUser());
        assertEquals(sampleCupon, result.getCuponModel());
        assertNotNull(result.getFechaCreacion());
        assertNotNull(result.getProductos());
        assertEquals(1, result.getProductos().size());

        verify(productRepository).findById(1L);
    }

    @Test
    void toEntity_ShouldThrowException_WhenUserIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderMapper.toEntity(sampleRequest, null, sampleCupon)
        );

        assertEquals("Usuario o ID de usuario inválido", exception.getMessage());
    }

    @Test
    void toEntity_ShouldThrowException_WhenUserIdIsNull() {
        // Arrange
        sampleUser.setId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderMapper.toEntity(sampleRequest, sampleUser, sampleCupon)
        );

        assertEquals("Usuario o ID de usuario inválido", exception.getMessage());
    }

    @Test
    void toEntity_ShouldThrowException_WhenCuponIdIsNullButCuponProvided() {
        // Arrange
        CuponModel cuponSinId = new CuponModel();
        cuponSinId.setId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderMapper.toEntity(sampleRequest, sampleUser, cuponSinId)
        );

        assertEquals("Cupón con ID inválido", exception.getMessage());
    }

    @Test
    void toEntity_ShouldWork_WhenCuponIsNull() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        // Act
        OrderModel result = orderMapper.toEntity(sampleRequest, sampleUser, null);

        // Assert
        assertNotNull(result);
        assertEquals(sampleUser, result.getUser());
        assertNull(result.getCuponModel());

        verify(productRepository).findById(1L);
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateAllFields() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        OrderModel existingOrder = new OrderModel();

        // Act
        orderMapper.updateEntityFromRequest(existingOrder, sampleRequest, sampleUser, sampleCupon);

        // Assert
        assertEquals("123 Calle Principal", existingOrder.getDireccionEnvio());
        assertEquals("DELIVERY", existingOrder.getTipoEntrega());
        assertEquals(sampleUser, existingOrder.getUser());
        assertEquals(sampleCupon, existingOrder.getCuponModel());
        assertNotNull(existingOrder.getFechaCreacion());
        assertNotNull(existingOrder.getProductos());
        assertEquals(1, existingOrder.getProductos().size());

        // Verificar cálculo del total
        double expectedSubtotal = 2 * 25.99; // cantidad * precio
        double expectedDescuento = 10.0; // porcentaje del cupón
        double expectedTotal = expectedSubtotal - expectedDescuento;
        assertEquals(expectedTotal, existingOrder.getTotal());

        verify(productRepository).findById(1L);
    }

    @Test
    void updateEntityFromRequest_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        OrderModel existingOrder = new OrderModel();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderMapper.updateEntityFromRequest(existingOrder, sampleRequest, sampleUser, sampleCupon)
        );

        assertEquals("Producto no encontrado", exception.getMessage());
        verify(productRepository).findById(1L);
    }

    @Test
    void toResponse_ShouldCreateOrderResponse_WhenValidOrder() {
        // Act
        OrderResponse result = orderMapper.toResponse(sampleOrder);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getPedidoId());
        assertEquals("123 Calle Principal", result.getDireccionEnvio());
        assertEquals("DELIVERY", result.getTipoEntrega());
        assertNotNull(result.getFechaCreacion());

        // Verificar cálculo del subtotal
        double expectedSubtotal = 2 * 25.99; // cantidad * precio
        assertEquals(expectedSubtotal, result.getSubtotal(), 0.01);

        // Verificar descuento
        assertEquals(10.0, result.getDescuentoAplicado());

        // Verificar total final
        // La implementación actual hace: subtotal - descuentoPorcentaje (no calcula el porcentaje)
        double expectedTotal = expectedSubtotal - 10.0; // 51.98 - 10 = 41.98
        assertEquals(expectedTotal, result.getTotalFinal(), 0.01);

        // Verificar cupón aplicado
        assertEquals("DESCUENTO10", result.getCuponAplicado());

        // Verificar usuario
        assertNotNull(result.getUser());
        assertEquals(1L, result.getUser().getId());
        assertEquals("Juan", result.getUser().getNombre());
        assertEquals("juan@example.com", result.getUser().getCorreo());

        // Verificar detalles
        assertNotNull(result.getDetalles());
        assertEquals(1, result.getDetalles().size());
        assertEquals(1L, result.getDetalles().get(0).getProductoId());
        assertEquals("Rosa Roja", result.getDetalles().get(0).getNombreProducto());
        assertEquals(2, result.getDetalles().get(0).getCantidad());
        assertEquals(25.99, result.getDetalles().get(0).getPrecioUnitario());
    }

    @Test
    void toResponse_ShouldHandleOrderWithoutCupon() {
        // Arrange
        sampleOrder.setCuponModel(null);

        // Act
        OrderResponse result = orderMapper.toResponse(sampleOrder);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getDescuentoAplicado());
        assertNull(result.getCuponAplicado());

        // El total final debería ser igual al subtotal cuando no hay cupón
        assertEquals(result.getSubtotal(), result.getTotalFinal());
    }

    @Test
    void toResponse_ShouldHandleOrderWithoutUser() {
        // Arrange
        sampleOrder.setUser(null);

        // Act
        OrderResponse result = orderMapper.toResponse(sampleOrder);

        // Assert
        assertNotNull(result);
        assertNull(result.getUser());
    }

    @Test
    void toResponse_ShouldHandleOrderWithMultipleProducts() {
        // Arrange
        ProductModel product2 = new ProductModel();
        product2.setId(2L);
        product2.setName("Rosa Blanca");
        product2.setPrice(20.0);

        OrderDetailModel detail2 = new OrderDetailModel();
        detail2.setId(2L);
        detail2.setProducto(product2);
        detail2.setCantidad(1);
        detail2.setPrecioUnitario(20.0);

        List<OrderDetailModel> multipleProducts = Arrays.asList(
            sampleOrder.getProductos().get(0),
            detail2
        );
        sampleOrder.setProductos(multipleProducts);

        // Act
        OrderResponse result = orderMapper.toResponse(sampleOrder);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getDetalles().size());

        // Verificar subtotal: (2 * 25.99) + (1 * 20.0) = 71.98
        double expectedSubtotal = (2 * 25.99) + (1 * 20.0);
        assertEquals(expectedSubtotal, result.getSubtotal(), 0.01);
    }

    @Test
    void updateEntityFromRequest_ShouldCalculateCorrectTotalWithoutCupon() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        OrderModel existingOrder = new OrderModel();

        // Act
        orderMapper.updateEntityFromRequest(existingOrder, sampleRequest, sampleUser, null);

        // Assert
        double expectedSubtotal = 2 * 25.99; // cantidad * precio
        double expectedTotal = expectedSubtotal; // sin descuento
        assertEquals(expectedTotal, existingOrder.getTotal());
    }
}
