package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.TipoIngresso;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Functional")
class RemoverSessaoServiceFunctionalTest {
    @Mock ISessaoRepository repository;
    @InjectMocks RemoverSessaoService service;

    @Test void removeSessaoSemIngressos() {
        Sessao s = sessao(); when(repository.buscarPorId(s.getId())).thenReturn(Optional.of(s));
        service.remover(s.getId()); verify(repository).remover(s.getId());
    }
    @Test void recusaSessaoInexistente() {
        UUID id = UUID.randomUUID(); when(repository.buscarPorId(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.remover(id)).isInstanceOf(java.util.NoSuchElementException.class);
        verify(repository, never()).remover(any());
    }
    @Test void recusaIdentificadorAusente() {
        assertThatThrownBy(() -> service.remover(null)).isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).buscarPorId(any());
    }
    @Test void recusaSessaoComIngressoVendido() {
        Sessao s = sessao(); s.registrarIngresso(new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"), new BigDecimal("50")));
        when(repository.buscarPorId(s.getId())).thenReturn(Optional.of(s));
        assertThatThrownBy(() -> service.remover(s.getId())).isInstanceOf(IllegalStateException.class);
        verify(repository, never()).remover(any());
    }
    private Sessao sessao() { return new Sessao(UUID.randomUUID(), UUID.randomUUID(), new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19,0), LocalTime.of(21,0)), 2, new BigDecimal("50")); }
}
