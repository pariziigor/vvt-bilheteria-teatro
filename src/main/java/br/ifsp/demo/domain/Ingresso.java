package br.ifsp.demo.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Ingresso {

    private final UUID id;
    private final TipoIngresso tipoIngresso;
    private final BigDecimal valor;
    private StatusIngresso status;

    public Ingresso(UUID id, TipoIngresso tipoIngresso, BigDecimal valor) {
        this(id, tipoIngresso, valor, StatusIngresso.VENDIDO);
    }

    public Ingresso(UUID id, TipoIngresso tipoIngresso, BigDecimal valor, StatusIngresso status) {
        this.id = id;
        this.tipoIngresso = tipoIngresso;
        this.valor = valor;
        this.status = status;
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

    public void cancelar() {
        if (status != StatusIngresso.VENDIDO) {
            throw new IllegalStateException("Somente ingressos vendidos podem ser cancelados");
        }
        status = StatusIngresso.CANCELADO;
    }
}
