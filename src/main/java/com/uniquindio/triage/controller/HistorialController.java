package com.uniquindio.triage.controller;

import com.uniquindio.triage.dto.response.HistorialDTO;
import com.uniquindio.triage.service.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialService historialService;

    // GET /api/solicitudes/{id}/historial
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialDTO>> obtenerHistorial(
            @PathVariable UUID id) {

        return ResponseEntity.ok(historialService.obtenerPorSolicitud(id));
    }
}