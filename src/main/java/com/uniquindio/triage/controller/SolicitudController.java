package com.uniquindio.triage.controller;

import com.uniquindio.triage.dto.request.*;
import com.uniquindio.triage.dto.response.*;
import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.enums.*;
import com.uniquindio.triage.security.UserDetailsImpl;
import com.uniquindio.triage.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    // POST /api/solicitudes
    @PostMapping
    public ResponseEntity<SolicitudDTO> crear(
            @Valid @RequestBody SolicitudRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Usuario usuarioActual = userDetails.getUsuario();
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(solicitudService.crear(request, usuarioActual));
    }

    // GET /api/solicitudes
    @GetMapping
    public ResponseEntity<PageSolicitudDTO> listar(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) UUID responsableId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
            solicitudService.listar(estado, tipo, prioridad, responsableId, page, size)
        );
    }

    // GET /api/solicitudes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudDTO> obtener(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Usuario usuarioActual = userDetails.getUsuario();
        return ResponseEntity.ok(solicitudService.obtener(id, usuarioActual));
    }

    // PATCH /api/solicitudes/{id}/clasificar
    @PatchMapping("/{id}/clasificar")
    public ResponseEntity<SolicitudDTO> clasificar(
            @PathVariable UUID id,
            @Valid @RequestBody ClasificarRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Usuario usuarioActual = userDetails.getUsuario();
        return ResponseEntity.ok(solicitudService.clasificar(id, request, usuarioActual));
    }

    // PATCH /api/solicitudes/{id}/asignar
    @PatchMapping("/{id}/asignar")
    public ResponseEntity<SolicitudDTO> asignar(
            @PathVariable UUID id,
            @Valid @RequestBody AsignarRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Usuario usuarioActual = userDetails.getUsuario();
        return ResponseEntity.ok(solicitudService.asignar(id, request, usuarioActual));
    }

    // PATCH /api/solicitudes/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudDTO> cambiarEstado(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarEstadoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Usuario usuarioActual = userDetails.getUsuario();
        return ResponseEntity.ok(solicitudService.cambiarEstado(id, request, usuarioActual));
    }

    // PATCH /api/solicitudes/{id}/cerrar
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<SolicitudDTO> cerrar(
            @PathVariable UUID id,
            @Valid @RequestBody CerrarRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Usuario usuarioActual = userDetails.getUsuario();
        return ResponseEntity.ok(solicitudService.cerrar(id, request, usuarioActual));
    }
}