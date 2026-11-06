package br.ifsp.demo.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UnitTest")
@Tag("Structural")
class StatusIngressoStructuralTest {

    @Test
    @Tag("Mutation")
    void exponeOsEstadosDeIngressoNaOrdemDoContrato() {
        assertThat(StatusIngresso.values())
                .containsExactly(StatusIngresso.VENDIDO, StatusIngresso.CANCELADO);
        assertThat(StatusIngresso.valueOf("VENDIDO")).isEqualTo(StatusIngresso.VENDIDO);
        assertThat(StatusIngresso.valueOf("CANCELADO")).isEqualTo(StatusIngresso.CANCELADO);
    }
}
