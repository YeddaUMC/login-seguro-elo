package com.pfc.thindesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChamadoRequest(
    @NotBlank @Size(max = 500) String descricao,
    @NotBlank String status,
    @NotBlank @Size(max = 80) String tipo,
    @Size(max = 120) String tecnico,
    @Size(max = 120) String usuario,
    String prioridade,
    @Size(max = 3000) String consideracoesTecnicas
) {}
