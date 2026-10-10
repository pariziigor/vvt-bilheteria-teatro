# Sistema de Bilheteria de Teatro

## Visão geral

Este projeto implementa um sistema de bilheteria para teatro, responsável pelo gerenciamento de sessões e pela venda e cancelamento de ingressos.

O domínio foi mantido propositalmente pequeno, com foco em regras de negócio relacionadas à capacidade das sessões e ao controle dos ingressos vendidos.

Uma peça teatral pode possuir diversas sessões, cada uma ocorrendo em uma data e horário específicos. Cada sessão possui uma capacidade máxima e controla os ingressos associados a ela.

A principal regra do domínio é:

> Uma sessão não pode possuir mais ingressos vendidos do que sua capacidade.

Além disso, o sistema controla regras relacionadas à validade das sessões, tipos de ingresso, descontos e cancelamentos.

---

# Modelo de domínio

O domínio principal é composto pelas seguintes estruturas:

* `Sessao`
* `Ingresso`
* `Peca`
* `DataHoraSessao`
* `TipoIngresso`

A entidade `Sessao` representa o Aggregate Root do módulo de bilheteria.

---

# Aggregate Root

## Sessao

A `Sessao` é o Aggregate Root do domínio.

Ela representa uma apresentação específica de uma peça em determinada data e horário.

Exemplo:

```text
Peça: O Rei Leão

Sessão — 20/09/2026 19:00
├── Ingresso 001 — INTEIRA
├── Ingresso 002 — ESTUDANTE
└── Ingresso 003 — INTEIRA
```

Toda operação relacionada aos ingressos de uma sessão deve ser controlada pela própria `Sessao`.

Isso garante que as regras de negócio relacionadas à capacidade e à disponibilidade não sejam violadas.

Uma possível estrutura é:

```text
Sessao
--------------------------------
id
peca
dataHora
capacidade
valorBaseIngresso
ingressos
status
--------------------------------
comprarIngresso()
cancelarIngresso()
estaLotada()
quantidadeIngressosDisponiveis()
```

### Responsabilidades da Sessao

A `Sessao` é responsável por:

* controlar seus ingressos;
* impedir vendas acima da capacidade;
* calcular a quantidade de vagas disponíveis;
* identificar quando está lotada;
* impedir vendas em sessões encerradas;
* permitir o cancelamento de ingressos;
* liberar novamente uma vaga quando um ingresso é cancelado;
* manter a consistência entre capacidade e quantidade de ingressos vendidos.

---

# Entidades

## Ingresso

O `Ingresso` é uma entidade interna ao agregado `Sessao`.

Cada ingresso pertence a exatamente **uma sessão**.

```text
Ingresso
--------------------------------
id
tipoIngresso
valor
status
--------------------------------
cancelar()
```

Exemplo:

```text
Ingresso
├── id: 123
├── tipo: ESTUDANTE
├── valor: R$ 25,00
└── status: VENDIDO
```

O ingresso não deve ser manipulado de forma independente da sessão.

Sua criação ocorre durante uma operação de compra realizada pela `Sessao`.

Da mesma forma, ao cancelar um ingresso, sua sessão correspondente pode ser obtida diretamente pela associação existente entre os objetos.

### Status do ingresso

Os principais estados considerados são:

```text
VENDIDO
CANCELADO
```

Um ingresso cancelado deixa de ocupar uma vaga da sessão.

---

## Peca

A `Peca` representa uma obra teatral.

Ela é uma entidade própria e não faz parte do agregado `Sessao`.

```text
Peca
--------------------------------
id
titulo
descricao
duracao
classificacao
--------------------------------
adicionarSessao()
```

Uma peça pode possuir diversas sessões:

```text
Peça
├── Sessão — 19/09/2026 19:00
├── Sessão — 20/09/2026 16:00
└── Sessão — 20/09/2026 20:00
```

A `Sessao` mantém apenas uma referência para a peça à qual pertence.

Exemplo:

```text
Sessao
└── pecaId
```

Essa separação evita a criação de um agregado muito grande e mantém os limites do domínio bem definidos.

---

# Value Objects

## DataHoraSessao

`DataHoraSessao` representa a data e os horários relacionados à realização da sessão.

```text
DataHoraSessao
--------------------------------
data
horaInicio
horaFim
--------------------------------
validar()
```

Esse Value Object é responsável por impedir a existência de datas e horários inválidos.

Entre suas regras estão:

* uma sessão deve possuir data e horário;
* uma sessão não pode ser criada em uma data e horário já passados;
* a data e o horário devem representar valores válidos;
* ao atualizar uma sessão, a nova data e horário também devem permanecer válidos.

---

## TipoIngresso

`TipoIngresso` representa a categoria do ingresso e sua respectiva regra de desconto.

```text
TipoIngresso
--------------------------------
categoria
percentualDesconto
--------------------------------
ehInteira()
ehMeiaEntrada()
calcularValor(valorBase)
```

Os tipos inicialmente utilizados são:

| Tipo      | Desconto |
| --------- | -------: |
| INTEIRA   |       0% |
| ESTUDANTE |      50% |
| PCD       |      60% |
| IDOSO     |      65% |

Exemplo:

```text
TipoIngresso("INTEIRA")
TipoIngresso("ESTUDANTE")
TipoIngresso("PCD")
TipoIngresso("IDOSO")
```

O próprio Value Object deve garantir que apenas tipos válidos sejam utilizados.

Ao realizar uma compra, o valor final do ingresso é calculado a partir do valor base definido para a sessão.

Exemplo:

```text
valorBase = R$ 50,00

INTEIRA
R$ 50,00

ESTUDANTE
R$ 25,00

PCD
R$ 20,00

IDOSO
R$ 17,50
```

---

# Repositório

Como `Sessao` é o Aggregate Root, ela possui um repositório responsável por sua persistência.

```text
ISessaoRepository
        ▲
        │
SessaoRepository
```

Operações previstas:

```text
buscarPorId(id)
listar()
salvar(sessao)
remover(id)
```

O restante do domínio deve acessar as sessões através dessa abstração, evitando dependência direta de uma implementação específica de persistência.

---

# Serviços de aplicação

Os serviços de aplicação fazem a comunicação entre a camada externa da aplicação e o domínio.

Inicialmente são previstos:

```text
CriarSessaoService

ListarSessoesService

BuscarSessaoService

AtualizarSessaoService

RemoverSessaoService

ComprarIngressoService

CancelarIngressoService
```

Os serviços são responsáveis por coordenar os casos de uso.

As regras de negócio, entretanto, devem permanecer dentro das entidades e Value Objects responsáveis por garanti-las.

Por exemplo:

```text
ComprarIngressoService
        │
        ▼
     Sessao
        │
        ├── verifica capacidade
        ├── valida o tipo do ingresso
        ├── calcula o valor
        ├── cria o ingresso
        └── registra a venda
```

O `ComprarIngressoService` coordena a operação, mas quem garante que a capacidade da sessão não será ultrapassada é a própria `Sessao`.

---

# Regras de negócio

## Capacidade da sessão

Toda sessão deve possuir uma capacidade maior que zero.

```text
capacidade > 0
```

Portanto:

```text
capacidade = -1 → inválida

capacidade = 0 → inválida

capacidade = 1 → válida
```

A capacidade igual a `1` representa a menor capacidade válida possível.

---

## Limite de ingressos

A quantidade de ingressos vendidos nunca pode ultrapassar a capacidade da sessão.

Exemplo:

```text
Capacidade: 100
Ingressos vendidos: 100
```

Nesse estado, a sessão está lotada e nenhuma nova venda pode ser realizada.

```text
quantidadeVendida <= capacidade
```

---

## Última vaga disponível

Se uma sessão ainda possuir uma única vaga, a venda é permitida.

Exemplo:

```text
Capacidade: 100
Vendidos: 99
Disponíveis: 1
```

Após uma nova venda:

```text
Capacidade: 100
Vendidos: 100
Disponíveis: 0
```

A sessão passa então a ser considerada lotada.

---

## Sessão encerrada

Não é permitido vender ingressos para uma sessão que já tenha sido encerrada.

Antes de realizar a compra, a sessão deve verificar se sua data e horário permitem novas vendas.

---

## Associação entre ingresso e sessão

Cada ingresso pertence a exatamente uma sessão.

```text
Sessao
   │
   └── Ingresso
```

Um ingresso não pode simultaneamente pertencer a duas sessões diferentes.

Sua sessão de origem é determinada pela própria relação entre as entidades.

---

## Cancelamento de ingresso

Um ingresso vendido pode ser cancelado.

Quando isso ocorre:

```text
VENDIDO → CANCELADO
```

O ingresso deixa de ocupar uma vaga da sessão.

Exemplo:

```text
Capacidade: 100
Vendidos: 100
Disponíveis: 0
```

Após o cancelamento de um ingresso:

```text
Capacidade: 100
Vendidos: 99
Disponíveis: 1
```

A sessão deixa de estar lotada e volta a permitir a venda de um ingresso.

---

## Cancelamento duplicado

Um ingresso que já possui status `CANCELADO` não pode ser cancelado novamente.

Essa regra evita operações redundantes e alterações incorretas na quantidade de vagas disponíveis.

---

## Alteração da capacidade

A capacidade de uma sessão pode ser alterada desde que permaneça válida.

Ela nunca pode ser:

```text
<= 0
```

Além disso, uma sessão que já possui ingressos vendidos não pode ter sua capacidade reduzida para uma quantidade inferior ao número de ingressos atualmente vendidos.

Exemplo:

```text
Vendidos: 80
```

São válidas:

```text
Nova capacidade: 100
Nova capacidade: 81
Nova capacidade: 80
```

É inválida:

```text
Nova capacidade: 79
```

Caso contrário, existiriam mais ingressos vendidos do que vagas disponíveis na sessão.

---

## Remoção de sessão

Uma sessão existente pode ser removida quando não possui ingressos vendidos.

Se houver ingressos vendidos associados à sessão, sua remoção deve ser recusada.

Essa regra evita a exclusão de uma sessão que ainda possui vendas registradas.

---

## Peça associada

Toda sessão deve estar associada a uma peça existente.

Portanto, uma sessão não pode ser criada ou atualizada:

* sem uma peça associada;
* com o identificador de uma peça inexistente.

---

## Tipos de ingresso

O sistema aceita inicialmente somente:

```text
INTEIRA
ESTUDANTE
PCD
IDOSO
```

Qualquer outro tipo deve ser considerado inválido.

O tipo também não pode estar ausente durante uma compra.

---

# Cálculo de disponibilidade

A quantidade de ingressos disponíveis pode ser calculada utilizando:

```text
disponiveis = capacidade - quantidadeIngressosVendidos
```

Ingressos com status `CANCELADO` não devem ser considerados na quantidade de ingressos vendidos.

Exemplo:

```text
Capacidade: 50

Ingressos:
40 VENDIDOS
3 CANCELADOS
```

Resultado:

```text
Ingressos vendidos: 40
Ingressos disponíveis: 10
```

---

# Invariantes do agregado

A entidade `Sessao` deve permanecer sempre em um estado válido.

As principais invariantes são:

```text
capacidade > 0
```

```text
quantidadeIngressosVendidos <= capacidade
```

```text
Sessao deve possuir uma data e horário válidos
```

```text
Sessao deve estar associada a uma Peca existente
```

```text
Ingresso pertence a exatamente uma Sessao
```

```text
Ingresso cancelado não ocupa vaga
```

```text
Sessao encerrada não permite novas vendas
```

Essas regras devem ser protegidas pelo domínio, independentemente da forma como a aplicação seja utilizada.

---

# Estrutura conceitual

```text
Peca
│
└── Sessao
      │
      ├── DataHoraSessao
      │
      ├── Capacidade
      │
      ├── ValorBaseIngresso
      │
      └── Ingressos
            │
            ├── TipoIngresso
            ├── Valor
            └── Status
```

Dentro do módulo de bilheteria:

```text
Sessao
   │
   ├── Aggregate Root
   │
   └── controla
          │
          └── Ingresso
```

Enquanto:

```text
DataHoraSessao
TipoIngresso
```

são Value Objects utilizados para encapsular regras específicas do domínio.
Assim, regras como capacidade máxima, disponibilidade, cancelamento e tipos de ingresso permanecem protegidas independentemente da camada que esteja utilizando o sistema.
---

# Implementação atual

O projeto utiliza Spring Boot, Java 21, Spring JDBC e SQLite. O domínio não utiliza JPA, Hibernate ou Lombok.

## Organização

```text
src/main/java/br/ifsp/demo
├── api          # Controladores REST e requests
├── application  # Serviços de aplicação
├── domain       # Agregados, entidades, value objects e contratos
└── persistence  # Repositórios JDBC
```

`Sessao` é o Aggregate Root e controla os ingressos, a capacidade e a disponibilidade.

## API REST

Base URL: `/api/v1/sessoes`

| Método | Endpoint | Operação |
| --- | --- | --- |
| POST | `/api/v1/sessoes` | Criar sessão |
| GET | `/api/v1/sessoes` | Listar sessões |
| GET | `/api/v1/sessoes/{sessaoId}` | Buscar sessão e disponibilidade |
| PUT | `/api/v1/sessoes/{sessaoId}` | Atualizar sessão |
| DELETE | `/api/v1/sessoes/{sessaoId}` | Remover sessão |
| POST | `/api/v1/sessoes/{sessaoId}/ingressos` | Comprar ingresso |
| DELETE | `/api/v1/sessoes/{sessaoId}/ingressos/{ingressoId}` | Cancelar ingresso |

Respostas principais: `201 Created`, `204 No Content`, `400 Bad Request`, `404 Not Found` e `409 Conflict`.

## Persistência

O banco é SQLite, configurado em `src/main/resources/application.properties`. O schema está em `src/main/resources/schema.sql` e contém as tabelas `peca`, `sessao` e `ingresso`. A persistência usa `JdbcTemplate`, sem JPA ou Lombok.

## Regras de negócio

- capacidade e valor base devem ser positivos;
- data e horários da sessão são obrigatórios, e o fim deve ser posterior ao início;
- sessões devem ocorrer no futuro;
- tipos aceitos: `INTEIRA`, `ESTUDANTE`, `PCD` e `IDOSO`;
- descontos: 0%, 50%, 60% e 65%, respectivamente;
- ingressos vendidos nunca ultrapassam a capacidade;
- ingressos cancelados não ocupam vagas;
- sessões encerradas não aceitam vendas;
- sessões com ingressos vendidos não podem ser removidas;
- disponibilidade = capacidade menos ingressos vendidos.

## Testes

Os testes usam JUnit 5, Mockito e AssertJ. As tags são `UnitTest`, `TDD` e `Functional`.

```powershell
mvn clean test
mvn -Dtest=TddSuite test
mvn -Dtest=FunctionalSuite test
mvn -Dtest=UnitTestSuite test
```

Os testes cobrem criação, busca, atualização e remoção de sessões, compra e cancelamento de ingressos, descontos, capacidade, disponibilidade, sessões encerradas e entradas inválidas.