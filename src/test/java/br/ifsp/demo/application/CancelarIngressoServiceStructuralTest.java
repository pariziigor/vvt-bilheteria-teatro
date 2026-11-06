package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.StatusIngresso;
import br.ifsp.demo.domain.TipoIngresso;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class CancelarIngressoServiceStructuralTest {

    @Mock
    private ISessaoRepository repository;

    @InjectMocks
    private CancelarIngressoService service;

    @Test
    @Tag("Mutation")
    void rejeitaIdentificadoresAusentesSemConsultarRepositorio() {
        UUID ingressoId = UUID.randomUUID();
        UUID sessaoId = UUID.randomUUID();

        assertThatThrownBy(() -> service.cancelar(null, ingressoId, "ana@email.com", false))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.cancelar(sessaoId, null, "ana@email.com", false))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository);
    }






    private Sessao sessao(LocalDate data) {
        return new Sessao(UUID.randomUUID(), UUID.randomUUID(),
                new DataHoraSessao(data, LocalTime.of(19, 0), LocalTime.of(21, 0)),
                10, new BigDecimal("50.00"));
    }

    private Ingresso ingresso(String compradorEmail) {
        return new Ingresso(UUID.randomUUID(), new TipoIngresso("INTEIRA"),
                new BigDecimal("50.00"), StatusIngresso.VENDIDO, compradorEmail);
    }
}
