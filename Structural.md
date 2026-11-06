# Cobertura estrutural

Os testes estruturais estão marcados com `@Tag("Structural")` e `@Tag("UnitTest")`. A suíte final `UnitTestSuite` executou 185 testes, sem falhas, erros ou testes ignorados. A cobertura de decisões/branches foi coletada pelo agente de cobertura da IntelliJ IDEA 2026.2.2.

## Cobertura de branches

| Classe | Branches cobertos / total |
| --- | ---: |
| `domain.DataHoraSessao` | 8/8 |
| `domain.Ingresso` | 2/2 |
| `domain.Peca` | 18/18 |
| `domain.Sessao` | 32/32 |
| `domain.TipoIngresso` | 8/8 |
| `domain.Usuario` | 8/8 |
| `application.AtualizarSessaoService` | 4/4 |
| `application.AutenticarUsuarioService` | 4/4 |
| `application.BuscarSessaoService` | 2/2 |
| `application.CancelarIngressoService` | 8/8 |
| `application.ComprarIngressoService` | 6/6 |
| `application.CriarSessaoService` | 17/20 |
| `application.RegistrarUsuarioService` | 6/6 |
| `application.RemoverPecaService` | 4/4 |
| `application.RemoverSessaoService` | 6/6 |

`ListarSessoesService`, `ListarPecasService`, `CadastrarPecaService` e `AtualizarPecaService` não possuem decisões; `StatusIngresso` e os contratos de repositório também não geram branches. Portanto, 0/0 nesses tipos não representa ramo não testado.

## Ramos inalcançáveis

| Classe | Linha | Justificativa da cobertura inalcançável |
| --- | ---: | --- |
| `CriarSessaoService` | 31 | Para uma instância não nula de `DataHoraSessao`, os componentes `data`, `horaInicio` e `horaFim` não podem ser nulos: o construtor do record rejeita esses valores. Assim, os três ramos que verificam componentes nulos não podem ocorrer sem violar o invariante do Value Object. A condição `dataHora == null` é alcançável e está coberta separadamente. |

Os demais branches do projeto cobertos por esta verificação foram exercitados. Não foram identificados outros ramos inalcançáveis nas classes de domínio e serviços avaliadas.
