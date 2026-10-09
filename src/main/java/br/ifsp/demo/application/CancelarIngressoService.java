package br.ifsp.demo.application;

import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class CancelarIngressoService {

    private final ISessaoRepository sessaoRepository;

    public CancelarIngressoService(ISessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public void cancelar(UUID sessaoId, UUID ingressoId) {
        if (sessaoId == null || ingressoId == null) {
            throw new IllegalArgumentException("Sessão e ingresso são obrigatórios");
        }
        Sessao sessao = sessaoRepository.buscarPorId(sessaoId)
                .orElseThrow(() -> new NoSuchElementException("Sessão não encontrada"));
        sessao.cancelarIngresso(ingressoId);
        sessaoRepository.salvar(sessao);
    }
}
