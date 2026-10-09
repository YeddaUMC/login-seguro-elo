package com.pfc.eloseguro.config;

import com.pfc.eloseguro.entity.Perfil;
import com.pfc.eloseguro.entity.Usuario;
import com.pfc.eloseguro.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInicialConfig implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.name:Administrador}")
    private String adminName;

    public AdminInicialConfig(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }
        String email = adminEmail.trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setNome(adminName.trim());
        admin.setEmail(email);
        admin.setSenha(passwordEncoder.encode(adminPassword));
        admin.setPerfil(Perfil.ADMIN);
        admin.setAtivo(true);
        admin.setCriadoEm(LocalDateTime.now());
        usuarioRepository.save(admin);
    }
}
