package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.StatusIngresso;
import br.ifsp.demo.domain.TipoIngresso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("TDD")
class CancelarIngressoServiceTest {

    @Mock
    private ISessaoRepository sessaoRepository;
    @InjectMocks
    private CancelarIngressoService service;

    @Test
    @DisplayName("[7.1] Cancela um ingresso vendido")
    void cancelaIngressoVendido() {
        Sessao sessao = sessao(10);
        Ingresso ingresso = ingresso();
        sessao.registrarIngresso(ingresso);
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        service.cancelar(sessao.getId(), ingresso.getId());

        assertThat(ingresso.getStatus()).isEqualTo(StatusIngresso.CANCELADO);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[7.2] Cancela ingresso de uma sessão lotada")
    void cancelaIngressoDeSessaoLotada() {
        Sessao sessao = sessao(1);
        Ingresso ingresso = ingresso();
        sessao.registrarIngresso(ingresso);
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        service.cancelar(sessao.getId(), ingresso.getId());

        assertThat(ingresso.getStatus()).isEqualTo(StatusIngresso.CANCELADO);
        assertThat(sessao.getIngressos()).filteredOn(i -> i.getStatus() == StatusIngresso.VENDIDO).isEmpty();
        Ingresso novoIngresso = ingresso();
        sessao.registrarIngresso(novoIngresso);
        assertThat(novoIngresso.getStatus()).isEqualTo(StatusIngresso.VENDIDO);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[7.3] Cancela um ingresso e preserva os demais vendidos")
    void cancelaUmIngressoEntreVariosVendidos() {
        Sessao sessao = sessao(5);
        Ingresso primeiro = ingresso();
        Ingresso segundo = ingresso();
        Ingresso terceiro = ingresso();
        sessao.registrarIngresso(primeiro);
        sessao.registrarIngresso(segundo);
        sessao.registrarIngresso(terceiro);
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        service.cancelar(sessao.getId(), segundo.getId());

        assertThat(primeiro.getStatus()).isEqualTo(StatusIngresso.VENDIDO);
        assertThat(segundo.getStatus()).isEqualTo(StatusIngresso.CANCELADO);
        assertThat(terceiro.getStatus()).isEqualTo(StatusIngresso.VENDIDO);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[7.4] Rejeita o cancelamento de um ingresso já cancelado")
    void rejeitaCancelamentoDeIngressoJaCancelado() {
        Sessao sessao = sessao(10);
        Ingresso ingresso = ingresso();
        sessao.registrarIngresso(ingresso);
        sessao.cancelarIngresso(ingresso.getId());
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> service.cancelar(sessao.getId(), ingresso.getId()))
                .isInstanceOf(IllegalStateException.class);

        assertThat(ingresso.getStatus()).isEqualTo(StatusIngresso.CANCELADO);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("[7.5] Informa que o ingresso não foi encontrado e não cancela nada")
    void informaQueIngressoNaoFoiEncontrado() {
        Sessao sessao = sessao(10);
        Ingresso vendido = ingresso();
        sessao.registrarIngresso(vendido);
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> service.cancelar(sessao.getId(), UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Ingresso não encontrado na sessão");

        assertThat(vendido.getStatus()).isEqualTo(StatusIngresso.VENDIDO);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("[7.6] Recusa cancelamento sem identificador do ingresso")
    void recusaCancelamentoSemIdentificadorDoIngresso() {
        Sessao sessao = sessao(10);

        assertThatThrownBy(() -> service.cancelar(sessao.getId(), null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sessao.getIngressos()).isEmpty();
        verify(sessaoRepository, never()).buscarPorId(any());
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("[7.7] Atualiza a disponibilidade após cancelamento")
    void atualizaDisponibilidadeAposCancelamento() {
        Sessao sessao = sessao(5);
        Ingresso cancelado = ingresso();
        Ingresso restante = ingresso();
        sessao.registrarIngresso(cancelado);
        sessao.registrarIngresso(restante);
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        service.cancelar(sessao.getId(), cancelado.getId());

        assertThat(sessao.getIngressos()).filteredOn(i -> i.getStatus() == StatusIngresso.VENDIDO).hasSize(1);
        assertThat(sessao.getIngressos()).filteredOn(i -> i.getStatus() == StatusIngresso.CANCELADO).hasSize(1);
        verify(sessaoRepository).salvar(sessao);
    }

    private Sessao sessao(int capacidade) {
        return new Sessao(
                UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0)),
                capacidade, new BigDecimal("50.00"));
    }

    private Ingresso ingresso() {
        return new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"), new BigDecimal("50.00"));
    }
}
