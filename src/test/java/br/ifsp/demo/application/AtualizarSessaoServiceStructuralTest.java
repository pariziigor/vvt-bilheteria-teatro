package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class AtualizarSessaoServiceStructuralTest {

    @Mock
    private ISessaoRepository sessaoRepository;
    @Mock
    private IPecaRepository pecaRepository;
    @InjectMocks
    private AtualizarSessaoService service;

    @Test
    @DisplayName("Rejeita atualização sem valor base do ingresso")
    void rejeitaAtualizacaoSemValorBase() {
        Sessao sessao = sessaoCadastrada();

        assertThatThrownBy(() -> service.atualizar(
                sessao.getId(), sessao.getPecaId(), sessao.getDataHora(), 100, null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sessao.getValorBaseIngresso()).isEqualByComparingTo("50.00");
        verify(sessaoRepository, never()).salvar(any());
    }

    // NOVOS TESTES ENTRAM AQUI

    private Sessao sessaoCadastrada() {
        Sessao sessao = new Sessao(
                UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0)),
                100, new BigDecimal("50.00"));
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));
        when(pecaRepository.existe(sessao.getPecaId())).thenReturn(true);
        return sessao;
    }
}