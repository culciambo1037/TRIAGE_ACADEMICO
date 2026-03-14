package com.uniquindio.triage.entity;

import com.uniquindio.triage.enums.Prioridad;
import com.uniquindio.triage.enums.TipoSolicitud;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "regla_priorizacion")
public class ReglasPriorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, length = 36)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_solicitud", nullable = false, length = 30)
    private TipoSolicitud tipoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad_base", nullable = false, length = 10)
    private Prioridad prioridadBase;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activa = true;
}