package br.ifsp.demo.api;

import br.ifsp.demo.application.CadastrarPecaService;
import br.ifsp.demo.application.AtualizarPecaService;
import br.ifsp.demo.application.ListarPecasService;
import br.ifsp.demo.application.RemoverPecaService;
import br.ifsp.demo.domain.Peca;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pecas")
public class PecaController {

    private final CadastrarPecaService cadastrarPecaService;
    private final AtualizarPecaService atualizarPecaService;
    private final RemoverPecaService removerPecaService;
    private final ListarPecasService listarPecasService;

    public PecaController(CadastrarPecaService cadastrarPecaService,
                          AtualizarPecaService atualizarPecaService,
                          RemoverPecaService removerPecaService,
                          ListarPecasService listarPecasService) {
        this.cadastrarPecaService = cadastrarPecaService;
        this.atualizarPecaService = atualizarPecaService;
        this.removerPecaService = removerPecaService;
        this.listarPecasService = listarPecasService;
    }

    @PostMapping
    public ResponseEntity<PecaResponse> cadastrar(@RequestBody CadastrarPecaRequest request) {
        Peca peca = cadastrarPecaService.cadastrar(request.titulo(), request.descricao(),
                Duration.ofMinutes(request.duracaoMinutos()), request.classificacao());
        return ResponseEntity.created(URI.create("/api/v1/pecas/" + peca.getId())).body(PecaResponse.de(peca));
    }

    @GetMapping
    public List<PecaResponse> listar() {
        return listarPecasService.listar().stream().map(PecaResponse::de).toList();
    }

    @PutMapping("/{pecaId}")
    public PecaResponse atualizar(@PathVariable UUID pecaId, @RequestBody AtualizarPecaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados da peça são obrigatórios");
        }
        Peca peca = atualizarPecaService.atualizar(
                pecaId,
                request.titulo(),
                request.descricao(),
                Duration.ofMinutes(request.duracaoMinutos()),
                request.classificacao()
        );
        return PecaResponse.de(peca);
    }

    @DeleteMapping("/{pecaId}")
    public ResponseEntity<Void> remover(@PathVariable UUID pecaId) {
        removerPecaService.remover(pecaId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> dadosInvalidos() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> naoEncontrado() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> conflito(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", exception.getMessage()));
    }
}
