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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("TDD")
class ListarSessoesServiceTest {

    @Mock
    private ISessaoRepository sessaoRepository;
    @InjectMocks
    private ListarSessoesService service;

    @Test
    @DisplayName("[2.1] Lista várias sessões cadastradas")
    void listaVariasSessoes() {
        List<Sessao> sessoes = List.of(sessao(UUID.randomUUID()), sessao(UUID.randomUUID()));
        when(sessaoRepository.listar()).thenReturn(sessoes);

        assertThat(service.listar()).containsExactlyElementsOf(sessoes);
    }

    private Sessao sessao(UUID pecaId) {
        DataHoraSessao dataHora = new DataHoraSessao(
                LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0));
        return new Sessao(UUID.randomUUID(), pecaId, dataHora, 100, new BigDecimal("50.00"));
    }
}
