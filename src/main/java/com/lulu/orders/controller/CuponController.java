package com.lulu.orders.controller;

import com.lulu.orders.dto.CuponRequestDTO;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.service.CuponService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAuthority('admin')")
@RestController
@RequestMapping("/api/cupones")
public class CuponController {

    private final CuponService cuponService;

    public CuponController(CuponService cuponService) {
        this.cuponService = cuponService;
    }

    @PostMapping
    public ResponseEntity<CuponModel> crearCupon(@RequestBody CuponRequestDTO dto) {
        CuponModel nuevoCupon = cuponService.crearCupon(dto);
        return ResponseEntity.ok(nuevoCupon);
    }
}
