package br.ifsp.demo.persistence;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.StatusIngresso;
import br.ifsp.demo.domain.TipoIngresso;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

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
    @Transactional
    public void salvar(Sessao sessao) {
        jdbcTemplate.update(
                "INSERT INTO sessao (id, peca_id, data, hora_inicio, hora_fim, capacidade, valor_base_ingresso) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                        + "ON CONFLICT(id) DO UPDATE SET peca_id = excluded.peca_id, data = excluded.data, "
                        + "hora_inicio = excluded.hora_inicio, hora_fim = excluded.hora_fim, "
                        + "capacidade = excluded.capacidade, valor_base_ingresso = excluded.valor_base_ingresso",
                sessao.getId().toString(),
                sessao.getPecaId().toString(),
                sessao.getDataHora().data().toString(),
                sessao.getDataHora().horaInicio().toString(),
                sessao.getDataHora().horaFim().toString(),
                sessao.getCapacidade(),
                sessao.getValorBaseIngresso().toPlainString()
        );
        jdbcTemplate.update("DELETE FROM ingresso WHERE sessao_id = ?", sessao.getId().toString());
        for (Ingresso ingresso : sessao.getIngressos()) {
            jdbcTemplate.update(
                    "INSERT INTO ingresso (id, sessao_id, categoria, valor, status) VALUES (?, ?, ?, ?, ?)",
                    ingresso.getId().toString(),
                    sessao.getId().toString(),
                    ingresso.getTipoIngresso().getCategoria(),
                    ingresso.getValor().toPlainString(),
                    ingresso.getStatus().name()
            );
        }
    }

    @Override
    @Transactional
    public void remover(UUID id) {
        jdbcTemplate.update("DELETE FROM ingresso WHERE sessao_id = ?", id.toString());
        jdbcTemplate.update("DELETE FROM sessao WHERE id = ?", id.toString());
    }

    @Override
    public Optional<Sessao> buscarPorId(UUID id) {
        return jdbcTemplate.query("SELECT * FROM sessao WHERE id = ?", SESSAO_MAPPER, id.toString())
                .stream()
                .peek(this::carregarIngressos)
                .findFirst();
    }

    @Override
    public List<Sessao> listar() {
        return jdbcTemplate.query("SELECT * FROM sessao", SESSAO_MAPPER).stream()
                .peek(this::carregarIngressos)
                .toList();
    }

    private void carregarIngressos(Sessao sessao) {
        List<Ingresso> ingressos = jdbcTemplate.query(
                "SELECT * FROM ingresso WHERE sessao_id = ? "
                        + "ORDER BY CASE WHEN status = 'CANCELADO' THEN 0 ELSE 1 END",
                (rs, rowNum) -> new Ingresso(
                        UUID.fromString(rs.getString("id")),
                        new TipoIngresso(rs.getString("categoria")),
                        new BigDecimal(rs.getString("valor")),
                        StatusIngresso.valueOf(rs.getString("status"))
                ),
                sessao.getId().toString()
        );
        ingressos.forEach(sessao::registrarIngresso);
    }
}
