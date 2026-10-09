package br.ifsp.demo.persistence;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.datasource.url=jdbc:sqlite:target/sessao-test.db")
class SessaoRepositoryTest {

    @Autowired
    private ISessaoRepository repository;

    @Test
    @DisplayName("Deve criar uma sessão e armazená-la no SQLite")
    void deveCriarSessaoEArmazenarNoSqlite() {
        DataHoraSessao dataHora = new DataHoraSessao(
                LocalDate.now().plusDays(30), LocalTime.of(19, 0), LocalTime.of(21, 0));
        Sessao sessao = new Sessao(UUID.randomUUID(), UUID.randomUUID(), dataHora, 100, new BigDecimal("50.00"));

        repository.salvar(sessao);

        Optional<Sessao> armazenada = repository.buscarPorId(sessao.getId());
        assertThat(armazenada).isPresent();
        assertThat(armazenada.get().getId()).isEqualTo(sessao.getId());
        assertThat(armazenada.get().getPecaId()).isEqualTo(sessao.getPecaId());
        assertThat(armazenada.get().getDataHora()).isEqualTo(dataHora);
        assertThat(armazenada.get().getCapacidade()).isEqualTo(100);
        assertThat(armazenada.get().getValorBaseIngresso()).isEqualByComparingTo("50.00");
    }
}
