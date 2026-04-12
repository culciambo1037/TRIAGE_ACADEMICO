package com.uniquindio.triage.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class AsignarRequest {

    @NotNull(message = "El ID del responsable es obligatorio")
    private UUID responsableId;
}