package br.ifsp.demo.api;

import br.ifsp.demo.application.AtualizarSessaoService;
import br.ifsp.demo.application.BuscarSessaoService;
import br.ifsp.demo.application.CancelarIngressoService;
import br.ifsp.demo.application.CriarSessaoService;
import br.ifsp.demo.application.ListarSessoesService;
import br.ifsp.demo.application.RemoverSessaoService;
import br.ifsp.demo.application.ComprarIngressoService;
import br.ifsp.demo.api.ComprarIngressoRequest;
import br.ifsp.demo.domain.Ingresso;
import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.Sessao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/sessoes")
public class SessaoController {

    private final CriarSessaoService criarSessaoService;
    private final ListarSessoesService listarSessoesService;
    private final BuscarSessaoService buscarSessaoService;
    private final AtualizarSessaoService atualizarSessaoService;
    private final CancelarIngressoService cancelarIngressoService;
    private final RemoverSessaoService removerSessaoService;
    private final ComprarIngressoService comprarIngressoService;

    public SessaoController(
            CriarSessaoService criarSessaoService,
            ListarSessoesService listarSessoesService,
            BuscarSessaoService buscarSessaoService,
            AtualizarSessaoService atualizarSessaoService,
            CancelarIngressoService cancelarIngressoService,
            RemoverSessaoService removerSessaoService,
            ComprarIngressoService comprarIngressoService
    ) {
        this.criarSessaoService = criarSessaoService;
        this.listarSessoesService = listarSessoesService;
        this.buscarSessaoService = buscarSessaoService;
        this.atualizarSessaoService = atualizarSessaoService;
        this.cancelarIngressoService = cancelarIngressoService;
        this.removerSessaoService = removerSessaoService;
        this.comprarIngressoService = comprarIngressoService;
    }

    @PostMapping
    public ResponseEntity<Sessao> criar(@RequestBody CriarSessaoRequest request) {
        Sessao sessao = criarSessaoService.criar(
                request.pecaId(),
                new DataHoraSessao(request.data(), request.horaInicio(), request.horaFim()),
                request.capacidade(),
                request.valorBaseIngresso()
        );
        return ResponseEntity.created(URI.create("/api/v1/sessoes/" + sessao.getId())).body(sessao);
    }

    @GetMapping
    public List<Sessao> listar() {
        return listarSessoesService.listar();
    }

    @GetMapping("/{sessaoId}")
    public Sessao buscar(@PathVariable UUID sessaoId) {
        return buscarSessaoService.buscar(sessaoId);
    }

    @PutMapping("/{sessaoId}")
    public Sessao atualizar(@PathVariable UUID sessaoId, @RequestBody AtualizarSessaoRequest request) {
        return atualizarSessaoService.atualizar(
                sessaoId,
                request.pecaId(),
                new DataHoraSessao(request.data(), request.horaInicio(), request.horaFim()),
                request.capacidade(),
                request.valorBaseIngresso()
        );
    }

    @DeleteMapping("/{sessaoId}/ingressos/{ingressoId}")
    public ResponseEntity<Void> cancelarIngresso(
            @PathVariable UUID sessaoId,
            @PathVariable UUID ingressoId
    ) {
        cancelarIngressoService.cancelar(sessaoId, ingressoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{sessaoId}")
    public ResponseEntity<Void> remover(@PathVariable UUID sessaoId) {
        removerSessaoService.remover(sessaoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{sessaoId}/ingressos")
    public ResponseEntity<Ingresso> comprarIngresso(@PathVariable UUID sessaoId,
                                                     @RequestBody ComprarIngressoRequest request) {
        Ingresso ingresso = comprarIngressoService.comprar(sessaoId, request.tipoIngresso());
        return ResponseEntity.status(201).body(ingresso);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> naoEncontrado() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> argumentoInvalido() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Void> conflito() {
        return ResponseEntity.status(409).build();
    }
}
