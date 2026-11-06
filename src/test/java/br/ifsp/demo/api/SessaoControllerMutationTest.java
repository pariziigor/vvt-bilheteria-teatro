package br.ifsp.demo.api;

import br.ifsp.demo.application.AtualizarSessaoService;
import br.ifsp.demo.application.BuscarSessaoService;
import br.ifsp.demo.application.CancelarIngressoService;
import br.ifsp.demo.application.ComprarIngressoService;
import br.ifsp.demo.application.CriarSessaoService;
import br.ifsp.demo.application.ListarSessoesService;
import br.ifsp.demo.application.RemoverSessaoService;
import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.StatusIngresso;
import br.ifsp.demo.domain.TipoIngresso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
class SessaoControllerMutationTest {

    @Mock private CriarSessaoService criarSessaoService;
    @Mock private ListarSessoesService listarSessoesService;
    @Mock private BuscarSessaoService buscarSessaoService;
    @Mock private AtualizarSessaoService atualizarSessaoService;
    @Mock private CancelarIngressoService cancelarIngressoService;
    @Mock private RemoverSessaoService removerSessaoService;
    @Mock private ComprarIngressoService comprarIngressoService;

    private SessaoController controller;

    @BeforeEach
    void setUp() {
        controller = new SessaoController(criarSessaoService, listarSessoesService, buscarSessaoService,
                atualizarSessaoService, cancelarIngressoService, removerSessaoService, comprarIngressoService);
    }

    @Test
    @Tag("Mutation")
    void criaSessaoEFiltraIngressosNaListagemPorPapel() {
        Sessao sessao = sessaoComIngressos();
        UUID pecaId = UUID.randomUUID();
        LocalDate data = LocalDate.now().plusDays(30);
        BigDecimal valor = new BigDecimal("75.00");
        CriarSessaoRequest request = new CriarSessaoRequest(
                pecaId, data, LocalTime.of(19, 0), LocalTime.of(21, 0), 10, valor);
        Authentication admin = authentication("admin@teste.com", "ROLE_ADMIN");
        when(criarSessaoService.criar(eq(pecaId), any(DataHoraSessao.class), eq(10), eq(valor)))
                .thenReturn(sessao);

        var criada = controller.criar(request, admin);

        assertThat(criada.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(criada.getHeaders().getLocation()).isEqualTo(java.net.URI.create("/api/v1/sessoes/" + sessao.getId()));
        assertThat(criada.getBody().ingressos()).hasSize(2);
        verify(criarSessaoService).criar(eq(pecaId),
                eq(new DataHoraSessao(data, LocalTime.of(19, 0), LocalTime.of(21, 0))), eq(10), eq(valor));

        when(listarSessoesService.listar()).thenReturn(List.of(sessao));
        assertThat(controller.listar(authentication("ana@teste.com", "ROLE_CLIENTE")))
                .singleElement().satisfies(response -> assertThat(response.ingressos())
                        .extracting(IngressoResponse::id).containsExactly(sessao.getIngressos().getFirst().getId()));
        assertThat(controller.listar(authentication("admin@teste.com", "ROLE_ADMIN")))
                .singleElement().satisfies(response -> assertThat(response.ingressos()).hasSize(2));
        assertThat(controller.listar(null)).singleElement()
                .satisfies(response -> assertThat(response.ingressos()).isEmpty());
    }

    @Test
    @Tag("Mutation")
    void buscaEAtualizaSessaoComOuSemUsuarioAutenticado() {
        Sessao sessao = sessaoComIngressos();
        UUID sessaoId = sessao.getId();
        when(buscarSessaoService.buscar(sessaoId)).thenReturn(sessao);

        assertThat(controller.buscar(sessaoId, null).ingressos()).isEmpty();
        assertThat(controller.buscar(sessaoId, authentication("ana@teste.com", "ROLE_CLIENTE"))
                .ingressos()).hasSize(1);
        assertThat(controller.buscar(sessaoId, authentication("admin@teste.com", "ROLE_ADMIN"))
                .ingressos()).hasSize(2);

        UUID pecaId = UUID.randomUUID();
        LocalDate data = LocalDate.now().plusDays(40);
        BigDecimal valor = new BigDecimal("80.00");
        AtualizarSessaoRequest request = new AtualizarSessaoRequest(
                pecaId, data, LocalTime.of(18, 0), LocalTime.of(20, 0), 12, valor);
        Authentication cliente = authentication("ana@teste.com", "ROLE_CLIENTE");
        when(atualizarSessaoService.atualizar(eq(sessaoId), eq(pecaId), any(DataHoraSessao.class), eq(12), eq(valor)))
                .thenReturn(sessao);

        SessaoResponse atualizada = controller.atualizar(sessaoId, request, cliente);

        assertThat(atualizada.id()).isEqualTo(sessaoId);
        assertThat(atualizada.ingressos()).hasSize(1);
        verify(atualizarSessaoService).atualizar(sessaoId, pecaId,
                new DataHoraSessao(data, LocalTime.of(18, 0), LocalTime.of(20, 0)), 12, valor);
    }




    private Authentication authentication(String email, String role) {
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        org.mockito.Mockito.doReturn(List.of(new SimpleGrantedAuthority(role)))
                .when(authentication).getAuthorities();
        return authentication;
    }

    private Sessao sessaoComIngressos() {
        Sessao sessao = new Sessao(UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0)),
                10, new BigDecimal("50.00"));
        sessao.registrarIngresso(new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"),
                new BigDecimal("50.00"), StatusIngresso.VENDIDO, "ana@teste.com"));
        sessao.registrarIngresso(new Ingresso(UUID.randomUUID(), new TipoIngresso("ESTUDANTE"),
                new BigDecimal("25.00"), StatusIngresso.VENDIDO, "bruno@teste.com"));
        return sessao;
    }
}
