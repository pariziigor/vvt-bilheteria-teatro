package br.ifsp.demo.persistence;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class DatabaseSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        addColumnIfMissing("usuario", "papel", "ALTER TABLE usuario ADD COLUMN papel TEXT NOT NULL DEFAULT 'CLIENTE'");
        addColumnIfMissing("ingresso", "comprador_email", "ALTER TABLE ingresso ADD COLUMN comprador_email TEXT NOT NULL DEFAULT ''");
    }

    private void addColumnIfMissing(String table, String column, String alterSql) {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList("PRAGMA table_info(" + table + ")");
        boolean exists = columns.stream().anyMatch(info -> column.equalsIgnoreCase(String.valueOf(info.get("name"))));
        if (!exists) {
            jdbcTemplate.execute(alterSql);
        }
    }
}
