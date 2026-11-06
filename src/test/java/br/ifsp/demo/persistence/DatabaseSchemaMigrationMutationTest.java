package br.ifsp.demo.persistence;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
class DatabaseSchemaMigrationMutationTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    @Tag("Mutation")
    void adicionaAsColunasAusentesDeUsuarioEIngresso() {
        when(jdbcTemplate.queryForList("PRAGMA table_info(usuario)"))
                .thenReturn(List.of(Map.of("name", "nome")));
        when(jdbcTemplate.queryForList("PRAGMA table_info(ingresso)"))
                .thenReturn(List.of(Map.of("name", "sessao_id")));
        DatabaseSchemaMigration migration = new DatabaseSchemaMigration(jdbcTemplate);

        migration.run(null);

        verify(jdbcTemplate).execute("ALTER TABLE usuario ADD COLUMN papel TEXT NOT NULL DEFAULT 'CLIENTE'");
        verify(jdbcTemplate).execute("ALTER TABLE ingresso ADD COLUMN comprador_email TEXT NOT NULL DEFAULT ''");
        verifyNoMoreInteractions(jdbcTemplate);
    }

}
