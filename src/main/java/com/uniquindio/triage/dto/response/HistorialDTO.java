package com.uniquindio.triage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class HistorialDTO {

    private UUID id;
    private String accionRealizada;
    private String observaciones;
    private LocalDateTime fechaAccion;
    private String nombreUsuario;
}