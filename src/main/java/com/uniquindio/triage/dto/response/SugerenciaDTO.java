package com.uniquindio.triage.dto.response;

import com.uniquindio.triage.enums.Prioridad;
import com.uniquindio.triage.enums.TipoSolicitud;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SugerenciaDTO {

    private TipoSolicitud tipoSugerido;
    private Prioridad prioridadSugerida;
    private Boolean confirmada;
}