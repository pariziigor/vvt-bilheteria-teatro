package br.ifsp.demo.persistence;

import br.ifsp.demo.domain.IUsuarioRepository;
import br.ifsp.demo.domain.PapelUsuario;
import br.ifsp.demo.domain.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Order(2)
public class DemoAccountsInitializer implements ApplicationRunner {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String clientEmail;
    private final String clientPassword;
    private final String adminEmail;
    private final String adminPassword;

    public DemoAccountsInitializer(
            IUsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${demo.accounts.enabled:true}") boolean enabled,
            @Value("${demo.accounts.client-email:cliente.teste@teatro.com}") String clientEmail,
            @Value("${demo.accounts.client-password:Cliente123!}") String clientPassword,
            @Value("${demo.accounts.admin-email:admin.teste@teatro.com}") String adminEmail,
            @Value("${demo.accounts.admin-password:Admin123!}") String adminPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.clientEmail = clientEmail;
        this.clientPassword = clientPassword;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) return;

        createIfMissing("Cliente de teste", clientEmail, clientPassword, PapelUsuario.CLIENTE);
        createIfMissing("Administrador de teste", adminEmail, adminPassword, PapelUsuario.ADMIN);
    }

    private void createIfMissing(String nome, String email, String senha, PapelUsuario papel) {
        if (usuarioRepository.buscarPorEmail(email).isPresent()) return;

        Usuario usuario = new Usuario(
                UUID.randomUUID(),
                nome,
                email,
                passwordEncoder.encode(senha),
                papel
        );
        usuarioRepository.salvar(usuario);
    }
}
