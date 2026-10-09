package br.ifsp.demo.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CriarSessaoRequest(
        UUID pecaId,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        int capacidade,
        BigDecimal valorBaseIngresso
) {
}
