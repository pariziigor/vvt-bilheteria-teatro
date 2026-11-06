package br.ifsp.demo.persistence;

import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.Peca;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PecaRepository implements IPecaRepository {

    private static final RowMapper<Peca> PECA_MAPPER = (rs, rowNum) -> new Peca(
            UUID.fromString(rs.getString("id")),
            rs.getString("titulo"),
            rs.getString("descricao"),
            Duration.ofSeconds(rs.getLong("duracao_segundos")),
            rs.getString("classificacao")
    );

    private final JdbcTemplate jdbcTemplate;

    public PecaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existe(UUID id) {
        Integer quantidade = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM peca WHERE id = ?",
                Integer.class,
                id.toString()
        );
        return quantidade != null && quantidade > 0;
    }

    @Override
    public Optional<Peca> buscarPorId(UUID id) {
        return jdbcTemplate.query("SELECT * FROM peca WHERE id = ?", PECA_MAPPER, id.toString())
                .stream()
                .findFirst();
    }

    @Override
    public boolean possuiSessoes(UUID id) {
        Integer quantidade = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sessao WHERE peca_id = ?",
                Integer.class,
                id.toString()
        );
        return quantidade != null && quantidade > 0;
    }

    @Override
    public void salvar(Peca peca) {
        jdbcTemplate.update(
                "INSERT INTO peca (id, titulo, descricao, duracao_segundos, classificacao) VALUES (?, ?, ?, ?, ?)",
                peca.getId().toString(),
                peca.getTitulo(),
                peca.getDescricao(),
                peca.getDuracao().toSeconds(),
                peca.getClassificacao()
        );
    }

    @Override
    public void atualizar(Peca peca) {
        jdbcTemplate.update(
                "UPDATE peca SET titulo = ?, descricao = ?, duracao_segundos = ?, classificacao = ? WHERE id = ?",
                peca.getTitulo(),
                peca.getDescricao(),
                peca.getDuracao().toSeconds(),
                peca.getClassificacao(),
                peca.getId().toString()
        );
    }

    @Override
    public void remover(UUID id) {
        jdbcTemplate.update("DELETE FROM peca WHERE id = ?", id.toString());
    }

    @Override
    public List<Peca> listar() {
        return jdbcTemplate.query("SELECT * FROM peca ORDER BY titulo", PECA_MAPPER);
    }
}
