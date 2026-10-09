package br.ifsp.demo.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

public class Sessao {

    private final UUID id;
    private UUID pecaId;
    private DataHoraSessao dataHora;
    private int capacidade;
    private BigDecimal valorBaseIngresso;
    private final List<Ingresso> ingressos = new ArrayList<>();

    public Sessao(UUID id, UUID pecaId, DataHoraSessao dataHora, int capacidade, BigDecimal valorBaseIngresso) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade da sessão deve ser positiva");
        }
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

    public void atualizar(UUID pecaId, DataHoraSessao dataHora, int capacidade, BigDecimal valorBaseIngresso) {
        this.pecaId = pecaId;
        this.dataHora = dataHora;
        this.capacidade = capacidade;
        this.valorBaseIngresso = valorBaseIngresso;
    }

    public void registrarIngresso(Ingresso ingresso) {
        Objects.requireNonNull(ingresso, "O ingresso é obrigatório");
        if (ingressos.stream().anyMatch(atual -> atual.getId().equals(ingresso.getId()))) {
            throw new IllegalArgumentException("Já existe um ingresso com esse identificador na sessão");
        }
        long vendidos = ingressos.stream()
                .filter(atual -> atual.getStatus() == StatusIngresso.VENDIDO)
                .count();
        if (ingresso.getStatus() == StatusIngresso.VENDIDO && vendidos >= capacidade) {
            throw new IllegalStateException("A sessão está lotada");
        }
        ingressos.add(ingresso);
    }

    public void cancelarIngresso(UUID ingressoId) {
        Ingresso ingresso = ingressos.stream()
                .filter(atual -> atual.getId().equals(ingressoId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Ingresso não encontrado na sessão"));
        ingresso.cancelar();
    }
}
