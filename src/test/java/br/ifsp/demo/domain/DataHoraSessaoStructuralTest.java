package br.ifsp.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
@Tag("Structural")
class DataHoraSessaoStructuralTest {

    private static final LocalDate DATA = LocalDate.of(2027, 5, 20);
    private static final LocalTime INICIO = LocalTime.of(19, 0);
    private static final LocalTime FIM = LocalTime.of(21, 0);

    @Test
    @DisplayName("Cria data e horários válidos")
    void criaDataHoraValida() {
        DataHoraSessao dataHora = new DataHoraSessao(DATA, INICIO, FIM);

        assertThat(dataHora.data()).isEqualTo(DATA);
        assertThat(dataHora.horaInicio()).isEqualTo(INICIO);
        assertThat(dataHora.horaFim()).isEqualTo(FIM);
    }

    @Test
    @DisplayName("Rejeita data nula")
    void rejeitaDataNula() {
        assertThatThrownBy(() -> new DataHoraSessao(null, INICIO, FIM))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rejeita horário de início nulo")
    void rejeitaHorarioInicioNulo() {
        assertThatThrownBy(() -> new DataHoraSessao(DATA, null, FIM))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rejeita horário de término nulo")
    void rejeitaHorarioFimNulo() {
        assertThatThrownBy(() -> new DataHoraSessao(DATA, INICIO, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rejeita término igual ao início")
    void rejeitaTerminoIgualAoInicio() {
        assertThatThrownBy(() -> new DataHoraSessao(DATA, INICIO, INICIO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rejeita término anterior ao início")
    void rejeitaTerminoAnteriorAoInicio() {
        assertThatThrownBy(() -> new DataHoraSessao(DATA, FIM, INICIO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
