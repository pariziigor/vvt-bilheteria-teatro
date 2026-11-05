package br.ifsp.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
@Tag("Structural")
class TipoIngressoStructuralTest {

    @Test
    @DisplayName("Rejeita categoria nula ou não suportada")
    void rejeitaCategoriaInvalida() {
        assertThatThrownBy(() -> new TipoIngresso(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TipoIngresso("CRIANÇA"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Retorna categoria e desconto de cada tipo aceito")
    void retornaCategoriaEDesconto() {
        assertTipo("INTEIRA", "0.00");
        assertTipo("ESTUDANTE", "0.50");
        assertTipo("PCD", "0.60");
        assertTipo("IDOSO", "0.65");
    }

    @Test
    @DisplayName("Compara a mesma instância como igual")
    void mesmaInstanciaEhIgual() {
        TipoIngresso tipo = new TipoIngresso("INTEIRA");

        assertThat(tipo).isEqualTo(tipo);
    }

    @Test
    @DisplayName("Compara tipos com a mesma categoria como iguais")
    void mesmaCategoriaEhIgual() {
        TipoIngresso primeiro = new TipoIngresso("ESTUDANTE");
        TipoIngresso segundo = new TipoIngresso("ESTUDANTE");

        assertThat(primeiro).isEqualTo(segundo);
        assertThat(primeiro.hashCode()).isEqualTo(segundo.hashCode());
    }

    @Test
    @DisplayName("Compara categorias diferentes, objeto de outro tipo e nulo como diferentes")
    void valoresDiferentesNaoSaoIguais() {
        TipoIngresso estudante = new TipoIngresso("ESTUDANTE");
        TipoIngresso inteira = new TipoIngresso("INTEIRA");

        assertThat(estudante).isNotEqualTo(inteira);
        assertThat(estudante).isNotEqualTo("ESTUDANTE");
        assertThat(estudante).isNotEqualTo(null);
    }

    private void assertTipo(String categoria, String desconto) {
        TipoIngresso tipo = new TipoIngresso(categoria);

        assertThat(tipo.getCategoria()).isEqualTo(categoria);
        assertThat(tipo.getPercentualDesconto()).isEqualByComparingTo(new BigDecimal(desconto));
    }
}
