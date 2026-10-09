package br.ifsp.demo.domain;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public final class TipoIngresso {

    private static final Map<String, BigDecimal> DESCONTOS = Map.of(
            "INTEIRA", new BigDecimal("0.00"),
            "ESTUDANTE", new BigDecimal("0.50"),
            "PCD", new BigDecimal("0.60"),
            "IDOSO", new BigDecimal("0.65")
    );

    private final String categoria;
    private final BigDecimal percentualDesconto;

    public TipoIngresso(String categoria) {
        this.categoria = categoria;
        this.percentualDesconto = DESCONTOS.get(categoria);
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipoIngresso that)) return false;
        return Objects.equals(categoria, that.categoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoria);
    }
}
