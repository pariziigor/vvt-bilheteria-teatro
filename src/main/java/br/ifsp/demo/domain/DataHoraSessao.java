package br.ifsp.demo.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public record DataHoraSessao(LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
}
