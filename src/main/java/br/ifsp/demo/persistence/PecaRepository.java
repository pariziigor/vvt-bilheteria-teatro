package br.ifsp.demo.persistence;

import br.ifsp.demo.domain.IPecaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class PecaRepository implements IPecaRepository {

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
}
