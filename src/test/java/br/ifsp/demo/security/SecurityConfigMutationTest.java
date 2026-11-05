package br.ifsp.demo.security;

import br.ifsp.demo.api.AuthController;
import br.ifsp.demo.api.PecaController;
import br.ifsp.demo.application.CadastrarPecaService;
import br.ifsp.demo.application.CredenciaisInvalidasException;
import br.ifsp.demo.application.ListarPecasService;
import br.ifsp.demo.application.AutenticarUsuarioService;
import br.ifsp.demo.application.RegistrarUsuarioService;
import br.ifsp.demo.domain.Peca;
import br.ifsp.demo.domain.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;
import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, PecaController.class})
@Import(SecurityConfig.class)
@Tag("UnitTest")
@Tag("Mutation")
class SecurityConfigMutationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilterChainProxy filterChainProxy;
    @Autowired
    private HttpSecurity httpSecurity;

    @MockBean
    private RegistrarUsuarioService registrarUsuarioService;
    @MockBean
    private AutenticarUsuarioService autenticarUsuarioService;
    @MockBean
    private CadastrarPecaService cadastrarPecaService;
    @MockBean
    private ListarPecasService listarPecasService;
    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("Permite registrar usuário sem autenticação")
    void permiteRegistrarUsuarioSemAutenticacao() throws Exception {
        UUID id = UUID.randomUUID();
        when(registrarUsuarioService.registrar("Ana", "ana@email.com", "segredo123"))
                .thenReturn(new Usuario(id, "Ana", "ana@email.com", "senha-codificada"));

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ana","email":"ana@email.com","senha":"segredo123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/usuarios/" + id))
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    @Test
    @DisplayName("Retorna conflito quando o e-mail já está cadastrado")
    void retornaConflitoParaEmailDuplicado() throws Exception {
        when(registrarUsuarioService.registrar("Ana", "ana@email.com", "segredo123"))
                .thenThrow(new IllegalStateException("Já existe um usuário com esse e-mail"));

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ana","email":"ana@email.com","senha":"segredo123"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Retorna erro de dados inválidos no cadastro de usuário")
    void retornaErroParaDadosInvalidosDeUsuario() throws Exception {
        when(registrarUsuarioService.registrar("", "ana@email.com", "segredo123"))
                .thenThrow(new IllegalArgumentException("O nome do usuário é obrigatório"));

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"","email":"ana@email.com","senha":"segredo123"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Permite autenticar usuário sem autenticação prévia")
    void permiteAutenticarUsuarioSemAutenticacao() throws Exception {
        when(autenticarUsuarioService.autenticar("ana@email.com", "segredo123"))
                .thenReturn("token-jwt");

        mockMvc.perform(post("/api/v1/autenticacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@email.com","senha":"segredo123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt"));

        org.assertj.core.api.Assertions.assertThat(filterChainProxy.getFilterChains())
                .anySatisfy(cadeia -> org.assertj.core.api.Assertions.assertThat(cadeia.getFilters())
                        .anyMatch(JwtAuthenticationFilter.class::isInstance));
    }

    @Test
    @DisplayName("Constrói uma cadeia de segurança com o filtro JWT")
    void constroiCadeiaDeSegurancaComFiltroJwt() throws Exception {
        SecurityFilterChain cadeia = new SecurityConfig(jwtService).securityFilterChain(httpSecurity);

        org.assertj.core.api.Assertions.assertThat(cadeia).isNotNull();
        org.assertj.core.api.Assertions.assertThat(cadeia.getFilters())
                .anyMatch(JwtAuthenticationFilter.class::isInstance);
    }

    @Test
    @DisplayName("Retorna não autorizado para credenciais inválidas")
    void retornaNaoAutorizadoParaCredenciaisInvalidas() throws Exception {
        when(autenticarUsuarioService.autenticar("ana@email.com", "senha-errada"))
                .thenThrow(new CredenciaisInvalidasException());

        mockMvc.perform(post("/api/v1/autenticacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@email.com","senha":"senha-errada"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Cadastra peça e retorna localização e dados")
    void cadastraPecaERetornaDados() throws Exception {
        UUID id = UUID.randomUUID();
        Peca peca = new Peca(id, "Hamlet", "Drama", Duration.ofMinutes(150), "14 anos");
        when(jwtService.extrairEmail("token-valido")).thenReturn("ana@email.com");
        when(cadastrarPecaService.cadastrar("Hamlet", "Drama", Duration.ofMinutes(150), "14 anos"))
                .thenReturn(peca);

        mockMvc.perform(post("/api/v1/pecas")
                        .header("Authorization", "Bearer token-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Hamlet","descricao":"Drama","duracaoMinutos":150,"classificacao":"14 anos"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/pecas/" + id))
                .andExpect(jsonPath("$.duracaoMinutos").value(150));
    }

    @Test
    @DisplayName("Lista peças convertidas para resposta da API")
    void listaPecasNaApi() throws Exception {
        Peca peca = new Peca(UUID.randomUUID(), "Hamlet", "Drama", Duration.ofMinutes(150), "14 anos");
        when(listarPecasService.listar()).thenReturn(List.of(peca));
        when(jwtService.extrairEmail("token-valido")).thenReturn("ana@email.com");

        mockMvc.perform(get("/api/v1/pecas")
                        .header("Authorization", "Bearer token-valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Hamlet"))
                .andExpect(jsonPath("$[0].duracaoMinutos").value(150));
    }

    @Test
    @DisplayName("Retorna erro de dados inválidos ao cadastrar peça")
    void retornaErroParaPecaInvalida() throws Exception {
        when(jwtService.extrairEmail("token-valido")).thenReturn("ana@email.com");
        when(cadastrarPecaService.cadastrar("", "Drama", Duration.ofMinutes(150), "14 anos"))
                .thenThrow(new IllegalArgumentException("Os dados textuais da peça são obrigatórios"));

        mockMvc.perform(post("/api/v1/pecas")
                        .header("Authorization", "Bearer token-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"","descricao":"Drama","duracaoMinutos":150,"classificacao":"14 anos"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Exige autenticação para rotas protegidas")
    void exigeAutenticacaoParaRotasProtegidas() throws Exception {
        mockMvc.perform(get("/api/v1/pecas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Permite requisições de preflight CORS")
    void permitePreflightCors() throws Exception {
        mockMvc.perform(options("/api/v1/pecas")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    @DisplayName("Cria um codificador de senhas BCrypt")
    void criaCodificadorDeSenhas() {
        PasswordEncoder passwordEncoder = new SecurityConfig(jwtService).passwordEncoder();
        String senhaCodificada = passwordEncoder.encode("segredo123");

        org.assertj.core.api.Assertions.assertThat(senhaCodificada)
                .isNotEqualTo("segredo123");
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches("segredo123", senhaCodificada))
                .isTrue();
    }

    @Test
    @DisplayName("Configura origens, métodos e cabeçalhos CORS")
    void configuraCors() {
        CorsConfigurationSource source = new SecurityConfig(jwtService)
                .corsConfigurationSource(new String[]{"http://localhost:5173"});
        CorsConfiguration cors = source.getCorsConfiguration(
                new MockHttpServletRequest("OPTIONS", "/api/v1/pecas"));

        org.assertj.core.api.Assertions.assertThat(cors.getAllowedOriginPatterns())
                .containsExactly("http://localhost:5173");
        org.assertj.core.api.Assertions.assertThat(cors.getAllowedMethods())
                .containsExactly("GET", "POST", "PUT", "DELETE", "OPTIONS");
        org.assertj.core.api.Assertions.assertThat(cors.getAllowedHeaders())
                .containsExactly("Authorization", "Content-Type");
    }
}
