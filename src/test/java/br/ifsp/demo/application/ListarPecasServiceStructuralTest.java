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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
}