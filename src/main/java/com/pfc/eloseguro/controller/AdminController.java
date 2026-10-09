package com.pfc.eloseguro.controller;

import com.pfc.eloseguro.dto.EdicaoUsuarioForm;
import com.pfc.eloseguro.entity.Perfil;
import com.pfc.eloseguro.entity.Usuario;
import com.pfc.eloseguro.service.UsuarioService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/admin/usuarios")
    public String listar(Model model, Principal principal) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("emailLogado", principal.getName());
        return "admin/usuarios";
    }

    @GetMapping("/admin/usuarios/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", usuarioService.criarFormularioEdicao(usuario));
        }
        model.addAttribute("usuarioId", id);
        model.addAttribute("perfis", Perfil.values());
        return "admin/editar-usuario";
    }

    @PostMapping("/admin/usuarios/{id}/editar")
    public String atualizar(@PathVariable String id,
                            @Valid @ModelAttribute("usuario") EdicaoUsuarioForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarioId", id);
            model.addAttribute("perfis", Perfil.values());
            return "admin/editar-usuario";
        }
        try {
            usuarioService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso.");
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("edicao", ex.getMessage());
            model.addAttribute("usuarioId", id);
            model.addAttribute("perfis", Perfil.values());
            return "admin/editar-usuario";
        }
    }

    @PostMapping("/admin/usuarios/{id}/excluir")
    public String excluir(@PathVariable String id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.excluir(id, principal.getName());
            redirectAttributes.addFlashAttribute("sucesso", "Usuário excluído com sucesso.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
