package br.ifsp.demo.persistence;

import br.ifsp.demo.domain.IUsuarioRepository;
import br.ifsp.demo.domain.PapelUsuario;
import br.ifsp.demo.domain.Usuario;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
class DemoAccountsInitializerMutationTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @Tag("Mutation")
    void inicializaContaDeClienteEAdministradorQuandoEstaoAusentes() {
        when(usuarioRepository.buscarPorEmail("cliente@teste.com")).thenReturn(Optional.empty());
        when(usuarioRepository.buscarPorEmail("admin@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha-cliente")).thenReturn("hash-cliente");
        when(passwordEncoder.encode("senha-admin")).thenReturn("hash-admin");
        DemoAccountsInitializer initializer = initializer(true);

        initializer.run(null);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, org.mockito.Mockito.times(2)).salvar(captor.capture());
        List<Usuario> usuarios = captor.getAllValues();
        assertThat(usuarios).extracting(Usuario::getEmail)
                .containsExactly("cliente@teste.com", "admin@teste.com");
        assertThat(usuarios).extracting(Usuario::getPapel)
                .containsExactly(PapelUsuario.CLIENTE, PapelUsuario.ADMIN);
        assertThat(usuarios).extracting(Usuario::getSenhaCodificada)
                .containsExactly("hash-cliente", "hash-admin");
    }



    private DemoAccountsInitializer initializer(boolean enabled) {
        return new DemoAccountsInitializer(usuarioRepository, passwordEncoder, enabled,
                "cliente@teste.com", "senha-cliente", "admin@teste.com", "senha-admin");
    }

    private Usuario usuario(String email, PapelUsuario papel) {
        return new Usuario(java.util.UUID.randomUUID(), "Usuário existente", email, "hash", papel);
    }
}
