package com.lulu.orders.repository;

import com.lulu.orders.model.OrderDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailModel, Long> {
}
