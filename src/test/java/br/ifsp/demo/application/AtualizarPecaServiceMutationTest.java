package br.ifsp.demo.application;

import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.Peca;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
class AtualizarPecaServiceMutationTest {

    @Mock
    private IPecaRepository pecaRepository;

    @Test
    @Tag("Mutation")
    void atualizaEDevolvePecaEncontrada() {
        UUID id = UUID.randomUUID();
        Peca peca = new Peca(id, "Antigo", "Sinopse antiga", Duration.ofMinutes(80), "L");
        when(pecaRepository.buscarPorId(id)).thenReturn(Optional.of(peca));
        AtualizarPecaService service = new AtualizarPecaService(pecaRepository);

        Peca atualizada = service.atualizar(id, "Novo", "Sinopse nova", Duration.ofMinutes(95), "10");

        assertThat(atualizada).isSameAs(peca);
        assertThat(atualizada.getTitulo()).isEqualTo("Novo");
        assertThat(atualizada.getDescricao()).isEqualTo("Sinopse nova");
        assertThat(atualizada.getDuracao()).isEqualTo(Duration.ofMinutes(95));
        assertThat(atualizada.getClassificacao()).isEqualTo("10");
        verify(pecaRepository).atualizar(peca);
    }

}
