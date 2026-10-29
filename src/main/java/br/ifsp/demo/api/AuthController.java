package br.ifsp.demo.api;

import br.ifsp.demo.application.AutenticarUsuarioService;
import br.ifsp.demo.application.CredenciaisInvalidasException;
import br.ifsp.demo.application.RegistrarUsuarioService;
import br.ifsp.demo.domain.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final RegistrarUsuarioService registrarUsuarioService;
    private final AutenticarUsuarioService autenticarUsuarioService;

    public AuthController(RegistrarUsuarioService registrarUsuarioService,
                          AutenticarUsuarioService autenticarUsuarioService) {
        this.registrarUsuarioService = registrarUsuarioService;
        this.autenticarUsuarioService = autenticarUsuarioService;
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody RegistrarUsuarioRequest request) {
        Usuario usuario = registrarUsuarioService.registrar(request.nome(), request.email(), request.senha());
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + usuario.getId()))
                .body(new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail()));
    }

    @PostMapping("/autenticacao")
    public AutenticacaoResponse autenticar(@RequestBody AutenticarUsuarioRequest request) {
        return new AutenticacaoResponse(autenticarUsuarioService.autenticar(request.email(), request.senha()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> dadosInvalidos() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Void> emailJaCadastrado() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Void> credenciaisInvalidas() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}