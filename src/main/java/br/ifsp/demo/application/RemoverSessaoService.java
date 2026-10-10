package br.ifsp.demo.application;

import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.StatusIngresso;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class RemoverSessaoService {

    private final ISessaoRepository sessaoRepository;

    public RemoverSessaoService(ISessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public void remover(UUID sessaoId) {
        if (sessaoId == null) {
            throw new IllegalArgumentException("O identificador da sessão é obrigatório");
        }
        Sessao sessao = sessaoRepository.buscarPorId(sessaoId)
                .orElseThrow(() -> new NoSuchElementException("Sessão não encontrada"));
        boolean possuiIngressosVendidos = sessao.getIngressos().stream()
                .anyMatch(ingresso -> ingresso.getStatus() == StatusIngresso.VENDIDO);
        if (possuiIngressosVendidos) {
            throw new IllegalStateException("Não é possível remover uma sessão com ingressos vendidos");
        }
        sessaoRepository.remover(sessaoId);
    }
}
