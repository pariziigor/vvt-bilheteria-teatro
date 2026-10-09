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
