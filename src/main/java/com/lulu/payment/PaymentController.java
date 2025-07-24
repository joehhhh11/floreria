package com.lulu.payment;

import com.lulu.orders.model.OrderModel;
import com.lulu.orders.repository.OrderRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
public class PaymentController {

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/confirm-payment/{orderId}")
    public String confirmPayment(@PathVariable Long orderId) {
        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        order.setEstado("PAGADA");
        orderRepository.save(order);

        return "Orden marcada como pagada correctamente.";
    }

    @PostMapping("/pay-order/{orderId}")
    public String createCheckoutSession(@PathVariable Long orderId) throws Exception {
        OrderModel order = orderRepository.findByIdWithProductos(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        double subtotal = order.getProductos().stream()
                .mapToDouble(detalle -> detalle.getCantidad() * detalle.getPrecioUnitario())
                .sum();

        double total = order.getTotal();
        double descuentoRatio = total / subtotal; //

        List<SessionCreateParams.LineItem> lineItems = order.getProductos().stream().map(detalle -> {
            long cantidad = detalle.getCantidad().longValue();

            double originalUnitPrice = detalle.getPrecioUnitario();

            long adjustedUnitPrice = Math.round(originalUnitPrice * descuentoRatio * 100);

            return SessionCreateParams.LineItem.builder()
                    .setQuantity(cantidad)
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(adjustedUnitPrice)
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(detalle.getProducto().getName())
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
        }).collect(Collectors.toList());

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?orderId=" + orderId)
                .setCancelUrl(cancelUrl)
                .putMetadata("order_id", orderId.toString())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

}
