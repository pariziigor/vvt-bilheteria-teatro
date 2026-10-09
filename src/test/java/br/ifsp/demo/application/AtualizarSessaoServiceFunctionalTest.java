package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.TipoIngresso;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Functional")
class AtualizarSessaoServiceFunctionalTest {

    @Mock
    private ISessaoRepository sessaoRepository;
    @Mock
    private IPecaRepository pecaRepository;
    @InjectMocks
    private AtualizarSessaoService service;

    @BeforeEach
    void setUp() {
        lenient().when(pecaRepository.existe(any())).thenReturn(true);
    }

    @Test
    @DisplayName("Aceita capacidade imediatamente acima da quantidade de ingressos vendidos")
    void aceitaCapacidadeUmAcimaDosIngressosVendidos() {
        Sessao sessao = sessaoCadastrada();
        venderIngresso(sessao);
        venderIngresso(sessao);
        venderIngresso(sessao);

        atualizar(sessao, sessao.getPecaId(), sessao.getDataHora(), 4);

        assertThat(sessao.getCapacidade()).isEqualTo(4);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("Ingresso cancelado não conta no limite mínimo de capacidade")
    void ingressoCanceladoNaoContaNoLimiteDeCapacidade() {
        Sessao sessao = sessaoCadastrada();
        Ingresso cancelado = venderIngresso(sessao);
        venderIngresso(sessao);
        venderIngresso(sessao);
        sessao.cancelarIngresso(cancelado.getId());

        atualizar(sessao, sessao.getPecaId(), sessao.getDataHora(), 2);

        assertThat(sessao.getCapacidade()).isEqualTo(2);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("Rejeita sessão para hoje em horário que já passou")
    void rejeitaSessaoParaHojeEmHorarioJaPassado() {
        Sessao sessao = sessaoCadastrada();
        DataHoraSessao hojeNoPassado = new DataHoraSessao(LocalDate.now(), LocalTime.MIN, LocalTime.of(1, 0));

        assertThatThrownBy(() -> atualizar(sessao, sessao.getPecaId(), hojeNoPassado, 100))
                .isInstanceOf(IllegalArgumentException.class);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Rejeita data e hora sem horário de início")
    void rejeitaDataHoraSemHorarioDeInicio() {
        Sessao sessao = sessaoCadastrada();
        DataHoraSessao semInicio = new DataHoraSessao(LocalDate.now().plusDays(5), null, LocalTime.of(21, 0));

        assertThatThrownBy(() -> atualizar(sessao, sessao.getPecaId(), semInicio, 100))
                .isInstanceOf(IllegalArgumentException.class);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Rejeita data e hora sem a data")
    void rejeitaDataHoraSemData() {
        Sessao sessao = sessaoCadastrada();
        DataHoraSessao semData = new DataHoraSessao(null, LocalTime.of(19, 0), LocalTime.of(21, 0));

        assertThatThrownBy(() -> atualizar(sessao, sessao.getPecaId(), semData, 100))
                .isInstanceOf(IllegalArgumentException.class);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Rejeita atualização sem peça associada")
    void rejeitaAtualizacaoSemPecaAssociada() {
        Sessao sessao = sessaoCadastrada();
        UUID pecaOriginal = sessao.getPecaId();

        assertThatThrownBy(() -> atualizar(sessao, null, sessao.getDataHora(), 100))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sessao.getPecaId()).isEqualTo(pecaOriginal);
        verify(sessaoRepository, never()).salvar(any());
    }

    private Sessao sessaoCadastrada() {
        Sessao sessao = new Sessao(
                UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0)),
                100, new BigDecimal("50.00"));
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));
        return sessao;
    }

    private Ingresso venderIngresso(Sessao sessao) {
        Ingresso ingresso = new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"), new BigDecimal("50.00"));
        sessao.registrarIngresso(ingresso);
        return ingresso;
    }

    private void atualizar(Sessao sessao, UUID pecaId, DataHoraSessao dataHora, int capacidade) {
        service.atualizar(sessao.getId(), pecaId, dataHora, capacidade, sessao.getValorBaseIngresso());
    }
}