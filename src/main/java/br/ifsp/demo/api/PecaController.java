package br.ifsp.demo.api;

import br.ifsp.demo.application.CadastrarPecaService;
import br.ifsp.demo.application.ListarPecasService;
import br.ifsp.demo.domain.Peca;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pecas")
public class PecaController {

    private final CadastrarPecaService cadastrarPecaService;
    private final ListarPecasService listarPecasService;

    public PecaController(CadastrarPecaService cadastrarPecaService, ListarPecasService listarPecasService) {
        this.cadastrarPecaService = cadastrarPecaService;
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> dadosInvalidos() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}