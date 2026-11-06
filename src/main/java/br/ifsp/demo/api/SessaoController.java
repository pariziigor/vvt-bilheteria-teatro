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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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
import java.util.Map;
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
    public ResponseEntity<SessaoResponse> criar(@RequestBody CriarSessaoRequest request,
                                                Authentication authentication) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados da sessão são obrigatórios");
        }
        Sessao sessao = criarSessaoService.criar(
                request.pecaId(),
                new DataHoraSessao(request.data(), request.horaInicio(), request.horaFim()),
                request.capacidade(),
                request.valorBaseIngresso()
        );
        return ResponseEntity.created(URI.create("/api/v1/sessoes/" + sessao.getId()))
                .body(SessaoResponse.de(sessao, authentication.getName(), isAdmin(authentication)));
    }

    @GetMapping
    public List<SessaoResponse> listar(Authentication authentication) {
        boolean administrador = isAdmin(authentication);
        String usuarioEmail = authentication == null ? null : authentication.getName();
        return listarSessoesService.listar().stream()
                .map(sessao -> SessaoResponse.de(sessao, usuarioEmail, administrador))
                .toList();
    }

    @GetMapping("/{sessaoId}")
    public SessaoResponse buscar(@PathVariable UUID sessaoId, Authentication authentication) {
        String usuarioEmail = authentication == null ? null : authentication.getName();
        return SessaoResponse.de(buscarSessaoService.buscar(sessaoId), usuarioEmail, isAdmin(authentication));
    }

    @PutMapping("/{sessaoId}")
    public SessaoResponse atualizar(@PathVariable UUID sessaoId, @RequestBody AtualizarSessaoRequest request,
                                    Authentication authentication) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados da sessão são obrigatórios");
        }
        Sessao sessao = atualizarSessaoService.atualizar(
                sessaoId,
                request.pecaId(),
                new DataHoraSessao(request.data(), request.horaInicio(), request.horaFim()),
                request.capacidade(),
                request.valorBaseIngresso()
        );
        return SessaoResponse.de(sessao, authentication.getName(), isAdmin(authentication));
    }

    @DeleteMapping("/{sessaoId}/ingressos/{ingressoId}")
    public ResponseEntity<Void> cancelarIngresso(
            @PathVariable UUID sessaoId,
            @PathVariable UUID ingressoId,
            Authentication authentication
    ) {
        cancelarIngressoService.cancelar(sessaoId, ingressoId, authentication.getName(), isAdmin(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{sessaoId}")
    public ResponseEntity<Void> remover(@PathVariable UUID sessaoId) {
        removerSessaoService.remover(sessaoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{sessaoId}/ingressos")
    public ResponseEntity<IngressoResponse> comprarIngresso(@PathVariable UUID sessaoId,
                                                            @RequestBody ComprarIngressoRequest request,
                                                            Authentication authentication) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados do ingresso são obrigatórios");
        }
        Ingresso ingresso = comprarIngressoService.comprar(sessaoId, request.tipoIngresso(), authentication.getName());
        return ResponseEntity.status(201).body(IngressoResponse.de(ingresso));
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
    public ResponseEntity<Map<String, String>> conflito(IllegalStateException exception) {
        return ResponseEntity.status(409).body(Map.of("mensagem", exception.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> acessoNegado() {
        return ResponseEntity.status(403).build();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
