package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarSessaoServiceTest {

    @Mock
    private ISessaoRepository repository;

    @InjectMocks
    private BuscarSessaoService service;

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    @DisplayName("3.1 - Deve retornar a sessão quando o identificador existe")
    void deveRetornarSessaoQuandoIdentificadorExiste() {
        UUID id = UUID.randomUUID();
        Sessao sessao = criarSessao(id);
        when(repository.buscarPorId(id)).thenReturn(Optional.of(sessao));

        Sessao resultado = service.buscar(id);

        assertThat(resultado).isEqualTo(sessao);
    }

    private Sessao criarSessao(UUID id) {
        DataHoraSessao dataHora = new DataHoraSessao(
                LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0));
        return new Sessao(id, UUID.randomUUID(), dataHora, 100, new BigDecimal("50.00"));
    }
}