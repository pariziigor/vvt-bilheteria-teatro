package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class BuscarSessaoServiceStructuralTest {

    @Mock
    private ISessaoRepository repository;

    @InjectMocks
    private BuscarSessaoService service;

    @Test
    @Tag("Mutation")
    void rejeitaIdentificadorAusenteSemConsultarRepositorio() {
        assertThatThrownBy(() -> service.buscar(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O identificador da sessão é obrigatório");
        verifyNoInteractions(repository);
    }

    @Test
    @Tag("Mutation")
    void retornaSessaoEncontrada() {
        UUID id = UUID.randomUUID();
        Sessao sessao = sessao(id);
        when(repository.buscarPorId(id)).thenReturn(Optional.of(sessao));

        assertThat(service.buscar(id)).isSameAs(sessao);
        verify(repository).buscarPorId(id);
    }


    private Sessao sessao(UUID id) {
        return new Sessao(id, UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0)),
                10, new BigDecimal("50.00"));
    }
}
