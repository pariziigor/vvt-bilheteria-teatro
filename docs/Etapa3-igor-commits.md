# Rastreabilidade dos testes de Igor — Etapa 3

Escopo: os **47 métodos de teste novos** introduzidos por Igor após o commit compartilhado `7b932294f1bd`.

Na revisão formal de 06/11/2026, os 17 commits locais ainda não publicados foram reorganizados em 52 commits: 47 introduzem exatamente um método de teste cada; os outros cinco registram ajustes em testes existentes, configuração da suíte e documentação. Os commits que introduzem testes usam o prefixo `test:` seguido da classe e da descrição do cenário expressa pelo nome do método.

Esta é uma reorganização posterior do histórico local, identificada no corpo de cada commit. Foram preservados o nome, o e-mail e a data de autoria original de Igor Parizi. A igualdade da árvore Git final com a versão anterior foi verificada antes de atualizar a branch: nenhum arquivo de produção ou teste foi alterado pela divisão dos commits.

Uma auditoria das diferenças entre cada commit e seu pai confirmou exatamente um teste novo nos 47 commits listados. Métodos auxiliares, imports, anotações e preparação dos testes acompanham o primeiro cenário que os utiliza.

O histórico compartilhado anterior ao limite acima foi preservado, incluindo as contribuições de Filippa e Yasmim. Esta tabela atesta o requisito de um commit por teste no escopo local de Igor; não reclassifica nem altera os commits já publicados dos demais integrantes e não pretende comprovar equilíbrio de participação apenas pela contagem de commits.

Backup local anterior à reorganização: `backup/etapa3-igor-before-formal-fixes-20261106`, apontando para `6ffbbeb8dd26cbeb4d3e7444cac87ca3b7aa6295`.

| Classe | Teste novo | Commit `test:` |
| --- | --- | --- |
| [SessaoStructuralTest](../src/test/java/br/ifsp/demo/domain/SessaoStructuralTest.java) | `rejeitaDadosObrigatoriosAusentesNaCriacao` | `79495040fdd2` |
| [SessaoStructuralTest](../src/test/java/br/ifsp/demo/domain/SessaoStructuralTest.java) | `rejeitaValorBaseNaoPositivoECapacidadeNaoPositiva` | `d2ba20029c24` |
| [SessaoStructuralTest](../src/test/java/br/ifsp/demo/domain/SessaoStructuralTest.java) | `calculaDisponibilidadeConsiderandoSomenteIngressosVendidos` | `c4976baa4864` |
| [SessaoStructuralTest](../src/test/java/br/ifsp/demo/domain/SessaoStructuralTest.java) | `atualizaSessaoQuandoNovaCapacidadeNaoFicaAbaixoDasVendas` | `7c17c16e0377` |
| [SessaoStructuralTest](../src/test/java/br/ifsp/demo/domain/SessaoStructuralTest.java) | `rejeitaAtualizacaoQueViolaValidacoesDeCapacidadeDadosEData` | `f6d503a58427` |
| [SessaoStructuralTest](../src/test/java/br/ifsp/demo/domain/SessaoStructuralTest.java) | `registraIngressoERejeitaNuloDuplicadoOuVendaAcimaDaCapacidade` | `1aa85fe1d8e0` |
| [SessaoStructuralTest](../src/test/java/br/ifsp/demo/domain/SessaoStructuralTest.java) | `cancelaIngressoExistenteAntesDoInicioESinalizaAusenteOuSessaoIniciada` | `cb6672d1e793` |
| [IngressoStructuralTest](../src/test/java/br/ifsp/demo/domain/IngressoStructuralTest.java) | `construtoresPreservamDadosEValoresPadrao` | `081b9ace3a45` |
| [IngressoStructuralTest](../src/test/java/br/ifsp/demo/domain/IngressoStructuralTest.java) | `cancelaIngressoVendidoERejeitaOutrosEstados` | `19ed4b41ff89` |
| [StatusIngressoStructuralTest](../src/test/java/br/ifsp/demo/domain/StatusIngressoStructuralTest.java) | `exponeOsEstadosDeIngressoNaOrdemDoContrato` | `d04d4af6396e` |
| [ListarSessoesServiceStructuralTest](../src/test/java/br/ifsp/demo/application/ListarSessoesServiceStructuralTest.java) | `devolveCopiaImutavelDaListaDoRepositorio` | `52646f9d7a4b` |
| [BuscarSessaoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/BuscarSessaoServiceStructuralTest.java) | `rejeitaIdentificadorAusenteSemConsultarRepositorio` | `e7be8e96a12f` |
| [BuscarSessaoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/BuscarSessaoServiceStructuralTest.java) | `retornaSessaoEncontrada` | `e11bb036ffa2` |
| [BuscarSessaoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/BuscarSessaoServiceStructuralTest.java) | `informaQuandoSessaoNaoExiste` | `98c7a97c32d8` |
| [RemoverSessaoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/RemoverSessaoServiceStructuralTest.java) | `rejeitaIdentificadorAusenteSemConsultarRepositorio` | `544f6499c768` |
| [RemoverSessaoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/RemoverSessaoServiceStructuralTest.java) | `informaQuandoSessaoNaoExiste` | `1c579d89d481` |
| [RemoverSessaoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/RemoverSessaoServiceStructuralTest.java) | `removeSessaoSemIngressosVendidosIncluindoApenasCancelados` | `4eaca7d57650` |
| [RemoverSessaoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/RemoverSessaoServiceStructuralTest.java) | `recusaRemoverSessaoComIngressoVendido` | `09f965fc1316` |
| [ComprarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/ComprarIngressoServiceStructuralTest.java) | `rejeitaSessaoOuCategoriaAusenteSemConsultarRepositorio` | `96fd7f3aa3a0` |
| [ComprarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/ComprarIngressoServiceStructuralTest.java) | `informaQuandoSessaoNaoExiste` | `b709f0b970c1` |
| [ComprarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/ComprarIngressoServiceStructuralTest.java) | `rejeitaSessaoEncerrada` | `95d9e82e1f44` |
| [ComprarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/ComprarIngressoServiceStructuralTest.java) | `compraIngressoDoCompradorERegistraOValorEDesconto` | `14000ddec5bc` |
| [ComprarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/ComprarIngressoServiceStructuralTest.java) | `rejeitaCategoriaInvalidaESessaoLotada` | `624a9e9b9005` |
| [CancelarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/CancelarIngressoServiceStructuralTest.java) | `rejeitaIdentificadoresAusentesSemConsultarRepositorio` | `2806ac2d1468` |
| [CancelarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/CancelarIngressoServiceStructuralTest.java) | `informaQuandoSessaoNaoExiste` | `9b88de87f44a` |
| [CancelarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/CancelarIngressoServiceStructuralTest.java) | `clienteNaoPodeCancelarIngressoDeOutroComprador` | `0a982ee80eff` |
| [CancelarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/CancelarIngressoServiceStructuralTest.java) | `clienteCancelaProprioIngressoEAdministradorPodeCancelarIngressoDeOutro` | `13226f7ca047` |
| [CancelarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/CancelarIngressoServiceStructuralTest.java) | `informaQuandoIngressoNaoPertenceASessaoEUsaSobrecargaAdministrativa` | `e8c31d110b8d` |
| [CancelarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/CancelarIngressoServiceStructuralTest.java) | `naoSalvaIngressoQuandoSessaoJaComecou` | `982f0e2bfe41` |
| [RemoverPecaServiceStructuralTest](../src/test/java/br/ifsp/demo/application/RemoverPecaServiceStructuralTest.java) | `rejeitaPecaInexistente` | `50949ea66ae9` |
| [RemoverPecaServiceStructuralTest](../src/test/java/br/ifsp/demo/application/RemoverPecaServiceStructuralTest.java) | `rejeitaPecaQueAindaPossuiSessoes` | `62a02bb8ceb5` |
| [RemoverPecaServiceStructuralTest](../src/test/java/br/ifsp/demo/application/RemoverPecaServiceStructuralTest.java) | `removePecaExistenteSemSessoes` | `7ac57a7c60f6` |
| [CancelarIngressoServiceStructuralTest](../src/test/java/br/ifsp/demo/application/CancelarIngressoServiceStructuralTest.java) | `cancelaIngressoSolicitadoQuandoSessaoPossuiOutrosIngressos` | `339db65b5368` |
| [DatabaseSchemaMigrationMutationTest](../src/test/java/br/ifsp/demo/persistence/DatabaseSchemaMigrationMutationTest.java) | `adicionaAsColunasAusentesDeUsuarioEIngresso` | `4f8a70c77375` |
| [DatabaseSchemaMigrationMutationTest](../src/test/java/br/ifsp/demo/persistence/DatabaseSchemaMigrationMutationTest.java) | `naoTentaAdicionarColunasQueJaExistem` | `4ce901ed1917` |
| [DemoAccountsInitializerMutationTest](../src/test/java/br/ifsp/demo/persistence/DemoAccountsInitializerMutationTest.java) | `inicializaContaDeClienteEAdministradorQuandoEstaoAusentes` | `2961113f16a7` |
| [DemoAccountsInitializerMutationTest](../src/test/java/br/ifsp/demo/persistence/DemoAccountsInitializerMutationTest.java) | `naoDuplicaContasDeDemonstracaoJaExistentes` | `98fd735d3f56` |
| [DemoAccountsInitializerMutationTest](../src/test/java/br/ifsp/demo/persistence/DemoAccountsInitializerMutationTest.java) | `naoConsultaRepositorioQuandoInicializacaoEstaDesabilitada` | `f34585a9fa70` |
| [SessaoRepositoryTest](../src/test/java/br/ifsp/demo/persistence/SessaoRepositoryTest.java) | `deveCarregarIngressosAssociadosAoBuscarSessao` | `fdeeebf25651` |
| [SessaoRepositoryTest](../src/test/java/br/ifsp/demo/persistence/SessaoRepositoryTest.java) | `deveListarSessoesComIngressosAssociados` | `9c3d1a78a248` |
| [SessaoControllerMutationTest](../src/test/java/br/ifsp/demo/api/SessaoControllerMutationTest.java) | `criaSessaoEFiltraIngressosNaListagemPorPapel` | `df2157997ec2` |
| [SessaoControllerMutationTest](../src/test/java/br/ifsp/demo/api/SessaoControllerMutationTest.java) | `buscaEAtualizaSessaoComOuSemUsuarioAutenticado` | `bb3044cba5de` |
| [SessaoControllerMutationTest](../src/test/java/br/ifsp/demo/api/SessaoControllerMutationTest.java) | `rejeitaCorposAusentesAntesDeChamarOsServicos` | `619318377d60` |
| [SessaoControllerMutationTest](../src/test/java/br/ifsp/demo/api/SessaoControllerMutationTest.java) | `delegaCompraCancelamentoERemocaoEConverteIngressoEmResposta` | `26a14daa3218` |
| [SessaoControllerMutationTest](../src/test/java/br/ifsp/demo/api/SessaoControllerMutationTest.java) | `converteExcecoesEmRespostasHttp` | `b85f08734f4d` |
| [AtualizarPecaServiceMutationTest](../src/test/java/br/ifsp/demo/application/AtualizarPecaServiceMutationTest.java) | `atualizaEDevolvePecaEncontrada` | `eff1f79a186b` |
| [AtualizarPecaServiceMutationTest](../src/test/java/br/ifsp/demo/application/AtualizarPecaServiceMutationTest.java) | `informaQuandoPecaNaoExisteENaoAtualizaRepositorio` | `68dde35fb797` |
