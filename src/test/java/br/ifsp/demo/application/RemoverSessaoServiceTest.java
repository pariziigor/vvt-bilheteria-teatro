package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Ingresso;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("TDD")
class RemoverSessaoServiceTest {
    @Mock private ISessaoRepository sessaoRepository;
    @InjectMocks private RemoverSessaoService service;

    @Test
    @DisplayName("[5.1] Remove uma sessão existente sem ingressos vendidos")
    void removeSessaoExistenteSemIngressosVendidos() {
        Sessao sessao = sessao();
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));
        service.remover(sessao.getId());
        verify(sessaoRepository).remover(sessao.getId());
    }

    @Test
    @DisplayName("[5.2] Recusa a remoção de uma sessão inexistente")
    void recusaRemocaoDeSessaoInexistente() {
        UUID id = UUID.randomUUID();
        when(sessaoRepository.buscarPorId(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.remover(id)).isInstanceOf(NoSuchElementException.class);
        verify(sessaoRepository, never()).remover(any());
    }

    @Test
    @DisplayName("[5.3] Recusa a remoção sem identificador")
    void recusaRemocaoSemIdentificador() {
        assertThatThrownBy(() -> service.remover(null)).isInstanceOf(IllegalArgumentException.class);
        verify(sessaoRepository, never()).buscarPorId(any());
        verify(sessaoRepository, never()).remover(any());
    }

    @Test
    @DisplayName("[5.4] Recusa a remoção de uma sessão com ingressos vendidos")
    void recusaRemocaoDeSessaoComIngressosVendidos() {
        Sessao sessao = sessao();
        sessao.registrarIngresso(new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"), new BigDecimal("50.00")));
        when(sessaoRepository.buscarPorId(sessao.getId())).thenReturn(Optional.of(sessao));
        assertThatThrownBy(() -> service.remover(sessao.getId())).isInstanceOf(IllegalStateException.class);
        verify(sessaoRepository, never()).remover(any());
    }

    private Sessao sessao() {
        return new Sessao(UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0)),
                100, new BigDecimal("50.00"));
    }
}
