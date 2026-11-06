package br.ifsp.demo.persistence;

import br.ifsp.demo.domain.IUsuarioRepository;
import br.ifsp.demo.domain.PapelUsuario;
import br.ifsp.demo.domain.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioRepository implements IUsuarioRepository {

    private static final RowMapper<Usuario> USUARIO_MAPPER = (rs, rowNum) -> new Usuario(
            UUID.fromString(rs.getString("id")),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("senha"),
            PapelUsuario.valueOf(rs.getString("papel"))
    );

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(Usuario usuario) {
        jdbcTemplate.update(
                "INSERT INTO usuario (id, nome, email, senha, papel) VALUES (?, ?, ?, ?, ?)",
                usuario.getId().toString(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenhaCodificada(),
                usuario.getPapel().name()
        );
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM usuario WHERE email = ?", USUARIO_MAPPER, email)
                .stream()
                .findFirst();
    }
}
