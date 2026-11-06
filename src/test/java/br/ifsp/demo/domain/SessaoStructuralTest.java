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

    @Test
    @Tag("Mutation")
    void rejeitaValorBaseNaoPositivoECapacidadeNaoPositiva() {
        assertThatThrownBy(() -> sessao(1, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sessao(1, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sessao(1, new BigDecimal("-0.01")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sessao(0, valor()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sessao(-1, valor()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Tag("Mutation")
    void calculaDisponibilidadeConsiderandoSomenteIngressosVendidos() {
        Sessao sessao = sessao(2, valor());
        Ingresso vendido = ingresso(StatusIngresso.VENDIDO);
        Ingresso cancelado = ingresso(StatusIngresso.CANCELADO);

        assertThat(sessao.quantidadeIngressosVendidos()).isZero();
        assertThat(sessao.quantidadeIngressosDisponiveis()).isEqualTo(2);
        assertThat(sessao.estaLotada()).isFalse();

        sessao.registrarIngresso(vendido);
        sessao.registrarIngresso(cancelado);
        Ingresso segundoVendido = ingresso(StatusIngresso.VENDIDO);
        sessao.registrarIngresso(segundoVendido);

        assertThat(sessao.getIngressos()).containsExactly(vendido, cancelado, segundoVendido);
        assertThat(sessao.quantidadeIngressosVendidos()).isEqualTo(2);
        assertThat(sessao.getQuantidadeIngressosVendidos()).isEqualTo(2);
        assertThat(sessao.quantidadeIngressosDisponiveis()).isZero();
        assertThat(sessao.getQuantidadeIngressosDisponiveis()).isZero();
        assertThat(sessao.estaLotada()).isTrue();
        assertThatThrownBy(() -> sessao.getIngressos().clear())
                .isInstanceOf(UnsupportedOperationException.class);
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
