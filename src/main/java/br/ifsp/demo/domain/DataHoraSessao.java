package br.ifsp.demo.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public record DataHoraSessao(LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
    public DataHoraSessao {
        if (data == null || horaInicio == null || horaFim == null) {
            throw new IllegalArgumentException("Data e horários da sessão são obrigatórios");
        }
        if (!horaFim.isAfter(horaInicio)) {
            throw new IllegalArgumentException("O horário de término deve ser posterior ao horário de início");
        }
    }
}
