package br.ifsp.demo.application;

import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.Peca;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class ListarPecasServiceStructuralTest {

    @Mock
    private IPecaRepository pecaRepository;
    @InjectMocks
    private ListarPecasService service;

    @Test
    @DisplayName("Lista as peças cadastradas")
    void listaPecasCadastradas() {
        Peca primeira = new Peca(UUID.randomUUID(), "Hamlet", "Drama", Duration.ofMinutes(150), "14 anos");
        Peca segunda = new Peca(UUID.randomUUID(), "O Rei Leão", "Musical", Duration.ofMinutes(120), "Livre");
        when(pecaRepository.listar()).thenReturn(List.of(primeira, segunda));

        List<Peca> pecas = service.listar();

        assertThat(pecas).containsExactly(primeira, segunda);
    }

    @Test
    @DisplayName("Retorna lista vazia quando não há peças cadastradas")
    void retornaListaVaziaQuandoNaoHaPecas() {
        when(pecaRepository.listar()).thenReturn(List.of());

        assertThat(service.listar()).isEmpty();
    }

    @Test
    @DisplayName("Retorna uma cópia imutável da lista de peças")
    void retornaCopiaImutavelDaLista() {
        Peca peca = new Peca(UUID.randomUUID(), "Hamlet", "Drama", Duration.ofMinutes(150), "14 anos");
        List<Peca> origem = new ArrayList<>(List.of(peca));
        when(pecaRepository.listar()).thenReturn(origem);

        List<Peca> resultado = service.listar();
        origem.clear();

        assertThat(resultado).containsExactly(peca);
        assertThatThrownBy(() -> resultado.add(peca))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
