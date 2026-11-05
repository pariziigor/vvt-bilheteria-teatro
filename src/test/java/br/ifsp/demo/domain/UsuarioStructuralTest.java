package br.ifsp.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
@Tag("Structural")
class UsuarioStructuralTest {

    @Test
    @DisplayName("Cria usuário com dados válidos")
    void criaUsuarioComDadosValidos() {
        UUID id = UUID.randomUUID();

        Usuario usuario = new Usuario(id, "Ana", "ana@email.com", "senha-codificada");

        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getNome()).isEqualTo("Ana");
        assertThat(usuario.getEmail()).isEqualTo("ana@email.com");
        assertThat(usuario.getSenhaCodificada()).isEqualTo("senha-codificada");
    }

    @Test
    @DisplayName("Rejeita identificador nulo")
    void rejeitaIdentificadorNulo() {
        assertThatThrownBy(() -> new Usuario(null, "Ana", "ana@email.com", "hash"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Rejeita nome nulo ou em branco")
    void rejeitaNomeNuloOuEmBranco() {
        assertUsuarioInvalido(null, "ana@email.com");
        assertUsuarioInvalido("   ", "ana@email.com");
    }

    @Test
    @DisplayName("Rejeita e-mail nulo ou sem arroba")
    void rejeitaEmailNuloOuInvalido() {
        assertUsuarioInvalido("Ana", null);
        assertUsuarioInvalido("Ana", "ana.email.com");
    }

    private void assertUsuarioInvalido(String nome, String email) {
        assertThatThrownBy(() -> new Usuario(UUID.randomUUID(), nome, email, "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
