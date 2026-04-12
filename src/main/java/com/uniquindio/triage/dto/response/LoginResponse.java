package com.uniquindio.triage.dto.response;

import com.uniquindio.triage.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private RolUsuario rol;
    private String nombre;
}