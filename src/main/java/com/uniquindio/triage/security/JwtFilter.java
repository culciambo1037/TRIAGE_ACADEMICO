package com.uniquindio.triage.security;

import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extraer el header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Si no hay token o no empieza con Bearer → dejar pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token sin el prefijo "Bearer "
        String token = authHeader.substring(7);

        // 4. Validar el token
        if (!jwtUtil.validarToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Extraer el correo del token
        String correo = jwtUtil.extraerCorreo(token);

        // 6. Buscar el usuario en BD
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 7. Crear el objeto de autenticación y registrarlo en Spring Security
        UserDetailsImpl userDetails = new UserDetailsImpl(usuarioOpt.get());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 8. Continuar con la petición
        filterChain.doFilter(request, response);
    }
}