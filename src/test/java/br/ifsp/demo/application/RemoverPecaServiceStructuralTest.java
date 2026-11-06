package br.ifsp.demo.application;

import br.ifsp.demo.domain.IPecaRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class RemoverPecaServiceStructuralTest {

    @Mock
    private IPecaRepository repository;

    @InjectMocks
    private RemoverPecaService service;

    @Test
    @Tag("Mutation")
    void rejeitaPecaInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.existe(id)).thenReturn(false);

        assertThatThrownBy(() -> service.remover(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Peça não encontrada");

        verify(repository).existe(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @Tag("Mutation")
    void rejeitaPecaQueAindaPossuiSessoes() {
        UUID id = UUID.randomUUID();
        when(repository.existe(id)).thenReturn(true);
        when(repository.possuiSessoes(id)).thenReturn(true);

        assertThatThrownBy(() -> service.remover(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Remova as sessões desta peça antes de removê-la.");

        verify(repository, never()).remover(id);
    }

}
