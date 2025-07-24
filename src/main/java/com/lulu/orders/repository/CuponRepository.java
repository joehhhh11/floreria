package com.lulu.orders.repository;

import com.lulu.orders.model.CuponModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuponRepository extends JpaRepository<CuponModel, Long> {
    Optional<CuponModel> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
