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
import java.util.NoSuchElementException;
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
@Tag("TDD")
class AtualizarSessaoServiceTest {

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
    @DisplayName("[4.1] Atualiza sessão com dados válidos")
    void atualizaSessaoComDadosValidos() {
        Sessao sessao = sessaoCadastrada();
        UUID novaPecaId = UUID.randomUUID();
        DataHoraSessao novaDataHora = dataHoraFutura(60);

        Sessao atualizada = service.atualizar(
                sessao.getId(), novaPecaId, novaDataHora, 150, new BigDecimal("80.00"));

        assertThat(atualizada.getPecaId()).isEqualTo(novaPecaId);
        assertThat(atualizada.getDataHora()).isEqualTo(novaDataHora);
        assertThat(atualizada.getCapacidade()).isEqualTo(150);
        assertThat(atualizada.getValorBaseIngresso()).isEqualByComparingTo("80.00");
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[4.2] Atualiza a capacidade para o valor mínimo válido")
    void atualizaCapacidadeParaValorMinimoValido() {
        Sessao sessao = sessaoCadastrada();

        atualizarCapacidade(sessao, 1);

        assertThat(sessao.getCapacidade()).isEqualTo(1);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[4.3] Rejeita atualização da capacidade para zero")
    void rejeitaCapacidadeIgualAZero() {
        Sessao sessao = sessaoCadastrada();

        assertThatThrownBy(() -> atualizarCapacidade(sessao, 0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sessao.getCapacidade()).isEqualTo(100);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("[4.4] Rejeita atualização da capacidade para valor negativo")
    void rejeitaCapacidadeNegativa() {
        Sessao sessao = sessaoCadastrada();

        assertThatThrownBy(() -> atualizarCapacidade(sessao, -1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sessao.getCapacidade()).isEqualTo(100);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("[4.5] Atualiza a capacidade para quantidade igual aos ingressos vendidos")
    void atualizaCapacidadeIgualAosIngressosVendidos() {
        Sessao sessao = sessaoCadastrada();
        venderIngressos(sessao, 3);

        atualizarCapacidade(sessao, 3);

        assertThat(sessao.getCapacidade()).isEqualTo(3);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[4.6] Rejeita capacidade menor que a quantidade de ingressos vendidos")
    void rejeitaCapacidadeMenorQueIngressosVendidos() {
        Sessao sessao = sessaoCadastrada();
        venderIngressos(sessao, 3);

        assertThatThrownBy(() -> atualizarCapacidade(sessao, 2))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sessao.getCapacidade()).isEqualTo(100);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("[4.7] Atualiza a data e hora para um momento futuro válido")
    void atualizaDataHoraParaMomentoFuturo() {
        Sessao sessao = sessaoCadastrada();
        DataHoraSessao novaDataHora = dataHoraFutura(90);

        atualizarDataHora(sessao, novaDataHora);

        assertThat(sessao.getDataHora()).isEqualTo(novaDataHora);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[4.8] Rejeita atualização para data e hora passadas")
    void rejeitaDataHoraPassada() {
        Sessao sessao = sessaoCadastrada();
        DataHoraSessao dataHoraOriginal = sessao.getDataHora();

        assertThatThrownBy(() -> atualizarDataHora(sessao, dataHoraFutura(-1)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sessao.getDataHora()).isEqualTo(dataHoraOriginal);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("[4.9] Rejeita atualização sem data e hora")
    void rejeitaAtualizacaoSemDataHora() {
        Sessao sessao = sessaoCadastrada();
        DataHoraSessao dataHoraOriginal = sessao.getDataHora();

        assertThatThrownBy(() -> atualizarDataHora(sessao, null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sessao.getDataHora()).isEqualTo(dataHoraOriginal);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("[4.10] Atualiza a peça associada para outra peça existente")
    void atualizaPecaAssociadaParaOutraPecaExistente() {
        Sessao sessao = sessaoCadastrada();
        UUID novaPecaId = UUID.randomUUID();

        atualizarPeca(sessao, novaPecaId);

        assertThat(sessao.getPecaId()).isEqualTo(novaPecaId);
        verify(sessaoRepository).salvar(sessao);
    }

    @Test
    @DisplayName("[4.11] Rejeita atualização para uma peça inexistente")
    void rejeitaPecaInexistente() {
        Sessao sessao = sessaoCadastrada();
        UUID pecaOriginal = sessao.getPecaId();
        UUID pecaInexistente = UUID.randomUUID();
        when(pecaRepository.existe(pecaInexistente)).thenReturn(false);

        assertThatThrownBy(() -> atualizarPeca(sessao, pecaInexistente))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sessao.getPecaId()).isEqualTo(pecaOriginal);
        verify(sessaoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("[4.12] Informa que a sessão não foi encontrada e não atualiza nada")
    void informaQueSessaoNaoFoiEncontrada() {
        UUID sessaoInexistente = UUID.randomUUID();
        when(sessaoRepository.buscarPorId(sessaoInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(
                sessaoInexistente, UUID.randomUUID(), dataHoraFutura(30), 100, new BigDecimal("50.00")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Sessão não encontrada");

        verify(sessaoRepository, never()).salvar(any());
    }

    private Sessao sessaoCadastrada() {
        Sessao sessao = new Sessao(
                UUID.randomUUID(), UUID.randomUUID(), dataHoraFutura(30), 100, new BigDecimal("50.00"));
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));
        return sessao;
    }

    private DataHoraSessao dataHoraFutura(int dias) {
        return new DataHoraSessao(LocalDate.now().plusDays(dias), LocalTime.of(19, 0), LocalTime.of(21, 0));
    }

    private void venderIngressos(Sessao sessao, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            sessao.registrarIngresso(
                    new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"), new BigDecimal("50.00")));
        }
    }

    private void atualizarCapacidade(Sessao sessao, int novaCapacidade) {
        service.atualizar(sessao.getId(), sessao.getPecaId(), sessao.getDataHora(),
                novaCapacidade, sessao.getValorBaseIngresso());
    }

    private void atualizarDataHora(Sessao sessao, DataHoraSessao novaDataHora) {
        service.atualizar(sessao.getId(), sessao.getPecaId(), novaDataHora,
                sessao.getCapacidade(), sessao.getValorBaseIngresso());
    }

    private void atualizarPeca(Sessao sessao, UUID novaPecaId) {
        service.atualizar(sessao.getId(), novaPecaId, sessao.getDataHora(),
                sessao.getCapacidade(), sessao.getValorBaseIngresso());
    }
}
