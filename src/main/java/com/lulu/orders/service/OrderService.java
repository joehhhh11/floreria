package com.lulu.orders.service;

import com.lulu.orders.dto.OrderRequest;
import com.lulu.orders.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse updateOrder(Long id, OrderRequest request);
    void deleteOrder(Long id);
    OrderResponse getOrder(Long id);
    List<OrderResponse> getAllOrders();
}
