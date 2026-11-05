package br.ifsp.demo.application;

import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.DataHoraSessao;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class CriarSessaoServiceStructuralTest {

    @Mock
    private ISessaoRepository sessaoRepository;
    @Mock
    private IPecaRepository pecaRepository;
    @InjectMocks
    private CriarSessaoService service;

    @Test
    @DisplayName("Rejeita valor-base nulo")
    void rejeitaValorBaseNulo() {
        assertValorBaseInvalido(null);
    }

    @Test
    @DisplayName("Rejeita valor-base igual a zero")
    void rejeitaValorBaseZero() {
        assertValorBaseInvalido(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Rejeita valor-base negativo")
    void rejeitaValorBaseNegativo() {
        assertValorBaseInvalido(new BigDecimal("-0.01"));
    }

    private void assertValorBaseInvalido(BigDecimal valorBase) {
        assertThatThrownBy(() -> service.criar(
                UUID.randomUUID(), dataHoraFutura(), 100, valorBase))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O valor base do ingresso deve ser positivo");

        verify(sessaoRepository, never()).salvar(org.mockito.ArgumentMatchers.any());
    }

    private DataHoraSessao dataHoraFutura() {
        return new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0));
    }
}
