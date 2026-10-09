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

    // NOVOS TESTES ENTRAM AQUI

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
}
