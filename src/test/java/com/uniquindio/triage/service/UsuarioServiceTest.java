package com.uniquindio.triage.service;

import com.uniquindio.triage.dto.request.EstadoUsuarioRequest;
import com.uniquindio.triage.dto.request.UsuarioRequest;
import com.uniquindio.triage.dto.response.UsuarioDTO;
import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.enums.RolUsuario;
import com.uniquindio.triage.exception.ConflictException;
import com.uniquindio.triage.exception.NotFoundException;
import com.uniquindio.triage.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioActivo;
    private Usuario usuarioInactivo;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();

        usuarioActivo = new Usuario();
        usuarioActivo.setId(usuarioId);
        usuarioActivo.setNombre("Juan");
        usuarioActivo.setApellido("Pérez");
        usuarioActivo.setCorreo("jperez@uqvirtual.edu.co");
        usuarioActivo.setIdentificacion("1094000789");
        usuarioActivo.setPasswordHash("hashedPassword");
        usuarioActivo.setRol(RolUsuario.ESTUDIANTE);
        usuarioActivo.setActivo(true);

        usuarioInactivo = new Usuario();
        usuarioInactivo.setId(UUID.randomUUID());
        usuarioInactivo.setNombre("María");
        usuarioInactivo.setApellido("González");
        usuarioInactivo.setCorreo("mgonzalez@uqvirtual.edu.co");
        usuarioInactivo.setIdentificacion("1094000456");
        usuarioInactivo.setPasswordHash("hashedPassword");
        usuarioInactivo.setRol(RolUsuario.RESPONSABLE);
        usuarioInactivo.setActivo(false);
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE CREACIÓN
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Crear usuario exitosamente")
    void crear_exitoso() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNombre("Pedro");
        request.setApellido("Ramirez");
        request.setCorreo("pramirez@uqvirtual.edu.co");
        request.setIdentificacion("1094000999");
        request.setRol(RolUsuario.ESTUDIANTE);

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(usuarioRepository.existsByIdentificacion(request.getIdentificacion())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioDTO resultado = usuarioService.crear(request);

        assertNotNull(resultado);
        assertEquals("Pedro", resultado.getNombre());
        assertEquals(RolUsuario.ESTUDIANTE, resultado.getRol());
        assertTrue(resultado.getActivo());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Crear usuario con correo duplicado lanza ConflictException")
    void crear_correoDuplicado_lanzaConflictException() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNombre("Pedro");
        request.setApellido("Ramirez");
        request.setCorreo("jperez@uqvirtual.edu.co");
        request.setIdentificacion("1094000999");
        request.setRol(RolUsuario.ESTUDIANTE);

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(true);

        assertThrows(ConflictException.class, () -> usuarioService.crear(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear usuario con identificación duplicada lanza ConflictException")
    void crear_identificacionDuplicada_lanzaConflictException() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNombre("Pedro");
        request.setApellido("Ramirez");
        request.setCorreo("pramirez@uqvirtual.edu.co");
        request.setIdentificacion("1094000789");
        request.setRol(RolUsuario.ESTUDIANTE);

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(usuarioRepository.existsByIdentificacion(request.getIdentificacion())).thenReturn(true);

        assertThrows(ConflictException.class, () -> usuarioService.crear(request));
        verify(usuarioRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE CAMBIO DE ESTADO
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Desactivar usuario activo exitosamente")
    void cambiarEstado_desactivar_exitoso() {
        EstadoUsuarioRequest request = new EstadoUsuarioRequest();
        request.setActivo(false);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioActivo));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UsuarioDTO resultado = usuarioService.cambiarEstado(usuarioId, request);

        assertFalse(resultado.getActivo());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Activar usuario inactivo exitosamente")
    void cambiarEstado_activar_exitoso() {
        EstadoUsuarioRequest request = new EstadoUsuarioRequest();
        request.setActivo(true);

        when(usuarioRepository.findById(usuarioInactivo.getId()))
            .thenReturn(Optional.of(usuarioInactivo));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UsuarioDTO resultado = usuarioService.cambiarEstado(usuarioInactivo.getId(), request);

        assertTrue(resultado.getActivo());
    }

    @Test
    @DisplayName("Cambiar estado de usuario inexistente lanza NotFoundException")
    void cambiarEstado_usuarioNoExiste_lanzaNotFoundException() {
        EstadoUsuarioRequest request = new EstadoUsuarioRequest();
        request.setActivo(false);
        UUID idInexistente = UUID.randomUUID();

        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> usuarioService.cambiarEstado(idInexistente, request));
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE CONSULTA
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Listar responsables activos exitosamente")
    void listarResponsables_activos_exitoso() {
        when(usuarioRepository.findByRolAndActivo(RolUsuario.RESPONSABLE, true))
            .thenReturn(List.of(usuarioActivo));

        List<UsuarioDTO> resultado = usuarioService.listarResponsables(true);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(usuarioRepository, times(1)).findByRolAndActivo(RolUsuario.RESPONSABLE, true);
    }

    @Test
    @DisplayName("Buscar usuario por correo exitosamente")
    void buscarPorCorreo_exitoso() {
        when(usuarioRepository.findByCorreo("jperez@uqvirtual.edu.co"))
            .thenReturn(Optional.of(usuarioActivo));

        Usuario resultado = usuarioService.buscarPorCorreo("jperez@uqvirtual.edu.co");

        assertNotNull(resultado);
        assertEquals("jperez@uqvirtual.edu.co", resultado.getCorreo());
    }

    @Test
    @DisplayName("Buscar usuario por correo inexistente lanza NotFoundException")
    void buscarPorCorreo_noExiste_lanzaNotFoundException() {
        when(usuarioRepository.findByCorreo("noexiste@uqvirtual.edu.co"))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> usuarioService.buscarPorCorreo("noexiste@uqvirtual.edu.co"));
    }
}