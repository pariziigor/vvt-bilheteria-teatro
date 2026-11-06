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
| `api` | 32/82 (39%) | 9/39 | 30 |
| `application` | 138/143 (97%) | 65/69 | 4 |
| `domain` | 137/137 (100%) | 82/83 | 0 |
| `persistence` | 120/123 (98%) | 23/23 | 0 |
| `security` | 62/62 (100%) | 14/14 | 0 |
| **Total** | **489/547 (89%)** | **193/228 (85%)** | **34** |

Na nova rodada, PIT gerou 133 mutantes: matou 99, não encontrou sobreviventes e marcou 34 como `NO_COVERAGE`. Os 34 mutantes sem cobertura estão em `IngressoResponse` (1), `SessaoController` (24), `SessaoResponse` (5) e `AtualizarPecaService` (4). Assim, a força de teste dos mutantes alcançados na rodada complementar foi 99/99 (100%). No consolidado, um sobrevivente da rodada anterior é equivalente e está justificado abaixo; a força de teste restante é 193/194 (99%).

## Mutantes equivalentes

| Classe | Linha | ID do mutante PIT | Justificativa |
| --- | ---: | --- | --- |
| `TipoIngresso` | 44 | `org.pitest.mutationtest.engine.gregor.mutators.returns.PrimitiveReturnsMutator` — substituir o retorno inteiro por `0` | O mutante faz `hashCode()` retornar `0` para todas as instâncias. Isso causa colisões, mas respeita o contrato de Java: objetos iguais devem ter o mesmo hash; objetos diferentes não precisam ter hashes diferentes. |

O registro antigo preservou a classe, a linha e a descrição da mutação, mas não o ordinal numérico do relatório PIT. O identificador acima usa o mutador PIT e a localização da alteração, que identificam a mutação registrada.

Os mutantes vivos encontrados nesta análise nas classes de sessões, ingressos e persistência foram cobertos por testes e mortos. Nenhum defeito de produção foi confirmado, portanto não foi aberta issue `bug`.
