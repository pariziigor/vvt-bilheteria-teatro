# Testes de mutação

## Alvos e execuções

Ferramenta: PIT 1.18.1. O registro anterior foi mantido sem repetir seus 17 alvos. A rodada complementar executou os 33 alvos restantes; juntos, os dois relatórios incluem as 50 classes Java de produção do projeto.

**Alvos já registrados:**

- `domain`: `Peca`, `Usuario`, `DataHoraSessao`, `TipoIngresso`;
- `application`: `RegistrarUsuarioService`, `AutenticarUsuarioService`, `CadastrarPecaService`, `ListarPecasService`, `CriarSessaoService`, `AtualizarSessaoService`;
- `api`: `AuthController`, `PecaController`;
- `persistence`: `PecaRepository`, `UsuarioRepository`;
- `security`: `JwtAuthenticationFilter`, `JwtService`, `SecurityConfig`.

**Alvos incluídos na rodada complementar:**

- `br.ifsp.demo.DemoAuthAppApplication`;
- `api`: `AtualizarPecaRequest`, `AtualizarSessaoRequest`, `AutenticacaoResponse`, `AutenticarUsuarioRequest`, `CadastrarPecaRequest`, `ComprarIngressoRequest`, `CriarSessaoRequest`, `IngressoResponse`, `OpenApiConfig`, `PecaResponse`, `RegistrarUsuarioRequest`, `SessaoController`, `SessaoResponse`, `UsuarioResponse`;
- `application`: `AtualizarPecaService`, `BuscarSessaoService`, `CancelarIngressoService`, `ComprarIngressoService`, `CredenciaisInvalidasException`, `ListarSessoesService`, `RemoverPecaService`, `RemoverSessaoService`;
- `domain`: `Ingresso`, `IPecaRepository`, `ISessaoRepository`, `IUsuarioRepository`, `PapelUsuario`, `Sessao`, `StatusIngresso`;
- `persistence`: `DatabaseSchemaMigration`, `DemoAccountsInitializer`, `SessaoRepository`.

A rodada complementar usou `-DtargetTests=br.ifsp.demo.*`. Os 33 alvos foram enviados ao PIT; 16 geraram mutações com os mutadores configurados. O relatório PIT detalhado fica em `target/pit-reports/index.html` após a execução.

## Resultado consolidado

| Pacote | Linhas cobertas | Mutantes mortos / gerados | Sem cobertura |
| --- | ---: | ---: | ---: |
| `api` | 82/82 (100%) | 39/39 | 0 |
| `application` | 143/143 (100%) | 69/69 | 0 |
| `domain` | 137/137 (100%) | 82/83 | 0 |
| `persistence` | 120/123 (98%) | 23/23 | 0 |
| `security` | 62/62 (100%) | 14/14 | 0 |
| **Total** | **544/547 (99%)** | **227/228 (99.6%)** | **0** |

Na rodada complementar final, PIT gerou e matou 133 mutantes, sem sobreviventes ou mutantes `NO_COVERAGE`; foram examinadas 90 classes de teste. A cobertura de linhas dos alvos complementares foi 303/306 (99%) e a força de teste foi 133/133 (100%). No consolidado, resta apenas o equivalente da rodada antiga, justificado abaixo; os outros 227/227 mutantes alcançados foram mortos.

## Mutantes equivalentes

| Classe | Linha | ID do mutante PIT | Justificativa |
| --- | ---: | --- | --- |
| `TipoIngresso` | 44 | `org.pitest.mutationtest.engine.gregor.mutators.returns.PrimitiveReturnsMutator` — substituir o retorno inteiro por `0` | O mutante faz `hashCode()` retornar `0` para todas as instâncias. Isso causa colisões, mas respeita o contrato de Java: objetos iguais devem ter o mesmo hash; objetos diferentes não precisam ter hashes diferentes. |

O registro antigo preservou a classe, a linha e a descrição da mutação, mas não o ordinal numérico do relatório PIT. O identificador acima usa o mutador PIT e a localização da alteração, que identificam a mutação registrada.

Todos os mutantes não equivalentes encontrados nas classes de sessões, ingressos, API e persistência foram cobertos por testes e mortos. O único mutante não morto é o equivalente de `TipoIngresso` descrito na tabela. Nenhum defeito de produção foi confirmado, portanto não foi aberta issue `bug`.
