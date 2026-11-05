package br.ifsp.demo.persistence;

import br.ifsp.demo.domain.Peca;
import br.ifsp.demo.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:target/mutation-user-peca.db",
        "spring.sql.init.mode=always"
})
@Tag("UnitTest")
@Tag("Mutation")
class PecaUsuarioRepositoryMutationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PecaRepository pecaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limparDados() {
        jdbcTemplate.update("DELETE FROM usuario");
        jdbcTemplate.update("DELETE FROM peca");
    }

    @Test
    @DisplayName("Persiste peça, verifica sua existência e mapeia seus dados")
    void persisteEConsultaPeca() {
        UUID id = UUID.randomUUID();
        Peca peca = new Peca(id, "Hamlet", "Drama", Duration.ofMinutes(150), "14 anos");

        assertThat(pecaRepository.existe(id)).isFalse();

        pecaRepository.salvar(peca);

        assertThat(pecaRepository.existe(id)).isTrue();
        assertThat(pecaRepository.listar())
                .singleElement()
                .satisfies(armazenada -> {
                    assertThat(armazenada.getId()).isEqualTo(id);
                    assertThat(armazenada.getTitulo()).isEqualTo("Hamlet");
                    assertThat(armazenada.getDescricao()).isEqualTo("Drama");
                    assertThat(armazenada.getDuracao()).isEqualTo(Duration.ofMinutes(150));
                    assertThat(armazenada.getClassificacao()).isEqualTo("14 anos");
                });
    }

    @Test
    @DisplayName("Lista peças em ordem alfabética pelo título")
    void listaPecasOrdenadasPorTitulo() {
        Peca zebra = new Peca(UUID.randomUUID(), "Zebra", "Descrição Z", Duration.ofMinutes(90), "Livre");
        Peca ancora = new Peca(UUID.randomUUID(), "Ancora", "Descrição A", Duration.ofMinutes(100), "Livre");
        pecaRepository.salvar(zebra);
        pecaRepository.salvar(ancora);

        List<Peca> resultado = pecaRepository.listar();

        assertThat(resultado).extracting(Peca::getId).containsExactly(ancora.getId(), zebra.getId());
    }

    @Test
    @DisplayName("Persiste usuário e busca por e-mail")
    void persisteEConsultaUsuarioPorEmail() {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario(id, "Ana", "ana-" + id + "@email.com", "senha-codificada");

        assertThat(usuarioRepository.buscarPorEmail(usuario.getEmail())).isEmpty();

        usuarioRepository.salvar(usuario);

        assertThat(usuarioRepository.buscarPorEmail(usuario.getEmail()))
                .hasValueSatisfying(armazenado -> {
                    assertThat(armazenado.getId()).isEqualTo(id);
                    assertThat(armazenado.getNome()).isEqualTo("Ana");
                    assertThat(armazenado.getEmail()).isEqualTo(usuario.getEmail());
                    assertThat(armazenado.getSenhaCodificada()).isEqualTo("senha-codificada");
                });
    }
}
