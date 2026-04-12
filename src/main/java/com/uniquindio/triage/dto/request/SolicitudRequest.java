package com.uniquindio.triage.dto.request;

import com.uniquindio.triage.enums.CanalOrigen;
import com.uniquindio.triage.enums.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.UUID;

@Data
public class SolicitudRequest {

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String descripcion;

    @NotNull(message = "El tipo de solicitud es obligatorio")
    private TipoSolicitud tipoSolicitud;

    @NotNull(message = "El canal de origen es obligatorio")
    private CanalOrigen canalOrigen;

    @NotNull(message = "El ID del solicitante es obligatorio")
    private UUID solicitanteId;
}