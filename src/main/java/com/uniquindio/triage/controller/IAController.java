package com.uniquindio.triage.controller;

import com.uniquindio.triage.dto.request.SugerirRequest;
import com.uniquindio.triage.dto.response.ResumenDTO;
import com.uniquindio.triage.dto.response.SugerenciaDTO;
import com.uniquindio.triage.service.IAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class IAController {

    private final IAService iaService;

    // POST /api/ia/sugerir
    @PostMapping("/sugerir")
    public ResponseEntity<SugerenciaDTO> sugerir(
            @Valid @RequestBody SugerirRequest request) {
        return ResponseEntity.ok(iaService.sugerir(request.getDescripcion()));
    }

    // POST /api/ia/resumen/{id}
    @PostMapping("/resumen/{id}")
    public ResponseEntity<ResumenDTO> resumen(
            @PathVariable UUID id) {
        return ResponseEntity.ok(iaService.generarResumen(id));
    }
}