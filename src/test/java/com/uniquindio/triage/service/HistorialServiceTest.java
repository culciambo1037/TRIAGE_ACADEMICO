package com.uniquindio.triage.service;

import com.uniquindio.triage.dto.response.HistorialDTO;
import com.uniquindio.triage.entity.HistorialSolicitud;
import com.uniquindio.triage.entity.Solicitud;
import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.enums.*;
import com.uniquindio.triage.exception.NotFoundException;
import com.uniquindio.triage.repository.HistorialSolicitudRepository;
import com.uniquindio.triage.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de HistorialService")
class HistorialServiceTest {

    @Mock
    private HistorialSolicitudRepository historialRepository;

    @Mock
    private SolicitudRepository solicitudRepository;

    @InjectMocks
    private HistorialService historialService;

    private Usuario usuario;
    private Solicitud solicitud;
    private HistorialSolicitud historial;
    private UUID solicitudId;

    @BeforeEach
    void setUp() {
        solicitudId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNombre("César");
        usuario.setApellido("Giraldo");
        usuario.setRol(RolUsuario.ADMIN);
        usuario.setActivo(true);

        solicitud = new Solicitud();
        solicitud.setId(solicitudId);
        solicitud.setDescripcion("Solicitud de prueba para historial");
        solicitud.setEstado(EstadoSolicitud.REGISTRADA);
        solicitud.setTipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA);
        solicitud.setPrioridad(Prioridad.MEDIA);
        solicitud.setCanalOrigen(CanalOrigen.CSU);
        solicitud.setSolicitante(usuario);
        solicitud.setJustificacionPrioridad("Pendiente");
        solicitud.setFechaRegistro(LocalDateTime.now());

        historial = new HistorialSolicitud();
        historial.setId(UUID.randomUUID());
        historial.setSolicitud(solicitud);
        historial.setUsuario(usuario);
        historial.setAccionRealizada("Solicitud registrada");
        historial.setObservaciones("Canal: CSU");
        historial.setFechaAccion(LocalDateTime.now());
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE REGISTRO
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Registrar acción en historial exitosamente")
    void registrar_exitoso() {
        when(historialRepository.save(any(HistorialSolicitud.class)))
            .thenAnswer(i -> i.getArgument(0));

        historialService.registrar(solicitud, usuario, "Solicitud registrada", "Canal: CSU");

        verify(historialRepository, times(1)).save(any(HistorialSolicitud.class));
    }

    @Test
    @DisplayName("Registrar múltiples acciones genera múltiples entradas")
    void registrar_multiplesAcciones_generaMultiplesEntradas() {
        when(historialRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        historialService.registrar(solicitud, usuario, "Solicitud registrada", "Canal: CSU");
        historialService.registrar(solicitud, usuario, "Solicitud clasificada", "Tipo: CONSULTA");
        historialService.registrar(solicitud, usuario, "Solicitud cerrada", "Observacion cierre");

        verify(historialRepository, times(3)).save(any(HistorialSolicitud.class));
    }

    // ─────────────────────────────────────────────────────────
    // TESTS DE CONSULTA
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Obtener historial de solicitud existente exitosamente")
    void obtenerPorSolicitud_exitoso() {
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(historialRepository.findBySolicitudOrderByFechaAccionAsc(solicitud))
            .thenReturn(List.of(historial));

        List<HistorialDTO> resultado = historialService.obtenerPorSolicitud(solicitudId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Solicitud registrada", resultado.get(0).getAccionRealizada());
        assertEquals("Canal: CSU", resultado.get(0).getObservaciones());
        assertEquals("César Giraldo", resultado.get(0).getNombreUsuario());
    }

    @Test
    @DisplayName("Obtener historial de solicitud inexistente lanza NotFoundException")
    void obtenerPorSolicitud_solicitudNoExiste_lanzaNotFoundException() {
        UUID idInexistente = UUID.randomUUID();
        when(solicitudRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> historialService.obtenerPorSolicitud(idInexistente));
    }

    @Test
    @DisplayName("Historial vacío retorna lista vacía")
    void obtenerPorSolicitud_sinEntradas_retornaListaVacia() {
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(historialRepository.findBySolicitudOrderByFechaAccionAsc(solicitud))
            .thenReturn(List.of());

        List<HistorialDTO> resultado = historialService.obtenerPorSolicitud(solicitudId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Historial se ordena cronológicamente")
    void obtenerPorSolicitud_ordenCronologico_correcto() {
        HistorialSolicitud historial2 = new HistorialSolicitud();
        historial2.setId(UUID.randomUUID());
        historial2.setSolicitud(solicitud);
        historial2.setUsuario(usuario);
        historial2.setAccionRealizada("Solicitud clasificada");
        historial2.setObservaciones("Tipo: CONSULTA_ACADEMICA");
        historial2.setFechaAccion(LocalDateTime.now().plusMinutes(5));

        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(historialRepository.findBySolicitudOrderByFechaAccionAsc(solicitud))
            .thenReturn(List.of(historial, historial2));

        List<HistorialDTO> resultado = historialService.obtenerPorSolicitud(solicitudId);

        assertEquals(2, resultado.size());
        assertEquals("Solicitud registrada", resultado.get(0).getAccionRealizada());
        assertEquals("Solicitud clasificada", resultado.get(1).getAccionRealizada());
    }
}