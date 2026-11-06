package br.ifsp.demo.application;

import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;

@Service
public class CancelarIngressoService {

    private final ISessaoRepository sessaoRepository;

    public CancelarIngressoService(ISessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public void cancelar(UUID sessaoId, UUID ingressoId) {
        cancelar(sessaoId, ingressoId, null, true);
    }

    public void cancelar(UUID sessaoId, UUID ingressoId, String usuarioEmail, boolean administrador) {
        if (sessaoId == null || ingressoId == null) {
            throw new IllegalArgumentException("Sessão e ingresso são obrigatórios");
        }
        Sessao sessao = sessaoRepository.buscarPorId(sessaoId)
                .orElseThrow(() -> new NoSuchElementException("Sessão não encontrada"));
        if (!administrador) {
            Ingresso ingresso = sessao.getIngressos().stream()
                    .filter(atual -> atual.getId().equals(ingressoId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Ingresso não encontrado na sessão"));
            if (!Objects.equals(usuarioEmail, ingresso.getCompradorEmail())) {
                throw new AccessDeniedException("Só o comprador pode cancelar este ingresso");
            }
        }
        sessao.cancelarIngresso(ingressoId);
        sessaoRepository.salvar(sessao);
    }
}
