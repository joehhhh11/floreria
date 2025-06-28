package com.lulu.auth.repository;

import com.lulu.auth.model.RolModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<RolModel, Long> {

    Optional<RolModel> findByTipoRol(String tipoRol);
}
