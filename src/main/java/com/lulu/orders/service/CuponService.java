package com.lulu.orders.service;

import com.lulu.orders.dto.CuponRequestDTO;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.repository.CuponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CuponService {
    private final CuponRepository cuponRepository;

    public CuponService(CuponRepository cuponRepository) {
        this.cuponRepository = cuponRepository;
    }

    public CuponModel crearCupon(CuponRequestDTO dto) {
        if (cuponRepository.existsByCodigo(dto.getCodigo())) {
            throw new RuntimeException("Ya existe un cupón con ese código.");
        }

        CuponModel cupon = new CuponModel();
        cupon.setActive(dto.getActive());
        cupon.setCodigo(dto.getCodigo());
        cupon.setDescuentoPorcentaje(dto.getDescuentoPorcentaje());
        cupon.setFechaCreacion(LocalDateTime.now());
        cupon.setFechaExpiracion(dto.getFechaExpiracion());

        return cuponRepository.save(cupon);
    }
}
