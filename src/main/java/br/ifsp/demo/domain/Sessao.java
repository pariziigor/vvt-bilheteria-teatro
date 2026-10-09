package br.ifsp.demo.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sessao {

    private final UUID id;
    private UUID pecaId;
    private DataHoraSessao dataHora;
    private int capacidade;
    private BigDecimal valorBaseIngresso;
    private final List<Ingresso> ingressos = new ArrayList<>();

    public Sessao(UUID id, UUID pecaId, DataHoraSessao dataHora, int capacidade, BigDecimal valorBaseIngresso) {
        this.id = id;
        this.pecaId = pecaId;
        this.dataHora = dataHora;
        this.capacidade = capacidade;
        this.valorBaseIngresso = valorBaseIngresso;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPecaId() {
        return pecaId;
    }

    public DataHoraSessao getDataHora() {
        return dataHora;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public BigDecimal getValorBaseIngresso() {
        return valorBaseIngresso;
    }

    public List<Ingresso> getIngressos() {
        return List.copyOf(ingressos);
    }
}
