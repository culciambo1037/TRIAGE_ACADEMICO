package com.uniquindio.triage.dto.request;

import com.uniquindio.triage.enums.EstadoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CambiarEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoSolicitud nuevoEstado;

    @NotBlank(message = "Las observaciones son obligatorias")
    @Size(min = 5, max = 500, message = "Las observaciones deben tener entre 5 y 500 caracteres")
    private String observaciones;
}