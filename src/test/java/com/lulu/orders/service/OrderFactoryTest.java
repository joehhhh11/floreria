package com.lulu.orders.service;

import com.lulu.auth.model.UserModel;
import com.lulu.orders.dto.OrderRequest;
import com.lulu.orders.dto.ProducQuantity;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.model.OrderDetailModel;
import com.lulu.orders.model.OrderModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderFactoryTest {

    @InjectMocks
    private OrderFactory orderFactory;

    private UserModel sampleUser;
    private OrderRequest sampleRequest;
    private CuponModel sampleCupon;

    @BeforeEach
    void setUp() {
        sampleUser = new UserModel();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");

        ProducQuantity producto = new ProducQuantity();
        producto.setProductoId(1L);
        producto.setCantidad(2);

        sampleRequest = new OrderRequest();
        sampleRequest.setDireccionEnvio("123 Calle Principal");
        sampleRequest.setTipoEntrega("DELIVERY");
        sampleRequest.setProductos(Arrays.asList(producto));

        sampleCupon = new CuponModel();
        sampleCupon.setId(1L);
        sampleCupon.setCodigo("DESCUENTO20");
        sampleCupon.setDescuentoPorcentaje(20);
    }

    @Test
    void createBaseOrder_ShouldCreateOrderWithCorrectProperties() {
        // Act
        OrderModel result = orderFactory.createBaseOrder(sampleUser, sampleRequest, sampleCupon);

        // Assert
        assertNotNull(result);
        assertEquals(sampleUser, result.getUser());
        assertEquals("123 Calle Principal", result.getDireccionEnvio());
        assertEquals("DELIVERY", result.getTipoEntrega());
        assertEquals(sampleCupon, result.getCuponModel());
        assertNotNull(result.getFechaCreacion());
        
        // Verificar que la fecha de creación sea reciente (dentro de los últimos 5 segundos)
        assertTrue(result.getFechaCreacion().isAfter(LocalDateTime.now().minusSeconds(5)));
        assertTrue(result.getFechaCreacion().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void createBaseOrder_ShouldWorkWithNullCupon() {
        // Act
        OrderModel result = orderFactory.createBaseOrder(sampleUser, sampleRequest, null);

        // Assert
        assertNotNull(result);
        assertEquals(sampleUser, result.getUser());
        assertEquals("123 Calle Principal", result.getDireccionEnvio());
        assertEquals("DELIVERY", result.getTipoEntrega());
        assertNull(result.getCuponModel());
        assertNotNull(result.getFechaCreacion());
    }

    @Test
    void calcularTotalConDescuento_ShouldCalculateCorrectTotal_WithoutCupon() {
        // Arrange
        OrderDetailModel detalle1 = new OrderDetailModel();
        detalle1.setPrecioUnitario(10.0);
        detalle1.setCantidad(2);

        OrderDetailModel detalle2 = new OrderDetailModel();
        detalle2.setPrecioUnitario(15.0);
        detalle2.setCantidad(3);

        List<OrderDetailModel> detalles = Arrays.asList(detalle1, detalle2);

        // Act
        double result = orderFactory.calcularTotalConDescuento(detalles, null);

        // Assert
        assertEquals(65.0, result); // (10*2) + (15*3) = 20 + 45 = 65
    }

    @Test
    void calcularTotalConDescuento_ShouldCalculateCorrectTotal_WithCupon() {
        // Arrange
        OrderDetailModel detalle1 = new OrderDetailModel();
        detalle1.setPrecioUnitario(10.0);
        detalle1.setCantidad(2);

        OrderDetailModel detalle2 = new OrderDetailModel();
        detalle2.setPrecioUnitario(15.0);
        detalle2.setCantidad(3);

        List<OrderDetailModel> detalles = Arrays.asList(detalle1, detalle2);

        // Act
        double result = orderFactory.calcularTotalConDescuento(detalles, sampleCupon);

        // Assert
        // Total esperado: (10*2) + (15*3) = 65, con 20% descuento = 52
        
        assertEquals(52.0, result, 0.01);
    }

    @Test
    void calcularTotalConDescuento_ShouldHandleEmptyList() {
        // Arrange
        List<OrderDetailModel> detallesVacios = Arrays.asList();

        // Act
        double result = orderFactory.calcularTotalConDescuento(detallesVacios, sampleCupon);

        // Assert
        assertEquals(0.0, result);
    }

    @Test
    void calcularTotalConDescuento_ShouldHandleSingleItem() {
        // Arrange
        OrderDetailModel detalle = new OrderDetailModel();
        detalle.setPrecioUnitario(50.0);
        detalle.setCantidad(1);

        List<OrderDetailModel> detalles = Arrays.asList(detalle);

        // Act
        double result = orderFactory.calcularTotalConDescuento(detalles, sampleCupon);

        // Assert
        // Total esperado: 50 con 20% descuento = 40
        
        assertEquals(40.0, result, 0.01);
    }

    @Test
    void calcularTotalConDescuento_ShouldHandle100PercentDiscount() {
        // Arrange
        CuponModel cupon100 = new CuponModel();
        cupon100.setDescuentoPorcentaje(100);

        OrderDetailModel detalle = new OrderDetailModel();
        detalle.setPrecioUnitario(30.0);
        detalle.setCantidad(2);

        List<OrderDetailModel> detalles = Arrays.asList(detalle);

        // Act
        double result = orderFactory.calcularTotalConDescuento(detalles, cupon100);

        // Assert
        assertEquals(0.0, result); // 100% de descuento = gratis
    }
}
