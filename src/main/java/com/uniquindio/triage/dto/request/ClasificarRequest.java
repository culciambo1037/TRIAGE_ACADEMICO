package com.uniquindio.triage.dto.request;

import com.uniquindio.triage.enums.Prioridad;
import com.uniquindio.triage.enums.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClasificarRequest {

    @NotNull(message = "El tipo de solicitud es obligatorio")
    private TipoSolicitud tipoSolicitud;

    @NotNull(message = "La prioridad es obligatoria")
    private Prioridad prioridad;

    @NotBlank(message = "La justificación de prioridad es obligatoria")
    @Size(min = 10, max = 255, message = "La justificación debe tener entre 10 y 255 caracteres")
    private String justificacionPrioridad;
}