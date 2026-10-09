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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("TDD")
class CriarSessaoServiceTest {

    @Mock
    private ISessaoRepository sessaoRepository;
    @Mock
    private IPecaRepository pecaRepository;
    @InjectMocks
    private CriarSessaoService service;

    @Test
    @DisplayName("[1.1] Cria sessão com dados válidos")
    void criaSessaoComDadosValidos() {
        UUID pecaId = UUID.randomUUID();
        DataHoraSessao dataHora = dataHoraFutura();
        when(pecaRepository.existe(pecaId)).thenReturn(true);

        Sessao sessao = service.criar(pecaId, dataHora, 100, new BigDecimal("50.00"));

        assertThat(sessao.getId()).isNotNull();
        assertThat(sessao.getPecaId()).isEqualTo(pecaId);
        assertThat(sessao.getDataHora()).isEqualTo(dataHora);
        assertThat(sessao.getCapacidade()).isEqualTo(100);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[1.2] Cria sessão com capacidade mínima válida")
    void criaSessaoComCapacidadeMinimaValida() {
        UUID pecaId = UUID.randomUUID();
        when(pecaRepository.existe(pecaId)).thenReturn(true);

        Sessao sessao = service.criar(pecaId, dataHoraFutura(), 1, new BigDecimal("50.00"));

        assertThat(sessao.getCapacidade()).isEqualTo(1);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[1.3] Rejeita capacidade igual a zero")
    void rejeitaCapacidadeIgualAZero() {
        assertThatThrownBy(() -> service.criar(UUID.randomUUID(), dataHoraFutura(), 0, new BigDecimal("50.00")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(sessaoRepository, never()).salvar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("[1.4] Rejeita capacidade negativa")
    void rejeitaCapacidadeNegativa() {
        assertThatThrownBy(() -> service.criar(UUID.randomUUID(), dataHoraFutura(), -1, new BigDecimal("50.00")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(sessaoRepository, never()).salvar(org.mockito.ArgumentMatchers.any());
    }

    private DataHoraSessao dataHoraFutura() {
        return new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0));
    }
}
