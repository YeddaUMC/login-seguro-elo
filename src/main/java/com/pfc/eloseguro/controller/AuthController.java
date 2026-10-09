package com.pfc.eloseguro.controller;

import com.pfc.eloseguro.dto.CadastroUsuarioForm;
import com.pfc.eloseguro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new CadastroUsuarioForm());
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("usuario") CadastroUsuarioForm form,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "cadastro";
        }
        try {
            usuarioService.cadastrar(form);
            redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado. Agora você já pode entrar.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("cadastro", ex.getMessage());
            return "cadastro";
        }
    }
}
