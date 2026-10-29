package br.ifsp.demo.application;

import br.ifsp.demo.domain.IUsuarioRepository;
import br.ifsp.demo.domain.Usuario;
import br.ifsp.demo.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class AutenticarUsuarioServiceStructuralTest {

    @Mock
    private IUsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AutenticarUsuarioService service;

    @Test
    @DisplayName("Autentica usuário com credenciais válidas e retorna o token")
    void autenticaComCredenciaisValidas() {
        Usuario usuario = usuarioCadastrado();
        when(passwordEncoder.matches("segredo123", "senha-codificada")).thenReturn(true);
        when(jwtService.gerarToken("ana@email.com")).thenReturn("token-jwt");

        String token = service.autenticar("ana@email.com", "segredo123");

        assertThat(token).isEqualTo("token-jwt");
    }

    @Test
    @DisplayName("Rejeita autenticação com senha incorreta")
    void rejeitaSenhaIncorreta() {
        usuarioCadastrado();
        when(passwordEncoder.matches("senha-errada", "senha-codificada")).thenReturn(false);

        assertThatThrownBy(() -> service.autenticar("ana@email.com", "senha-errada"))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha inválidos");

        verify(jwtService, never()).gerarToken(any());
    }

    private Usuario usuarioCadastrado() {
        Usuario usuario = new Usuario(UUID.randomUUID(), "Ana", "ana@email.com", "senha-codificada");
        when(usuarioRepository.buscarPorEmail("ana@email.com")).thenReturn(Optional.of(usuario));
        return usuario;
    }
}