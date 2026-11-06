package br.ifsp.demo.api;

import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.StatusIngresso;

import java.math.BigDecimal;
import java.util.UUID;

public record IngressoResponse(UUID id, String tipoIngresso, BigDecimal valor, StatusIngresso status) {

    public static IngressoResponse de(Ingresso ingresso) {
        return new IngressoResponse(ingresso.getId(), ingresso.getTipoIngresso().getCategoria(),
                ingresso.getValor(), ingresso.getStatus());
    }
}
