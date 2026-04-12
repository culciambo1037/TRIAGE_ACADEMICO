package com.uniquindio.triage.repository;

import com.uniquindio.triage.entity.ReglasPriorizacion;
import com.uniquindio.triage.enums.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReglasPriorizacionRepository extends JpaRepository<ReglasPriorizacion, UUID> {

    // Busca la regla activa para un tipo de solicitud
    Optional<ReglasPriorizacion> findByTipoSolicitudAndActivaTrue(TipoSolicitud tipoSolicitud);
}