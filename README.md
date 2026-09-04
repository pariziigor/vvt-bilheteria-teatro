# Sistema de Bilheteria de Teatro

## Verificação, Validação e Teste de Software

**Instituição:** IFSP Câmpus São Carlos

**Disciplina:** Verificação, Validação e Teste de Software

**Professor:** Dr. Lucas Oliveira

**Grupo:** Igor, Yasmim, Filippa


---

## 1. Descrição do Projeto

O projeto consiste no desenvolvimento de um **Sistema de Bilheteria de Teatro**, com foco na aplicação de conceitos de Verificação, Validação e Teste de Software.

O sistema tem como objetivo permitir o cadastro de sessões de teatro, emissão e controle de ingressos, confirmação de presença e geração de relatórios de ocupação.

As regras de negócio relacionadas à meia-entrada, capacidade da sessão e distanciamento entre assentos são implementadas e validadas no domínio da aplicação.

---

## 2. Agregado DDD

O sistema utiliza o conceito de **Aggregate** do Domain-Driven Design (DDD).

### Aggregate Root

#### Sessao

A classe `Sessao` representa a sessão de teatro e é a **Aggregate Root** do sistema.

Ela é responsável por manter as regras de consistência da sessão, incluindo:

* Data e horário;
* Capacidade total;
* Assentos disponíveis e ocupados;
* Cota de meia-entrada;
* Regra de distanciamento entre assentos;
* Controle dos ingressos emitidos.

### Entity

#### IngressoEmitido

Representa um ingresso emitido dentro de uma sessão.

Possui identidade própria e pode assumir os seguintes estados:

* `EMITIDO`
* `CONFIRMADO`
* `CANCELADO`

O `IngressoEmitido` só pode ser acessado através da `Sessao`.

### Value Objects

#### Assento

Representa um assento do teatro.

É composto por:

* Fileira;
* Número.

O objeto é imutável.

#### CategoriaPreco

Representa a categoria de preço do ingresso.

Possui:

* Tipo:

  * Inteira;
  * Meia;
  * Idoso;
  * PCD.
* Valor.

### Repository

#### SessaoRepository

É o único ponto de acesso à agregação `Sessao`.

O repositório é responsável pelo armazenamento e recuperação das sessões.

---

## 3. Invariantes do Domínio

A `Sessao` deve garantir que as seguintes regras sejam sempre respeitadas:

### Cota de meia-entrada

A quantidade de ingressos de meia-entrada emitidos não pode ultrapassar **40% da capacidade total da sessão**.

```text
Ingressos de meia-entrada <= 40% da capacidade total
```

### Distanciamento

Quando o modo de distanciamento estiver ativo, nenhum assento adjacente a um assento ocupado pode ser vendido.

### Capacidade

A quantidade de ingressos emitidos nunca pode ultrapassar a capacidade total da sessão.

```text
Ocupação <= 100% da capacidade
```

---

# 4. Application Services

O sistema possui um Application Service para cada User Story:

1. `CadastrarSessaoService`
2. `EmitirIngressoService`
3. `ConfirmarPresencaService`
4. `RelatorioOcupacaoService`

---

# 5. User Stories e Cenários BDD

## US01 — Cadastrar sessão de teatro

**Como** funcionário da bilheteria,
**eu quero** cadastrar uma sessão de teatro com a configuração de cotas (meia-entrada, idoso, PCD),
**para que** a venda de ingressos respeite os limites legais e de distanciamento desde o início.

### Scenario 1.1 — Cadastro válido

**Dado que** informo data, horário, capacidade total e ativo o modo de distanciamento,
**quando** cadastro a sessão,
**então** a sessão é criada com status disponível e cota de meia-entrada calculada em 40% da capacidade.

### Scenario 1.2 — Capacidade inválida

**Dado que** informo uma capacidade total menor ou igual a zero,
**quando** tento cadastrar a sessão,
**então** o cadastro é rejeitado e nenhuma sessão é criada.

### Scenario 1.3 — Data no passado

**Dado que** informo uma data anterior à data atual,
**quando** tento cadastrar a sessão,
**então** o cadastro é rejeitado.

---

## US02 — Emitir ingressos

**Como** funcionário da bilheteria,
**eu quero** emitir ingressos para os clientes em diferentes quantidades e tipos,
**para que** a venda ocorra sem violar as cotas de meia-entrada nem as regras de distanciamento.

### Scenario 2.1 — Emissão dentro da cota

**Dado que** a sessão tem menos de 40% dos ingressos emitidos como meia-entrada,
**quando** emito um novo ingresso meia-entrada para um assento livre,
**então** o ingresso é emitido com status `EMITIDO`.

### Scenario 2.2 — Limite de 40% atingido

**Dado que** a sessão já emitiu exatamente 40% de ingressos meia-entrada,
**quando** solicito a emissão de mais um ingresso meia-entrada,
**então** a operação é rejeitada.

### Scenario 2.3 — Ocupação em 100%

**Dado que** todos os assentos da sessão já estão ocupados,
**quando** solicito a emissão de um novo ingresso,
**então** a operação é rejeitada por capacidade esgotada.

### Scenario 2.4 — Bloqueio por distanciamento

**Dado que** o modo de distanciamento está ativo e o assento adjacente já está ocupado,
**quando** tento emitir um ingresso para esse assento,
**então** a operação é rejeitada.

### Scenario 2.5 — Emissão em lote

**Dado que** solicito a emissão de 3 ingressos inteiros e 1 meia-entrada em uma única operação, todos dentro dos limites,
**quando** confirmo a emissão,
**então** os 4 ingressos são emitidos com status `EMITIDO`.

---

## US03 — Confirmar presença/uso do ingresso

**Como** funcionário da bilheteria,
**eu quero** confirmar a presença do cliente na entrada da sessão,
**para que** o controle de uso dos ingressos fique registrado.

### Scenario 3.1 — Confirmação válida

**Dado que** um ingresso está com status `EMITIDO`,
**quando** confirmo a presença do cliente,
**então** o status do ingresso muda para `CONFIRMADO`.

### Scenario 3.2 — Ingresso já cancelado

**Dado que** um ingresso está com status `CANCELADO`,
**quando** tento confirmar a presença,
**então** a operação é rejeitada.

### Scenario 3.3 — Confirmação duplicada

**Dado que** um ingresso já está com status `CONFIRMADO`,
**quando** tento confirmar a presença novamente,
**então** a operação é rejeitada.

---

## US04 — Relatório de ocupação

**Como** gestor do teatro,
**eu quero** visualizar um relatório de ocupação filtrado por data, horário e tipo de ingresso,
**para que** eu possa acompanhar o faturamento e a taxa de ocupação das sessões.

### Scenario 4.1 — Relatório com filtros aplicados

**Dado que** existem sessões cadastradas em diferentes datas e horários,
**quando** solicito o relatório filtrando por uma data específica e tipo `meia-entrada`,
**então** o relatório retorna apenas os ingressos que atendem aos filtros, com faturamento e taxa de ocupação calculados.

### Scenario 4.2 — Nenhum resultado encontrado

**Dado que** não existem ingressos emitidos que atendam aos filtros informados,
**quando** solicito o relatório,
**então** o relatório retorna faturamento zero e taxa de ocupação zero.

### Scenario 4.3 — Relatório sem filtros

**Dado que** não informo nenhum filtro,
**quando** solicito o relatório,
**então** o relatório retorna os dados agregados de todas as sessões.

---

# 6. Estrutura de Services

| Service                    | Responsabilidade                                | User Story |
| -------------------------- | ----------------------------------------------- | ---------- |
| `CadastrarSessaoService`   | Cadastro e validação de novas sessões           | US01       |
| `EmitirIngressoService`    | Emissão individual ou em lote de ingressos      | US02       |
| `ConfirmarPresencaService` | Confirmação do uso dos ingressos                | US03       |
| `RelatorioOcupacaoService` | Geração de relatórios de ocupação e faturamento | US04       |

---

# 7. Fluxo Geral do Sistema

```text
                    +----------------------+
                    |  Cadastrar Sessão    |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    |       Sessao         |
                    |   Aggregate Root     |
                    +----------+-----------+
                               |
              +----------------+----------------+
              |                |                |
              v                v                v
       Emitir Ingresso  Confirmar Presença  Relatório
              |                |                |
              v                v                v
        IngressoEmitido   Alterar Estado    Ocupação /
              |                              Faturamento
              v
       Regras de Domínio
       - Cota 40%
       - Capacidade
       - Distanciamento
```

---

# 8. Estados do Ingresso

O `IngressoEmitido` possui três estados principais:

```text
          +---------+
          | EMITIDO |
          +----+----+
               |
               | confirmação
               v
        +-------------+
        | CONFIRMADO  |
        +-------------+

          +---------+
          | EMITIDO |
          +----+----+
               |
               | cancelamento
               v
        +-------------+
        | CANCELADO   |
        +-------------+
```

As transições de estado devem respeitar as regras definidas pelo domínio.

Um ingresso `CANCELADO` não pode ser confirmado.

Um ingresso `CONFIRMADO` não pode ser confirmado novamente.

---

# 9. Critérios de Validação

O sistema deve validar, entre outros, os seguintes pontos:

* Capacidade da sessão maior que zero;
* Data e horário válidos;
* Data da sessão não pode estar no passado;
* Limite de 40% para ingressos de meia-entrada;
* Capacidade máxima da sessão;
* Disponibilidade do assento;
* Regras de distanciamento;
* Estado atual do ingresso;
* Confirmação de presença somente para ingressos `EMITIDO`;
* Cálculo correto de faturamento;
* Cálculo correto da taxa de ocupação;
* Aplicação correta dos filtros do relatório.
