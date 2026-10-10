package br.ifsp.demo.application;

import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.TipoIngresso;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ComprarIngressoService {
    private final ISessaoRepository sessaoRepository;

    public ComprarIngressoService(ISessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public Ingresso comprar(UUID sessaoId, String categoria) {
        if (sessaoId == null || categoria == null) {
            throw new IllegalArgumentException("Sessão e tipo de ingresso são obrigatórios");
        }
        Sessao sessao = sessaoRepository.buscarPorId(sessaoId)
                .orElseThrow(() -> new NoSuchElementException("Sessão não encontrada"));
        if (!LocalDateTime.of(sessao.getDataHora().data(), sessao.getDataHora().horaFim()).isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("A sessão já foi encerrada");
        }
        TipoIngresso tipo = new TipoIngresso(categoria);
        BigDecimal valor = sessao.getValorBaseIngresso()
                .multiply(BigDecimal.ONE.subtract(tipo.getPercentualDesconto()))
                .setScale(2, RoundingMode.HALF_UP);
        Ingresso ingresso = new Ingresso(UUID.randomUUID(), tipo, valor);
        sessao.registrarIngresso(ingresso);
        sessaoRepository.salvar(sessao);
        return ingresso;
    }
}
