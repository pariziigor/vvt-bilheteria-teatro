package br.ifsp.demo.application;

import br.ifsp.demo.domain.IUsuarioRepository;
import br.ifsp.demo.domain.Usuario;
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
class RegistrarUsuarioServiceStructuralTest {

    @Mock
    private IUsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private RegistrarUsuarioService service;

    @Test
    @DisplayName("Registra usuário com dados válidos e senha codificada")
    void registraUsuarioComDadosValidos() {
        when(passwordEncoder.encode("segredo123")).thenReturn("senha-codificada");

        Usuario usuario = service.registrar("Ana", "ana@email.com", "segredo123");

        assertThat(usuario.getId()).isNotNull();
        assertThat(usuario.getNome()).isEqualTo("Ana");
        assertThat(usuario.getEmail()).isEqualTo("ana@email.com");
        assertThat(usuario.getSenhaCodificada()).isEqualTo("senha-codificada");
        verify(usuarioRepository).salvar(usuario);
    }

    @Test
    @DisplayName("Rejeita cadastro com e-mail já utilizado")
    void rejeitaEmailJaCadastrado() {
        Usuario existente = new Usuario(UUID.randomUUID(), "Outra", "ana@email.com", "hash");
        when(usuarioRepository.buscarPorEmail("ana@email.com")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.registrar("Ana", "ana@email.com", "segredo123"))
                .isInstanceOf(IllegalStateException.class);

        verify(usuarioRepository, never()).salvar(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Rejeita cadastro com senha nula")
    void rejeitaSenhaNula() {
        assertThatThrownBy(() -> service.registrar("Ana", "ana@email.com", null))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(usuarioRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Rejeita cadastro com senha em branco")
    void rejeitaSenhaEmBranco() {
        assertThatThrownBy(() -> service.registrar("Ana", "ana@email.com", "   "))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(usuarioRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Rejeita cadastro com nome nulo")
    void rejeitaNomeNulo() {
        assertThatThrownBy(() -> service.registrar(null, "ana@email.com", "segredo123"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(usuarioRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Rejeita cadastro com nome em branco")
    void rejeitaNomeEmBranco() {
        assertThatThrownBy(() -> service.registrar("   ", "ana@email.com", "segredo123"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(usuarioRepository, never()).salvar(any());
    }

}