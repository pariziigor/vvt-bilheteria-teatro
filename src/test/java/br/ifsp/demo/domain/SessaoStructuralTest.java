package br.ifsp.demo.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
@Tag("Structural")
class SessaoStructuralTest {

    @Test
    @Tag("Mutation")
    void rejeitaDadosObrigatoriosAusentesNaCriacao() {
        UUID id = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        DataHoraSessao dataHora = dataHoraFutura();

        assertThatThrownBy(() -> new Sessao(null, pecaId, dataHora, 1, valor()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Sessao(id, null, dataHora, 1, valor()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Sessao(id, pecaId, null, 1, valor()))
                .isInstanceOf(NullPointerException.class);
    }







    private Sessao sessao(int capacidade, BigDecimal valorBase) {
        return new Sessao(UUID.randomUUID(), UUID.randomUUID(), dataHoraFutura(), capacidade, valorBase);
    }

    private DataHoraSessao dataHoraFutura() {
        return new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0));
    }

    private Ingresso ingresso(StatusIngresso status) {
        return ingresso(UUID.randomUUID(), status);
    }

    private Ingresso ingresso(UUID id, StatusIngresso status) {
        return new Ingresso(id, new TipoIngresso("INTEIRA"), valor(), status);
    }

    private BigDecimal valor() {
        return new BigDecimal("50.00");
    }
}
