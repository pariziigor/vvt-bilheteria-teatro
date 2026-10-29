package br.ifsp.demo.security;

import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Mutation")
class JwtAuthenticationFilterMutationTest {

    @Mock
    private JwtService jwtService;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final MockFilterChain chain = new MockFilterChain();

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Autentica a requisição com token válido e segue a cadeia de filtros")
    void autenticaRequisicaoComTokenValido() throws Exception {
        request.addHeader("Authorization", "Bearer token-valido");
        when(jwtService.extrairEmail("token-valido")).thenReturn("ana@email.com");

        new JwtAuthenticationFilter(jwtService).doFilter(request, response, chain);

        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        assertThat(autenticacao).isNotNull();
        assertThat(autenticacao.getName()).isEqualTo("ana@email.com");
        assertThat(autenticacao.isAuthenticated()).isTrue();
        assertThat(chain.getRequest()).isSameAs(request);
    }

    // NOVOS TESTES ENTRAM AQUI
}