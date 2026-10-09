package br.ifsp.demo.api;

import br.ifsp.demo.application.CancelarIngressoService;
import br.ifsp.demo.application.CriarSessaoService;
import br.ifsp.demo.application.ListarSessoesService;
import br.ifsp.demo.domain.DataHoraSessao;
import br.ifsp.demo.domain.Sessao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessoes")
public class SessaoController {

    private final CriarSessaoService criarSessaoService;
    private final ListarSessoesService listarSessoesService;
    private final CancelarIngressoService cancelarIngressoService;

    public SessaoController(
            CriarSessaoService criarSessaoService,
            ListarSessoesService listarSessoesService,
            CancelarIngressoService cancelarIngressoService
    ) {
        this.criarSessaoService = criarSessaoService;
        this.listarSessoesService = listarSessoesService;
        this.cancelarIngressoService = cancelarIngressoService;
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

    @DeleteMapping("/{sessaoId}/ingressos/{ingressoId}")
    public ResponseEntity<Void> cancelarIngresso(
            @PathVariable UUID sessaoId,
            @PathVariable UUID ingressoId
    ) {
        cancelarIngressoService.cancelar(sessaoId, ingressoId);
        return ResponseEntity.noContent().build();
    }
}
