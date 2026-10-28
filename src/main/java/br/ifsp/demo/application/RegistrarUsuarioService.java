package br.ifsp.demo.application;

import br.ifsp.demo.domain.IUsuarioRepository;
import br.ifsp.demo.domain.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrarUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrarUsuarioService(IUsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(String nome, String email, String senha) {
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("A senha do usuário é obrigatória");
        }
        if (usuarioRepository.buscarPorEmail(email).isPresent()) {
            throw new IllegalStateException("Já existe um usuário com esse e-mail");
        }
        Usuario usuario = new Usuario(UUID.randomUUID(), nome, email, passwordEncoder.encode(senha));
        usuarioRepository.salvar(usuario);
        return usuario;
    }
}
