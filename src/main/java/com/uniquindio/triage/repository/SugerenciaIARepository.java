package com.uniquindio.triage.repository;

import com.uniquindio.triage.entity.Solicitud;
import com.uniquindio.triage.entity.SugerenciaIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SugerenciaIARepository extends JpaRepository<SugerenciaIA, UUID> {

    // Optional → expresa el 0..1 del modelo
    Optional<SugerenciaIA> findBySolicitud(Solicitud solicitud);
}