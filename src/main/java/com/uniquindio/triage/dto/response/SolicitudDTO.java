package com.uniquindio.triage.dto.response;

import com.uniquindio.triage.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SolicitudDTO {

    private UUID id;
    private String descripcion;
    private EstadoSolicitud estado;
    private TipoSolicitud tipoSolicitud;
    private Prioridad prioridad;
    private CanalOrigen canalOrigen;
    private String justificacionPrioridad;
    private String observacionCierre;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaCierre;
    private String nombreSolicitante;
    private String nombreResponsable;  // puede ser null
}