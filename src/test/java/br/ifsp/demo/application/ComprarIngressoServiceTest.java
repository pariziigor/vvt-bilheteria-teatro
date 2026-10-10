package br.ifsp.demo.application;

import br.ifsp.demo.domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("TDD")
class ComprarIngressoServiceTest {
    @Mock
    ISessaoRepository repository;
    @InjectMocks
    ComprarIngressoService service;

    @Test
    void compraInteira() {
        compra("INTEIRA", "50.00");
    }

    @Test
    void compraEstudante() {
        compra("ESTUDANTE", "25.00");
    }

    @Test
    void compraPcd() {
        compra("PCD", "20.00");
    }

    @Test
    void compraIdoso() {
        compra("IDOSO", "17.50");
    }

    @Test
    void compraUltimaVaga() {
        Sessao s = sessao(1);
        when(repository.buscarPorId(s.getId())).thenReturn(Optional.of(s));
        service.comprar(s.getId(), "INTEIRA");
        assertThat(s.getIngressos()).hasSize(1);
    }

    @Test
    void recusaLotada() {
        Sessao s = sessao(1);
        s.registrarIngresso(ingresso());
        when(repository.buscarPorId(s.getId())).thenReturn(Optional.of(s));
        assertThatThrownBy(() -> service.comprar(s.getId(), "INTEIRA")).isInstanceOf(IllegalStateException.class);
        verify(repository, never()).salvar(any());
    }

    @Test
    void recusaEncerrada() {
        Sessao s = new Sessao(UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().minusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0)), 2,
                new BigDecimal("50"));
        when(repository.buscarPorId(s.getId())).thenReturn(Optional.of(s));
        assertThatThrownBy(() -> service.comprar(s.getId(), "INTEIRA")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void recusaInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.buscarPorId(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.comprar(id, "INTEIRA")).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void recusaSemTipo() {
        assertThatThrownBy(() -> service.comprar(UUID.randomUUID(), null)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void recusaTipoInvalido() {
        Sessao s = sessao(2);
        when(repository.buscarPorId(s.getId())).thenReturn(Optional.of(s));
        assertThatThrownBy(() -> service.comprar(s.getId(), "VIP")).isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).salvar(any());
    }

    @Test
    void associaSomenteASessaoComprada() {
        Sessao a = sessao(2), b = sessao(2);
        when(repository.buscarPorId(a.getId())).thenReturn(Optional.of(a));
        service.comprar(a.getId(), "INTEIRA");
        assertThat(a.getIngressos()).hasSize(1);
        assertThat(b.getIngressos()).isEmpty();
    }

    @Test
    void mantemDisponibilidade() {
        Sessao s = sessao(3);
        when(repository.buscarPorId(s.getId())).thenReturn(Optional.of(s));
        service.comprar(s.getId(), "INTEIRA");
        assertThat(s.getIngressos()).hasSize(1);
        assertThat(s.getCapacidade()).isGreaterThan(s.getIngressos().size());
    }

    private void compra(String tipo, String valor) {
        Sessao s = sessao(2);
        when(repository.buscarPorId(s.getId())).thenReturn(Optional.of(s));
        Ingresso i = service.comprar(s.getId(), tipo);
        assertThat(i.getValor()).isEqualByComparingTo(valor);
        assertThat(i.getStatus()).isEqualTo(StatusIngresso.VENDIDO);
        assertThat(s.getIngressos()).contains(i);
        verify(repository).salvar(s);
    }

    private Sessao sessao(int capacidade) {
        return new Sessao(UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(2), LocalTime.of(19, 0), LocalTime.of(21, 0)), capacidade,
                new BigDecimal("50"));
    }

    private Ingresso ingresso() {
        return new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"), new BigDecimal("50"));
    }
}
