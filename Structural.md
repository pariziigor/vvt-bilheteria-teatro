# Cobertura estrutural

| Classe | Linha | Justificativa da cobertura inalcançável |
| --- | ---: | --- |
| `CriarSessaoService` | 31 | Para uma instância não nula de `DataHoraSessao`, os componentes `data`, `horaInicio` e `horaFim` não podem ser nulos: o construtor do record rejeita esses valores. Assim, os ramos em que `dataHora.data()`, `dataHora.horaInicio()` ou `dataHora.horaFim()` são nulos não podem ocorrer sem violar o invariante do Value Object. A condição `dataHora == null` é alcançável e coberta separadamente. |
