package com.uniquindio.triage.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de JwtUtil")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    @DisplayName("Generar token JWT exitosamente")
    void generarToken_exitoso() {
        String token = jwtUtil.generarToken("cesar@uqvirtual.edu.co", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Extraer correo del token correctamente")
    void extraerCorreo_exitoso() {
        String correo = "cesar@uqvirtual.edu.co";
        String token = jwtUtil.generarToken(correo, "ADMIN");

        String correoExtraido = jwtUtil.extraerCorreo(token);

        assertEquals(correo, correoExtraido);
    }

    @Test
    @DisplayName("Extraer rol del token correctamente")
    void extraerRol_exitoso() {
        String token = jwtUtil.generarToken("cesar@uqvirtual.edu.co", "RESPONSABLE");

        String rol = jwtUtil.extraerRol(token);

        assertEquals("RESPONSABLE", rol);
    }

    @Test
    @DisplayName("Validar token válido retorna true")
    void validarToken_valido_retornaTrue() {
        String token = jwtUtil.generarToken("cesar@uqvirtual.edu.co", "ADMIN");

        assertTrue(jwtUtil.validarToken(token));
    }

    @Test
    @DisplayName("Validar token inválido retorna false")
    void validarToken_invalido_retornaFalse() {
        assertFalse(jwtUtil.validarToken("tokencompletamenteinvalido"));
    }

    @Test
    @DisplayName("Validar token vacío retorna false")
    void validarToken_vacio_retornaFalse() {
        assertFalse(jwtUtil.validarToken(""));
    }

    @Test
    @DisplayName("Tokens generados para distintos usuarios son diferentes")
    void generarToken_usuariosDiferentes_tokensDistintos() {
        String token1 = jwtUtil.generarToken("usuario1@uq.edu.co", "ADMIN");
        String token2 = jwtUtil.generarToken("usuario2@uq.edu.co", "ESTUDIANTE");

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Token modificado es inválido")
    void validarToken_modificado_retornaFalse() {
        String token = jwtUtil.generarToken("cesar@uqvirtual.edu.co", "ADMIN");
        String tokenModificado = token.substring(0, token.length() - 5) + "XXXXX";

        assertFalse(jwtUtil.validarToken(tokenModificado));
    }
}