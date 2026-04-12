package com.uniquindio.triage.service;

import com.uniquindio.triage.dto.request.UsuarioRequest;
import com.uniquindio.triage.dto.request.EstadoUsuarioRequest;
import com.uniquindio.triage.dto.response.UsuarioDTO;
import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.enums.RolUsuario;
import com.uniquindio.triage.exception.ConflictException;
import com.uniquindio.triage.exception.NotFoundException;
import com.uniquindio.triage.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // POST /api/usuarios (RF-13)
    @Transactional
    public UsuarioDTO crear(UsuarioRequest request) {

        // Verificar duplicados
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new ConflictException("El correo ya está registrado");
        }
        if (usuarioRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new ConflictException("La identificación ya está registrada");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setCorreo(request.getCorreo());
        usuario.setIdentificacion(request.getIdentificacion());
        usuario.setRol(request.getRol());
        usuario.setActivo(true);
        // La contraseña inicial es la identificación hasheada
        usuario.setPasswordHash(passwordEncoder.encode(request.getIdentificacion()));

        return toDTO(usuarioRepository.save(usuario));
    }

    // GET /api/usuarios/responsables (RF-05)
    public List<UsuarioDTO> listarResponsables(Boolean activo) {
        return usuarioRepository
            .findByRolAndActivo(RolUsuario.RESPONSABLE, activo)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    // PATCH /api/usuarios/{id}/estado (RF-13)
    @Transactional
    public UsuarioDTO cambiarEstado(UUID id, EstadoUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        usuario.setActivo(request.getActivo());
        return toDTO(usuarioRepository.save(usuario));
    }

    // Convierte entidad → DTO
    public UsuarioDTO toDTO(Usuario u) {
        return new UsuarioDTO(
            u.getId(),
            u.getNombre(),
            u.getApellido(),
            u.getCorreo(),
            u.getIdentificacion(),
            u.getRol(),
            u.getActivo()
        );
    }

    // Para uso interno — buscar entidad por ID
    public Usuario buscarEntidadPorId(UUID id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    // Para uso interno — buscar entidad por correo (login)
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }
}