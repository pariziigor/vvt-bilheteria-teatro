package br.ifsp.demo.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Ingresso {

    private final UUID id;
    private final TipoIngresso tipoIngresso;
    private final BigDecimal valor;
    private StatusIngresso status;

    public Ingresso(UUID id, TipoIngresso tipoIngresso, BigDecimal valor) {
        this.id = id;
        this.tipoIngresso = tipoIngresso;
        this.valor = valor;
        this.status = StatusIngresso.VENDIDO;
    }

    public UUID getId() {
        return id;
    }

    public TipoIngresso getTipoIngresso() {
        return tipoIngresso;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public StatusIngresso getStatus() {
        return status;
    }
}
