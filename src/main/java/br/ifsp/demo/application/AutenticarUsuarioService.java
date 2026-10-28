package br.ifsp.demo.application;

import br.ifsp.demo.domain.IUsuarioRepository;
import br.ifsp.demo.domain.Usuario;
import br.ifsp.demo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticarUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutenticarUsuarioService(IUsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                                    JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String autenticar(String email, String senha) {
        if (email == null || senha == null) {
            throw new CredenciaisInvalidasException();
        }
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .filter(encontrado -> passwordEncoder.matches(senha, encontrado.getSenhaCodificada()))
                .orElseThrow(CredenciaisInvalidasException::new);
        return jwtService.gerarToken(usuario.getEmail());
    }
}
