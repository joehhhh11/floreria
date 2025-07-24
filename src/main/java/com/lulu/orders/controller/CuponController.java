package com.lulu.orders.controller;

import com.lulu.orders.dto.CuponRequestDTO;
import com.lulu.orders.model.CuponModel;
import com.lulu.orders.service.CuponService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cupones")
public class CuponController {

    private final CuponService cuponService;

    public CuponController(CuponService cuponService) {
        this.cuponService = cuponService;
    }

    @GetMapping
    public ResponseEntity<?> getCupon(@RequestParam(required = false) String codigo) {
        if (codigo != null && !codigo.isEmpty()) {
            CuponModel cupon = cuponService.obtenerPorCodigo(codigo);
            return ResponseEntity.ok(cupon);
        } else {
            return ResponseEntity.ok(cuponService.obtenerTodos());
        }
    }

    @PostMapping
    public ResponseEntity<CuponModel> crearCupon(@RequestBody CuponRequestDTO dto) {
        CuponModel nuevoCupon = cuponService.crearCupon(dto);
        return ResponseEntity.ok(nuevoCupon);
    }
    @PutMapping("/estado")
    public ResponseEntity<CuponModel> actualizarEstadoCupon(
            @RequestParam String codigo,
            @RequestParam boolean activo
    ) {
        CuponModel actualizado = cuponService.actualizarEstadoCupon(codigo, activo);
        return ResponseEntity.ok(actualizado);
    }
    @DeleteMapping
    public ResponseEntity<?> eliminarCupon(@RequestParam String codigo) {
        cuponService.eliminarCupon(codigo);
        return ResponseEntity.ok("Cupón eliminado correctamente.");
    }


}
