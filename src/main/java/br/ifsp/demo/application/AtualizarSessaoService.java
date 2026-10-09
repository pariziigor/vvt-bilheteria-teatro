package br.ifsp.demo.application;

import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AtualizarSessaoService {

    private final ISessaoRepository sessaoRepository;
    private final IPecaRepository pecaRepository;

    public AtualizarSessaoService(ISessaoRepository sessaoRepository, IPecaRepository pecaRepository) {
        this.sessaoRepository = sessaoRepository;
        this.pecaRepository = pecaRepository;
    }

    public Sessao atualizar(UUID sessaoId, UUID pecaId, DataHoraSessao dataHora, int capacidade,
                            BigDecimal valorBaseIngresso) {
        Sessao sessao = sessaoRepository.buscarPorId(sessaoId)
                .orElseThrow(() -> new NoSuchElementException("Sessão não encontrada"));
        sessao.atualizar(pecaId, dataHora, capacidade, valorBaseIngresso);
        sessaoRepository.salvar(sessao);
        return sessao;
    }
}