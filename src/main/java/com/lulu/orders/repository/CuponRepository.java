package com.lulu.orders.repository;

import com.lulu.orders.model.CuponModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuponRepository extends JpaRepository<CuponModel, Long> {
}
