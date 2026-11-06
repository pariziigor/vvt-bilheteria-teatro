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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class ListarSessoesServiceStructuralTest {

    @Mock
    private ISessaoRepository repository;

    @InjectMocks
    private ListarSessoesService service;

    @Test
    @Tag("Mutation")
    void devolveCopiaImutavelDaListaDoRepositorio() {
        List<Sessao> sessoes = List.of(sessao(), sessao());
        when(repository.listar()).thenReturn(sessoes);

        List<Sessao> resultado = service.listar();

        assertThat(resultado).containsExactlyElementsOf(sessoes);
        assertThatThrownBy(resultado::clear).isInstanceOf(UnsupportedOperationException.class);
        verify(repository).listar();
    }

    private Sessao sessao() {
        return new Sessao(UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0)),
                10, new BigDecimal("50.00"));
    }
}
