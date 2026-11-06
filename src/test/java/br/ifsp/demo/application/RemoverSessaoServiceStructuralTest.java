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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class RemoverSessaoServiceStructuralTest {

    @Mock
    private ISessaoRepository repository;

    @InjectMocks
    private RemoverSessaoService service;

    @Test
    @Tag("Mutation")
    void rejeitaIdentificadorAusenteSemConsultarRepositorio() {
        assertThatThrownBy(() -> service.remover(null))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository);
    }

    @Test
    @Tag("Mutation")
    void informaQuandoSessaoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remover(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Sessão não encontrada");
        verify(repository, never()).remover(any());
    }

    @Test
    @Tag("Mutation")
    void removeSessaoSemIngressosVendidosIncluindoApenasCancelados() {
        Sessao semIngressos = sessao();
        when(repository.buscarPorId(semIngressos.getId())).thenReturn(Optional.of(semIngressos));
        service.remover(semIngressos.getId());
        verify(repository).remover(semIngressos.getId());

        Sessao soComIngressoCancelado = sessao();
        soComIngressoCancelado.registrarIngresso(ingresso(StatusIngresso.CANCELADO));
        when(repository.buscarPorId(soComIngressoCancelado.getId())).thenReturn(Optional.of(soComIngressoCancelado));
        service.remover(soComIngressoCancelado.getId());
        verify(repository).remover(soComIngressoCancelado.getId());
    }

    @Test
    @Tag("Mutation")
    void recusaRemoverSessaoComIngressoVendido() {
        Sessao sessao = sessao();
        sessao.registrarIngresso(ingresso(StatusIngresso.VENDIDO));
        when(repository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> service.remover(sessao.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Não é possível remover uma sessão com ingressos vendidos");
        verify(repository, never()).remover(any());
    }

    private Sessao sessao() {
        return new Sessao(UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0)),
                10, new BigDecimal("50.00"));
    }

    private Ingresso ingresso(StatusIngresso status) {
        return new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"), new BigDecimal("50.00"), status);
    }
}
