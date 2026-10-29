package br.ifsp.demo.api;

import br.ifsp.demo.domain.Peca;

import java.util.UUID;

public record PecaResponse(UUID id, String titulo, String descricao, long duracaoMinutos, String classificacao) {

    public static PecaResponse de(Peca peca) {
        return new PecaResponse(peca.getId(), peca.getTitulo(), peca.getDescricao(),
                peca.getDuracao().toMinutes(), peca.getClassificacao());
    }
}