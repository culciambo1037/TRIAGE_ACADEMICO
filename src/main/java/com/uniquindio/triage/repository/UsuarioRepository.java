package com.uniquindio.triage.repository;

import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // Para login — busca por correo
    Optional<Usuario> findByCorreo(String correo);

    // Para verificar duplicados al crear usuario
    boolean existsByCorreo(String correo);
    boolean existsByIdentificacion(String identificacion);

    // Para listar responsables activos (RF-05)
    List<Usuario> findByRolAndActivo(RolUsuario rol, Boolean activo);
}