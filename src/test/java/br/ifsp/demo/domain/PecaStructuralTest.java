package br.ifsp.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
@Tag("Structural")
class PecaStructuralTest {

    @Test
    @DisplayName("Cria peça com dados válidos")
    void criaPecaComDadosValidos() {
        UUID id = UUID.randomUUID();
        Duration duracao = Duration.ofMinutes(95);

        Peca peca = new Peca(id, "Hamlet", "Uma tragédia", duracao, "14 anos");

        assertThat(peca.getId()).isEqualTo(id);
        assertThat(peca.getTitulo()).isEqualTo("Hamlet");
        assertThat(peca.getDescricao()).isEqualTo("Uma tragédia");
        assertThat(peca.getDuracao()).isEqualTo(duracao);
        assertThat(peca.getClassificacao()).isEqualTo("14 anos");
    }

    @Test
    @DisplayName("Rejeita identificador nulo")
    void rejeitaIdentificadorNulo() {
        assertThatThrownBy(() -> new Peca(null, "Hamlet", "Uma tragédia", Duration.ofMinutes(95), "14 anos"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Rejeita título nulo ou em branco")
    void rejeitaTituloNuloOuEmBranco() {
        assertPecaInvalida(null, "Uma tragédia", "14 anos", Duration.ofMinutes(95));
        assertPecaInvalida("   ", "Uma tragédia", "14 anos", Duration.ofMinutes(95));
    }

    @Test
    @DisplayName("Rejeita descrição nula ou em branco")
    void rejeitaDescricaoNulaOuEmBranco() {
        assertPecaInvalida("Hamlet", null, "14 anos", Duration.ofMinutes(95));
        assertPecaInvalida("Hamlet", "   ", "14 anos", Duration.ofMinutes(95));
    }

    @Test
    @DisplayName("Rejeita classificação nula ou em branco")
    void rejeitaClassificacaoNulaOuEmBranco() {
        assertPecaInvalida("Hamlet", "Uma tragédia", null, Duration.ofMinutes(95));
        assertPecaInvalida("Hamlet", "Uma tragédia", "   ", Duration.ofMinutes(95));
    }

    @Test
    @DisplayName("Rejeita duração nula, zero ou negativa")
    void rejeitaDuracaoInvalida() {
        assertPecaInvalida("Hamlet", "Uma tragédia", "14 anos", null);
        assertPecaInvalida("Hamlet", "Uma tragédia", "14 anos", Duration.ZERO);
        assertPecaInvalida("Hamlet", "Uma tragédia", "14 anos", Duration.ofMinutes(-1));
    }

    private void assertPecaInvalida(String titulo, String descricao, String classificacao, Duration duracao) {
        assertThatThrownBy(() -> new Peca(UUID.randomUUID(), titulo, descricao, duracao, classificacao))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
