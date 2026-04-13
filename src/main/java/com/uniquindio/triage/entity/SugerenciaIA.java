package com.uniquindio.triage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.uniquindio.triage.enums.Prioridad;
import com.uniquindio.triage.enums.TipoSolicitud;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sugerencia_ia")
public class SugerenciaIA {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, length = 36)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sugerido", nullable = false, length = 30)
    private TipoSolicitud tipoSugerido;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad_sugerida", nullable = false, length = 10)
    private Prioridad prioridadSugerida;

    @Column(name = "resumen_generado", columnDefinition = "TEXT")
    private String resumenGenerado;

    @Column(nullable = false)
    private Boolean confirmada = false;

    @Column(name = "fecha_sugerencia", nullable = false, updatable = false)
    private LocalDateTime fechaSugerencia;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Solicitud solicitud;

    @PrePersist
    protected void onCreate() {
        this.fechaSugerencia = LocalDateTime.now();
        this.confirmada = false;
    }
}