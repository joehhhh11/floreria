package com.lulu.orders.repository;

import com.lulu.orders.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    List<OrderModel> findByUserId(Long userId);
    @Query("SELECT o FROM OrderModel o JOIN FETCH o.productos WHERE o.id = :id")
    Optional<OrderModel> findByIdWithProductos(@Param("id") Long id);

}
