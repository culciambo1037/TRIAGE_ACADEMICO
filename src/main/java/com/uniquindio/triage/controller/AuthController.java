package com.uniquindio.triage.controller;

import com.uniquindio.triage.dto.request.LoginRequest;
import com.uniquindio.triage.dto.response.LoginResponse;
import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.security.JwtUtil;
import com.uniquindio.triage.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        // 1. Verificar credenciales
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getCorreo(),
                request.getPassword()
            )
        );

        // 2. Obtener el usuario autenticado
        Usuario usuario = usuarioService.buscarPorCorreo(request.getCorreo());

        // 3. Generar el token JWT
        String token = jwtUtil.generarToken(
            usuario.getCorreo(),
            usuario.getRol().name()
        );

        // 4. Devolver token + rol + nombre
        return ResponseEntity.ok(new LoginResponse(
            token,
            usuario.getRol(),
            usuario.getNombre() + " " + usuario.getApellido()
        ));
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Con JWT stateless el logout es responsabilidad del cliente
        // El cliente simplemente elimina el token
        return ResponseEntity.ok().body(
            java.util.Map.of("mensaje", "Sesión cerrada exitosamente")
        );
    }
}