package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.StatusIngresso;
import br.ifsp.demo.domain.TipoIngresso;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class ComprarIngressoServiceStructuralTest {

    @Mock
    private ISessaoRepository repository;

    @InjectMocks
    private ComprarIngressoService service;

    @Test
    @Tag("Mutation")
    void rejeitaSessaoOuCategoriaAusenteSemConsultarRepositorio() {
        assertThatThrownBy(() -> service.comprar(null, "INTEIRA"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.comprar(UUID.randomUUID(), null))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository);
    }

    @Test
    @Tag("Mutation")
    void informaQuandoSessaoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.comprar(id, "INTEIRA"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Sessão não encontrada");
        verify(repository, never()).salvar(any());
    }

    @Test
    @Tag("Mutation")
    void rejeitaSessaoEncerrada() {
        Sessao sessao = sessao(LocalDate.now().minusDays(1), 2);
        when(repository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> service.comprar(sessao.getId(), "INTEIRA"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A sessão já foi encerrada");
        verify(repository, never()).salvar(any());
    }

    @Test
    @Tag("Mutation")
    void compraIngressoDoCompradorERegistraOValorEDesconto() {
        Sessao sessao = sessao(LocalDate.now().plusDays(2), 2);
        when(repository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        Ingresso ingresso = service.comprar(sessao.getId(), "ESTUDANTE", "ana@email.com");

        assertThat(ingresso.getTipoIngresso()).isEqualTo(new TipoIngresso("ESTUDANTE"));
        assertThat(ingresso.getValor()).isEqualByComparingTo("25.00");
        assertThat(ingresso.getStatus()).isEqualTo(StatusIngresso.VENDIDO);
        assertThat(ingresso.getCompradorEmail()).isEqualTo("ana@email.com");
        assertThat(sessao.getIngressos()).containsExactly(ingresso);
        verify(repository).salvar(sessao);
    }


    private Sessao sessao(LocalDate data, int capacidade) {
        return new Sessao(UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(data, LocalTime.of(19, 0), LocalTime.of(21, 0)),
                capacidade, new BigDecimal("50.00"));
    }
}
