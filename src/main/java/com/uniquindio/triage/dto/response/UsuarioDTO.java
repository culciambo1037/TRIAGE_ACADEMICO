package com.uniquindio.triage.dto.response;

import com.uniquindio.triage.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UsuarioDTO {

    private UUID id;
    private String nombre;
    private String apellido;
    private String correo;
    private String identificacion;
    private RolUsuario rol;
    private Boolean activo;
}