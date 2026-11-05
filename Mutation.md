# Testes de mutação

## Escopo

A rodada abaixo cobre a parte da Etapa 3 atribuída à Filippa: autenticação e cadastro/listagem de peças na API, segurança/JWT, repositórios de usuário e peça, além das classes de domínio e serviços estruturais definidos para essa parte, incluindo criação e atualização de sessões. As demais classes de sessão e ingresso atribuídas ao Igor não fazem parte desta rodada.

## Resultado do PIT

Ferramenta: PIT 1.18.1.

| Pacote | Linhas cobertas | Mutações mortas | Resultado |
| --- | ---: | ---: | ---: |
| `api` | 20/20 (100%) | 8/8 (100%) | Completo |
| `application` | 65/65 (100%) | 33/33 (100%) | Completo |
| `domain` | 51/51 (100%) | 32/33 (97%) | Um mutante equivalente |
| `persistence` | 43/43 (100%) | 7/7 (100%) | Completo |
| `security` | 62/62 (100%) | 14/14 (100%) | Completo |
| **Total** | **241/241 (100%)** | **94/95 (99%)** | **Um mutante equivalente** |

O único mutante sobrevivente substitui o retorno de `TipoIngresso.hashCode()` por `0`. Isso causa colisões de hash, mas continua respeitando o contrato obrigatório de `hashCode`: instâncias iguais têm o mesmo hash. Não é necessário escrever um teste que dependa de hashes diferentes para objetos diferentes, pois isso não é garantido pelo contrato.

## Como reproduzir

Execute da raiz do projeto:

```sh
./mvnw org.pitest:pitest-maven:mutationCoverage \
  '-DtargetClasses=br.ifsp.demo.domain.Peca,br.ifsp.demo.domain.Usuario,br.ifsp.demo.domain.DataHoraSessao,br.ifsp.demo.domain.TipoIngresso,br.ifsp.demo.application.RegistrarUsuarioService,br.ifsp.demo.application.AutenticarUsuarioService,br.ifsp.demo.application.CadastrarPecaService,br.ifsp.demo.application.ListarPecasService,br.ifsp.demo.application.CriarSessaoService,br.ifsp.demo.application.AtualizarSessaoService,br.ifsp.demo.api.AuthController,br.ifsp.demo.api.PecaController,br.ifsp.demo.persistence.PecaRepository,br.ifsp.demo.persistence.UsuarioRepository,br.ifsp.demo.security.JwtAuthenticationFilter,br.ifsp.demo.security.JwtService,br.ifsp.demo.security.SecurityConfig' \
  '-DtargetTests=br.ifsp.demo.application.*,br.ifsp.demo.domain.*,br.ifsp.demo.security.*,br.ifsp.demo.persistence.*'
```

O relatório detalhado é gerado em `target/pit-reports/index.html`.
