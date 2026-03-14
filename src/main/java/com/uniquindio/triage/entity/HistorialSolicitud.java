package com.uniquindio.triage.entity;

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
@Table(name = "historial_solicitud")
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, length = 36)
    private UUID id;

    @Column(name = "accion_realizada", nullable = false, length = 255)
    private String accionRealizada;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_accion", nullable = false, updatable = false)
    private LocalDateTime fechaAccion;

    // Relación con Solicitud — siempre existe (composición)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;

    // Relación con Usuario — quién ejecutó la acción
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    protected void onCreate() {
        this.fechaAccion = LocalDateTime.now();
    }
}