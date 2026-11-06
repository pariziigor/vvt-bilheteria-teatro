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

### `br.ifsp.demo.domain.TipoIngresso`

| Linha | ID do mutante PIT | Justificativa |
| ---: | --- | --- |
| 44 | `MutationIdentifier [location=Location [clazz=br.ifsp.demo.domain.TipoIngresso, method=hashCode, methodDesc=()I], indexes=[11], mutator=org.pitest.mutationtest.engine.gregor.mutators.returns.PrimitiveReturnsMutator]` | Substitui o retorno de `hashCode()` por `0`. Isso causa colisões, mas respeita o contrato de Java: objetos iguais devem ter o mesmo hash; objetos diferentes não precisam ter hashes diferentes. A categoria e as comparações por `equals` permanecem iguais. Não há requisito de distribuição específica dos hashes no domínio. |

O ID completo foi recuperado em 06/11/2026 com a engine do PIT 1.18.1 sobre o bytecode compilado, selecionando apenas `hashCode()` e o mutador `PRIMITIVE_RETURNS`. O índice de instrução é **11** e o descritor do método é **`()I`**. A classe de produção não mudou desde o registro da rodada anterior. O relatório antigo não preservou seu ordinal visual; a tabela usa o `MutationIdentifier` da API do PIT, que identifica classe, método, assinatura, instrução e operador.

Esta recuperação apenas inspecionou os metadados da mutação: não executou testes, não repetiu a rodada antiga e não alterou seus resultados. O estado equivalente continua baseado na análise documentada da rodada anterior. A saída integral e o SHA-256 do bytecode estão em [docs/evidence/tipo-ingresso-mutation-id.txt](docs/evidence/tipo-ingresso-mutation-id.txt).

Para reproduzir a identificação, use Java 21 ou superior, após compilar o projeto com `./mvnw -DskipTests compile` (no Windows, `mvnw.cmd -DskipTests compile`). No PowerShell:

```powershell
$pitJar = "$env:USERPROFILE/.m2/repository/org/pitest/pitest/1.18.1/pitest-1.18.1.jar"
java --class-path "$pitJar" scripts/IdentifyEquivalentMutation.java
```

O utilitário [scripts/IdentifyEquivalentMutation.java](scripts/IdentifyEquivalentMutation.java) exige exatamente uma mutação no método selecionado e imprime seu identificador, linha e descrição. O hash do bytecode pode variar com a versão do compilador.

Todos os mutantes não equivalentes encontrados nas classes de sessões, ingressos, API e persistência foram cobertos por testes e mortos. O único mutante não morto é o equivalente de `TipoIngresso` descrito na tabela. Nenhum defeito de produção foi confirmado, portanto não foi aberta issue `bug`.
