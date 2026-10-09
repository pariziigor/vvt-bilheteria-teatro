package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CriarSessaoService {

    private final ISessaoRepository sessaoRepository;
    private final IPecaRepository pecaRepository;

    public CriarSessaoService(ISessaoRepository sessaoRepository, IPecaRepository pecaRepository) {
        this.sessaoRepository = sessaoRepository;
        this.pecaRepository = pecaRepository;
    }

    public Sessao criar(UUID pecaId, DataHoraSessao dataHora, int capacidade, BigDecimal valorBaseIngresso) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade da sessão deve ser positiva");
        }
        if (dataHora == null || dataHora.data() == null || dataHora.horaInicio() == null
                || dataHora.horaFim() == null) {
            throw new IllegalArgumentException("A data e os horários da sessão são obrigatórios");
        }
        if (!LocalDateTime.of(dataHora.data(), dataHora.horaInicio()).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("A sessão deve ocorrer em uma data e horário futuros");
        }
        if (pecaId == null) {
            throw new IllegalArgumentException("A peça associada à sessão é obrigatória");
        }
        if (!pecaRepository.existe(pecaId)) {
            throw new IllegalArgumentException("A peça informada não existe");
        }

        Sessao sessao = new Sessao(UUID.randomUUID(), pecaId, dataHora, capacidade, valorBaseIngresso);
        sessaoRepository.salvar(sessao);
        return sessao;
    }
}
