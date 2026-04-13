package com.uniquindio.triage.controller;

import com.uniquindio.triage.dto.request.EstadoUsuarioRequest;
import com.uniquindio.triage.dto.request.UsuarioRequest;
import com.uniquindio.triage.dto.response.UsuarioDTO;
import com.uniquindio.triage.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // POST /api/usuarios
    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(
            @Valid @RequestBody UsuarioRequest request) {

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(usuarioService.crear(request));
    }

    // GET /api/usuarios/responsables
    @GetMapping("/responsables")
    public ResponseEntity<List<UsuarioDTO>> listarResponsables(
            @RequestParam(defaultValue = "true") Boolean activo) {

        return ResponseEntity.ok(usuarioService.listarResponsables(activo));
    }

    // PATCH /api/usuarios/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioDTO> cambiarEstado(
            @PathVariable UUID id,
            @Valid @RequestBody EstadoUsuarioRequest request) {

        return ResponseEntity.ok(usuarioService.cambiarEstado(id, request));
    }
}