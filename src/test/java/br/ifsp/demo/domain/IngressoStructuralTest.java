package br.ifsp.demo.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
@Tag("Structural")
class IngressoStructuralTest {

    @Test
    @Tag("Mutation")
    void construtoresPreservamDadosEValoresPadrao() {
        UUID id = UUID.randomUUID();
        TipoIngresso tipo = new TipoIngresso("ESTUDANTE");
        BigDecimal valor = new BigDecimal("25.00");

        Ingresso vendido = new Ingresso(id, tipo, valor);
        assertThat(vendido.getId()).isEqualTo(id);
        assertThat(vendido.getTipoIngresso()).isEqualTo(tipo);
        assertThat(vendido.getValor()).isEqualByComparingTo(valor);
        assertThat(vendido.getStatus()).isEqualTo(StatusIngresso.VENDIDO);
        assertThat(vendido.getCompradorEmail()).isNull();

        Ingresso cancelado = new Ingresso(id, tipo, valor, StatusIngresso.CANCELADO);
        assertThat(cancelado.getStatus()).isEqualTo(StatusIngresso.CANCELADO);
        assertThat(cancelado.getCompradorEmail()).isNull();

        Ingresso associado = new Ingresso(id, tipo, valor, StatusIngresso.VENDIDO, "cliente@email.com");
        assertThat(associado.getCompradorEmail()).isEqualTo("cliente@email.com");
    }


    private Ingresso ingresso(StatusIngresso status) {
        return new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"), new BigDecimal("50.00"), status);
    }
}
