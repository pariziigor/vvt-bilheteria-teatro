package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Functional")
class CancelarIngressoServiceFunctionalTest {

    @Mock
    private ISessaoRepository sessaoRepository;
    @InjectMocks
    private CancelarIngressoService service;

    @Test
    @DisplayName("Rejeita o cancelamento de ingresso que não pertence à sessão")
    void rejeitaIngressoInexistente() {
        Sessao sessao = sessao();
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> service.cancelar(sessao.getId(), UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
        verify(sessaoRepository, never()).salvar(sessao);
    }

    @Test
    @DisplayName("Impede cancelar novamente um ingresso já cancelado")
    void impedeCancelamentoDuplicado() {
        Sessao sessao = sessao();
        Ingresso ingresso = new Ingresso(
                UUID.randomUUID(), new TipoIngresso("INTEIRA"), new BigDecimal("50.00"));
        sessao.registrarIngresso(ingresso);
        sessao.cancelarIngresso(ingresso.getId());
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> service.cancelar(sessao.getId(), ingresso.getId()))
                .isInstanceOf(IllegalStateException.class);
        verify(sessaoRepository, never()).salvar(sessao);
    }

    private Sessao sessao() {
        return new Sessao(
                UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0)),
                1, new BigDecimal("50.00"));
    }
}
