package br.ifsp.demo.application;

import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.Peca;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("Structural")
class CadastrarPecaServiceStructuralTest {

    @Mock
    private IPecaRepository pecaRepository;
    @InjectMocks
    private CadastrarPecaService service;

    @Test
    @DisplayName("Cadastra peça com dados válidos")
    void cadastraPecaComDadosValidos() {
        Peca peca = service.cadastrar("O Rei Leão", "Musical", Duration.ofMinutes(120), "Livre");

        assertThat(peca.getId()).isNotNull();
        assertThat(peca.getTitulo()).isEqualTo("O Rei Leão");
        assertThat(peca.getDescricao()).isEqualTo("Musical");
        assertThat(peca.getDuracao()).isEqualTo(Duration.ofMinutes(120));
        assertThat(peca.getClassificacao()).isEqualTo("Livre");
        verify(pecaRepository).salvar(peca);
    }
}