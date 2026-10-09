CREATE TABLE IF NOT EXISTS peca (
    id TEXT PRIMARY KEY,
    titulo TEXT NOT NULL,
    descricao TEXT NOT NULL,
    duracao_segundos INTEGER NOT NULL,
    classificacao TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS sessao (
    id TEXT PRIMARY KEY,
    peca_id TEXT NOT NULL,
    data TEXT NOT NULL,
    hora_inicio TEXT NOT NULL,
    hora_fim TEXT NOT NULL,
    capacidade INTEGER NOT NULL,
    valor_base_ingresso TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS ingresso (
    id TEXT PRIMARY KEY,
    sessao_id TEXT NOT NULL,
    categoria TEXT NOT NULL,
    valor TEXT NOT NULL,
    status TEXT NOT NULL,
    FOREIGN KEY (sessao_id) REFERENCES sessao(id)
);
