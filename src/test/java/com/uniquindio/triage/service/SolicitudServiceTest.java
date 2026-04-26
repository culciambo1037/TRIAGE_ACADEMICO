package com.uniquindio.triage.service;

import com.uniquindio.triage.dto.request.AsignarRequest;
import com.uniquindio.triage.dto.request.CerrarRequest;
import com.uniquindio.triage.dto.request.ClasificarRequest;
import com.uniquindio.triage.dto.request.SolicitudRequest;
import com.uniquindio.triage.dto.response.SolicitudDTO;
import com.uniquindio.triage.entity.Solicitud;
import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.enums.*;
import com.uniquindio.triage.exception.ConflictException;
import com.uniquindio.triage.exception.ForbiddenException;
import com.uniquindio.triage.exception.NotFoundException;
import com.uniquindio.triage.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de SolicitudService")
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private HistorialService historialService;

    @InjectMocks
    private SolicitudService solicitudService;

    private Usuario admin;
    private Usuario estudiante;
    private Usuario responsable;
    private Solicitud solicitudRegistrada;
    private Solicitud solicitudClasificada;
    private Solicitud solicitudCerrada;
    private UUID solicitudId;

    @BeforeEach
    void setUp() {
        // Usuarios de prueba
        admin = new Usuario();
        admin.setId(UUID.randomUUID());
        admin.setNombre("César");
        admin.setApellido("Giraldo");
        admin.setRol(RolUsuario.ADMIN);
        admin.setActivo(true);

        estudiante = new Usuario();
        estudiante.setId(UUID.randomUUID());
        estudiante.setNombre("Juan");
        estudiante.setApellido("Pérez");
        estudiante.setRol(RolUsuario.ESTUDIANTE);
        estudiante.setActivo(true);

        responsable = new Usuario();
        responsable.setId(UUID.randomUUID());
        responsable.setNombre("María");
        responsable.setApellido("González");
        responsable.setRol(RolUsuario.RESPONSABLE);
        responsable.setActivo(true);

        // Solicitudes de prueba
        solicitudId = UUID.randomUUID();

        solicitudRegistrada = new Solicitud();
        solicitudRegistrada.setId(solicitudId);
        solicitudRegistrada.setDescripcion("Solicitud de prueba para tests unitarios");
        solicitudRegistrada.setEstado(EstadoSolicitud.REGISTRADA);
        solicitudRegistrada.setTipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA);
        solicitudRegistrada.setPrioridad(Prioridad.MEDIA);
        solicitudRegistrada.setCanalOrigen(CanalOrigen.CSU);
        solicitudRegistrada.setSolicitante(estudiante);
        solicitudRegistrada.setJustificacionPrioridad("Pendiente de clasificación");
        solicitudRegistrada.setFechaRegistro(LocalDateTime.now());

        solicitudClasificada = new Solicitud();
        solicitudClasificada.setId(UUID.randomUUID());
        solicitudClasificada.setDescripcion("Solicitud clasificada para tests");
        solicitudClasificada.setEstado(EstadoSolicitud.CLASIFICADA);
        solicitudClasificada.setTipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA);
        solicitudClasificada.setPrioridad(Prioridad.ALTA);
        solicitudClasificada.setCanalOrigen(CanalOrigen.CORREO);
        solicitudClasificada.setSolicitante(estudiante);
        solicitudClasificada.setJustificacionPrioridad("Justificación de prueba");
        solicitudClasificada.setFechaRegistro(LocalDateTime.now());

        solicitudCerrada = new Solicitud();
        solicitudCerrada.setId(UUID.randomUUID());
        solicitudCerrada.setDescripcion("Solicitud cerrada para tests");
        solicitudCerrada.setEstado(EstadoSolicitud.CERRADA);
        solicitudCerrada.setTipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA);
        solicitudCerrada.setPrioridad(Prioridad.MEDIA);
        solicitudCerrada.setCanalOrigen(CanalOrigen.CSU);
        solicitudCerrada.setSolicitante(estudiante);
        solicitudCerrada.setJustificacionPrioridad("Justificación de prueba");
        solicitudCerrada.setObservacionCierre("Solicitud atendida.");
        solicitudCerrada.setFechaRegistro(LocalDateTime.now());
        solicitudCerrada.setFechaCierre(LocalDateTime.now());
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE CREACIÓN
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Crear solicitud exitosamente")
    void crearSolicitud_exitoso() {
        SolicitudRequest request = new SolicitudRequest();
        request.setDescripcion("Solicitud de prueba con suficientes caracteres");
        request.setTipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA);
        request.setCanalOrigen(CanalOrigen.CSU);
        request.setSolicitanteId(estudiante.getId());

        when(usuarioService.buscarEntidadPorId(estudiante.getId())).thenReturn(estudiante);
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(i -> i.getArgument(0));

        SolicitudDTO resultado = solicitudService.crear(request, admin);

        assertNotNull(resultado);
        assertEquals(EstadoSolicitud.REGISTRADA, resultado.getEstado());
        assertEquals(Prioridad.MEDIA, resultado.getPrioridad());
        verify(historialService, times(1)).registrar(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Crear solicitud con solicitante inexistente lanza NotFoundException")
    void crearSolicitud_solicitanteNoExiste_lanzaNotFoundException() {
        SolicitudRequest request = new SolicitudRequest();
        request.setDescripcion("Solicitud de prueba con suficientes caracteres");
        request.setTipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA);
        request.setCanalOrigen(CanalOrigen.CSU);
        request.setSolicitanteId(UUID.randomUUID());

        when(usuarioService.buscarEntidadPorId(any())).thenThrow(new NotFoundException("Usuario no encontrado"));

        assertThrows(NotFoundException.class, () -> solicitudService.crear(request, admin));
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE CLASIFICACIÓN
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Clasificar solicitud en estado REGISTRADA exitosamente")
    void clasificar_estadoRegistrada_exitoso() {
        ClasificarRequest request = new ClasificarRequest();
        request.setTipoSolicitud(TipoSolicitud.REGISTRO_ASIGNATURAS);
        request.setPrioridad(Prioridad.ALTA);
        request.setJustificacionPrioridad("Periodo de matrículas activo urgente");

        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitudRegistrada));
        when(solicitudRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SolicitudDTO resultado = solicitudService.clasificar(solicitudId, request, admin);

        assertEquals(EstadoSolicitud.CLASIFICADA, resultado.getEstado());
        assertEquals(Prioridad.ALTA, resultado.getPrioridad());
        verify(historialService, times(1)).registrar(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Clasificar solicitud CERRADA lanza ConflictException")
    void clasificar_estadoCerrada_lanzaConflictException() {
        ClasificarRequest request = new ClasificarRequest();
        request.setTipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA);
        request.setPrioridad(Prioridad.MEDIA);
        request.setJustificacionPrioridad("Intento de clasificar solicitud cerrada");

        when(solicitudRepository.findById(solicitudCerrada.getId()))
            .thenReturn(Optional.of(solicitudCerrada));

        assertThrows(ConflictException.class,
            () -> solicitudService.clasificar(solicitudCerrada.getId(), request, admin));
    }

    @Test
    @DisplayName("Clasificar solicitud CLASIFICADA lanza ConflictException")
    void clasificar_estadoClasificada_lanzaConflictException() {
        ClasificarRequest request = new ClasificarRequest();
        request.setTipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA);
        request.setPrioridad(Prioridad.MEDIA);
        request.setJustificacionPrioridad("Intento de reclasificar");

        when(solicitudRepository.findById(solicitudClasificada.getId()))
            .thenReturn(Optional.of(solicitudClasificada));

        assertThrows(ConflictException.class,
            () -> solicitudService.clasificar(solicitudClasificada.getId(), request, admin));
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE ASIGNACIÓN
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Asignar responsable activo a solicitud CLASIFICADA exitosamente")
    void asignar_responsableActivo_exitoso() {
        AsignarRequest request = new AsignarRequest();
        request.setResponsableId(responsable.getId());

        when(solicitudRepository.findById(solicitudClasificada.getId()))
            .thenReturn(Optional.of(solicitudClasificada));
        when(usuarioService.buscarEntidadPorId(responsable.getId())).thenReturn(responsable);
        when(solicitudRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SolicitudDTO resultado = solicitudService.asignar(solicitudClasificada.getId(), request, admin);

        assertEquals(EstadoSolicitud.EN_ATENCION, resultado.getEstado());
        verify(historialService, times(1)).registrar(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Asignar responsable inactivo lanza ConflictException")
    void asignar_responsableInactivo_lanzaConflictException() {
        responsable.setActivo(false);
        AsignarRequest request = new AsignarRequest();
        request.setResponsableId(responsable.getId());

        when(solicitudRepository.findById(solicitudClasificada.getId()))
            .thenReturn(Optional.of(solicitudClasificada));
        when(usuarioService.buscarEntidadPorId(responsable.getId())).thenReturn(responsable);

        assertThrows(ConflictException.class,
            () -> solicitudService.asignar(solicitudClasificada.getId(), request, admin));
    }

    @Test
    @DisplayName("Asignar responsable a solicitud REGISTRADA lanza ConflictException")
    void asignar_estadoRegistrada_lanzaConflictException() {
        AsignarRequest request = new AsignarRequest();
        request.setResponsableId(responsable.getId());

        when(solicitudRepository.findById(solicitudId))
            .thenReturn(Optional.of(solicitudRegistrada));

        assertThrows(ConflictException.class,
            () -> solicitudService.asignar(solicitudId, request, admin));
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE CIERRE
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Cerrar solicitud CERRADA lanza ConflictException")
    void cerrar_estadoCerrada_lanzaConflictException() {
        CerrarRequest request = new CerrarRequest();
        request.setObservacionCierre("Intento de cerrar solicitud ya cerrada.");

        when(solicitudRepository.findById(solicitudCerrada.getId()))
            .thenReturn(Optional.of(solicitudCerrada));

        assertThrows(ConflictException.class,
            () -> solicitudService.cerrar(solicitudCerrada.getId(), request, admin));
    }

    @Test
    @DisplayName("Cerrar solicitud REGISTRADA lanza ConflictException")
    void cerrar_estadoRegistrada_lanzaConflictException() {
        CerrarRequest request = new CerrarRequest();
        request.setObservacionCierre("Intento de cerrar solicitud registrada.");

        when(solicitudRepository.findById(solicitudId))
            .thenReturn(Optional.of(solicitudRegistrada));

        assertThrows(ConflictException.class,
            () -> solicitudService.cerrar(solicitudId, request, admin));
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE CONSULTA
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("ESTUDIANTE puede ver su propia solicitud")
    void obtener_estudiantePropiasSolicitud_exitoso() {
        when(solicitudRepository.findById(solicitudId))
            .thenReturn(Optional.of(solicitudRegistrada));

        SolicitudDTO resultado = solicitudService.obtener(solicitudId, estudiante);

        assertNotNull(resultado);
        assertEquals(solicitudId, resultado.getId());
    }

    @Test
    @DisplayName("ESTUDIANTE no puede ver solicitud de otro usuario")
    void obtener_estudianteAjenaSolicitud_lanzaForbiddenException() {
        Usuario otroEstudiante = new Usuario();
        otroEstudiante.setId(UUID.randomUUID());
        otroEstudiante.setRol(RolUsuario.ESTUDIANTE);
        otroEstudiante.setActivo(true);

        when(solicitudRepository.findById(solicitudId))
            .thenReturn(Optional.of(solicitudRegistrada));

        assertThrows(ForbiddenException.class,
            () -> solicitudService.obtener(solicitudId, otroEstudiante));
    }

    @Test
    @DisplayName("ADMIN puede ver cualquier solicitud")
    void obtener_admin_exitoso() {
        when(solicitudRepository.findById(solicitudId))
            .thenReturn(Optional.of(solicitudRegistrada));

        SolicitudDTO resultado = solicitudService.obtener(solicitudId, admin);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Solicitud inexistente lanza NotFoundException")
    void obtener_solicitudNoExiste_lanzaNotFoundException() {
        UUID idInexistente = UUID.randomUUID();
        when(solicitudRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> solicitudService.obtener(idInexistente, admin));
    }
}