package com.uniquindio.triage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CerrarRequest {

    @NotBlank(message = "La observación de cierre es obligatoria")
    @Size(min = 10, max = 1000, message = "La observación debe tener entre 10 y 1000 caracteres")
    private String observacionCierre;
}