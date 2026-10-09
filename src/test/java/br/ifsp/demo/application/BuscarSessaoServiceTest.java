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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;

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

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    @DisplayName("3.2 - Deve retornar somente a sessão do identificador informado entre várias cadastradas")
    void deveRetornarSomenteSessaoDoIdentificadorInformado() {
        UUID idProcurado = UUID.randomUUID();
        Sessao primeira = criarSessao(UUID.randomUUID());
        Sessao procurada = criarSessao(idProcurado);
        Sessao terceira = criarSessao(UUID.randomUUID());
        lenient().when(repository.buscarPorId(primeira.getId())).thenReturn(Optional.of(primeira));
        when(repository.buscarPorId(idProcurado)).thenReturn(Optional.of(procurada));
        lenient().when(repository.buscarPorId(terceira.getId())).thenReturn(Optional.of(terceira));

        Sessao resultado = service.buscar(idProcurado);

        assertThat(resultado).isEqualTo(procurada);
        assertThat(resultado.getId()).isEqualTo(idProcurado);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    @DisplayName("3.3 - Deve informar que a sessão não foi encontrada quando o identificador não existe")
    void deveInformarQueSessaoNaoFoiEncontrada() {
        UUID id = UUID.randomUUID();
        when(repository.buscarPorId(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscar(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Sessão não encontrada");
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    @DisplayName("3.4 - Deve recusar a busca quando o identificador não é informado")
    void deveRecusarBuscaSemIdentificador() {
        assertThatThrownBy(() -> service.buscar(null))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(repository);
    }

    private Sessao criarSessao(UUID id) {
        DataHoraSessao dataHora = new DataHoraSessao(
                LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0));
        return new Sessao(id, UUID.randomUUID(), dataHora, 100, new BigDecimal("50.00"));
    }
}