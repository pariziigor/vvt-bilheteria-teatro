package br.ifsp.demo.persistence;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessaoRepository implements ISessaoRepository {

    private static final RowMapper<Sessao> SESSAO_MAPPER = (rs, rowNum) -> new Sessao(
            UUID.fromString(rs.getString("id")),
            UUID.fromString(rs.getString("peca_id")),
            new DataHoraSessao(
                    LocalDate.parse(rs.getString("data")),
                    LocalTime.parse(rs.getString("hora_inicio")),
                    LocalTime.parse(rs.getString("hora_fim"))
            ),
            rs.getInt("capacidade"),
            new BigDecimal(rs.getString("valor_base_ingresso"))
    );

    private final JdbcTemplate jdbcTemplate;

    public SessaoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(Sessao sessao) {
        jdbcTemplate.update(
                "INSERT INTO sessao (id, peca_id, data, hora_inicio, hora_fim, capacidade, valor_base_ingresso) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                sessao.getId().toString(),
                sessao.getPecaId().toString(),
                sessao.getDataHora().data().toString(),
                sessao.getDataHora().horaInicio().toString(),
                sessao.getDataHora().horaFim().toString(),
                sessao.getCapacidade(),
                sessao.getValorBaseIngresso().toPlainString()
        );
    }

    @Override
    public Optional<Sessao> buscarPorId(UUID id) {
        return jdbcTemplate.query("SELECT * FROM sessao WHERE id = ?", SESSAO_MAPPER, id.toString())
                .stream()
                .findFirst();
    }
}
