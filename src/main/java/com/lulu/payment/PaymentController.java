package com.lulu.payment;

import com.lulu.orders.model.OrderModel;
import com.lulu.orders.repository.OrderRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collector;
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

        order.setEstado("PAGADA"); // o el valor que uses
        orderRepository.save(order);

        return "Orden marcada como pagada correctamente.";
    }

    @PostMapping("/pay-order/{orderId}")
    public String createCheckoutSession(@PathVariable Long orderId) throws Exception {
        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Convertimos los productos a lineItems de Stripe
        List<SessionCreateParams.LineItem> lineItems = order.getProductos().stream().map(detalle ->
                SessionCreateParams.LineItem.builder()
                        .setQuantity(detalle.getCantidad().longValue())
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmount((long) (detalle.getPrecioUnitario() * 100)) // en centavos
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(detalle.getProducto().getCategoria().getNombre()) // nombre del producto real
                                                        .build()
                                        )
                                        .build()
                        )
                        .build()
        ).collect(Collectors.toList());

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?orderId=" + orderId)
                .setCancelUrl(cancelUrl)
                .putMetadata("order_id", orderId.toString()) // para el webhook
                .build();

        Session session = Session.create(params);
        return session.getUrl(); // frontend redirige a esta URL
    }

}
