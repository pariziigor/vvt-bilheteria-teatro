package br.ifsp.demo.api;

import br.ifsp.demo.domain.Sessao;
import br.ifsp.demo.domain.DataHoraSessao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record SessaoResponse(
        UUID id,
        UUID pecaId,
        DataHoraSessao dataHora,
        int capacidade,
        BigDecimal valorBaseIngresso,
        long quantidadeIngressosVendidos,
        long quantidadeIngressosDisponiveis,
        List<IngressoResponse> ingressos
) {
    public static SessaoResponse de(Sessao sessao, String usuarioEmail, boolean administrador) {
        List<IngressoResponse> ingressosVisiveis = sessao.getIngressos().stream()
                .filter(ingresso -> administrador || usuarioEmail != null
                        && Objects.equals(usuarioEmail, ingresso.getCompradorEmail()))
                .map(IngressoResponse::de)
                .toList();
        return new SessaoResponse(sessao.getId(), sessao.getPecaId(), sessao.getDataHora(),
                sessao.getCapacidade(), sessao.getValorBaseIngresso(), sessao.quantidadeIngressosVendidos(),
                sessao.quantidadeIngressosDisponiveis(), ingressosVisiveis);
    }
}
