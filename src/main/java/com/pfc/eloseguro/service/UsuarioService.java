package com.pfc.eloseguro.service;

import com.pfc.eloseguro.dto.CadastroUsuarioForm;
import com.pfc.eloseguro.dto.EdicaoUsuarioForm;
import com.pfc.eloseguro.entity.Perfil;
import com.pfc.eloseguro.entity.Usuario;
import com.pfc.eloseguro.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(CadastroUsuarioForm form) {
        String email = normalizarEmail(form.getEmail());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com esse e-mail.");
        }
        if (!form.getSenha().equals(form.getConfirmarSenha())) {
            throw new IllegalArgumentException("As senhas não são iguais.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(form.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        usuario.setPerfil(Perfil.USUARIO);
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    public void atualizar(String id, EdicaoUsuarioForm form) {
        Usuario usuario = buscarPorId(id);
        String email = normalizarEmail(form.getEmail());
        usuarioRepository.findByEmailIgnoreCase(email)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new IllegalArgumentException("Já existe outro usuário com esse e-mail.");
                });
        usuario.setNome(form.getNome().trim());
        usuario.setEmail(email);
        usuario.setPerfil(form.getPerfil());
        usuario.setAtivo(form.isAtivo());
        usuarioRepository.save(usuario);
    }

    public void excluir(String id, String emailLogado) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getEmail().equalsIgnoreCase(emailLogado)) {
            throw new IllegalArgumentException("Você não pode excluir o próprio usuário enquanto está logado.");
        }
        usuarioRepository.delete(usuario);
    }

    public EdicaoUsuarioForm criarFormularioEdicao(Usuario usuario) {
        EdicaoUsuarioForm form = new EdicaoUsuarioForm();
        form.setNome(usuario.getNome());
        form.setEmail(usuario.getEmail());
        form.setPerfil(usuario.getPerfil());
        form.setAtivo(usuario.isAtivo());
        return form;
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
