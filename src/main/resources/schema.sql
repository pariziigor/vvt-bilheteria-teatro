CREATE TABLE IF NOT EXISTS sessao (
    id TEXT PRIMARY KEY,
    peca_id TEXT NOT NULL,
    data TEXT NOT NULL,
    hora_inicio TEXT NOT NULL,
    hora_fim TEXT NOT NULL,
    capacidade INTEGER NOT NULL,
    valor_base_ingresso TEXT NOT NULL
);
